/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-3-20 下午8:55:28
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins.ThematicClassify;

import com.myhexin.qparser.node.DateNode;

public class DateThematicCondition extends ThematicCondition{
	
	boolean isReport = false;

	//只要有一个报告期类型时间, 就判断为报告期
	@Override
    public boolean isMatch(ThematicBasicInfos tbi) {
	    for (DateNode dn : tbi.dates) {        
	        if (dn != null && isReport && dn.getDateinfo()!=null && dn.getDateinfo().isReport())
	        	return true;	     
		}
	    return false;
    }

	@Override
    public boolean isContain(String something) {	    
	    return false;
    }
	
	

}
