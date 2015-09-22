/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-3-13 下午7:44:40
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins.ThematicClassify;


public abstract class ThematicCondition implements Cloneable {
	protected boolean mustExist = false;
	protected String matchSrc;
	
	//一个专题是否匹配
	public abstract boolean  isMatch(ThematicBasicInfos tbi);
	
	public abstract boolean  isContain(String something);
	

}