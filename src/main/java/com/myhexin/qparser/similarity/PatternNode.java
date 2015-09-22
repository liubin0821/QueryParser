package com.myhexin.qparser.similarity;

import java.util.List;

import com.myhexin.qparser.define.EnumDef.ChangeType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.TechOpType;
import com.myhexin.qparser.define.EnumDef.TechPeriodType;
import com.myhexin.qparser.node.AvgNode;
import com.myhexin.qparser.node.ChangeNode;
import com.myhexin.qparser.node.OperatorNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.SortNode;
import com.myhexin.qparser.node.TechOpNode;
import com.myhexin.qparser.node.TechPeriodNode;


public class PatternNode {
	
	public PatternNode(String text){
		this.text = text ;
	} 
	
	public PatternNode(SemanticNode node){
		this(node.getText()) ;
		this.nodeType = this.snTpyeToPNodeTpye(node.type) ;
	}
	
	public static PatternNode makePatternNode(SemanticNode node){
		NodeType type = node.type ;
		switch(type){
		case AVG:
			AvgPNode avgNode = new AvgPNode(null) ;
			avgNode.compare_ = ((AvgNode)node).compare_ ;
			return avgNode ;
		case CHANGE:
			ChangePNode changePNode = new ChangePNode(null) ;
			changePNode.changeType = ((ChangeNode)node).getChangeType_() ;
			return changePNode ;
		case CLASS:
			IndexPNode indexPNode = new IndexPNode(node.getText()) ;
			return indexPNode ;
		case OPERATOR:
			OperPNode operPNode = new OperPNode(null) ;
			operPNode.setOperatorType(((OperatorNode)node).operatorType.toString()) ;
			return operPNode ;
		case SORT:
			SortPNode sortPNode = new SortPNode(null) ;
			sortPNode.isDescending = ((SortNode)node).isDescending_() ;
			return sortPNode ;
		case TECHOPERATOR:
			TechopPNode techopPNode = new TechopPNode(null) ;
			techopPNode.operType = ((TechOpNode)node).getOperType() ;
			return techopPNode ;
		case TECH_PERIOD:
			TechperiodPNode techperiodPNode = new TechperiodPNode(null) ;
			techperiodPNode.periodType = ((TechPeriodNode)node).getPeriodType() ;
			return techperiodPNode ;
		default:
			return null ;
		}
	}
	
	
	protected PatternNodeType nodeType ;
	protected String text;
	protected Integer tag ;
	protected Double priority ;
	
	public String text(){
		return this.text ;
	}
	
	public void setText(String text){
		this.text = text ;
	}
	
	public int tag(){
		return this.tag ;
	}
	
	/**
	 * !
	 */
	public void setNotMatchTag(){
		this.tag  = new Integer(1) ;
	}
	
	/**
	 * 必须出现
	 */
	public void setMatchTag(){
		this.tag  = new Integer(0) ;
	}
	
	/***
	 * 可选出现
	 */
	public void setOptionMatchTag(){
		this.tag  = new Integer(2) ;
	}
	
	public PatternNodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(PatternNodeType patternNodeType) {
		this.nodeType = patternNodeType;
	}
	
	public double getPriority() {
		if(this.priority == null) 
			return 0.0 ;
		else 
			return priority;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}
	
	/**
	 * 普通匹配分值
	 */
	public void matchPriority(){
		if(this.priority == null) this.priority = new Double(0) ;
		this.priority += 1;
	}
	
	/**
	 * 可选匹配分值
	 */
	public void optionPriority(){
		if(this.priority == null) this.priority = new Double(0) ;
		this.priority += 0.5;
	}
	
	/**
	 * 非匹配分值
	 */
	public void notMatchPriority(){
		if(this.priority == null) this.priority = new Double(0) ;
		this.priority += 0;
	}

	
	/**
	 * 具体指标文本分值
	 */
	public void textPriority(){
		if(this.priority == null) this.priority = new Double(0) ;
		this.priority += 2;
	} 
	
	/**
	 * 具有特定属性的优先级
	 */
	public void attrPriority(){
		if(this.priority == null) this.priority = new Double(0) ;
		this.priority += 1.5;
	}
	
	/**
	 * 类型分值
	 */
	public void typePriority(){
		if(this.priority == null) this.priority = new Double(0) ;
		this.priority += 1;
	}
	
	/**
	 * end分值
	 */
	public void endPriority(){
		if(this.priority == null) this.priority = new Double(0) ;
		this.priority += 3;
	}
	
