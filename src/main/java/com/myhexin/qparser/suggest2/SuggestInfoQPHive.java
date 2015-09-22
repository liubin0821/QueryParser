package com.myhexin.qparser.suggest2;

import java.util.ArrayList;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;
import com.myhexin.qparser.ParseLog;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.NodeUtil;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.NumNode.MoveType;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.number.NumRange;

public class SuggestInfoQPHive {
    private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
            .getLogger(SuggestInfoQPHive.class.getName());

    private static final ArrayList<String> ACCEPT_MID_TMP_NODE_TYPE = new ArrayList<String>();
    private static final ArrayList<String> ACCEPT_COARSE_TMP_NODE_TYPE = new ArrayList<String>();
    private static final ArrayList<String> ACCEPT_INDEX_NODE_TYPE = new ArrayList<String>();

    static {
        // 填充中等粒度模板所能够接受的节点类型
        ACCEPT_MID_TMP_NODE_TYPE.add("LOGIC");
        ACCEPT_MID_TMP_NODE_TYPE.add("DATE");
        ACCEPT_MID_TMP_NODE_TYPE.add("NUM");
        ACCEPT_MID_TMP_NODE_TYPE.add("OPERATOR");
        ACCEPT_MID_TMP_NODE_TYPE.add("SORT");
        ACCEPT_MID_TMP_NODE_TYPE.add("AVG");
        ACCEPT_MID_TMP_NODE_TYPE.add("UNKNOWN");

        // 填充粗粒度模板所能够接受的节点类型
        ACCEPT_COARSE_TMP_NODE_TYPE.add("LOGIC");
        ACCEPT_COARSE_TMP_NODE_TYPE.add("DATE");
        ACCEPT_COARSE_TMP_NODE_TYPE.add("NUM");
        ACCEPT_COARSE_TMP_NODE_TYPE.add("OPERATOR");
        ACCEPT_COARSE_TMP_NODE_TYPE.add("SORT");
        ACCEPT_COARSE_TMP_NODE_TYPE.add("AVG");
        ACCEPT_COARSE_TMP_NODE_TYPE.add("INST");
        ACCEPT_COARSE_TMP_NODE_TYPE.add("PROP");
        ACCEPT_COARSE_TMP_NODE_TYPE.add("INDEX");
        ACCEPT_COARSE_TMP_NODE_TYPE.add("STR_VAL");

        // 填充指标列表所能够接受的节点类型
        ACCEPT_INDEX_NODE_TYPE.add("INDEX");
    }

    /**
     * 根据QueryParser解析后的Query来填充一个SuggestInfo
     * 
     * @param query
     * @return
     */
    private SuggestInfo fillQPRltSuggestInfo(Query query) {
        SuggestInfo si = new SuggestInfo();
        si.suggestInfoType_ = "q2s_qprlt";
        si.userQueryStr_ = query.text;
        for (SemanticNode snode : query.getNodes()) {
            si.smticNodeTextList_.add(snode.getText());
            si.smticNodeTypeList_
                    .add(smticNodeType2SuggestInfoNodeType(snode.type));
        }
        si.qpOK_ = query.hasFatalError() ? "failed" : "success";
        boolean onlyStock = (query.getNodes().size() == 1)
                && NodeUtil.isStockNode(query.getNodes().get(0));
        si.isStock_ = onlyStock ? "true" : "false";
        boolean containStock = false;
        for (SemanticNode node : query.getNodes()) {
            if (NodeUtil.isStockNode(node)) {
                containStock = true;
                si.stockInfo_.add(node.getText());
            }
        }
        si.containStock_ = containStock ? "true" : "false";
        for (int i = 0; i < query.getNodes().size(); i++) {
            SemanticNode node = query.getNodes().get(i);
            if (node.type == EnumDef.NodeType.DATE) {
                String di = createDateNodeInfoStr(node, i);
                if (di == null) {
                    si.dateAndNumInfo_.add("NULL");
                } else {
                    si.dateAndNumInfo_.add(di);
                }
            }
            if (node.type == EnumDef.NodeType.NUM) {
                String ni = createNumNodeInfoStr(node, i);
                if (ni == null) {
                    si.dateAndNumInfo_.add("NULL");
                } else {
                    si.dateAndNumInfo_.add(ni);
                }
            }
        }
        si.treeLog_ = query.getLog().getMsg(ParseLog.LOG_TREE);
        // si.stockInfo_ = onlyStock ? query.nodes().get(0).text : "";
        return si;
    }

