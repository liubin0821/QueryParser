package com.myhexin.qparser.util.backtest.minutedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.date.DateParser;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.UrlReqType;
import com.myhexin.qparser.except.BadDictException;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.AvgNode;
import com.myhexin.qparser.node.ChangeNode;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.FakeDateNode;
import com.myhexin.qparser.node.FakeNumNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.GeoNode;
import com.myhexin.qparser.node.LogicNode;
import com.myhexin.qparser.node.NegativeNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.OperatorNode;
import com.myhexin.qparser.node.QuestNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.SortNode;
import com.myhexin.qparser.node.SpecialNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.node.TechPeriodNode;
import com.myhexin.qparser.node.TriggerNode;
import com.myhexin.qparser.node.UnknownNode;
import com.myhexin.qparser.number.NumParser;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.MemOnto;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.onto.UserPropNodeFacade;
import com.myhexin.qparser.phrase.parsePlugins.ParsePluginsUtil;
import com.myhexin.qparser.phrase.util.Consts;
import com.myhexin.qparser.time.TimeParser;
import com.myhexin.qparser.util.RequestItem;
import com.myhexin.qparser.util.ResponseItem;
import com.myhexin.qparser.util.URLReader;
import com.myhexin.qparser.util.backtest.minutedata.querypartten.MinuteDataQueryPattern;
import com.myhexin.qparser.util.backtest.minutedata.querypartten.impl.MaxMinExpressionPattern;
import com.myhexin.qparser.util.backtest.minutedata.querypartten.impl.NormalExpressionPattern;

public class MinuteDataBackTestQueryParser {

    private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(MinuteDataBackTestQueryParser.class
            .getName());

    private static Set<String> TECH_KEY_WORDS = new HashSet<String>();
    
    private static Map<String, String> OP_KEY_WORDS_CHANGE = new HashMap<String, String>();

    static {
        ApplicationContextHelper.loadApplicationContext();

        TECH_KEY_WORDS.add("macd");
        TECH_KEY_WORDS.add("kdj");
        TECH_KEY_WORDS.add("vr");
        TECH_KEY_WORDS.add("boll");
        
        OP_KEY_WORDS_CHANGE.put("大于", ">");
        OP_KEY_WORDS_CHANGE.put("高于", ">");
        OP_KEY_WORDS_CHANGE.put("大于等于", ">=");

        OP_KEY_WORDS_CHANGE.put("小于", "<");
        OP_KEY_WORDS_CHANGE.put("低于", "<");
        OP_KEY_WORDS_CHANGE.put("小于等于", "<=");
    }

    private static ArrayList<SemanticNode> processCommonParser(ArrayList<SemanticNode> nodes) {
        nodes = ApplicationContextHelper.getBean(TimeParser.class).parse(nodes);
        nodes = ApplicationContextHelper.getBean(DateParser.class).parse(nodes, null);
        nodes = ApplicationContextHelper.getBean(NumParser.class).parse(nodes);

        // lookup the key words in the SemanticNode list.
        for (int i = 0; i < nodes.size(); i++) {
            try {
                FocusNode focusNode = ParsePluginsUtil.getFocusNode(nodes.get(i).getPubText(), nodes.get(i));
                if (focusNode != null) {
                    nodes.set(i, focusNode);
                }
            } catch (UnexpectedException e) {
                logger_.error("Encountered UnexpectedException when lookup key word in minute query parser caused by "
                        + e.getMessage(), e);
                continue;
            }

        }
        return nodes;
    }

