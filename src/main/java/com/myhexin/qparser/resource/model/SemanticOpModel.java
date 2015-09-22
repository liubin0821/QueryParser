package com.myhexin.qparser.resource.model;

import java.util.ArrayList;
import java.util.List;

public class SemanticOpModel {

	private int semanticId;
	private String chineseText;
	private String keyWord;
	private String opClazzName;
	private String opName;
	private String opProperty;
	private int opSonSize;
	
	private List<SemanticIndexOpModel> opList;

	
	public void addIndexOp(String opName, String opProperty) {
		if(opList==null) {
			opList = new ArrayList<SemanticIndexOpModel>();
		}
		opList.add(new SemanticIndexOpModel(opName, opProperty));
	}
	
	public int getSemanticId() {
		return semanticId;
	}

	public void setSemanticId(int semanticId) {
		this.semanticId = semanticId;
	}

	public String getChineseText() {
		return chineseText;
	}

	public void setChineseText(String chineseText) {
		this.chineseText = chineseText;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public String getOpName() {
		return opName;
	}

	public void setOpName(String opName) {
		this.opName = opName;
	}

	public String getOpProperty() {
		return opProperty == null ? "" : opProperty;
	}

	public void setOpProperty(String opProperty) {
		this.opProperty = opProperty;
	}

	public int getOpSonSize() {
		return opSonSize;
	}

	public void setOpSonSize(int opSonSize) {
		this.opSonSize = opSonSize;
	}

	public List<SemanticIndexOpModel> getOpList() {
		return opList;
	}

	public void setOpList(List<SemanticIndexOpModel> opList) {
		this.opList = opList;
	}

	public String getOpClazzName() {
		return opClazzName;
	}

	public void setOpClazzName(String opClazzName) {
		this.opClazzName = opClazzName;
	}
	
}
