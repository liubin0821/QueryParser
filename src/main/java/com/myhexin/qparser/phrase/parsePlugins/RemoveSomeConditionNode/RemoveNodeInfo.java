/**
 * @author: 	    吴永行 
 * @dateTime:	  2013-10-28 下午7:26:42
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins.RemoveSomeConditionNode;

public class RemoveNodeInfo {

	private String lable;
	private String preProp;
	private String preLable;
	private String postProp;

	public String getPostProp() {
    	return postProp;
    }

	public void setPostProp(String postProp) {
    	this.postProp = postProp;
    }

	public String getLable() {
		return lable;
	}

	public void setLable(String lable) {
		this.lable = lable;
	}

	public String getPreProp() {
    	return preProp;
    }

	public void setPreProp(String preProp) {
    	this.preProp = preProp;
    }

	public String getPreLable() {
    	return preLable;
    }

	public void setPreLable(String preLable) {
    	this.preLable = preLable;
    }



}
