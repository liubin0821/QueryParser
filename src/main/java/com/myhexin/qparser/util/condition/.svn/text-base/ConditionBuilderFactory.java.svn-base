package com.myhexin.qparser.util.condition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.util.Consts;

class ConditionBuilderFactory {

	private static Map<String, ConditionBuilderAbstract> builderMap = new HashMap<String, ConditionBuilderAbstract>();

	//不要每次都创建zhubiCondBuilder
	private final static ConditionBuilderZhubi zhubiCondBuilder  = new ConditionBuilderZhubi();
	
	static {
		builderMap.put("INDEX", new ConditionBuilderIndex());
		builderMap.put("CONTAIN", new ConditionBuilderContain());
		builderMap.put("STR_INSTANCE", new ConditionBuilderStringInstance());
		builderMap.put("FREE_VAR", new ConditionBuilderFreeVar());
		builderMap.put("TECH", new ConditionBuilderTechOpAsIndex());
		builderMap.put("SORT", new ConditionBuilderSort());
		builderMap.put("ARIT", new ConditionBuilderArithmetic());
		builderMap.put("COMP", new ConditionBuilderComparator());
		builderMap.put("KEY_VALUE", new ConditionBuilderKeyValue());
		builderMap.put("RECORD", new ConditionBuilderRecordExtremum());
		//builderMap.put("ZHUBI", zhubiCondBuilder);
		builderMap.put(Consts.CONST_absXinxiDomain, new ConditionBuilderFreeVar());
	}

	private final static String CONST_ZHUBI = "逐笔";
	private final static String CONST_ZHUBI2 = "一笔";
	protected static ConditionBuilderAbstract getConditionBuilder(String patternId, List<SemanticNode> nodes, BoundaryInfos boundaryInfos) {
		ConditionBuilderAbstract builder = builderMap.get(patternId);
		if (builder == null) {
			builder = builderMap.get("INDEX");
		}
		
		//逐笔的一定是有显式句式语义的, 否则就用FREE_VAR_CondBuilder处理
		if(!("FREE_VAR".equals(patternId) || "STR_INSTANCE".equals(patternId) || "KEY_VALUE".equals(patternId))) {
			//特殊处理, 逐笔 类指标
			int start = boundaryInfos.bStart;
			int end = boundaryInfos.bEnd;
			
			int i=0;
			int stop = nodes.size();
			if(start>0) i = start;
			if(end>0 && end>start && end <=stop) stop=end;
			
			for (;i<stop;i++ ) {
				SemanticNode node = nodes.get(i);
				if (node.getType() == NodeType.FOCUS) {
					String text = node.getText();
					if (text.contains(CONST_ZHUBI) || text.contains(CONST_ZHUBI2)) {
						return zhubiCondBuilder;
					}
				}
			}
		}
		return builder;
	}
}
