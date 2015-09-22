package com.myhexin.qparser.util.condition.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.SyntacticSemantic.Semantic.SemanticBindTo;



/**
 * Condition 解析结果
 * 
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2014-12-9
 *
 */
public class BackTestCondAnnotation {

	//输入参数
	private String query;
	private String backtestTime;
	private Calendar backtestTimeRange;
	
	//输出参数
	private String standardQuery;
	private String outputs;
	private String resultCondJson;
	private String conditionHtml;
	
	//private List<String> resultChunksInfo;
	private Integer score;
	private String queryType; //是不是指数相关问句
	//private List<String> domains;
	
	//中间值
	private BoundaryInfos boundaryInfos;
	private List<SemanticNode> nodes;
	private SemanticBindTo theBindTo;
	private String pattern;
	
	//private String bindIndexTitle;
	private Map<String, List<SemanticNode>> bindMap;
	public String getBindIndexTitle(SemanticNode indexNode, String defaultIndex) {
		if(bindMap==null) {
			return null;
		}
		
		Set<String> keys  = bindMap.keySet();
		for(Iterator<String> it = keys.iterator(); it.hasNext(); ) {
			String k = it.next();
			List<SemanticNode> nodes = bindMap.get(k);
			if(nodes!=null) {
				for(SemanticNode node  :nodes) {
					if(indexNode!=null) {
						//
						if(indexNode.getText()!=null && indexNode.getText().equals(node.getText())) {
							return k;
						}else if(indexNode.isFocusNode()) {
							//是focusNode.index的时候还要处理一下
							FocusNode fNode = (FocusNode)indexNode;
							if(fNode.hasIndex() && fNode.getIndex()!=null) {
								if(fNode.getIndex().getText()!=null && fNode.getIndex().getText().equals(node.getText())) {
									return k;
								}
							}
						}
						
					}else if(indexNode==null || indexNode.getText()==null) {
						if(defaultIndex!=null && defaultIndex.equals(node.getText())) {
							return k;
						}
					}
				}
			}
		}
		
		return null;
	}
	
	
	
	
	public String getConditionHtml() {
		return conditionHtml;
	}

	public void setConditionHtml(String conditionHtml) {
		this.conditionHtml = conditionHtml;
	}




	public SemanticBindTo getTheBindTo() {
		return theBindTo;
	}



	public void setTheBindTo(SemanticBindTo theBindTo) {
		this.theBindTo = theBindTo;
	}


	public String getPattern() {
		return pattern;
	}



	public void setPattern(String pattern) {
		this.pattern = pattern;
	}


	private String domainStr=null;
	public void setDomainStr(String domainStr) {
		this.domainStr = domainStr;
	}
	
	public String getDomainStr() {
		if(domainStr!=null )
			return domainStr;
		else
			return "";
	}

	/*public List<String> getDomains() {
		return domains;
	}



	public void setDomains(List<String> domains) {
		this.domains = domains;
	}*/



	public String toString() {
		return "query : " + query + "\nscore : " + score + "\nqueryType : " + queryType + "\nresultCondJson : " + resultCondJson;
	}
	
	public BoundaryNode getbNode() {
		if(nodes!=null && boundaryInfos!=null && boundaryInfos.bStart<nodes.size()) {
			SemanticNode node = nodes.get(boundaryInfos.bStart);
			if(node.type == NodeType.BOUNDARY)
				return (BoundaryNode) node;
			else{
				return null;
			}
		}else{
			return null;
		}
	}
	public BoundaryInfos getBoundaryInfos() {
		return boundaryInfos;
	}
	public void setBoundaryInfos(BoundaryInfos boundaryInfos) {
		this.boundaryInfos = boundaryInfos;
	}
	public List<SemanticNode> getNodes() {
		return nodes;
	}
	public void setNodes(List<SemanticNode> nodes) {
		this.nodes = nodes;
	}
	/*public String getBindIndexTitle() {
		return bindIndexTitle;
	}*/
	/*public void setBindIndexTitle(String bindIndexTitle) {
		this.bindIndexTitle = bindIndexTitle;
	}*/



	private List<SemanticNode> bindValues=null;
	public void setBindMap(Map<String, List<SemanticNode>> bindMap) {
		this.bindMap = bindMap;
		
		
		if(bindMap!=null && bindMap.isEmpty()==false) {
			bindValues = new ArrayList<SemanticNode>();
			Collection<List<SemanticNode>> values = bindMap.values();
			for(List<SemanticNode> v : values) {
				for(SemanticNode n : v) {
					bindValues.add(n);
				}
			}
		}
	}
	
	public List<SemanticNode> getBindValues() {
		return bindValues;
	}
	
	public String getScoreStr() {
		if(score!=null) {
			return score.intValue()+"";
		}else{
			return "0";
		}
	}
	
	public String getStandardQuery() {
		return standardQuery;
	}


	public void setStandardQuery(String standardQuery) {
		this.standardQuery = standardQuery;
	}
	

	public String getOutputs() {
		return outputs;
	}



	public void setOutputs(String outputs) {
		this.outputs = outputs;
	}



	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	public Map<String, List<SemanticNode>> getBindMap() {
		return bindMap;
	}

	public Calendar getBacktestTimeRange() {
		return backtestTimeRange;
	}
	public void setBacktestTimeRange(Calendar backtestTimeRange) {
		this.backtestTimeRange = backtestTimeRange;
	}
	public String getBacktestTime() {
		return backtestTime;
	}
	public void setBacktestTime(String backtestTime) {
		this.backtestTime = backtestTime;
	}
	public String getQueryType() {
		//json结果null, 长度不够, []都返回"",走老系统接口
		if(resultCondJson==null || resultCondJson.length()<=5 || resultCondJson.equals("[]")) {
			return "";
		}else{
			return queryType!=null?queryType:"";	
		}
		
		
	}
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getResultCondJson() {
		return resultCondJson;
	}
	public void setResultCondJson(String resultCondJson) {
		this.resultCondJson = resultCondJson;
	}
}