    /**
     * 用从QueryParsre处获取的SuggestInfo信息填充一个SemanticQ
     * 
     * @param si
     * @return 成功填充，返回一个SemanticQ,否则，返回null
     */
    private SemanticQ fillSmticQBySuggestInfo(SuggestInfo si) {
        if (!si.suggestInfoType_.equals("q2s_qprlt")) {
            String msgStr = String.format("非QueryParser提供给问句推荐的问句解析结果包：\n%s",
                    si.toString());
            System.out.println(msgStr);
            logger_.error(msgStr);
            return null;
        }
        SemanticQ sq = new SemanticQ();
        sq.rawQueryStr_ = si.userQueryStr_;
        createSingleWordSegList(si, sq.singleWordSegList_);
        createTemplates(si, sq.fineGritTemplate_, sq.midGritTemplate_,
                sq.coarseGrainedTemplate_, sq.indexList_);
        sq.qpusable_ = "success".equals(si.qpOK_) ? true : false;
        sq.onlyStock_ = "true".equals(si.isStock_) ? true : false;
        sq.containStock_ = "true".equals(si.containStock_) ? true : false;
        createStockInfo(si, sq.stockInfo_);
        fillDateAndNumInfo(si, sq.dataAndNumInfoList_);
        sq.treeLog_ = si.treeLog_;
        return sq;
    }

    private void fillDateAndNumInfo(SuggestInfo si, ArrayList<String> list) {
        if (si == null) {
            return;
        }
        if (si.dateAndNumInfo_ == null) {
            return;
        }
        if (list == null) {
            return;
        }
        list.addAll(si.dateAndNumInfo_);
    }

    public String getSuggestMetaInfo(Query query) {
        try {
            SuggestInfo suggest_info = fillQPRltSuggestInfo(query);
            SemanticQ semantic_q = fillSmticQBySuggestInfo(suggest_info);
            Gson gson = new Gson();

            // convert java object to JSON format,
            // and returned as JSON formatted string
            String json = gson.toJson(semantic_q);
            return Base64.encodeBase64String(json.getBytes());
        } catch (Exception e) {
            String logStr = "获取问句解析推荐相关信息失败，请以SuggesInfoQPHive中的getSuggestMetaInfo函数为入口";
            logger_.error(logStr);
            return "";
        }
    }

    /**
     * 因为在QueryParser进行填充StrValue的时候，其可能在其之间使用STR_OR进行连接，所以在这我们要断开连接
     * 
     * @param si
     * @param list
     */
    public void createStockInfo(SuggestInfo si, ArrayList<String> list) {
        ArrayList<String> stockInfo = si.stockInfo_;
        for (String str : stockInfo) {
            if (str.indexOf(SuggestInfo.STOCK_INFO_SPL) > 0) {
                String[] stockArray = str.split(SuggestInfo.STOCK_INFO_SPL);
                for (int i = 0; i < stockArray.length; i++) {
                    list.add(stockArray[i]);
                }
            } else {
                list.add(str);
            }
        }
    }

    private String createNumNodeInfoStr(SemanticNode node, int nodePosition) {
        if (node == null) {
            return null;
        }
        if (node.type != EnumDef.NodeType.NUM) {
            return null;
        }
        NumNode nnode = (NumNode) node;
        StringBuilder sb = new StringBuilder();
        sb.append("num_");
        sb.append("unit|");
        String nunit = null;
        nunit = numUnit2DesUnit(nnode.getUnit());
        sb.append(nunit);
        sb.append("_index|");
        sb.append(Integer.toString(nodePosition));
        sb.append("_isBetween|");
        if (nnode.isBetween) {
            sb.append("true");
        } else {
            sb.append("false");
        }
        sb.append("_needMove|");
        if (nnode.needMove) {
            sb.append("true");
        } else {
            sb.append("false");
        }
        sb.append("_moveType|");
        sb.append(createMoveType(nnode.moveType));
        sb.append("_range|");
        sb.append(createNumRange(nnode.getNuminfo()));
        return sb.toString();
    }

