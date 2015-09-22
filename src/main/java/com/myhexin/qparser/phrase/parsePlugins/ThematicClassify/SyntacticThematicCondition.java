/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-3-13 下午9:07:09
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePlugins.ThematicClassify;

import java.util.HashSet;

public class SyntacticThematicCondition extends ThematicCondition { 
	
	//private String id = null;
	private String[] ids = null;
	private HashSet<String> syntactics = null;
	
	public void setId(String id) {
		//this.id = id;
		if(id!=null){
			ids = id.split("\\|");
			fillSyntactics();
		}
	}

	@Override
    public boolean isMatch(ThematicBasicInfos tbi) {
		if(ids==null) {
			return false;
		}
		for (String id : ids) {
			if (tbi.syntactics.contains(id)) {
				return true;
			}
		}
	    
	    return false;
    }

	@Override
    public boolean isContain(String something) {
		if(syntactics!=null)
			return syntactics.contains(something);
		else{
			return false;
		}
    }

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-4-1 上午11:27:02
     * @description:   	
     * 
     */
    private void fillSyntactics() {
    	if(ids!=null) {
    		syntactics = new HashSet<String>();
    		for (String id : ids) {
    			syntactics.add(id);
    		}
    	}
    	
    }
	
}


