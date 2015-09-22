/**
 * @author: 	    吴永行 
 * @dateTime:	  2013-12-16 下午3:37:10
 * @description:   	
 * 
 */
package com.myhexin.qparser.date.parser;

import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;

public interface Parser
{
	public DateRange doParse(String dateString, String backtestTime) throws NotSupportedException,
			UnexpectedException;
}
