/**
 * Copyright©Zhejiang Hexin Flush Network Services Ltd. All rights reserved.
 * Zhejiang Hexin Flush Network Services Ltd.
 * 
 * http://www.10jqka.com.cn/
 */
package com.myhexin.qparser.rule;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.define.MiscDef;
import com.myhexin.qparser.define.EnumDef.HiddenType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.OperatorNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.number.NumRange;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.util.Pair;


/**
 * 对指标进行一定的规则转换。其转换相对pattern更为灵活，主要是根据指标在一定的上下文环境（
 * 目前由一个词语代替，但是累计增长之类的需要“累计”和“增长”两个词语的语境）进行相应转换。
 * 例如：2011年营业收入增长30%，根据“营业总收入”在“增长”的转换条件下，变为“2011年营业总收入(同比增长率)>30%”
 */
public class IndexRuleProcessor {
	private static Map<String, Map<String, SemanticNode>> stockRule_ =
			new HashMap<String, Map<String, SemanticNode>>();
	private static Map<String, Map<String, SemanticNode>> fundRule_ = 
			new HashMap<String, Map<String, SemanticNode>>();
	private static Map<String, Pair<NumRange, ClassNodeFacade>> stockTransByValRule_ =
			new HashMap<String, Pair<NumRange, ClassNodeFacade>>();
	private static Map<String, Pair<NumRange, ClassNodeFacade>> fundTransByValRule_ =
			new HashMap<String, Pair<NumRange, ClassNodeFacade>>();
	
	/**
	 * @param query
	 */
	public IndexRuleProcessor(Query query){ 
		this.query = query;
	}
	
	/**
	 * process index calculation using ruler <br>
	 * 即根据问句中的指标，查找其转换的上下关键词，然后转换为相应的指标。由于对于增长类型rule转换，
	 * 相当于一类计算问题，只是该类计算问题可以有其他指标明确表示。例如：“净利润的增长” 与“毛利增长”就不一样
	 * 所以，对于该类指标的转换，也需要将增长、变化的隐含数值补全到后面去，如果已经有了则不需要补全。<br>
	 * 此外，转换后要将原指标和关键词删去。<br>
	 * 注意：对于形如，“2011年净利润和毛利都增长25%以上的小盘股”，在前面数值补全阶段{@link NumCompletor}已经将
	 * “增长25%以上”补全到两个指标上了，该阶段形成为“2011年净利润增长25%以上和毛利都增长25%以上的小盘股”，
	 * 因此可以放心将“净利润”的“增长”删除
	 * 
	 */
	public void process(){
		int leftLowIndex = 0;
		for(int index = 0; index < query.getNodes().size();){
			NodeType nodeType = query.getNodes().get(index).type;
			//本来只对class做变换的，现在增加unknown
			if(nodeType != NodeType.CLASS && nodeType != NodeType.UNKNOWN){
				index++;
				continue ;
			}
			
			int resultIndex = process(index, leftLowIndex);
			//rule替换失败
			if(resultIndex < 0){
				index++;//下一个位置
				if(nodeType == NodeType.CLASS){//leftLowIndex
					leftLowIndex = index;
				}
			}else{//rule替换成功
				index = resultIndex;
				leftLowIndex = resultIndex;
			}
		}
	}
	
	/**
	 * 根据指标的数值范围，获取改变后相应指标。如果该指标没有任何数值范围的配置信息，则返回为null。
	 * @param fromTitle 原始指标
	 * @param numRange 数值范围
	 * @param type 问句类型
	 * @return 更改后的指标，无则为null
	 */
	public static ClassNodeFacade transClassNodeByVal(String fromTitle, NumRange numRange, Query.Type type){
		Pair<NumRange, ClassNodeFacade> pair = null;
		if(type == Query.Type.STOCK){
			pair = stockTransByValRule_.get(fromTitle);
		}else if(type == Query.Type.FUND){
			pair = fundTransByValRule_.get(fromTitle);
		}
		if(pair == null) return null;
		if(pair.first.containsTermipointOf(numRange)) return pair.second;
		return null;
	}
	
	/**
	 * @param stockTransByValRule the stockTransByValRule_ to set
	 */
	public static void setStockTransByValRule(
			Map<String, Pair<NumRange, ClassNodeFacade>> stockTransByValRule) {
		IndexRuleProcessor.stockTransByValRule_ = stockTransByValRule;
	}
	
