package com.myhexin.qparser.onto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.xalan.xsltc.compiler.util.MultiHashtable;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.myhexin.DB.mybatis.mode.Aliases;
import com.myhexin.DB.mybatis.mode.ResolveAliasesConflicts;
import com.myhexin.qparser.Param;
import com.myhexin.qparser.ambiguty.AmbiguityCondInfoAbstract;
import com.myhexin.qparser.ambiguty.AmbiguityCondInfoDefaultVal;
import com.myhexin.qparser.ambiguty.AmbiguityCondInfoSet;
import com.myhexin.qparser.ambiguty.AmbiguityCondInfoWords;
import com.myhexin.qparser.define.EnumConvert;
import com.myhexin.qparser.define.EnumDef.IndexType;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.define.EnumDef.ReportType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.ExceptionUtil;
import com.myhexin.qparser.node.DBMeta;
//import com.myhexin.qparser.ambiguty.AmbiguityProcessor;

public class OntoXmlReaderOldSystem {
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(OntoXmlReader.class.getName());
	
	private static HashMap<String, String> conditions = new HashMap<String, String>();
	public static MultiHashtable subTypeToProp = new MultiHashtable();
	public static String ignoreWords = "";
 
    private static void loadCondition(Document doc) throws DataConfException{
        Element root = doc.getDocumentElement();
        NodeList infonodes = root.getChildNodes();
        if (infonodes.getLength() == 0) {
            throw new DataConfException(Param.ALL_ONTO_CONDITION_FILE, -1, "未定义任何内容");
        }
        for (int i = 0; i < infonodes.getLength(); i++) {
            Node xmlClass = infonodes.item(i);
            if (xmlClass.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            
            if (xmlClass.getNodeName().equals("condition")) {
            	NamedNodeMap nnm = xmlClass.getAttributes();
                String name = nnm.getNamedItem("name").getNodeValue();
                String keyword = nnm.getNamedItem("keyword").getNodeValue();
                conditions.put(name, keyword);
                continue;
			} else if(xmlClass.getNodeName().equals("ignoreWords")) {
				loadIgnoreWord(xmlClass);
			}
        }
    }
    
    /**
	 * @author: 吴永行
	 * 
	 */
	private static void loadIgnoreWord(Node node) throws DataConfException {		
        NodeList infonodes = node.getChildNodes();
        if (infonodes.getLength() == 0) {
        	logger_.error(Param.ALL_ONTO_CONDITION_FILE, -1, "未定义任何内容ignoreWords");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < infonodes.getLength(); i++) {
            Node xmlClass = infonodes.item(i);
            if (xmlClass.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (xmlClass.getNodeName().equals("word")) {
            	if (sb.length()!=0) sb.append("|");
            	sb.append(xmlClass.getTextContent());
            }
        }
        ignoreWords=sb.toString();
	}

	// 增加1修改返回值类型，返回包括not_alias和alias
	public static MultiValueMap loadOnto(Document doc, Document docCondition) throws DataConfException {
    	MultiValueMap rtn = new MultiValueMap();
        Element root = doc.getDocumentElement();
        NodeList infonodes = root.getChildNodes();
        if (infonodes.getLength() == 0) {
        	logger_.error(Param.ALL_ONTO_FILE_OLD_SYSTEM, -1, "未定义任何内容");
        	return rtn;
        }
        
        // 加载指标转换condition
        loadCondition(docCondition);

        for (int i = 0; i < infonodes.getLength(); i++) {
            Node xmlClass = infonodes.item(i);
            if (xmlClass.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            
            if (!xmlClass.getNodeName().equals("class")) {
            	logger_.error(Param.ALL_ONTO_FILE_OLD_SYSTEM, -1, "expect class node while got "+xmlClass.getNodeName());
            	continue;
            }

            NamedNodeMap nnm = xmlClass.getAttributes();       
            String label = nnm.getNamedItem("label").getNodeValue();
            Node reportTypeNode = nnm.getNamedItem("report_type");
            Node indexTypeNode = nnm.getNamedItem("index_type");
            
            String reportTypeStr = reportTypeNode != null ? reportTypeNode
                    .getNodeValue() : null;
            String indexTypeStr = indexTypeNode != null ? indexTypeNode
                    .getNodeValue() : null;

            if (label.length() == 0) {
            	logger_.error(Param.ALL_ONTO_FILE_OLD_SYSTEM, "有Class的label为空");
            	continue;
            } else if (rtn.get(label) != null) {
            	logger_.error(Param.ALL_ONTO_FILE_OLD_SYSTEM, "Duplicated Class ", label);
            	continue;
            }

            ClassNode cn = new ClassNode(label);
            ReportType reportType = parseReportType(reportTypeStr);
            cn.setReportType(reportType);
            IndexType indexType = parseIndexType(indexTypeStr);
            cn.setIndexType(indexType);
			rtn.put(label, cn.toFacade());
            try {
				loadClassInfo_(rtn, cn, xmlClass.getChildNodes());
        	} catch (Exception e) {
        		
        		logger_.error(e.getMessage());
        		logger_.error(ExceptionUtil.getStackTrace(e));
        		continue;
        	}
        }
        parserAlias(rtn);
        getCategorysPropAddTOIndex(rtn);
        return rtn;
    }
    
    /**
	 * @author: 	    吴永行 
	 * @dateTime:	  2013-12-31 上午10:29:38
	 * @description:   	
	 * @param rtn
	 * 
	 */
	private static void getCategorysPropAddTOIndex(MultiValueMap rtn) {
		Collection c = rtn.values();
		Iterator iterator = c.iterator();
		while(iterator.hasNext()){  
			Object sn = iterator.next(); 
			if (sn instanceof ClassNode) {
				ClassNode cn = (ClassNode) sn;
				getCategoryPropAddTOIndex(rtn, cn);
			}
		}
	}

	/**
	 * @author: 	    吴永行 
	 * @dateTime:	  2013-12-31 上午10:53:18
	 * @description:   	
	 * @param rtn
	 * @param cn
	 * 
	 */
	private static void getCategoryPropAddTOIndex(
			MultiValueMap rtn, ClassNode cn) {
		for (String category : cn.getCategorysLevel1()) {
			if (!rtn.containsKey(category)) {
				logger_.error("指标"+cn.getText() +"的category"+category+"未定义");
				continue;
			} else if(category.equals(cn.getText() )) { // 排除category指向自身
				continue;
			}		
			Collection c = rtn.getCollection(category);
			Iterator iterator = c.iterator();
				while(iterator.hasNext()){  
					Object snc = iterator.next();
					if (!(snc instanceof ClassNode) ) 
						continue;
					ClassNode categoryClassNode = (ClassNode) snc;
					if (categoryClassNode.getDomains().containsAll(cn.getDomains() ))
						cn.getSuperClass().add(categoryClassNode);
			}
		}
	}
 
	public static ReportType parseReportType(String reportTypeStr)
            throws DataConfException {
        ReportType reportType = null;
        boolean isNull = reportTypeStr == null;
        if (isNull) {
            return null;
        }
        boolean isYear = reportTypeStr.equals("YEAR".toLowerCase()) || reportTypeStr.equals("YEAR");
        boolean isHalfYear = reportTypeStr.equals("HALF_YEAR".toLowerCase()) || reportTypeStr.equals("HALF_YEAR");
        boolean isQuarter = reportTypeStr.equals("QUARTER".toLowerCase()) || reportTypeStr.equals("QUARTER");
        boolean isMonth = reportTypeStr.equals("MONTH".toLowerCase()) || reportTypeStr.equals("MONTH");
        boolean isDaily = reportTypeStr.equals("DAILY".toLowerCase()) || reportTypeStr.equals("DAILY");
        boolean isFutureQuarter = reportTypeStr.equals("FUTURE_QUARTER".toLowerCase()) || reportTypeStr.equals("FUTURE_QUARTER");
        boolean isFutureDaily = reportTypeStr.equals("FUTURE_DAILY".toLowerCase()) || reportTypeStr.equals("FUTURE_DAILY");
        boolean isTradeDaily = reportTypeStr.equals("TRADE_DAILY".toLowerCase()) || reportTypeStr.equals("TRADE_DAILY");
        boolean isNaturalDaily = reportTypeStr.equals("NATURAL_DAILY".toLowerCase()) || reportTypeStr.equals("NATURAL_DAILY");
        boolean isNow = reportTypeStr.equals("NOW".toLowerCase()) || reportTypeStr.equals("NOW");
        if (isYear) {
            reportType = ReportType.YEAR;
        } else if (isHalfYear) {
            reportType = ReportType.HALF_YEAR;
        } else if (isQuarter) {
            reportType = ReportType.QUARTER;
        } else if (isMonth) {
            reportType = ReportType.MONTH;
        } else if (isDaily) {
            reportType = ReportType.DAILY;
        } else if (isFutureQuarter) {
            reportType = ReportType.FUTURE_QUARTER;
        } else if (isFutureDaily) {
            reportType = ReportType.FUTURE_DAILY;
        } else if (isTradeDaily) {
            reportType = ReportType.TRADE_DAILY;
        } else if (isNaturalDaily) {
            reportType = ReportType.NATURAL_DAILY;
        } else if (isNow) {
            reportType = ReportType.NOW;
        } else {
        	// 目前主要处理report_type=""这种情况
        	if (!reportTypeStr.equals("")) {
        		logger_.error("stock_onto.xml: report_type-" + reportTypeStr);
        	}
            return null;
        }
        return reportType;
    }

    private static IndexType parseIndexType(String classType)
            throws DataConfException {
        if (classType == null) {
            return IndexType.UNKNOW;
        } 

        IndexType indexType = null;
        try {
            indexType = IndexType.valueOf(classType);
        } catch (IllegalArgumentException illArg) {
        	logger_.error(Param.ALL_ONTO_FILE_OLD_SYSTEM + "Unknow IndexType “%s”" + classType);
        }
        
        return indexType;
    }

    private static void loadClassInfo_(MultiValueMap memOnto,
            ClassNode smClass, NodeList xmlProps) throws DataConfException {
        ArrayList<String> propLabels = new ArrayList<String>();
        
        // 自己是自己的一个类别，为匹配时使用
        smClass.addCategorysLevel1(smClass.getText() );
        
        //AmbiguityProcessor ambiguityProcessor = new AmbiguityProcessor();
        for (int i = 0; i < xmlProps.getLength(); i++) {
            Node xmlProp = xmlProps.item(i);
            if (xmlProp.getNodeType() != Node.ELEMENT_NODE) { 
            	continue; 
            }
            String xmlPropName = xmlProp.getNodeName();
            if (xmlPropName.equals("subclass") || xmlPropName.equals("superclass")){
                ; // ignored for now
            } else if (xmlPropName.equals("description")) {
                ; // do nothing
            } else if (xmlPropName.equals("aliases")) {
            	parseConfig(xmlProp, smClass);
            } else if (xmlPropName.equals("relations")) {
				//loadRelations(xmlProp, smClass);
			} 
            else if (xmlPropName.equals("category")) {
            	NamedNodeMap nnm = xmlProp.getAttributes();
            	String ref = nnm.getNamedItem("ref")== null?null:nnm.getNamedItem("ref").getNodeValue();
            	if(ref!=null && ref!="") {
            		smClass.addCategorysLevel1(ref);
            	}
            } else if (xmlPropName.equals("field")) {
            	// 处理指标领域的逻辑
            	NamedNodeMap nnm = xmlProp.getAttributes();
            	String label = nnm.getNamedItem("label")== null ? null : nnm.getNamedItem("label").getNodeValue();
            	if(label != null) {
            		smClass.addFieldsLevel1(label);
            		smClass.addDomain(label);
            	}
            } else if (xmlPropName.equals("prop") ) {
                NamedNodeMap nnm = xmlProp.getAttributes();
                if (nnm.getLength() == 0 
                		|| nnm.getNamedItem("label") == null 
                		|| nnm.getNamedItem("type") == null
                		|| nnm.getNamedItem("unit") == null) {
                	continue;
                }
                String propLabel = nnm.getNamedItem("label").getNodeValue().toLowerCase();
                String type = nnm.getNamedItem("type").getNodeValue().toLowerCase();
                String unit = nnm.getNamedItem("unit").getNodeValue().toLowerCase(); 
                String subTypes = nnm.getNamedItem("subType") == null ? "" : nnm.getNamedItem("subType").getNodeValue().toLowerCase();
                // 暂时还没有配置
                String max = nnm.getNamedItem("max") == null ? null : nnm.getNamedItem("max").getNodeValue();
                String min = nnm.getNamedItem("min")== null ? null : nnm.getNamedItem("min").getNodeValue();
                String defaultVal = nnm.getNamedItem("default")==null ? "false" : "true";
                if (propLabel.length() == 0) {
                	logger_.error(Param.ALL_ONTO_FILE_OLD_SYSTEM + "Class %s 有prop的label为空" + smClass.getText() );
                	continue;
                } else if(propLabels.contains(propLabel)){
                	logger_.error(Param.ALL_ONTO_FILE_OLD_SYSTEM + "Class %s 的prop %s 有重复" + smClass.getText() , propLabel);
                	continue;
                } else {
                    propLabels.add(propLabel);
                }
                
                PropNode pn = new PropNode(propLabel);
                pn.setValueType(parseValueType_(type));
                
                //pn.setMax(max==null ? 0.0 : Double.parseDouble(max));
                //pn.setMin(min==null ? 0.0 : Double.parseDouble(min));
                String lable = propLabel + subTypes;
                if (!memOnto.containsKey(lable)) {
                    memOnto.put(lable, pn.toFacade());
                } 
                //无意义  change by wyh 2014.11.11
                /*else if (!checkPropInfo_(memOnto, smClass, pn)) {
                    continue;
                }*/
                
                parseValueUnit_(unit, pn, smClass);
                pn.addOfWhat(smClass);
                smClass.addPropLevel1(pn);
                if (type.equals("str") && subTypes != null) {
                	for (String subType : subTypes.split("\\|")){
                		putMapValue(subTypeToProp,subType, pn);
                        pn.subType.add(subType);
                	}
                }
            } else {
            	logger_.error(Param.ALL_ONTO_FILE_OLD_SYSTEM + "Class %s 下有未识别的子节点 %s" + smClass.getText()  + xmlPropName);
            }
        }
    }
    
    private static void putMapValue(MultiHashtable result, String subType, PropNode pn) {		
		if (result.get(subType) == null) {
			result.put(subType, pn);
			return;
		}		
		//包含该text的节点不添加
		Vector<BaseOntoNode> vector = ((Vector<BaseOntoNode>) result.get(subType));
		for (BaseOntoNode sn : vector) {
			if (!sn.getText() .equals(pn.getText() )) continue;
			else return;
		}
		result.put(subType, pn);
	}
    
    /**
	 * @author: 	    吴永行 
	 * @dateTime:	  2013-11-12 下午8:09:44
	 */
	/*private static void loadRelations(Node xmlProp, ClassNode smClass) {
		NodeList nl = xmlProp.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++)
		{
			Node n = nl.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE) { continue; }
			NamedNodeMap nnm = n.getAttributes();
			RelationShip rs = new RelationShip();
			String combineType = nnm.getNamedItem("combineType").getNodeValue();
			String isCombined = nnm.getNamedItem("needCombined").getNodeValue();
			String conditionName = nnm.getNamedItem("conditionName").getNodeValue();
			String condition = conditions.get(conditionName);
			String[] coditionList = null;
			if (condition != null) coditionList = condition.split("\\|");
			rs.setTargetIndex(nnm.getNamedItem("targetIndex").getNodeValue());
			
			if (isCombined == null || isCombined.equals("")) {
				rs.setIsCombined(true);
			}else if (isCombined != null && isCombined.toUpperCase().equals("TRUE")) {
				rs.setIsCombined(true);
			}else if (isCombined != null && isCombined.toUpperCase().equals("FALSE")) {
				rs.setIsCombined(false);
			}else {
				logger_.error("指标配置文件,relationShip子节点中 isCombined只能为TRUE|FALSE 或者不填, 不能为 "
						+ isCombined);
				continue;
			}
			
			if (combineType == null || combineType.equals("")) {//不填 为BOTH
				rs.setCombineType(EnumDef.CombineType.BOTH);			
			}else if (combineType.toUpperCase().equals("PRE") || combineType.toUpperCase().equals("POST") || combineType.toUpperCase().equals("BOTH")) {
				rs.setCombineType(EnumDef.CombineType.valueOf(combineType));			
			} else {
				//配置文集错误,记到日志文件中
				logger_.error("指标配置文件中,relationShip子节点中 combineType只能为PRE|POST|BOTH或者不填, 不能为 "
						+ nnm.getNamedItem("combineType").getNodeValue());
				continue;	
			}
			if (coditionList ==null) {
				logger_.error("指标配置文件中,relationShip子节点中 conditionName:\""+conditionName+"\" 不存在 ");
				continue;
			}
			for (String c : coditionList) {
				RelationShip rs2 = rs.clone();
				rs2.setCondition(c);
				//smClass.relations.put(c, rs2);	
			}
				
		}
	}*/
    
    // 增加2通过memOnto获得memOntoAlias
	private static void parserAlias(MultiValueMap memOnto) {
    	MultiValueMap memOntoTemp = new MultiValueMap();
    	Collection c = memOnto.values();
		Iterator iterator = c.iterator();
		while(iterator.hasNext()){  
			Object sn = iterator.next(); 
    		if (!(sn instanceof ClassNode)) {
    			continue;
    		}
    		ClassNode cNode = (ClassNode) sn;
    		List<String> alias = cNode.getAlias();
    		if (alias == null) {
    			continue;
    		}
    		for (int i = 0; i < alias.size(); i ++) {
    			String str = alias.get(i);
    			if ((memOnto.getCollection(str) == null || !memOnto.getCollection(str).contains(cNode))
    					&& (memOntoTemp.getCollection(str) == null || !memOntoTemp.getCollection(str).contains(cNode)))
    				memOntoTemp.put(str, cNode);
    		}
    	}
		memOnto.putAll(memOntoTemp);
    }
    
    /*public static boolean checkPropInfo_(MultiValueMap memOnto,
            ClassNode smClass, PropNode pn) throws DataConfException {
		Collection c = memOnto.getCollection(pn.getText() +pn.subType);
		Iterator iterator = c.iterator();
		while(iterator.hasNext()) {  
			SemanticNode snc = (SemanticNode) iterator.next();
			if (snc.getType() != NodeType.PROP) {
		        PropNode existing = (PropNode) memOnto.get(pn.getText() +pn.subType);
		        if (smClass.getDataSrc() == DataSource.ONTO) {
		            if (!pn.uri.equals(existing.uri)) {
		            	logger_.error(Param.ALL_ONTO_FILE_OLD_SYSTEM + "Prop %s 有不同的uri" + pn.getText() );
		            	//return false;
		            }
		        }
		        if (pn.getValueType() != existing.getValueType()) {
		        	logger_.error(Param.ALL_ONTO_FILE_OLD_SYSTEM + "Prop %s 有不同的type: %s & %s" + pn.getText() , pn.getValueType(), existing.getValueType());
		        	//return false;
		        }
			}
		}
		return true;
    }*/
    
    public static void parseValueUnit_(String unit, PropNode pn, ClassNode ofWhat) {
        DBMeta dbm = new DBMeta();
        dbm.unit2ColName_ = new HashMap<Unit, String>();
        dbm.unit2ColName_.put(Unit.UNKNOWN, "colname");
        //make sure UNKNOWN is first unit
        if(pn.getUnitIndex(Unit.UNKNOWN) == -1){
            pn.addValueUnit(Unit.UNKNOWN);
        }   
        String[] unitStrs = unit.split("\\|");
        for(int i=0;i<unitStrs.length;i++){
            String unitStr = unitStrs[i];
            Unit pu = EnumConvert.getUnitFromStr(unitStr);
            if(pn.getUnitIndex(pu) == -1){
                pn.addValueUnit(pu);
            }
            if(pu != Unit.UNKNOWN){
                dbm.unit2ColName_.put(pu,"colname");
            }
        }
        pn.addDbMeta(ofWhat, dbm);
    }

    public static PropType parseValueType_(String typeStr) throws DataConfException {
        if (typeStr.equals("DOUBLE") || typeStr.equals("DOUBLE".toLowerCase())) {
            return PropType.DOUBLE;
        }else if (typeStr.equals("LONG") || typeStr.equals("LONG".toLowerCase())) {
            return PropType.LONG;
        }else if (typeStr.equals("GEO") || typeStr.equals("GEO".toLowerCase())) {
            return PropType.GEO;
        } else if (typeStr.equals("STR") || typeStr.equals("STR".toLowerCase())) {
            return PropType.STR;
        } else if (typeStr.equals("DATE") || typeStr.equals("DATE".toLowerCase())) {
            return PropType.DATE;
        } else if (typeStr.equals("BOOL") || typeStr.equals("BOOL".toLowerCase())) {
            return PropType.BOOL;
        } else if (typeStr.equals("INST") || typeStr.equals("INST".toLowerCase())) {
            return PropType.INST;
        } else if (typeStr.equals("TECH_PERIOD") || typeStr.equals("TECH_PERIOD".toLowerCase())){
            return PropType.TECH_PERIOD;
        } else if(typeStr.equals("UNKNOWN") || typeStr.equals("UNKNOWN".toLowerCase())){
        	return PropType.UNKNOWN;
        } else if(typeStr.equals("INDEX") || typeStr.equals("INDEX".toLowerCase())){
        	return PropType.INDEX;
        } else {
        	logger_.error(Param.ALL_ONTO_FILE_OLD_SYSTEM+"Unknow value type %s", typeStr);
        	return null;
        }
    }
    
    
    static void parseConfig(Node configNode, ClassNode cn) {
    	NodeList children = configNode.getChildNodes();
    	for (int i = 0; i < children.getLength(); i ++) {
    		Node node = children.item(i);
    		if (!node.getNodeName().equals("alias")) {
    			continue;
    		}
    		if (node.getAttributes() == null) {
    			continue;
    		}
    		Attr attrTitle = (Attr)node.getAttributes().getNamedItem("title");
    		if (attrTitle == null || attrTitle.getValue().length() == 0) {
    			continue;
    		}
    		String alias = attrTitle.getValue();
    		Aliases a = new Aliases();
			a.setLabel(alias);
    		
    		NodeList subAlias = node.getChildNodes();
    		for (int j = 0; j < subAlias.getLength(); j ++) {
    			if (subAlias.item(j).getNodeName().equals("ResolvingConflicts")) {
    				List<ResolveAliasesConflicts> resolveAliasConflicts = new ArrayList<ResolveAliasesConflicts>();
    				Node subNode = subAlias.item(j);
    				NodeList children1 = subNode.getChildNodes();
    				for (int n = 0; n < children1.getLength(); n++) {
    					Node node1 = children1.item(n);
    					if (!node1.getNodeName().equals("word")) {
    						continue;
    					}
    					String str = node1.getFirstChild().getNodeValue();
    					ResolveAliasesConflicts r = new ResolveAliasesConflicts();
    					r.setWord(str);
    					resolveAliasConflicts.add(r);
    				}
    				ambiguityProcessor_parseCondConfig(a, cn, resolveAliasConflicts);
    			}
    		}
		    cn.setAlias(alias);
    	}
    }
    
    private static void ambiguityProcessor_parseCondConfig(Aliases alias, ClassNode cn,List<?> list) {
		ambiguityProcessor_word_parseConfig(alias, cn,list);
		ambiguityProcessor_defaultValue_parseConfig(alias, cn,list);
    }
	
	private static void ambiguityProcessor_word_parseConfig(Aliases alias, ClassNode cn,List<?> list){
		AmbiguityCondInfoWords condInfo = new AmbiguityCondInfoWords();
		if(list == null) return;
		for (Object rac : list) {
			condInfo.words_.add(((ResolveAliasesConflicts)rac).getWord());
		}
		ambiguityProcessor_addCondInfo(cn, alias.getLabel(), "words", condInfo);
		return;
	}
	
	
	
	private static void ambiguityProcessor_defaultValue_parseConfig(Aliases alias, ClassNode cn,List<?> list){
		AmbiguityCondInfoDefaultVal acid = new AmbiguityCondInfoDefaultVal();
		acid.defaultVal = alias.getIsDefault();
					
		ambiguityProcessor_addCondInfo(cn, alias.getLabel(), "defaultval", acid);
		return;
	}
	
	private static void ambiguityProcessor_addCondInfo(ClassNode cn, String alias, String type, AmbiguityCondInfoAbstract condInfo)
    {
        AmbiguityCondInfoSet infoSet = cn.getAmbiguityCondInfoSet();
        if(infoSet == null)
            return;
        infoSet.addCondInfo(alias, type, condInfo);
    }
}
