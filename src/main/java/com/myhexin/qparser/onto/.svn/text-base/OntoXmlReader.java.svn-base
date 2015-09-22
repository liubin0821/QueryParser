package com.myhexin.qparser.onto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.xalan.xsltc.compiler.util.MultiHashtable;
import org.w3c.dom.Document;

import com.myhexin.DB.mybatis.MybatisHelp;
import com.myhexin.DB.mybatis.mode.Aliases;
import com.myhexin.DB.mybatis.mode.DataRelationV;
import com.myhexin.DB.mybatis.mode.IndexFieldV;
import com.myhexin.DB.mybatis.mode.IndexPropV2;
import com.myhexin.DB.mybatis.mode.Indexs;
import com.myhexin.DB.mybatis.mode.NormalRelation;
import com.myhexin.DB.mybatis.mode.ObjectRelationV;
import com.myhexin.DB.mybatis.mode.ResolveAliasesConflicts;
import com.myhexin.DB.mybatis.mode.SuperIndexV;
import com.myhexin.qparser.Param;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.ambiguty.AmbiguityCondInfoAbstract;
import com.myhexin.qparser.ambiguty.AmbiguityCondInfoDefaultVal;
import com.myhexin.qparser.ambiguty.AmbiguityCondInfoSet;
import com.myhexin.qparser.ambiguty.AmbiguityCondInfoWords;
import com.myhexin.qparser.ambiguty.AmbiguityProcessor;
import com.myhexin.qparser.define.EnumConvert;
import com.myhexin.qparser.define.EnumDef.IndexType;
import com.myhexin.qparser.define.EnumDef.OntoPropType;
import com.myhexin.qparser.define.EnumDef.PropType;
import com.myhexin.qparser.define.EnumDef.ReportType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.ExceptionUtil;
import com.myhexin.qparser.node.DBMeta;
import com.myhexin.qparser.node.SemanticNode;

public class OntoXmlReader {
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(OntoXmlReader.class.getName());

	private static MultiHashtable subTypeToProp = new MultiHashtable();//以后需要删除,直接从数据库查询
	private static MultiHashtable subTypeToIndex = new MultiHashtable();//值属性

	private static MybatisHelp mybatisHelp = ApplicationContextHelper.getBean("mybatisHelp");
	
	public static boolean subTypeToIndexContainKey(String name) {
		return subTypeToIndex.containsKey(name);
	}
	
	public static List<ClassNodeFacade> subTypeToIndexGet(String name) {
		Vector<ClassNodeFacade> nodes = (Vector<ClassNodeFacade>)subTypeToIndex.get(name);
		if(nodes!=null) {
			for(ClassNodeFacade node : nodes) {
				node.clearPropValues();
			}
		}
		
		return nodes;
	}
	
	public static List<PropNodeFacade> subTypeToPropGet(String name) {
		Vector<PropNodeFacade> nodes = (Vector<PropNodeFacade>)subTypeToProp.get(name);
		if(nodes!=null) {
			for(PropNodeFacade node : nodes) {
				node.setValue(null);
			}
		}
		return nodes;
	}
	
