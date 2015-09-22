package com.myhexin.qparser.util.condition;


import java.util.Calendar;
import java.util.List;

import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;
import com.myhexin.qparser.resource.model.SemanticOpModel;
import com.myhexin.qparser.util.condition.model.ConditionModel;

public class ConditionParam {

	public ConditionParam(String domain) {
		this.domain = domain;
	}
	private SemanticOpModel opModel;
	private List<SemanticNode> nodes;
	private SemanticBindTo sbt;
	private BoundaryInfos boundaryInfos;
	private String domain;
	private List<ConditionModel> prevConds;
	private Calendar backtestTime;
	
	
	public Calendar getBacktestTime() {
		return backtestTime;
	}
	public void setBacktestTime(Calendar backtestTime) {
		this.backtestTime = backtestTime;
	}
	public List<ConditionModel> getPrevConds() {
		return prevConds;
	}
	public void setPrevConds(List<ConditionModel> prevConds) {
		this.prevConds = prevConds;
	}
	public SemanticOpModel getOpModel() {
		return opModel;
	}
	public void setOpModel(SemanticOpModel opModel) {
		this.opModel = opModel;
	}
	public List<SemanticNode> getNodes() {
		return nodes;
	}
	public void setNodes(List<SemanticNode> nodes) {
		this.nodes = nodes;
	}
	public SemanticBindTo getSbt() {
		return sbt;
	}
	public void setSbt(SemanticBindTo sbt) {
		this.sbt = sbt;
	}
	public BoundaryInfos getBoundaryInfos() {
		return boundaryInfos;
	}
	public void setBoundaryInfos(BoundaryInfos boundaryInfos) {
		this.boundaryInfos = boundaryInfos;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	
}
