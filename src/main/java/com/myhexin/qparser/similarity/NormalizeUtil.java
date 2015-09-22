package com.myhexin.qparser.similarity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.QueryNodes;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.UrlReqType;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.except.BadDictException;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.node.AvgNode;
import com.myhexin.qparser.node.ChangeNode;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.FakeDateNode;
import com.myhexin.qparser.node.FakeNumNode;
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
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.MemOnto;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.onto.UserPropNodeFacade;
import com.myhexin.qparser.util.Pair;
import com.myhexin.qparser.util.RequestItem;
import com.myhexin.qparser.util.ResponseItem;
import com.myhexin.qparser.util.URLReader;
import com.myhexin.qparser.util.Util;
//import com.myhexin.qparser.define.EnumDef.DebugType;
//import com.myhexin.qparser.define.EnumDef.LogMsg;
//import com.myhexin.qparser.define.EnumDef.MODULE;
//import com.myhexin.qparser.logs.ParseLog;
//import com.myhexin.qparser.util.QTypeUtil;

public class NormalizeUtil {
	
	public static org.slf4j.Logger logger_ = 
	        org.slf4j.LoggerFactory.getLogger(NormalizeUtil.class.getName());
	
	
	
	public static String getSeggerUrl(Query.Type qtype) {
		if(qtype == Query.Type.STOCK) return Param.stock_segger_url;
		else if(qtype == Query.Type.FUND) return Param.fund_segger_url;
        //if(qtype == Query.Type.FINA_PROD) return FINA_SEGGER_URL;
        //if(qtype == Query.Type.TRUST) return TRUST_SEGGER_URL;
		else if(qtype == Query.Type.SEARCH) return Param.search_segger_url;
        //if(qtype == Query.Type.INDUSTRY) return INDUSTRY_SEGGER_URL;
        //if(qtype == Query.Type.CONCEPT) return CONCEPT_SEGGER_URL;
        //if(qtype == Query.Type.REGION) return REGION_SEGGER_URL;
        //if(qtype == Query.Type.SECTOR) return SECTOR_SEGGER_URL;
        //if(qtype == Query.Type.PERSON) return PERSON_SEGGER_URL;
        //if(qtype == Query.Type.BOND) return BOND_SEGGER_URL;
        //if(qtype == Query.Type.REPORT) return REPORT_SEGGER_URL;
        //if(qtype == Query.Type.FUND_MANAGER) return FUNDMANAGER_SEGGER_URL;
		else if(qtype == Query.Type.HKSTOCK) return Param.hkstock_segger_url;
		else return Param.stock_segger_url;
	}
	
	/** 
	 * 独立的调用分词，返回原始的分词结果。
	 * @param line
	 * @param type
	 * @return segLine
	 */
	public static String tokenizeBySegger(String line, Query.Type type) {
	    String logStr = null;
		String segLine = null;
        line = CodeInfo.toLowerAndHalf(line);
        String seggerUrl = getSeggerUrl(type);
        try {
            seggerUrl += java.net.URLEncoder.encode(line, "utf-8");
        } catch (UnsupportedEncodingException e) {
            logStr = e.getMessage();
            logger_.error(logStr);
            return null;
        }
        seggerUrl += "&not=true";
        URLReader urlReader = new URLReader(seggerUrl);
        RequestItem reqItem = new RequestItem(UrlReqType.SIMI_NOTRANS_TOKENIZE);
        ResponseItem rspItem = urlReader.run(reqItem);
        boolean runOK = rspItem.getRspOK();
        if (!runOK) {
            logStr = Util.linkStringArrayListBySpl(rspItem.getRspLogs(), "\n");
            logger_.error(logStr);
            return null;
        }
        List<String> rspRlt = rspItem.getRspRlts();
        if (rspRlt == null || rspRlt.isEmpty()) {
            return null;
        }
        segLine = rspRlt.get(0);
		return segLine;
	}
	
	/**
	 * 调用带有同义词转换的分词，返回该分词的结果
	 * @param line
	 * @param type
	 * @return
	 */
	public static String tokenizeByHasTransSegger(String line, Query.Type type){
	    String logStr = null;
	    RequestItem reqItem = new RequestItem(UrlReqType.SIM_HASTRANS_TOKENIZE);
	    reqItem.setQueryStr(line);
	    String segUrl = getSeggerUrl(type);
	    URLReader reader = new URLReader(segUrl);
	    ResponseItem rspItem = reader.run(reqItem);
	    boolean runOK = rspItem.getRspOK();
	    if( !runOK ){
	        logStr = Util.linkStringArrayListBySpl(rspItem.getRspLogs(), "\n");
	        logger_.error(logStr);
	        return null;
	    }
	    List<String> rspList = rspItem.getRspRlts();
	    String rlt = Util.linkStringArrayListBySpl(rspList, "");
	    return rlt;
	}
	
	/**
	 * 获取分词结果中的分词及分词信息，如："五/onto_num:"  => pair(五，onto_num)	
	 * @param segLine 原始的分词信息
	 * @return
	 */
	public static List<Pair<String, String>> getWordInfoList(String segLine){
		List<Pair<String, String>> wordInfoList = new ArrayList<Pair<String,String>>() ;
		for (String token : segLine.split("\t")) {
		    int pos = token.lastIndexOf('/');
			if (pos == -1) {
				Pair<String, String> wordInfoPair = new Pair<String, String>(
						token, "");
				wordInfoList.add(wordInfoPair);
				continue ;
			}
		    String word = token.substring(0, pos);
		    String info = (pos == token.length() - 1 ? "" : token
		            .substring(pos + 1));
		    Pair<String, String> wordInfoPair = new Pair<String, String>(word, info) ;
		    wordInfoList.add(wordInfoPair) ;
		}
		return wordInfoList ;
	}
	
	

