/**
 * @author: 	    吴永行 
 * @dateTime:	  2013-11-12 下午4:55:33
 * @description:   	
 * 
 */
package com.myhexin.qparser.node;

import com.myhexin.qparser.define.EnumDef;

public class RelationShip {

	private String condition = null;
	private String targetIndex = null;
	private boolean needCombined = true;
	private EnumDef.CombineType combineType= null;
	
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getTargetIndex() {
		return targetIndex;
	}
	public void setTargetIndex(String targetIndex) {
		this.targetIndex = targetIndex;
	}
	public EnumDef.CombineType getCombineType() {
		return combineType;
	}
	public void setCombineType(EnumDef.CombineType combineType) {
		this.combineType = combineType;
	}
	
	public boolean needCombined() {
		return needCombined;
	}
	public void setIsCombined(boolean isCombined) {
		this.needCombined = isCombined;
	}
	protected RelationShip copy() {
		RelationShip rs = new RelationShip();
		rs.combineType = this.combineType;
		rs.condition = this.condition;
		rs.targetIndex = this.targetIndex;
		rs.needCombined = this.needCombined;
		return rs;
	}
	
}