	/**
	 * @param fundTransByValRule the fundTransByValRule_ to set
	 */
	public static void setFundTransByValRule(
			Map<String, Pair<NumRange, ClassNodeFacade>> fundTransByValRule) {
		IndexRuleProcessor.fundTransByValRule_ = fundTransByValRule;
	}

	/**
	 * @return the stockRule
	 */
	public static Map<String, Map<String, SemanticNode>> getStockRule() {
		return stockRule_;
	}

	/**
	 * @param stockRule the stockRule to set
	 */
	public static void setStockRule(Map<String, Map<String, SemanticNode>> stockRule) {
		IndexRuleProcessor.stockRule_ = stockRule;
	}

	/**
	 * @return the fundRule_
	 */
	public static Map<String, Map<String, SemanticNode>> getFundRule() {
		return fundRule_;
	}

	/**
	 * @param fundRule the fundRule to set
	 */
	public static void setFundRule(Map<String, Map<String, SemanticNode>> fundRule) {
		IndexRuleProcessor.fundRule_ = fundRule;
	}
	
	/**
	 * 指标Rule转换单元，我们主要查找触发词（关键词位置）、计算结果等
	 */
	private static class RuleChangeUnit{
		private int changePos = -1;
		private int triggerPos = -1;
		private RuleType triggerType = null;
		/** 计算单元的最右边词语*/
		private int rightIndex = -1;
		private int numIndex = -1;
		private SemanticNode repNode = null;
		private boolean canNotAddNum = false;
	}
	private static enum RuleType{
		LEIJI,
		TONGBI,
		HUANBI,
		YUCE,
		FUHE,
		COMMON
	}
	/**
	 * 首先查找处理指标的转换关键词，用一个{@link RuleChangeUnit}来表示，根据查找到的处理单元
	 * 进行相应变化，再接着处理下一个指标处理指标。处理成功返回下一个查找位置，处理失败返回-1
	 * @param classIndex
	 * @param leftLowIndex
	 * @return
	 */
	private int process(int classIndex, int leftLowIndex) {
		Map<String, SemanticNode> rules = getIndexRuleMap(
				query.getNodes().get(classIndex).getText(), query.getType());
		//如果没有该指标的增长方式，则放回下一个指标位置
		if(rules == null){
			return -1;
		}
		RuleChangeUnit unit = findIndexRuleUnit(rules, classIndex, leftLowIndex);
		if(unit == null || unit.repNode == null){
			return -1;
		}
		
		//下一个位置就是该计算单元最有变位置+1
		int resultIndex = unit.rightIndex + 1;
		
		query.getNodes().set(classIndex, unit.repNode);
		int triggerPos = unit.triggerPos;
		int changePos = unit.changePos;
		
		if(triggerPos == changePos){
			triggerPos = -1;
		}else if(triggerPos > changePos){
			triggerPos = unit.changePos;
			changePos = unit.triggerPos;
		}
		
		//删除改变词语（改变词语默认在后面）
		if(changePos >= 0){
			NodeType tmpType = query.getNodes().get(changePos).type;
			if(tmpType==NodeType.OPERATOR && unit.numIndex < 0 && !unit.canNotAddNum){
				NumNode numNode = ((OperatorNode)(query.getNodes().get(changePos))).standard;
				numNode.hiddenType = HiddenType.HIDDEN;
				query.getNodes().set(changePos, numNode);
			}else{
				query.getNodes().remove(changePos);
				resultIndex--;
			}
		}
		//删除增长方式词语（增长方式词语默认在前面）
		if(triggerPos >= 0){
			NodeType tmpType = query.getNodes().get(triggerPos).type;
			if(tmpType==NodeType.OPERATOR && unit.numIndex < 0  && !unit.canNotAddNum){
				NumNode numNode = ((OperatorNode)(query.getNodes().get(triggerPos))).standard;
				numNode.hiddenType = HiddenType.HIDDEN;
				query.getNodes().set(triggerPos, numNode);
			}else{
				query.getNodes().remove(triggerPos);
				resultIndex--;
			}
		}
		return resultIndex;
	}
	