    private static SemanticNode parseNode(String text, String smInfo, Query.Type qtype) throws QPException {
    	//System.out.println("text:"+text+"  smInfo:" + smInfo);
        if (smInfo.isEmpty() || smInfo.equals("onto_change:") || smInfo.equals("onto_class:") || smInfo.equals("onto_techOp:")) {
            return new UnknownNode(text);
        } else if(smInfo.startsWith("trans:")) {
            //a bug when transforming words in LTP server
            logger_.error("trans bug: {} with {}", text, smInfo);
            return new UnknownNode(text);
        } else if (!smInfo.startsWith("onto_")
                || smInfo.indexOf(':', 5) < 6) {
            throw new BadDictException("Dict info not starts with onto_",
                    NodeType.UNKNOWN, text);
        }

        int pos = smInfo.indexOf(':', 5);
        String strType = smInfo.substring(5, pos);
        String dictInfo = ++pos < smInfo.length() ? smInfo.substring(pos)
                : null;
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
        }else if (strType.equals("special")) {
            rtn = new SpecialNode(text);
        }else if (strType.equals("change")) {
            rtn = new ChangeNode(text);
        } else if (strType.equals("trigger")) {
           rtn = new TriggerNode(text);
        } else if (strType.equals("operator")) {
            rtn = new OperatorNode(text);
        } else if (strType.equals("techOp")){
            return new UnknownNode(text);
            //rtn = new TechOpNode(text);
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
                String err = String.format(Param.FOR_IFIND_SERVER ?
                        MsgDef.NOT_EXIST_IN_ONTO_FMT :
                            MsgDef.INDEX_NOT_AVAIL_CLT_FMT,
                        text);
                throw Param.FOR_IFIND_SERVER ?
                        new BadDictException(err, NodeType.CLASS, text) :
                            new QPException(err);
            }
        } else if (strType.equals("fakeClass")) {
            rtn = MemOnto.getSysOnto(text, ClassNodeFacade.class, qtype);
            if (rtn == null) {
                String err = String.format(Param.FOR_IFIND_SERVER ?
                        MsgDef.NOT_EXIST_IN_ONTO_FMT :
                            MsgDef.INDEX_NOT_AVAIL_CLT_FMT,
                        text);
                throw Param.FOR_IFIND_SERVER ?
                        new BadDictException(err, NodeType.CLASS, text) :
                            new QPException(err);
            }
        }else if (strType.equals("fakeProp")) {
            rtn = MemOnto.getUserOnto(text, UserPropNodeFacade.class, qtype);
            if (rtn == null) {
                throw new BadDictException(text + "在本体配置文件中不存在",
                        NodeType.PROP, text);
            } 
        }else if (strType.equals("prop")) {
            rtn = MemOnto.getSysOnto(text, PropNodeFacade.class, qtype);
            if (rtn == null) {
                throw new BadDictException(text + "在本体配置文件中不存在",
                        NodeType.PROP, text);
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
            throw new BadDictException(String.format("unknown type [onto_%s]", strType),
                    NodeType.UNKNOWN, text);
        }
        
        rtn.parseNode(k2v, qtype);

        return rtn;
    }
    
    private static HashMap<String, String> parseMoreInfo(String moreInfo)
            throws BadDictException {
        HashMap<String, String> k2v = new HashMap<String, String>();
        if (moreInfo == null)
            return k2v;
        for (String kvs : moreInfo.split(";")) {
            int posOf = kvs.indexOf("=");
            if (posOf < 0) {
                throw new BadDictException("词典信息错误:KV非=分隔", NodeType.UNKNOWN,
                        kvs);
            }
            String infoName = kvs.substring(0, posOf);
            String infosVal = kvs.substring(posOf + 1);
            k2v.put(infoName, infosVal);
        }
        return k2v;
    }
	
	public static ArrayList<SemanticNode> makeQueryBySegLine(String segLine, Type type) throws QPException {
		ArrayList<SemanticNode> nodes = new ArrayList<SemanticNode>();
		StringBuilder ltpSegRlt = new StringBuilder();// 存储ltp分词的原始结果
		StringBuilder sbParseText = new StringBuilder();
		ltpSegRlt.append(segLine).append("\n");
		for (String token : segLine.split("\t")) {
			int pos = token.lastIndexOf('/');
			String word = token.substring(0, pos);
			String info = (pos == token.length() - 1 ? "" : token
					.substring(pos + 1));
			SemanticNode sn= parseNode(word, info, type);
			nodes.add(sn);
			sbParseText.append(sn.getText());
		}
		String text = sbParseText.toString() ;
		Query query = new Query(text) ;
		query.setType(type) ;
		nodes.trimToSize();
		
		//TODO delete setQueryNode here, not sure if this will cause issue
		//LXF
		//query.setQueryNode(new QueryNodes(nodes));
		
		
		
		//query.setParseText(sbParseText.toString());
		//query.nodes().chunkBySpecialMark();
        /*try {
            new Tokenizer(query).tokenize();
        } catch (QPException e) {
            logger_.error(e.getLogMsg());
        }*/
		return nodes;
	}

}
