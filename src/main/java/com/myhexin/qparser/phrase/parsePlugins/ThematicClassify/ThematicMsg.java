/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-3-13 下午7:43:43
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins.ThematicClassify;

import java.util.ArrayList;


/**
 * 对应data/stock_thematic.xml配置文件
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-4-27
 *
 */
public class ThematicMsg { //implements Cloneable 
	public String name;
	public String type;
	public String dateRange;
	
	public ArrayList<ThematicCondition> otherCondition = new ArrayList<ThematicCondition>(); 	
	
	//必须存在的
	public ArrayList<ThematicCondition> mustCondition = new ArrayList<ThematicCondition>();
	

	/*public ThematicMsg clone() {
		try {
			ThematicMsg rtn = (ThematicMsg) super.clone();
			return rtn;
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}*/

	@Override
	public String toString() {
		return "ThematicMsg [name=" + name + ", type=" + type
		        + ", indexTitles=" + "" + ", dateRange=" + dateRange
		        + "]";
	}

}