    public static List<BackTestMinuteCondition> parseMinuteDataQuery(String query) {

        String splitor = "[;；]";
        String[] subQueries = query.split(splitor);
        List<BackTestMinuteCondition> conditions = new ArrayList<BackTestMinuteCondition>();
        for (int i = 0; i < subQueries.length; i++) {
            String ltpResult = getLtpRlt(subQueries[i]);

            try {
                ArrayList<SemanticNode> nodes = convertTokensToNode(ltpResult);

                nodes = processCommonParser(nodes);
                conditions.add(buildMinuteCondition(nodes));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return conditions;
    }

    private static BackTestMinuteCondition buildMinuteCondition(ArrayList<SemanticNode> nodes) {
        BackTestMinuteCondition condition = new BackTestMinuteCondition();

        for (int i = 0; i < nodes.size(); i++) {
            SemanticNode n = nodes.get(i);
            if (n.isFocusNode()) {
                FocusNode fN = (FocusNode) n;
            }
        }
        nodes = normalizeQueryProcess(nodes);
        nodes = ignoreAllUnknownNodes(nodes);
        nodes = processQueryTimeRange(condition, nodes);
        nodes = processQueryLastTime(condition, nodes);

        boolean isTechQuery = isTechRelatedQuery(nodes);
        
        processPattern(condition, nodes);

        return condition;
    }
    
    private static List<MinuteDataQueryPattern> getAllPatterns() {
        List<MinuteDataQueryPattern> patterns = new ArrayList<MinuteDataQueryPattern>();
        patterns.add(new NormalExpressionPattern());
        patterns.add(new MaxMinExpressionPattern());
        return patterns;
    }
    
    private static void processPattern(BackTestMinuteCondition condition,  ArrayList<SemanticNode> nodes) {
        
        for (MinuteDataQueryPattern p : getAllPatterns()) {
            String expression = p.matchPattern(nodes);
            if (expression != null && expression.length() > 0) {

                condition.setExpression(expression);
                return;
            }
        }
    }
    
    /**
     * 直接判断 最后一个几点
     * @param condition
     * @param nodes
     * @return
     */
    private static ArrayList<SemanticNode> processQueryTimeAfterExpression(BackTestMinuteCondition condition, ArrayList<SemanticNode> nodes) {
        if (nodes.get(nodes.size() - 1).getType() == NodeType.DATE) {
            //TimeAfterExpression afterTime = new TimeAfterExpression();
            //condition.setTimeAfterExpr(timeAfterExpr);
        }
        return nodes;
    }
    
    /**
     * 把 大于，高于，
     * @param nodes
     * @return
     */
    private static ArrayList<SemanticNode> normalizeQueryProcess(ArrayList<SemanticNode> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            if (OP_KEY_WORDS_CHANGE.keySet().contains(nodes.get(i).getPubText())) {
                
                String changedValue = OP_KEY_WORDS_CHANGE.get(nodes.get(i).getPubText());
                
                nodes.get(i).setText(changedValue);
            }
        }
        return nodes;
    }

    private static boolean isTechRelatedQuery(ArrayList<SemanticNode> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            if (TECH_KEY_WORDS.contains(nodes.get(i).getPubText())) {
                return true;
            }
        }
        return false;
    }