    private String createNumRange(NumRange nr) {
        String errorStr = "0.0|0.0";
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(String.valueOf(nr.getDoubleFrom()));
            sb.append("|");
            sb.append(String.valueOf(nr.getDoubleTo()));
            return sb.toString();
        } catch (Exception e) {
            return errorStr;
        }
    }

    private String createDateNodeInfoStr(SemanticNode node, int nodePosition) {
        if (node == null) {
            return null;
        }
        if (node.type != EnumDef.NodeType.DATE) {
            return null;
        }
        DateNode dnode = (DateNode) node;
        StringBuilder sb = new StringBuilder();
        sb.append("date_");
        sb.append("unit|");
        String dunit = null;
        try {
            dunit = DateUtil.getDateUnit(dnode);
        }catch( UnexpectedException e ){
            ;
        }catch (Exception e) {
            ;
        }
        if (dunit == null) {
            dunit = "NULL";
        }
        sb.append(dunit);
        sb.append("_index|");
        sb.append(Integer.toString(nodePosition));
        sb.append("_isSequence|");
        if (dnode.isSequence) {
            sb.append("true");
        } else {
            sb.append("false");
        }
        sb.append("_mayTrade|");
        if (dnode.mayTrade) {
            sb.append("true");
        } else {
            sb.append("false");
        }
        sb.append("_fromto|");
        DateRange dateInfo = dnode.getDateinfo();
        String dateInfoStr = createDateInfoStr(dateInfo);
        sb.append(dateInfoStr);
        sb.append("_expandfromto|");
        DateRange expandDateInfo = dnode.getExpandedRange();
        String expandDateInfoStr = createDateInfoStr(expandDateInfo);
        sb.append(expandDateInfoStr);
        return sb.toString();
    }

    private String createDateInfoStr(DateRange dr) {
        String errorDateStr = "0000000000|0000000000";
        if (dr == null) {
            return errorDateStr;
        }
        StringBuilder sb = new StringBuilder();
        try {
            String fyearStr = String.format("%1$04d", dr.getFrom().getYear());
            String fmonthStr = String.format("%1$02d", dr.getFrom().getMonth());
            String fdayStr = String.format("%1$02d", dr.getFrom().getDay());
            String fweekStr = String.format("%1$02d", dr.getFrom().getWeek());
            sb.append(fyearStr);
            sb.append(fmonthStr);
            sb.append(fdayStr);
            sb.append(fweekStr);
            sb.append("|");
            String tyearStr = String.format("%1$04d", dr.getTo().getYear());
            String tmonthStr = String.format("%1$02d", dr.getTo().getMonth());
            String tdayStr = String.format("%1$02d", dr.getTo().getDay());
            String tweekStr = String.format("%1$02d", dr.getTo().getWeek());
            sb.append(tyearStr);
            sb.append(tmonthStr);
            sb.append(tdayStr);
            sb.append(tweekStr);
            return sb.toString();
        } catch (Exception e) {
            return errorDateStr;
        }
    }

    /**
     * 通过一个SuggestInfo对象生成一个单字分词的字符串list
     * 
     * @param si
     * @param single
     */
    private void createSingleWordSegList(SuggestInfo si,
            ArrayList<String> single) {
        if (single == null)
            return;
        SingleCharTokenizer.tokenize(si.userQueryStr_, single);
    }

    /**
     * 通过一个SuggestInfo的对象，生成三种粒度的模板
     * 
     * @param si
     * @param fine
     *            ：细粒度模板，完全是分词结果
     * @param mid
     *            ：中粒度模板，保存有句式和部分分词
     * @param coarse
     *            ：粗粒度模板，保存全部句式
     */
    private void createTemplates(SuggestInfo si, ArrayList<String> fine,
            ArrayList<String> mid, ArrayList<String> coarse,
            ArrayList<String> indexlist) {
        if (fine == null || mid == null || coarse == null)
            return;
        if (si.smticNodeTextList_.size() != si.smticNodeTypeList_.size()) {
            String msgStr = String.format(
                    "语义解析问句%s，解析节点内容个数为%s，类型个数为%s，不相等\n%s", si.userQueryStr_,
                    si.smticNodeTextList_.size(), si.smticNodeTypeList_.size(),
                    si.toString());
            System.out.println(msgStr);
            logger_.error(msgStr);
        }
        int nc = si.smticNodeTextList_.size();
        String nowNodeText = null;
        String nowNodeType = null;
        for (int i = 0; i < nc; i++) {
            nowNodeText = si.smticNodeTextList_.get(i);
            nowNodeType = si.smticNodeTypeList_.get(i);
            // 填充细粒度模板
            fine.add(nowNodeText);
            // 填充中等粒度模板
            if (ACCEPT_MID_TMP_NODE_TYPE.contains(nowNodeType)) {
                mid.add(nowNodeType);
            } else {
                mid.add(nowNodeText);
            }
            // 填充粗粒度模板
            if (ACCEPT_COARSE_TMP_NODE_TYPE.contains(nowNodeType)) {
                coarse.add(nowNodeType);
            }
            // 填充指标列表
            if (ACCEPT_INDEX_NODE_TYPE.contains(nowNodeType)) {
                indexlist.add(nowNodeText);
            }
        }
    }

    private String createMoveType(MoveType mt) {
        if (mt == null) {
            return "NULL";
        }
        if (mt.equals(MoveType.BOTH)) {
            return "BOTH";
        }
        if (mt.equals(MoveType.DOWN)) {
            return "DOWN";
        }
        if (mt.equals(MoveType.UP)) {
            return "UP";
        }
        return "NULL";
    }

    private String numUnit2DesUnit(Unit punit) {
        if (punit == null) {
            return "NULL";
        }
        if (punit.equals(Unit.BEI)) {
            return "BEI";
        }
        if (punit.equals(Unit.GE)) {
            return "GE";
        }
        if (punit.equals(Unit.GU)) {
            return "GU";
        }
        if (punit.equals(Unit.HU)) {
            return "HU";
        }
        if (punit.equals(Unit.JIA)) {
            return "JIA";
        }
        if (punit.equals(Unit.WEI)) {
            return "WEI";
        }
        if (punit.equals(Unit.PERCENT)) {
            return "PERCENT";
        }
        if (punit.equals(Unit.SHOU)) {
            return "SHOU";
        }
        if (punit.equals(Unit.YUAN)) {
            return "YUAN";
        }
        if (punit.equals(Unit.ZHI)) {
            return "ZHI";
        }
        return "NULL";
    }

    /**
     * 从SemanticNodeType到SuggestInfo所需的类型的映射
     * 
     * @param snt
     * @return
     */
    private String smticNodeType2SuggestInfoNodeType(NodeType snt) {
        // 顺序按照各种类型的Node出现的可能性从大到小排列
        if (snt == EnumDef.NodeType.UNKNOWN) {
            return "UNKNOWN";
        }
        if (snt == EnumDef.NodeType.STR_VAL) {
            return "STR_VAL";
        }
        if (snt == EnumDef.NodeType.CLASS) {
            return "INDEX";
        }
        if (snt == EnumDef.NodeType.DATE) {
            return "DATE";
        }
        if (snt == EnumDef.NodeType.NUM) {
            return "NUM";
        }
        if (snt == EnumDef.NodeType.LOGIC) {
            return "LOGIC";
        }
        if (snt == EnumDef.NodeType.SORT) {
            return "SORT";
        }
        if (snt == EnumDef.NodeType.AVG) {
            return "AVG";
        }
        if (snt == EnumDef.NodeType.SPECIAL) {
            return "SPECIAL";
        }
        if (snt == EnumDef.NodeType.CHANGE) {
            return "CHANGE";
        }
        if (snt == EnumDef.NodeType.OPERATOR) {
            return "OPERATOR";
        }
        if (snt == EnumDef.NodeType.GEO) {
            return "GEO";
        }
        if (snt == EnumDef.NodeType.QWORD) {
            return "QWORD";
        }
        if (snt == EnumDef.NodeType.INST) {
            return "INST";
        }
        if (snt == EnumDef.NodeType.PROP) {
            return "PROP";
        }
        if (snt == EnumDef.NodeType.PREDICT) {
            return "PREDICT";
        }
        if (snt == EnumDef.NodeType.TRIGGER) {
            return "TRIGGER";
        }
        if (snt == EnumDef.NodeType.OTHER) {
            return "OTHER";
        }
        if (snt == EnumDef.NodeType.BOOL) {
            return "BOOL";
        }
        // 如果未在当前定义的SemanticNode的Type当中则返回"UNKNOWN_TYPE"
        return "UNKNOWN_TYPE";
    }

    /*public static void main(String args[]) {
        String tstr = "同花顺开盘价";
        QueryParser qp = QueryParser.getParser("./conf/qparser.conf");
        Query query = new Query(tstr);
        qp.parse(query);
        // main driver should consider two functions below
        SuggestInfoQPHive siQPHive = new SuggestInfoQPHive();
        String s = siQPHive.getSuggestMetaInfo(query);
        s = new String(Base64.decodeBase64(s.getBytes()));
        Gson gson = new Gson();
        System.out.println(s);
        SemanticQ obj = gson.fromJson(s, SemanticQ.class);
        System.out.println(obj.toString());
    }*/
}
