package com.myhexin.qparser.util.condition.model;

import com.google.gson.annotations.Expose;
import com.myhexin.qparser.phrase.util.Consts;

public class ConditionOpModel  extends ConditionModel{

	@Expose private String opName;
	@Expose private String opProperty;
	@Expose private int sonSize;
	public String getOpName() {
		return opName;
	}
	public void setOpName(String opName) {
		this.opName = opName;
	}
	public String getOpProperty() {
		return opProperty;
	}
	public void setOpProperty(String opProperty) {
		this.opProperty = opProperty;
	}
	public int getSonSize() {
		return sonSize;
	}
	public void setSonSize(int sonSize) {
		this.sonSize = sonSize;
	}
	
	public void checkAndSetProperties() {
		if(opProperty==null) opProperty = Consts.STR_BLANK;
	}
	public int getConditionType() {
		return ConditionModel.TYPE_OP;
	}
}