	// 增加1修改返回值类型，返回包括not_alias和alias
	public static MultiValueMap loadOnto(Document doc, Document docCondition)
	        throws DataConfException {
		MultiValueMap rtn = new MultiValueMap();
		subTypeToProp = new MultiHashtable();
		subTypeToIndex = new MultiHashtable();//值属性
		
		//long start = System.currentTimeMillis();
		
		Map<Integer, ArrayList<Aliases>> aliases = new HashMap<Integer, ArrayList<Aliases>>();
		Map<Integer, ArrayList<ResolveAliasesConflicts>> resolveAliases = new HashMap<Integer, ArrayList<ResolveAliasesConflicts>>();
		Map<Integer, ArrayList<IndexFieldV>> field = new HashMap<Integer, ArrayList<IndexFieldV>>();
		Map<Integer, ArrayList<SuperIndexV>> superIndex = new HashMap<Integer, ArrayList<SuperIndexV>>();
		Map<Integer, ArrayList<IndexPropV2>> indexPropV2 = new HashMap<Integer, ArrayList<IndexPropV2>>();
		Map<Integer, ArrayList<DataRelationV>> dataRelationV = new HashMap<Integer, ArrayList<DataRelationV>>();
		Map<Integer, ArrayList<NormalRelation>> normalRelation = new HashMap<Integer, ArrayList<NormalRelation>>();
		Map<Integer, ArrayList<ObjectRelationV>> objectRelationV = new HashMap<Integer, ArrayList<ObjectRelationV>>();
		
		getAllIndexDbData(aliases,resolveAliases,field, superIndex, indexPropV2, dataRelationV, normalRelation,objectRelationV);
		for (Indexs index : mybatisHelp.getIndexsMapper().selectAll()) {
			String label = index.getLabel();
			if (label == null || label.length() == 0) {
				logger_.error(Param.ALL_ONTO_FILE + "有Class的label为空");
				continue;
			}

			ClassNode cn = new ClassNode(label);
			cn.setReportType(parseReportType(index.getReportType()));
			cn.setId(index.getId());

			if(label.equals("pe")) {
				logger_.info("[DEBUG]ADD CLASS NODE pe");
			}
			
			ClassNodeFacade cnf = cn.toFacade();
			rtn.put(label, cnf);
			try {
				loadClassInfo(rtn, cnf, index, aliases,resolveAliases,field, superIndex, indexPropV2, dataRelationV,normalRelation, objectRelationV);
			} catch (Exception e) {
				e.printStackTrace();
				logger_.error(ExceptionUtil.getStackTrace(e));
				continue;
			}
		}
		aliases=null; resolveAliases=null; field=null; superIndex=null;indexPropV2=null ;dataRelationV=null; normalRelation=null;objectRelationV=null;
		//long end = System.currentTimeMillis();
		//System.out.println(String.format("## %dms \n", end-start));

		getCategorysPropAddTOIndex(rtn);
		parserAlias(rtn);
		
		//添加superClass Prop
		Collection c = rtn.values();
		Iterator iterator = c.iterator();
		while (iterator.hasNext()) {
			Object sn =  iterator.next();
			if (sn instanceof ClassNodeFacade) {
				ClassNodeFacade cf = (ClassNodeFacade)sn;
				cf.core.getSuperClassInfo(cf.core,cf.core.superClass);
			}
		}
		
		return rtn;
	}

	private static void addSuperInfo(MultiValueMap rtn) {
		Collection c = rtn.values();
		Iterator iterator = c.iterator();
		while (iterator.hasNext()) {
			Object sn =  iterator.next();
			if (sn instanceof ClassNodeFacade) {
				ClassNodeFacade snf = (ClassNodeFacade)sn;
				snf.core.getSuperClassInfo(snf, snf.getSuperClass());
			}
		}
	}
	
	private static void getAllIndexDbData(
			final Map<Integer, ArrayList<Aliases>> aliases,
			final Map<Integer, ArrayList<ResolveAliasesConflicts>> resolveAliases, 
			final Map<Integer, ArrayList<IndexFieldV>> field,
			final Map<Integer, ArrayList<SuperIndexV>> superIndex,
			final Map<Integer, ArrayList<IndexPropV2>> indexPropV2,
			final Map<Integer, ArrayList<DataRelationV>> dataRelationV,
			final Map<Integer, ArrayList<NormalRelation>> normalRelation,
			final Map<Integer, ArrayList<ObjectRelationV>> objectRelationV) {
		for (Aliases a : mybatisHelp.getAliasesMapper().selectAll())
			putInMap(aliases, a.getIndexId(), a);
		for(ResolveAliasesConflicts rac : mybatisHelp.getResolveAliasesConflictsMapper().selectAll())
			putInMap(resolveAliases, rac.getAliaseId(), rac);
		for (IndexFieldV f : mybatisHelp.getIndexFieldVMapper().selectAll())
			putInMap(field, f.getIndexId(), f);
		for (SuperIndexV si : mybatisHelp.getSuperIndexVMapper().selectAll())
			putInMap(superIndex, si.getIndexId(), si);
		for (IndexPropV2 ip : mybatisHelp.getIndexPropV2Mapper().selectAll())
			putInMap(indexPropV2, ip.getIndexId(), ip);
		for (DataRelationV dr : mybatisHelp.getDataRelationVMapper().selectAll())
			putInMap(dataRelationV, dr.getPropId(), dr);
		for (NormalRelation nr : mybatisHelp.getNormalRelationMapper().selectAll())
			putInMap(normalRelation, nr.getPropId(), nr);
		for (ObjectRelationV or : mybatisHelp.getObjectRelationVMapper().selectAll())
			putInMap(objectRelationV, or.getPropId(), or);
	}

