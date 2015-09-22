package com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic;

import java.util.HashMap;
import java.util.Set;

import com.myhexin.qparser.node.BoundaryNode;

public class SyntacticPatternMap {

    public SyntacticPatternMap() {
        syntacticPatternMap_ = new HashMap<String, SyntacticPattern>();
    }

    public SyntacticPattern getSyntacticPattern(String id) {
	    return getSyntacticPattern(id, true);
    }
    
    public SyntacticPattern getSyntacticPattern(String id, boolean create) {
    	if (id == null || id.length() == 0) {
    		return null;
    	}
    	id = id.trim();
    	if (BoundaryNode.getImplicitPattern(id) != null) {
    		return null;
    	}
        SyntacticPattern syntacticPattern = syntacticPatternMap_.get(id);
        if(syntacticPattern == null && create) {
            syntacticPattern = new SyntacticPattern(id);
            syntacticPatternMap_.put(id, syntacticPattern);
        }
        return syntacticPattern;
    }
    
    public Set<String> getAllSyntacticPatternIds() {
        Set<String> keys = syntacticPatternMap_.keySet();
        return keys;
    }
    
    private HashMap<String, SyntacticPattern> syntacticPatternMap_ = null;
}