	/**
	 * start分值
	 */
	public void startPriority(){
		if(this.priority == null) this.priority = new Double(0) ;
		this.priority += 3;
	}
	
	
	/** SemanticNode Type to PatternNodeType*/
	private PatternNodeType snTpyeToPNodeTpye(NodeType type){
		switch(type){
		case AVG:
			return PatternNodeType.avg ;
		case CHANGE:
			return PatternNodeType.change ;
		case CLASS:
			return PatternNodeType.index ;
		case DATE:
			return PatternNodeType.date ;
		case LOGIC:
			return PatternNodeType.logic ;
		case NEGATIVE:
			return PatternNodeType.negative ;
		case NUM:
			return PatternNodeType.num ;
		case OPERATOR:
			return PatternNodeType.oper ;
		case PROP:
			return PatternNodeType.prop ;
		case QWORD:
			return PatternNodeType.qword ;
		case SORT:
			return PatternNodeType.sort ;
		case STR_VAL:
			return PatternNodeType.str ;
		case TECHOPERATOR:
			return PatternNodeType.techop ;
		case TECH_PERIOD:
			return PatternNodeType.techperiod ;
		case TRIGGER:
			return PatternNodeType.trigger ;
		default:
			return PatternNodeType.unknown ;
		}
	}
	
	/** 以下值需要传递给solr，约定小写 */
	public static enum PatternNodeType{
		avg,
		change,
		date,
		end,
		fakedate,
		fakeindex,
		fakenum,
		index,
		logic,
		multi,
		negative,
		num,
		oper,
		prop,
		qword,
		sort,
		start,
		str,
		techop,
		techperiod,
		trigger,
		whatever,
		unknown 
	}
	
	
	
	
	public static class AvgPNode extends PatternNode {
		String compare_ = null;
		public AvgPNode(String text) {
			super(text);
			this.setNodeType(PatternNodeType.avg) ;
		}
	}
	
	public static class ChangePNode extends PatternNode {
		ChangeType changeType = null ;
		public ChangePNode(String text) {
			super(text);
			this.setNodeType(PatternNodeType.change) ;
		}

	}
	
	

	public static class DatePNode extends PatternNode {

		public DatePNode(String text) {
			super(text);
			this.setNodeType(PatternNodeType.date) ;
		}
		
		/** 单位 */
		private String unit ;
		
		/** 是否连续时间, 因为需要使用json传递数据，所以使用字符串做变量*/
		private String isSequence ;
		
		/** 是否周期时间 */
		private String isCycle;
		
		/** 是否频率时间 */
		private String isFrequence;
		
		/** Getter and Setter */
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public boolean isSequence() {
			return Boolean.getBoolean(isSequence);
		}
		public void setSequence(boolean isSequence) {
			this.isSequence = String.valueOf(isSequence);
		}
		public boolean isCycle() {
			return Boolean.getBoolean(isCycle);
		}
		public void setCycle(boolean isCycle) {
			this.isCycle = String.valueOf(isCycle);
		}
		public boolean isFrequence() {
			return Boolean.getBoolean(isFrequence);
		}
		public void setFrequence(boolean isFrequence) {
			this.isFrequence = String.valueOf(isFrequence);
		}
	}
	
	public static class EndPNode extends PatternNode {

		public EndPNode() {
			super("end");
			this.setNodeType(PatternNodeType.end) ;
		}

	}
	public static class FakeDatePNode extends PatternNode {

		public FakeDatePNode(String text) {
			super(text);
			this.setNodeType(PatternNodeType.fakedate) ;
		}

	}
	
	public static class FakeIndexPNode extends PatternNode {

		public FakeIndexPNode(String text) {
			super(text);
			this.setNodeType(PatternNodeType.fakeindex) ;
		}
	}

	
	public static class FakeNumPNode extends PatternNode {

		public FakeNumPNode(String text) {
			super(text);
			this.setNodeType(PatternNodeType.fakenum) ;
		}
	}
	

public static class IndexPNode extends PatternNode {

	public IndexPNode(String text) {
		super(text);
		this.setNodeType(PatternNodeType.index) ;
	}
	
	/** 分类 */
	private String catalogue ;
	
	/** 指标类型 */
	private String indexType ;
	
	/** 指标单位 */
	private String unit ;
	
	/** 技术指标类型 */
	private String techType ;
	