	private static final <T> void putInMap(Map<Integer, ArrayList<T>> map, Integer id, T t) {
		if (map.containsKey(id))
			map.get(id).add(t);
		else {
			ArrayList<T> list = new ArrayList<T>();
			list.add(t);
			map.put(id, list);
		}
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
		while (iterator.hasNext()) {
			Object sn =  iterator.next();
			if (sn instanceof ClassNodeFacade) {
				ClassNodeFacade cn = (ClassNodeFacade) sn;
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
	private static void getCategoryPropAddTOIndex(MultiValueMap rtn, ClassNodeFacade cn) {
		for (String category : cn.getCategorysLevel1()) {
			if (!rtn.containsKey(category)) {
				logger_.error("指标" + cn.getText()  + "的category" + category + "未定义");
				continue;
			} else if (category.equals(cn.getText() )) { // 排除category指向自身
				continue;
			}
			Collection c = rtn.getCollection(category);
			Iterator iterator = c.iterator();
			while (iterator.hasNext()) {
				Object snc = iterator.next();
				if (snc instanceof ClassNodeFacade) {
					ClassNodeFacade ccn = (ClassNodeFacade) snc;
					if (cn.getCategorysId1().contains(ccn.getId()))
						cn.core.addSuperClass(ccn.core);
				}
			}
		}
	}

	public static final ReportType parseReportType(String reportTypeStr) throws DataConfException {
		if(reportTypeStr==null || reportTypeStr.equals(""))
			return null;
		try {
			return ReportType.valueOf(reportTypeStr.trim().toUpperCase());
		} catch (Exception e) {
				logger_.error("stock_onto.xml: report_type-" + reportTypeStr);
		}
		return null;
		
		/*ReportType reportType = null;
		boolean isYear = reportTypeStr.equals("YEAR".toLowerCase()) || reportTypeStr.equals("YEAR");
		boolean isHalfYear = reportTypeStr.equals("HALF_YEAR".toLowerCase())
		        || reportTypeStr.equals("HALF_YEAR");
		boolean isQuarter = reportTypeStr.equals("QUARTER".toLowerCase())
		        || reportTypeStr.equals("QUARTER");
		boolean isMonth = reportTypeStr.equals("MONTH".toLowerCase())
		        || reportTypeStr.equals("MONTH");
		boolean isDaily = reportTypeStr.equals("DAILY".toLowerCase())
		        || reportTypeStr.equals("DAILY");
		boolean isFutureQuarter = reportTypeStr.equals("FUTURE_QUARTER".toLowerCase())
		        || reportTypeStr.equals("FUTURE_QUARTER");
		boolean isFutureDaily = reportTypeStr.equals("FUTURE_DAILY".toLowerCase())
		        || reportTypeStr.equals("FUTURE_DAILY");
		boolean isTradeDaily = reportTypeStr.equals("TRADE_DAILY".toLowerCase())
		        || reportTypeStr.equals("TRADE_DAILY");
		boolean isNaturalDaily = reportTypeStr.equals("NATURAL_DAILY".toLowerCase())
		        || reportTypeStr.equals("NATURAL_DAILY");
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
		return reportType;*/
	}

	private static IndexType parseIndexType(String classType) throws DataConfException {
		if (classType == null) {
			return IndexType.UNKNOW;
		}

		IndexType indexType = null;
		try {
			indexType = IndexType.valueOf(classType);
		} catch (IllegalArgumentException illArg) {
			logger_.error(Param.ALL_ONTO_FILE + "Unknow IndexType “%s”" + classType);
		}

		return indexType;
	}

	private static final void loadClassInfo(MultiValueMap memOnto, ClassNodeFacade cnf, Indexs index,
			final Map<Integer, ArrayList<Aliases>> aliases ,
			final Map<Integer, ArrayList<ResolveAliasesConflicts>> resolveAliases, 
			final Map<Integer, ArrayList<IndexFieldV>> field2, 
			final Map<Integer, ArrayList<SuperIndexV>> superIndex,
			final Map<Integer, ArrayList<IndexPropV2>> indexPropV2,
			final Map<Integer, ArrayList<DataRelationV>> dataRelationV,
			final Map<Integer, ArrayList<NormalRelation>> normalRelation,
			final Map<Integer, ArrayList<ObjectRelationV>> objectRelationV) throws DataConfException {
		// 自己是自己的一个类别，为匹配时使用
		ClassNode cn = cnf.core;
		cn.addCategorysLevel1(cn.getText() );

		//aliases
		AmbiguityProcessor ambiguityProcessor = new AmbiguityProcessor();
		ambiguityProcessor.parseConfigNew(index, cnf,aliases.get(index.getId()),resolveAliases);
		//ambiguityProcessor_parseConfigNew(index, cn,aliases.get(index.getId()),resolveAliases);
		
		
		//field
		List<IndexFieldV> fieldList = field2.get(index.getId());
		if (fieldList != null)
			for (IndexFieldV field : fieldList) {
				String fieldStr = field.getFileds();
				cn.addFieldsLevel1(fieldStr);
				cn.addDomain(fieldStr);
			}

		//category
		List<SuperIndexV> sivList = superIndex.get(index.getId());
		if (sivList != null)
			for (SuperIndexV siv : sivList) {
				String superLable = siv.getSuperLable();
				if (superLable == null || superLable == "")
					continue;
				cn.addCategorysLevel1(superLable);
				cn.addCategorysId1(siv.getSuperId());
			}

		//prop
		List<IndexPropV2> indexPropList = indexPropV2.get(index.getId());
		if(indexPropList!=null)
		for (IndexPropV2 ip2 : indexPropList) {
			String propLabel = ip2.getLabel();
			PropNode pn = new PropNode(propLabel);
			if(ip2.getIsValueProp()!=null)
				pn.setValueProp(ip2.getIsValueProp());
			else{
				pn.setValueProp(false);
			}
			
			switch (OntoPropType.valueOf(ip2.getType())) {
			case DP:
				pn.setValueType(parseValueType_("STR"));
				List<DataRelationV> list1 = dataRelationV.get(ip2.getId());
				if(list1!=null)
				for (DataRelationV drv : list1) {
					pn.subType.add(drv.getLabel());
					putMapValue(subTypeToProp, drv.getLabel(), pn.toFacade());
					if (ip2.getIsValueProp())
						subTypeToIndex.put(drv.getLabel(), cn.toFacade());
				}
				break;
			case NP:
				List<NormalRelation> list2 = normalRelation.get(ip2.getId());
				if (list2 == null || list2.size() < 1)
					continue;
				NormalRelation nr = list2.get(0);
				parseValueUnit_(nr.getUnit(), pn, cn);
				pn.setValueType(parseValueType_(nr.getType()));
				//pn.subType.add(ip2.getLabel());
				break;
			case IP:
				pn.setValueType(parseValueType_("INDEX"));
				List<ObjectRelationV> list3 = objectRelationV.get(ip2.getId());
				if(list3!=null)
				for (ObjectRelationV orv : list3)
					pn.subType.add(orv.getLabel());
				break;
			}
			pn.addOfWhat(cn);
			cn.addPropLevel1(pn);
			
			//临时处理 为了兼容   MemOnto.getOntoC 获取属性 ,以后改为直接从数据库读取
			memOnto.put(pn.getText() , pn.toFacade());
			/*String subTypes = sb.length()>0 ? sb.substring(1).toString():"";
			pn.subType = subTypes;

			String lable = propLabel + subTypes;
			if (!memOnto.containsKey(lable)) {
				memOnto.put(lable, pn);
			} else if (!checkPropInfo_(memOnto, cn, pn)) {
				continue;
			}*/
		}
		cn.addAllProps(cn.propsLevel1);
	}

	private static void putMapValue(MultiHashtable result, String subType, PropNodeFacade pn) {
		if (result.get(subType) == null) {
			result.put(subType, pn);
			return;
		}
		//包含该text的节点不添加
		Vector<SemanticNode> vector = ((Vector<SemanticNode>) result.get(subType));
		for (SemanticNode sn : vector) {
			if (!sn.getText() .equals(pn.getText() ))
				continue;
			else
				return;
		}
		result.put(subType, pn);
	}

	// 增加2通过memOnto获得memOntoAlias
	private static void parserAlias(MultiValueMap memOnto) {
		MultiValueMap memOntoTemp = new MultiValueMap();
		Iterator iterator = memOnto.values().iterator();
		while (iterator.hasNext()) {
			Object sn = iterator.next();
			if(!(sn instanceof ClassNodeFacade) ) {
				continue;
			}
			ClassNodeFacade cNode = (ClassNodeFacade) sn;
			List<String> alias = cNode.getAlias();
			if (alias == null) {
				continue;
			}
			for (int i = 0; i < alias.size(); i++) {
				String str = alias.get(i);
				if ((memOnto.getCollection(str) == null || !memOnto.getCollection(str).contains( cNode))
				        && (memOntoTemp.getCollection(str) == null || !memOntoTemp.getCollection(str).contains(cNode)))
					memOntoTemp.put(str, cNode);
			}
		}
		memOnto.putAll(memOntoTemp);
	}

	public static void parseValueUnit_(String unit, PropNode pn, ClassNode ofWhat) {
		DBMeta dbm = new DBMeta();
		dbm.unit2ColName_ = new HashMap<Unit, String>();
		dbm.unit2ColName_.put(Unit.UNKNOWN, "colname");
		//make sure UNKNOWN is first unit
		if (pn.getUnitIndex(Unit.UNKNOWN) == -1) {
			pn.addValueUnit(Unit.UNKNOWN);
		}
		String[] unitStrs = unit.split("\\|");
		for (int i = 0; i < unitStrs.length; i++) {
			String unitStr = unitStrs[i];
			Unit pu = EnumConvert.getUnitFromStr(unitStr);
			if (pn.getUnitIndex(pu) == -1) {
				pn.addValueUnit(pu);
			}
			if (pu != Unit.UNKNOWN) {
				dbm.unit2ColName_.put(pu, "colname");
			}
		}
		pn.addDbMeta(ofWhat, dbm);
	}
	
	public static void parseValueUnit_(String unit, PropNodeFacade pn, ClassNodeFacade ofWhat) {
		parseValueUnit_(unit, pn.core, ofWhat.core);
	}

	public static final PropType parseValueType_(String typeStr) {
		
		try {
			return PropType.valueOf(typeStr.trim().toUpperCase());
		} catch (Exception e) {
			logger_.error(Param.ALL_ONTO_FILE + "Unknow value type %s", typeStr);
		}
		return null;
			
		/*if (typeStr.equals("DOUBLE") || typeStr.equals("DOUBLE".toLowerCase())) {
			return PropType.DOUBLE;
		} else if (typeStr.equals("LONG") || typeStr.equals("LONG".toLowerCase())) {
			return PropType.LONG;
		} else if (typeStr.equals("GEO") || typeStr.equals("GEO".toLowerCase())) {
			return PropType.GEO;
		} else if (typeStr.equals("STR") || typeStr.equals("STR".toLowerCase())) {
			return PropType.STR;
		} else if (typeStr.equals("DATE") || typeStr.equals("DATE".toLowerCase())) {
			return PropType.DATE;
		} else if (typeStr.equals("BOOL") || typeStr.equals("BOOL".toLowerCase())) {
			return PropType.BOOL;
		} else if (typeStr.equals("INST") || typeStr.equals("INST".toLowerCase())) {
			return PropType.INST;
		} else if (typeStr.equals("TECH_PERIOD") || typeStr.equals("TECH_PERIOD".toLowerCase())) {
			return PropType.TECH_PERIOD;
		} else if (typeStr.equals("UNKNOWN") || typeStr.equals("UNKNOWN".toLowerCase())) {
			return PropType.UNKNOWN;
		} else if (typeStr.equals("INDEX") || typeStr.equals("INDEX".toLowerCase())) {
			return PropType.INDEX;
		} else {
			logger_.error(Param.ALL_ONTO_FILE + "Unknow value type %s", typeStr);
			return null;
		}*/
	}
	

	/*static void ambiguityProcessor_parseConfigNew(Indexs index,ClassNode cn,List<Aliases> aliasesList, Map<Integer, ArrayList<ResolveAliasesConflicts>> resolveAliases){		
		if(aliasesList == null)
			return;
		
		for(Aliases alias : aliasesList){
			ambiguityProcessor_parseCondConfig(alias,cn, resolveAliases.get(alias.getId()));
			cn.setAlias(alias.getLabel());
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
    }*/

}
