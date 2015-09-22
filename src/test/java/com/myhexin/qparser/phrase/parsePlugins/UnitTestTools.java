package com.myhexin.qparser.phrase.parsePlugins;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.define.EnumDef.NodeType;
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
import com.myhexin.qparser.tool.encode.xml.GetObjectFromXml;
import com.myhexin.qparser.tool.encode.xml.GetXmlFromObject;

public class UnitTestTools {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginMatchSyntacticPatterns.class.getName());
	/*
	 * 调用私有方法
	 * className	类名，包含包路径
	 * methodName	方法名
	 * prams		参数列表
	 */
	public static Object invokePrivateMethod(String className, String methodName, Object[] prams) {
		try {
			Class classObject = Class.forName(className);
			Method[] ma = classObject.getDeclaredMethods();
			for (Method m : ma) {
				if (m.getName().equals(methodName)) {
					m.setAccessible(true);
					Object classInstance = classObject.newInstance();
					Object result = m.invoke(classInstance, prams);
					return result;
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * 判断两个ArrayList<ArrayList<SemanticNode>>是否相同
	 * expected	期望的值
	 * actual	实际的值
	 */
	public static boolean isEqualNodesList(ArrayList<ArrayList<SemanticNode>> expected,
			ArrayList<ArrayList<SemanticNode>> actual) {
		if (expected.size() != actual.size())
			return false;
		for (int i=0; i < expected.size(); i++) {
			ArrayList<SemanticNode> list1 = expected.get(i);
			ArrayList<SemanticNode> list2 = actual.get(i);
			for (int j=0; j < list1.size(); j++) {
				SemanticNode sn1 = list1.get(j);
				SemanticNode sn2 = list2.get(j);
				if (!sn1.equals(sn2)) {
					return false;
				} 
			}
		}
		return true;
	}
	
	/*
	 * 判断两个ArrayList<SemanticNode>是否相同
	 * expected	期望的值
	 * actual	实际的值
	 */
	public static boolean isEqualNodesListNode(ArrayList<SemanticNode> expected,
			ArrayList<SemanticNode> actual) {
		if (expected.size() != actual.size())
			return false;
			for (int j=0; j < expected.size(); j++) {
				SemanticNode sn1 = expected.get(j);
				SemanticNode sn2 = actual.get(j);
				if (!sn1.equals(sn2)) {
					return false;
				} 
			}
		
		return true;
	}
	
	//========================add by wyh=============================
	public static final void writeToXml(String fileName, Object object,String objectId) {
		new GetXmlFromObject(Param.UNIT_TEST_DIR + "/" + fileName)
				.createXML(object,objectId);
	}
	public static final Object getXmlObj(String fileName, String id) {
		return new GetObjectFromXml(Param.UNIT_TEST_DIR + "/" + fileName)
				.getObject(id);
	}
	
	//不设置xml中mainObjectId,默认使用object1
	public static final void writeToXml(String fileName, Object object) {
		new GetXmlFromObject(Param.UNIT_TEST_DIR + "/" + fileName)
				.createXML(object);
	}
	public static final Object getXmlObj(String fileName) {
		return new GetObjectFromXml(Param.UNIT_TEST_DIR + "/" + fileName)
				.getObject();
	}
	public static final String getQueryFromNode(ArrayList<SemanticNode> nodes) {
		StringBuilder sb = new StringBuilder();
		for (SemanticNode sn : nodes) {
			sb.append(sn.getText());
		}
		return sb.toString();
	}
	
	/**
	 * 根据分词结果生成对应节点
	 * @param token
	 * @return
	 * @throws QPException 
	 */
	public static SemanticNode parseNodeFromToken(String token) {
		try {
			int backSlantPos = token.lastIndexOf('/');
	        String text = (backSlantPos == -1) ? token : token.substring(0, backSlantPos);
	        String smInfo = (backSlantPos == token.length() - 1 ? "" : token.substring(backSlantPos + 1));
	        smInfo = (backSlantPos == -1) ? "" : smInfo;
	        Type qtype = Type.STOCK;
	        
	        if (smInfo.isEmpty() || smInfo.equals("onto_change:") || smInfo.equals("onto_class:") || smInfo.equals("onto_techOp:")) {
	            return new UnknownNode(text);
	        } else if(smInfo.startsWith("trans:")) {
	            logger_.error("trans bug: {} with {}", text, smInfo);
	            return new UnknownNode(text);
	        } else if (!smInfo.startsWith("onto_") || smInfo.indexOf(':', 5) < 6) {
	        	logger_.error("Dict info not starts with onto_", text, smInfo);
	            return new UnknownNode(text);
	        }
	
	        int pos = smInfo.indexOf(':', 5);
	        String strType = smInfo.substring(5, pos);
	        String moreInfo = ++pos < smInfo.length() ? smInfo.substring(pos) : null;
	        HashMap<String, String> k2v = new HashMap<String, String>();
	        if (moreInfo != null) {
		        for (String kvs : moreInfo.split(";")) {
		            int posOf = kvs.indexOf("=");
		            if (posOf < 0) {
		            	logger_.error("词典信息错误:KV非=分隔", text, smInfo);
		                return new UnknownNode(text);
		            }
		            String infoName = kvs.substring(0, posOf);
		            String infosVal = kvs.substring(posOf + 1);
		            k2v.put(infoName, infosVal);
		        }
	        }
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
	            rtn = MemOnto.getUserOnto(text, PropNodeFacade.class, qtype);
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
	        if (rtn.type == NodeType.CLASS || rtn.type == NodeType.DATE || rtn.type == NodeType.NUM)
	        	return ParsePluginsUtil.getSemanticNodeFromStr(rtn.getText(), rtn.type, Query.Type.ALL);
	        return rtn;
		} catch(QPException e) {
			logger_.error("UnitTestTools.parseNodeFromToken error", token);
			return null;
		}
	}
}
