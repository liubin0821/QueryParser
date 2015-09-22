/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-3-13 下午9:07:09
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins.ThematicClassify;

import java.util.HashSet;


public class SemanticThematicCondition extends ThematicCondition { 
	String id = "";
	HashSet<String> semantics = null;
	@Override
    public boolean isMatch(ThematicBasicInfos tbi) {
		String[] ids = id.split("\\|");
		for (String id : ids) {
			if (tbi.semantics.contains(id)) {
				return true;
			}
		}
	    
	    return false;
    }

	@Override
    public boolean isContain(String something) {
	    if (semantics==null) {
			fillSemantics();
		}
	    return semantics.contains(something);
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-4-1 上午11:29:57
     * @description:   	
     * 
     */
    private synchronized void fillSemantics() {
    	semantics = new HashSet<String>();
    	String[] ids = id.split("\\|");
		for (String id : ids) {
			semantics.add(id);
		}
    }
	
}