	/**
	 * @param text
	 * @param type
	 * @return rule map
	 */
	public static Map<String, SemanticNode> getIndexRuleMap(String text, Type type) {
		if(type == Type.STOCK){
			if(stockRule_ == null){
				return null;
			}
			return stockRule_.get(text);
		}else if(type==Type.FUND){
			if(fundRule_ == null){
				return null;
			}
			return fundRule_.get(text);
		}
		return null;
	}
	
    
    public static ClassNodeFacade getLatestIndexFor(String ifindIndexName, Type type) {
        Map<String, SemanticNode>indexRuleMap = getIndexRuleMap(ifindIndexName, type);
        if(indexRuleMap == null){
            return null;
        }
        return (ClassNodeFacade)indexRuleMap.get(MiscDef.RULE_LATEST);
    }

	/**
	 * 查找指标增长方式的计算单元，包括指标、改变词、增长方方式词、计算结果（特别是对于自增长的指标）。<br>
	 * 查找过程可以看成是一个特殊的计算问题（除了少数非计算问题，例如：XX曾经担任XX股票的董事长），
	 * 大多数情况都是形如：净利润去年增长20%。因此，主要按照计算问题去查找变化单元
	 * 
	 * @param rules
	 * @param classIndex
	 * @param lowIndex
	 * @return
	 */
	private RuleChangeUnit findIndexRuleUnit(Map<String, SemanticNode> rules, 
			int classIndex, int lowIndex){
		RuleChangeUnit unit = new RuleChangeUnit();
		unit.rightIndex = classIndex;
		int passNumCount = 0;
		int beforeOperCount = 0;
		int afterOperCount = 0;
		
		for(int index = lowIndex; index < query.getNodes().size(); index++){
			if(index == classIndex){
				continue ;
			}
			SemanticNode tmpNode = query.getNodes().get(index);
			String text = query.getNodes().get(index).getText();
			NodeType tmpType = query.getNodes().get(index).type;
			//只允许该类型的词语通过
			if(tmpType != NodeType.CHANGE  &&  tmpType != NodeType.UNKNOWN 
					&& tmpType != NodeType.OPERATOR  && tmpType != NodeType.DATE
					&& tmpType != NodeType.NUM && tmpType != NodeType.STR_VAL
					&& tmpType != NodeType.SORT){
				if(index <= classIndex){
					continue ;
				}
				return unit;
			}/*else if(tmpType == NodeType.DATE){
				passDateCount++;
				if(passDateCount >= 3){
					return unit;
				}
				unit.rightIndex = index;
				continue ;
			//指标排序或NUM可以作为计算结果
			}*/else if(tmpType == NodeType.NUM || tmpType == NodeType.SORT){
				passNumCount++;
				if(passNumCount >= 2){
					return unit;
				}
				unit.numIndex = index;
				unit.rightIndex = index;
				continue ;
			}else if(tmpType == NodeType.OPERATOR){
				if(index > classIndex){
					afterOperCount++;
				}else{
					beforeOperCount++;
					unit.canNotAddNum = true;
					//class前面最多容许一个oper节点，并且只容许后面的oper进行rule转换
					if(beforeOperCount >= 2){
						return unit;
					}else{
						continue ;
					}
				}
				//遇到两个operNode
				if(afterOperCount >= 2){
					unit.canNotAddNum = true;//因为其可能作为一个计算节点
					return unit;
				}
			}
			
			if(PTN_CALC_CONN_TONGBI.matcher(text).matches()){
				unit.triggerPos = index;
				unit.triggerType = RuleType.TONGBI;
				unit.repNode = rules.get(STR_RULE_TONGBI);
        		if(unit.repNode == null) unit.repNode = rules.get(STR_RULE_ZHANG);
        	}else if(PTN_CALC_CONN_FUHE.matcher(text).matches()){
        		
        		unit.triggerPos = index;
        		unit.triggerType = RuleType.FUHE;
				unit.repNode = rules.get(STR_RULE_FUHE);
				if(unit.repNode == null) unit.repNode = rules.get(STR_RULE_ZHANG);
        	}else if(PTN_CALC_CONN_LEIJI.matcher(text).matches()){
        		unit.triggerPos = index;
        		unit.triggerType = RuleType.LEIJI;
        		//累计+增长 才变成变成累计增长
				if(tmpType == NodeType.OPERATOR && ((OperatorNode)tmpNode).isFromChange
						||unit.changePos != -1){
					unit.repNode = rules.get(STR_RULE_LEIJI);
	        		if(unit.repNode == null) unit.repNode = rules.get(STR_RULE_ZHANG);
        		}
        	}else if(PTN_CALC_CONN_HUANBI.matcher(text).matches()){
        		unit.triggerPos = index;
        		unit.triggerType = RuleType.HUANBI;
				unit.repNode = rules.get(STR_RULE_HUANBI);
        		if(unit.repNode == null) unit.repNode = rules.get(STR_RULE_ZHANG);
        	}else if(PTN_CALC_CONN_YUCE.matcher(text).matches()){
        		unit.triggerPos = index;
        		unit.triggerType = RuleType.YUCE;
				unit.repNode = rules.get(STR_RULE_YUCE);
				if(unit.repNode == null) unit.repNode = rules.get(STR_RULE_ZHANG);
        	}else if(PTN_CALC_CONN_YUGAO.matcher(text).matches()){
        		//不需要 同义匹配"增长"
        		unit.triggerPos = index;
        		unit.triggerType = RuleType.YUCE;
				unit.repNode = rules.get(STR_RULE_YUGAO);
        	}else if(tmpType == NodeType.OPERATOR && ((OperatorNode)tmpNode).isFromChange
        			/*PTN_CALC_SELF_CHANGE.matcher(text).matches()*/){
        		unit.changePos = index;
        		//累计+增长 才变成变成累计增长
        		if(unit.triggerType == RuleType.LEIJI){
        			unit.repNode = rules.get(STR_RULE_LEIJI);
        		}
        		if(unit.repNode == null) unit.repNode = rules.get(STR_RULE_ZHANG);
        	//对于其他trigger字符串，放在最末匹配利用精确匹配。例如：董事+曾经 => 董事(历任)
        	}else if(rules.containsKey(text)){
        		unit.triggerPos = index;
        		unit.rightIndex = index;
        		unit.repNode = rules.get(text);
        		break;
        	}/*else if(IndexCalcPreBuilder.getPtnCalcConn().matcher(text).matches()){
        		if(index <= classIndex){
        			continue ;
        		}
        		//不能越过计算连接词语 例如：10年每股收益相对09年增幅最大的股票，遇到相对则跳出
        		for(int k = index + 1; k < query.getNodes().size(); k++){
        			NodeType tmpType2 = query.getNodes().get(k).type;
        			if(tmpType2 == NodeType.DATE || tmpType2 == NodeType.CLASS || tmpType2 == NodeType.PROP){
        				if(unit.repNode == null && unit.changePos != -1){
        					unit.repNode = rules.get(STR_RULE_ZHANG);
        				}
        				return unit;
        			}
        		}
        	}*/
			
			unit.rightIndex = index;
		}
		
		if(unit.rightIndex < classIndex){
			unit.rightIndex = classIndex;
		}
		return unit;
	}
	
