/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-4-23 上午11:07:47
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase;

import java.util.ArrayList;

public class IndexMultPossibility {

	public int startPos = -1;
	public int endPos = -1;
	public String originalText = "";

	public ArrayList<String> possibleIndex = new ArrayList<String>();

	public String getDefaultResult() {
		if (possibleIndex.size() > 0)
			return possibleIndex.get(0);
		return null;
	}

	public int getStartPos() {
    	return startPos;
    }

	public int getEndPos() {
    	return endPos;
    }

	public String getOriginalText() {
    	return originalText;
    }

	public ArrayList<String> getPossibleIndex() {
    	return possibleIndex;
    }
	
	//仅仅网页输出使用
	@Override
	public String toString(){
		return "<br>&nbsp;&nbsp;"+originalText+"-->"+possibleIndex.toString();
		
	}
	
	public IndexMultPossibility copy() {
		IndexMultPossibility a = new IndexMultPossibility();
		a.startPos = startPos;
		a.endPos = endPos;
		a.originalText = originalText;
		if(possibleIndex!=null) {
			a.possibleIndex.addAll(possibleIndex);
		}
		
		return a;
	}

}
