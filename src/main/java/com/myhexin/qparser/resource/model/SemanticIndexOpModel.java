package com.myhexin.qparser.resource.model;

public class SemanticIndexOpModel {

	private String indexOp;
	private String indexOpValue;
	public SemanticIndexOpModel(String indexOp, String indexOpValue) {
		super();
		this.indexOp = indexOp;
		this.indexOpValue = indexOpValue;
	}
	public String getIndexOp() {
		return indexOp;
	}
	private final static String GT  =">";
	private final static String GT2  ="(";
	public String getIndexOp2() {
		if(indexOp!=null && indexOp.equals(GT) ) {
			return GT2;
		}else
			return indexOp;
	}
	public String getIndexOpValue() {
		return indexOpValue;
	}
	public String toString() {
		return indexOp + ","  + indexOpValue;
	}
}