	private static final Pattern PTN_CALC_CONN_TONGBI=
            Pattern.compile("同比.{0,2}");
    private static final Pattern PTN_CALC_CONN_FUHE =
    		Pattern.compile("复合.{0,2}");
    private static final Pattern PTN_CALC_CONN_LEIJI =
    		Pattern.compile("累计.{0,2}");
    private static final Pattern PTN_CALC_CONN_YUCE =
    		Pattern.compile("预[测|计].{0,2}");
    private static final Pattern PTN_CALC_CONN_YUGAO =
    		Pattern.compile("预告.{0,2}");
    private static final Pattern PTN_CALC_CONN_HUANBI =
    		Pattern.compile("环比.{0,2}");
    /** 用于取Rule里同比增长率配置的归一化说法*/
    private static final String STR_RULE_TONGBI = "同比增长";
    /** 用于取Rule里环比增长率配置的归一化说法*/
    private static final String STR_RULE_HUANBI = "环比增长";
    /** 用于取Rule里增长率配置的归一化说法*/
    private static final String STR_RULE_ZHANG = "增长";
    /** 用于取Rule里累计增长配置的归一化说法*/
    private static final String STR_RULE_LEIJI = "累计";
    /** 用于取Rule里复合增长率配置的归一化说法*/
    private static final String STR_RULE_YUCE = "预测";
    private static final String STR_RULE_YUGAO = "预告";
    /** 用于取Rule里复合增长率配置的归一化说法*/
    private static final String STR_RULE_FUHE = "复合";
    
	private Query query;
}
