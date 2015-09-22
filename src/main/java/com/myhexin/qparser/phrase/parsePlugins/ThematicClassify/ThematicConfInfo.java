package com.myhexin.qparser.phrase.parsePlugins.ThematicClassify;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.myhexin.qparser.define.EnumDef.IndexType;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.util.Util;

public class ThematicConfInfo {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(ThematicConfInfo.class.getName());
	
	
	/*
	 * 
	 * 
	 */
	public static ArrayList<ThematicMsg> thematicMsgs = new ArrayList<ThematicMsg>();
	
	public static void init() {
		thematicMsgs = new ArrayList<ThematicMsg>();
	}
	
	public static void reload(ArrayList<ThematicMsg> thematicMsgsTemp) {
		thematicMsgs = thematicMsgsTemp;
	}
	
	public static void loadResource(String fileName) throws DataConfException {
		thematicMsgs = loadXML(Util.readXMLFile(fileName));
	}

	public static ArrayList<ThematicMsg> loadXML(Document doc) throws DataConfException {
		ArrayList<ThematicMsg> thematicMsgs = new ArrayList<ThematicMsg>();
		Element root = doc.getDocumentElement();
		NodeList infoNodes = root.getChildNodes();
		if (infoNodes.getLength() == 0) {
			return null;
		}

		for (int i = 0; i < infoNodes.getLength(); i++) {
			ThematicMsg tm = new ThematicMsg();
			Node xmlClass = infoNodes.item(i);
			if (xmlClass.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			} else if (!xmlClass.getNodeName().equals("thematic")) {
				continue;
			}
			NamedNodeMap nnm = xmlClass.getAttributes();
			Node thematicNode = nnm.getNamedItem("name");
			String thematicName = thematicNode.getNodeValue();
			tm.name = thematicName;
			tm.type = nnm.getNamedItem("type").getNodeValue();
			if (nnm.getNamedItem("dateRange") != null)
				tm.dateRange = nnm.getNamedItem("dateRange").getNodeValue();
			NodeList typeList = xmlClass.getChildNodes();

			getThematicConditions(tm, typeList);
			//新增	
			thematicMsgs.add(tm);
		}
		return thematicMsgs;
	}

	private static void getThematicConditions(ThematicMsg tm, NodeList typeList) {

		for (int j = 0; j < typeList.getLength(); j++) {
			Node type = typeList.item(j);
			if (type.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			String xmlPropName = type.getNodeName();
			if (xmlPropName.equals("prop")) {
				//对prop属性的处理
				getIndexCondition(tm, type);
			} else if (xmlPropName.equals("sp_prop")) {
				//getSpProp(tm,type);
			} else if (xmlPropName.equals("syntactic")) {
				getSyntacticCondition(tm, type);
			} else if (xmlPropName.equals("semantic")) {
				getSemanticCondition(tm, type);
			} else if (xmlPropName.equals("date")) {
				getDateCondition(tm, type);
			}
		}
	}

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-3-20 下午9:05:08
     * @description:   	
     * 
     */
    private static void getDateCondition(ThematicMsg tm, Node type) {
		DateThematicCondition dtc = new DateThematicCondition();
		NamedNodeMap nnmt = type.getAttributes();
		dtc.mustExist = Boolean
		        .parseBoolean(nnmt.getNamedItem("must_exist") == null ? ""
		                : nnmt.getNamedItem("must_exist").getNodeValue().trim());
		
		dtc.isReport = Boolean
		        .parseBoolean(nnmt.getNamedItem("isReport") == null ? ""
		                : nnmt.getNamedItem("isReport").getNodeValue().trim());
		
		if (dtc.mustExist) 
			tm.mustCondition.add(dtc);
		else
			tm.otherCondition.add(dtc);
    }

	/**
	 * @author: 	    吴永行 
	 * @dateTime:	  2014-3-14 下午2:49:11
	 * @description:  语义
	 * 
	 */
	private static void getSemanticCondition(ThematicMsg tm, Node type) {
		SemanticThematicCondition stc = new SemanticThematicCondition();
		NamedNodeMap nnmt = type.getAttributes();
		stc.mustExist = Boolean
		        .parseBoolean(nnmt.getNamedItem("must_exist") == null ? ""
		                : nnmt.getNamedItem("must_exist").getNodeValue().trim());
		
		stc.id = nnmt.getNamedItem("id") == null ? "" : nnmt
		        .getNamedItem("id").getNodeValue().trim();
		
		if (stc.mustExist) 
			tm.mustCondition.add(stc);
		else
			tm.otherCondition.add(stc);
	}

	/**
	 * 
	 * @author: 	    吴永行 
	 * @dateTime:	  2014-3-14 下午2:49:46
	 * @description:   句式	
	 *
	 */
	private static void getSyntacticCondition(ThematicMsg tm, Node type) {
		SyntacticThematicCondition stc = new SyntacticThematicCondition();
		NamedNodeMap nnmt = type.getAttributes();

		stc.mustExist = Boolean
		        .parseBoolean(nnmt.getNamedItem("must_exist") == null ? ""
		                : nnmt.getNamedItem("must_exist").getNodeValue().trim());

		stc.setId(nnmt.getNamedItem("id") == null ? "" : nnmt.getNamedItem("id").getNodeValue().trim());

		
		if (stc.mustExist) 
			tm.mustCondition.add(stc);
		else
			tm.otherCondition.add(stc);
	}

	private static void getIndexCondition(ThematicMsg tm, Node type) {
		IndexThematicCondition sIndex = new IndexThematicCondition();
		NamedNodeMap nnmt = type.getAttributes();
		sIndex.setIndexName(nnmt.getNamedItem("index") == null ? "" : nnmt.getNamedItem("index").getNodeValue().trim());
		String indexType = nnmt.getNamedItem("index_type") == null ? "" : nnmt
		        .getNamedItem("index_type").getNodeValue().trim();
		if (indexType != "") {
			String[] types = indexType.split("\\|");
			for (String indType : types) {
				IndexType indeType = null;
				try {
					indeType = Enum.valueOf(IndexType.class, indType.trim());
				} catch (IllegalArgumentException ex) {
					String logStr = String.format("关于[%s]中[%s]Prop属性配置有误",
					        tm.name, indexType);
					logger_.error(logStr);
				}
				sIndex.addIndexType(indeType);
			}
		}
		sIndex.mustExist = Boolean.parseBoolean(nnmt.getNamedItem("must_exist") == null ? "" : nnmt.getNamedItem("must_exist").getNodeValue().trim());
		sIndex.matchSrc = nnmt.getNamedItem("src") == null ? "" : nnmt.getNamedItem("src").getNodeValue().trim();
		boolean isIlegalIndexName = sIndex.getIndexName() == null || sIndex.getIndexName().isEmpty();
		boolean isIlegalIndexType = indexType == null || sIndex.getIndexTypes()==null || sIndex.getIndexTypes().isEmpty();
		if (isIlegalIndexName && isIlegalIndexType) {
			String logStr = String.format(
			        "关于[%s]Prop属性中index和index_type不能同时为空", tm.name);
			logger_.error(logStr);
		} else {
			if (sIndex.mustExist)
				tm.mustCondition.add(sIndex);
			else
				tm.otherCondition.add(sIndex);
		}
	}
}
