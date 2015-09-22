package com.myhexin.qparser.ml;

import java.util.List;

import com.myhexin.qparser.node.SemanticNode;

public class NodesSemanticInfo {

	private String query;
	
	//句式匹配发生之前的nodes
	private List<SemanticNode> nodes;

	private int score;
	private String syntId;
	private String semanticId;
	
	
	private String syntactDesc;
	private String syntactDesc2;
	private String syntactDesc3;
	
	private String semanticDesc;
	private String semanticDesc2;
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("query=" + query + "\n");
		buf.append("\tnodes=" + nodes + "\n");
		buf.append("\tsyntactDesc=" + syntactDesc + "\n");
		buf.append("\tsyntactDesc2=" + syntactDesc2 + "\n");
		buf.append("\tsyntactDesc3=" + syntactDesc3 + "\n");
		buf.append("\tsemanticDesc=" + semanticDesc + "\n");
		buf.append("\tsemanticDesc2=" + semanticDesc2 + "\n");
		return buf.toString();
	}
	
	public List<SemanticNode> getNodes() {
		return nodes;
	}
	public void setNodes(List<SemanticNode> nodes) {
		this.nodes = nodes;
	}
	public String getSyntactDesc() {
		return syntactDesc;
	}
	public void setSyntactDesc(String syntactDesc) {
		this.syntactDesc = syntactDesc;
	}
	public String getSyntactDesc2() {
		return syntactDesc2;
	}
	public void setSyntactDesc2(String syntactDesc2) {
		this.syntactDesc2 = syntactDesc2;
	}
	
	public String getSyntactDesc3() {
		return syntactDesc3;
	}

	public void setSyntactDesc3(String syntactDesc3) {
		this.syntactDesc3 = syntactDesc3;
	}

	public String getSemanticDesc() {
		return semanticDesc;
	}
	public void setSemanticDesc(String semanticDesc) {
		this.semanticDesc = semanticDesc;
	}
	public String getSemanticDesc2() {
		return semanticDesc2;
	}
	public void setSemanticDesc2(String semanticDesc2) {
		this.semanticDesc2 = semanticDesc2;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}

	public String getSyntId() {
		return syntId;
	}

	public void setSyntId(String syntId) {
		this.syntId = syntId;
	}

	public String getSemanticId() {
		return semanticId;
	}

	public void setSemanticId(String semanticId) {
		this.semanticId = semanticId;
	}
	
	
}