    private static ArrayList<SemanticNode> ignoreAllUnknownNodes(ArrayList<SemanticNode> nodes) {
        ArrayList<SemanticNode> subList = new ArrayList<SemanticNode>();
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getType() != NodeType.UNKNOWN) {
                subList.add(nodes.get(i));
            }
        }
        return subList;
    }

    private static ArrayList<SemanticNode> processQueryLastTime(BackTestMinuteCondition condition,
            ArrayList<SemanticNode> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getType() != NodeType.FOCUS) {
                continue;
            }
            String nodeText = nodes.get(i).getPubText();
            if ("连续".equals(nodeText)) {
                if (nodes.get(i + 1).getType() == NodeType.DATE) {
                    String[] temp = nodes.get(i + 1).getPubText().split("[分钟]");
                    if (temp.length > 0) {
                        try {
                            condition.setStateLength(Integer.parseInt(temp[0]));
                        } catch (NumberFormatException e) {
                            logger_.error(" 分时策略 连续 后面的数值格式不对， " + e.getMessage(), e);
                        }
                    }
                    ArrayList<SemanticNode> subList = new ArrayList<SemanticNode>();
                    for (int j = i + 2; j < nodes.size(); j++) {
                        subList.add(nodes.get(j));
                    }
                    return subList;
                }
            }
        }
        return nodes;
    }

    private static ArrayList<SemanticNode> processQueryTimeRange(BackTestMinuteCondition condition,
            ArrayList<SemanticNode> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getType() != NodeType.FOCUS) {
                continue;
            }
            String nodeText = nodes.get(i).getPubText();
            if ("-".equals(nodeText) || "到".equals(nodeText)) {
                if (nodes.get(i - 1).getType() != NodeType.TIME && nodes.get(i + 1).getType() != NodeType.TIME) {
                    throw new IllegalStateException("问句中的 时间参数格式不是 9:30-11:30 或是  9:30到11:30");
                }
                int startTimeIndex = MinuteDataTimeRange.convertToMinuteIndexTime(nodes.get(i - 1).getPubText());
                int endTimeIndex = MinuteDataTimeRange.convertToMinuteIndexTime(nodes.get(i + 1).getPubText());
                condition.setStartTime(startTimeIndex);
                condition.setEndTime(endTimeIndex);

                ArrayList<SemanticNode> subList = new ArrayList<SemanticNode>();
                for (int j = i + 2; j < nodes.size(); j++) {
                    subList.add(nodes.get(j));
                }
                return subList;
            }
        }
        return nodes;
    }

    private static ArrayList<SemanticNode> convertTokensToNode(String ltpResult) throws QPException {
        ArrayList<SemanticNode> nodes = new ArrayList<SemanticNode>();
        String[] tokens = ltpResult.split(Consts.STR_TAB);
        if (tokens != null && tokens.length > 0) {
            for (String token : tokens) {
                int pos = token.indexOf(Consts.CHAR_SLASH, 1);// 为什么从1开始,原因是charAt(0)可以使除号
                if (pos > 0) {
                    String word = token.substring(0, pos);
                    String info = (pos == token.length() - 1 ? Consts.STR_BLANK : token.substring(pos + 1));
                    SemanticNode sn = parseNode(word, info, Query.Type.STOCK);
                    nodes.add(sn);
                }
            }
        }

        return nodes;

    }

    /**
     * 单种可能性的分词 返回分词结果
     */
    private static String getLtpRlt(String text) {
        RequestItem reqItem = new RequestItem(UrlReqType.TKN_LTP);
        reqItem.setQueryStr(text);

        String seggerUrl = Param.ALL_SEGGER_URL;
        URLReader urlReader = new URLReader(seggerUrl);
        ResponseItem rspItem = null;
        for (int i = 0; i < Param.MAX_CONNECT_TIMES; i++) {
            rspItem = urlReader.run(reqItem);
            if (rspItem == null || !rspItem.getRspOK()) {
                logger_.info(seggerUrl + text);
                logger_.info("try " + (i + 1));
                logger_.info(rspItem.getRspLogsStr(Consts.STR_NEW_LINE));
            } else {
                break;
            }
        }
        if (rspItem == null || !rspItem.getRspOK()) {
            logger_.error("try " + Param.MAX_CONNECT_TIMES);
            logger_.error(seggerUrl + text);
            logger_.error(rspItem.getRspLogsStr(Consts.STR_NEW_LINE));
        }
        String rspStr = rspItem.getRspRltsStr(Consts.STR_NEW_LINE);
        return rspStr;
    }

    private static SemanticNode parseNode(String text, String smInfo, Query.Type qtype) throws QPException {
        // System.out.println("text:"+text+"  smInfo:" + smInfo);
        if (smInfo.isEmpty() || smInfo.equals("onto_change:") || smInfo.equals("onto_class:")
                || smInfo.equals("onto_techOp:")) {
            return new UnknownNode(text);
        } else if (smInfo.startsWith("trans:")) {
            // a bug when transforming words in LTP server
            logger_.error("trans bug: {} with {}", text, smInfo);
            return new UnknownNode(text);
        } else if (!smInfo.startsWith("onto_") || smInfo.indexOf(':', 5) < 6) {
            throw new BadDictException("Dict info not starts with onto_", NodeType.UNKNOWN, "smInfo=" + smInfo
                    + ", text=" + text);
        }

        int pos = smInfo.indexOf(':', 5);
        String strType = smInfo.substring(5, pos);
        String dictInfo = ++pos < smInfo.length() ? smInfo.substring(pos) : null;
        HashMap<String, String> k2v = parseMoreInfo(dictInfo);
        SemanticNode rtn = null;
        if (strType.equals("date")) {
            rtn = new DateNode(text);
        } else if (strType.equals("num")) {
            rtn = new NumNode(text);
        } else if (strType.equals("vagueDate")) {
            rtn = new FakeDateNode(text);
        } else if (strType.equals("vagueNum")) {
            rtn = new FakeNumNode(text);
        } else if (strType.equals("special")) {
            rtn = new SpecialNode(text);
        } else if (strType.equals("change")) {
            rtn = new ChangeNode(text);
        } else if (strType.equals("trigger")) {
            rtn = new TriggerNode(text);
        } else if (strType.equals("operator")) {
            rtn = new OperatorNode(text);
        } else if (strType.equals("techOp")) {
            return new UnknownNode(text);
            // rtn = new TechOpNode(text);
        } else if (strType.equals("geoname")) {
            rtn = new GeoNode(text);
        } else if (strType.equals("logic")) {
            rtn = new LogicNode(text);
        } else if (strType.equals("qword")) {
            rtn = new QuestNode(text);
        } else if (strType.equals("techPeriod")) {
            rtn = new TechPeriodNode(text);
        } else if (strType.equals("class")) {
            rtn = MemOnto.getSysOnto(text, ClassNodeFacade.class, qtype);
            if (rtn == null) {
                String err = String.format(Param.FOR_IFIND_SERVER ? MsgDef.NOT_EXIST_IN_ONTO_FMT
                        : MsgDef.INDEX_NOT_AVAIL_CLT_FMT, text);
                throw Param.FOR_IFIND_SERVER ? new BadDictException(err, NodeType.CLASS, text) : new QPException(err);
            }
        } else if (strType.equals("fakeClass")) {
            rtn = MemOnto.getSysOnto(text, ClassNodeFacade.class, qtype);
            if (rtn == null) {
                String err = String.format(Param.FOR_IFIND_SERVER ? MsgDef.NOT_EXIST_IN_ONTO_FMT
                        : MsgDef.INDEX_NOT_AVAIL_CLT_FMT, text);
                throw Param.FOR_IFIND_SERVER ? new BadDictException(err, NodeType.CLASS, text) : new QPException(err);
            }
        } else if (strType.equals("fakeProp")) {
            rtn = MemOnto.getUserOnto(text, UserPropNodeFacade.class, qtype);
            if (rtn == null) {
                throw new BadDictException(text + "在本体配置文件中不存在", NodeType.PROP, text);
            }
        } else if (strType.equals("prop")) {
            rtn = MemOnto.getSysOnto(text, PropNodeFacade.class, qtype);
            if (rtn == null) {
                throw new BadDictException(text + "在本体配置文件中不存在", NodeType.PROP, text);
            }
        } else if (strType.equals("value")) {
            rtn = new StrNode(text);
        } else if (strType.equals("sort")) {
            rtn = new SortNode(text);
        } else if (strType.equals("avg")) {
            rtn = new AvgNode(text);
        } else if (strType.equals("neg")) {
            rtn = new NegativeNode(text);
        } else if (strType.equals("keyword")) {
            rtn = new UnknownNode(text);
        } else {
            throw new BadDictException(String.format("unknown type [onto_%s]", strType), NodeType.UNKNOWN, text);
        }

        rtn.parseNode(k2v, qtype);

        return rtn;
    }

    private static HashMap<String, String> parseMoreInfo(String moreInfo) throws BadDictException {
        HashMap<String, String> k2v = new HashMap<String, String>();
        if (moreInfo == null)
            return k2v;
        for (String kvs : moreInfo.split(";")) {
            int posOf = kvs.indexOf("=");
            if (posOf < 0) {
                throw new BadDictException("词典信息错误:KV非=分隔", NodeType.UNKNOWN, kvs);
            }
            String infoName = kvs.substring(0, posOf);
            String infosVal = kvs.substring(posOf + 1);
            k2v.put(infoName, infosVal);
        }
        return k2v;
    }
}
