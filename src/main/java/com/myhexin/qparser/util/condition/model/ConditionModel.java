package com.myhexin.qparser.util.condition.model;

import com.google.gson.annotations.Expose;

public abstract class ConditionModel {

	
	public final static int TYPE_INDEX = 1;
	public final static int TYPE_OP = 2;
	/**
	 * 比如:
	 * JsonBuilder不用getIndexProperties()去拿indexProperties,
	 * 当indexProperties==null时,json丢失这个属性
	 * 
	 */
	public abstract void checkAndSetProperties();
	public abstract int getConditionType();
	
	@Expose
	private String uiText;
	public String getUIText() {
		return uiText;
	}
	public void setUIText(String uiText) {
		this.uiText = uiText;
	}
}
