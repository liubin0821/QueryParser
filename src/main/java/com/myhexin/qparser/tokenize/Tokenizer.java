package com.myhexin.qparser.tokenize;

import java.util.ArrayList;
import java.util.HashMap;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.Query;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.except.BadDictException;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.node.AvgNode;
import com.myhexin.qparser.node.ChangeNode;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.Environment;
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
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.util.Consts;

public class Tokenizer {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(Tokenizer.class);
	
	//private Query query_;ParserAnnotation annotation;this.query_ = query;
    
    private Tokenizer() {
        
    }
    
    //private void _tokenize(ParserAnnotation annotation) { //Environment ENV, String line
    //    tokenizeBySegger(ENV,line);
    //}
    
    /** 
     * 单分词可能性的分词模块
     *
     * @param query
     */
    public static void tokenize(ParserAnnotation annotation) {
    	tokenizeBySegger(annotation);
    }
    
    private static void tokenizeBySegger(ParserAnnotation annotation){ //Environment ENV, String line) {
        try {
        	String line = annotation.getSegmentedText();
        	if(line==null || line.length()==0)
        	{
        		return;
        	}
        	
        	Environment ENV = annotation.getEnv();
        	Query.Type qType = annotation.getQueryType();
        	ArrayList<SemanticNode> nodes = new ArrayList<SemanticNode>();
        	
        	Environment listEnv = new Environment();
        	listEnv.put("queryEnv", ENV, true);
        	nodes.add(listEnv);//list中添加  列表的环境信息
            
        	
        	StringBuilder sbParseText = new StringBuilder();
        	int segNum = 0;
            String[] tokens = line.split(Consts.STR_TAB);
            if(tokens!=null && tokens.length>0) {
            	for (String token : tokens) {
            		String word = null;
                    String info = null ;
            		int pos = token.indexOf(Consts.CHAR_SLASH_ONTO); //处理"净利润/营业收入"这种词中间有slash的词
            		if(pos>0) {
            			word = token.substring(0, pos);
                        info = (pos == token.length() - 1 ? Consts.STR_BLANK : token.substring(pos + 1));
            		}else{
            			//int pos = token.lastIndexOf(Consts.CHAR_SLASH);
                		//lxf 2015/4/22. 000430词典info中包含多个CHAR_SLASH,导致出错,所以改成indexOf
                		pos = token.indexOf(Consts.CHAR_SLASH,1);//为什么从1开始,原因是charAt(0)可以使除号
                        if(pos>0) {
                        	word = token.substring(0, pos);
                            info = (pos == token.length() - 1 ? Consts.STR_BLANK : token.substring(pos + 1));
                            //System.out.println("word ++"+word+"++info ++"+info+"++type ++"+query_.getType());
                        }
            		}
                    if(word!=null && info!=null) {
                    	 SemanticNode sn = parseNode(word, info, qType);
                         nodes.add(sn);
                         segNum++;
                    }
                }
            	listEnv.put("segNum", segNum, true);
            }
            
            //set in annotation
            annotation.setTokenizeText(sbParseText.toString());
            ArrayList<ArrayList<SemanticNode>> qlist = new ArrayList<ArrayList<SemanticNode>>(1); 
        	qlist.add(nodes);
        	annotation.setQlist(qlist);
        	
        	
            //query_.parseText = sbParseText.toString();
        } catch (BadDictException bde) {
            //query_.getLog().logMsg(ParseLog.LOG_ERROR, MsgDef.BAD_DICT_INFO_STR);
            logger_.error(bde.getLogMsg());
        } catch (QPException e) {
            //query_.getLog().logMsg(ParseLog.LOG_ERROR, e.getMessage());
            logger_.error(e.getLogMsg());
        } 
    }

    private static SemanticNode parseNode(String text, String smInfo, Query.Type qtype) throws QPException {
    	//System.out.println("text:"+text+"  smInfo:" + smInfo);
        if (smInfo.isEmpty() || smInfo.equals("onto_change:") || smInfo.equals("onto_class:") || smInfo.equals("onto_techOp:")) {
            //if(text.trim().length()==0) text=","; //强制将" "=>,
        	return new UnknownNode(text);
        } else if(smInfo.startsWith("trans:")) {
            //a bug when transforming words in LTP server
            logger_.error("trans bug: {} with {}", text, smInfo);
            return new UnknownNode(text);
        } else if (!smInfo.startsWith("onto_") || smInfo.indexOf(':', 5) < 6) {
            throw new BadDictException("Dict info not starts with onto_", NodeType.UNKNOWN, "smInfo=" +smInfo  + ", text=" + text);
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
            throw new BadDictException(String.format("unknown type [onto_%s]", strType),NodeType.UNKNOWN, text);
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
                throw new BadDictException("词典信息错误:KV非=分隔", NodeType.UNKNOWN, kvs);
            }
            String infoName = kvs.substring(0, posOf);
            String infosVal = kvs.substring(posOf + 1);
            k2v.put(infoName, infosVal);
        }
        return k2v;
    }
}