	/** Getter and Setter */
	public String getCatalogue() {
		return catalogue;
	}
	public String getCatalogueStr() {
		return catalogue;
	}
	public void setCatalogue(String catalogue) {
		this.catalogue = catalogue;
	}
	public String getIndexType() {
		return indexType;
	}
	public String getIndexTypeStr() {
		if(indexType.equals("N")){
			return "number" ;
		} else if(indexType.equals("D")){
			return "date" ;
		} else if(indexType.equals("B")){
			return "bool" ;
		} else if(indexType.equals("S")){
			return "string" ;
		}
		return indexType;
	}
	
	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}
	public String getUnit() {
		return unit;
	}
	public String getUnitStr() {
		return unit;
	}
	public void setUnit(String unit) {
		if(unit != null && (unit.equals("null") || unit.isEmpty()))
			unit = null ;
		this.unit = unit;
	}
	public String getTechType() {
		return techType;
	}
	public String getTechTypeStr() {
		return techType.toLowerCase();
	}
	public void setTechType(String techType) {
		this.techType = techType;
	}
}


public static class LogicPNode extends PatternNode {

	public LogicPNode(String text) {
		super(text);
		this.setNodeType(PatternNodeType.logic) ;
	}
	
	String logicType ;
	
	public String getLogicType() {
		return logicType;
	}
	public void setLogicType(String logicType) {
		this.logicType = logicType;
	}
	
}


public static class MultiPNode extends PatternNode {

	public MultiPNode(String text) {
		super(text);
		this.setNodeType(PatternNodeType.multi) ;
	}
	
	private List<PatternNode> nodes ;
	
	public List<PatternNode> getNodes() {
		return nodes;
	}
	public void setNodes(List<PatternNode> nodes) {
		this.nodes = nodes;
	}
}

public static class NegativePNode extends PatternNode {

	public NegativePNode(String text) {
		super(text);
		this.setNodeType(PatternNodeType.negative) ;
	}
}


public static class NumPNode extends PatternNode{

	public NumPNode(String text) {
		super(text);
		this.setNodeType(PatternNodeType.num) ;
	}

	private String unit ;

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

}

public static class OperPNode extends PatternNode {

	public OperPNode(String text) {
		super(text);
		this.setNodeType(PatternNodeType.oper) ;
	}
	
	String operatorType ;
	
	public String getOperatorType() {
		return operatorType;
	}
	public void setOperatorType(String operatorType) {
		this.operatorType = operatorType;
	}
	
}


public static class PropPNode extends PatternNode {

	public PropPNode(String text) {
		super(text);
		this.setNodeType(PatternNodeType.prop) ;
	}

	private String propType ;

	public String getPropType() {
		return propType;
	}

	public void setPropType(String propType) {
		if(propType != null && (propType.equals("null") || propType.isEmpty()))
			propType = null ;
		this.propType = propType;
	}
	
}

public static class QwordPNode extends PatternNode {

	public QwordPNode(String text) {
		super(text);
		this.setNodeType(PatternNodeType.qword) ;
		}
	
	String qwordType ;
	
	public String getQwordType() {
		return qwordType;
	}
	public void setQwordType(String qwordType) {
		this.qwordType = qwordType;
	}
	
}


public static class SortPNode extends PatternNode {

	public SortPNode(String text) {
		super(text);
		this.setNodeType(PatternNodeType.sort) ;
	}
	
	Boolean isDescending ;

	public boolean isDescending() {
		return isDescending;
	}

	public void setDescending(boolean isDescending) {
		this.isDescending = isDescending;
	}

	
	
	
}

public static class StrPNode extends PatternNode {

	public StrPNode(String text) {
		super(text);
		this.setNodeType(PatternNodeType.str) ;
	}
	
	private List<String> ofWhat;
	
	public List<String> getOfWhat() {
		return ofWhat;
	}
	public void setOfWhat(List<String> ofWhat) {
		this.ofWhat = ofWhat;
	}
}


public static class TechopPNode extends PatternNode {
	TechOpType operType;
	public TechopPNode(String text) {
		super(text);
		this.setNodeType(PatternNodeType.techop) ;
	}

}


public static class TechperiodPNode extends PatternNode {
	TechPeriodType periodType = null ;
	public TechperiodPNode(String text) {
		super(text);
		this.setNodeType(PatternNodeType.techperiod) ;
	}
}
public static class TriggerPNode extends PatternNode {

	public TriggerPNode(String text) {
		super(text);
		this.setNodeType(PatternNodeType.trigger) ;
	}

}

public static class WhateverPNode extends PatternNode {

	public WhateverPNode(String text) {
		super(text);
		this.setNodeType(PatternNodeType.whatever) ;
	}

}


public static class StartPNode extends PatternNode {

	public StartPNode() {
		super("start");
		this.setNodeType(PatternNodeType.start) ;
	}
}
}
