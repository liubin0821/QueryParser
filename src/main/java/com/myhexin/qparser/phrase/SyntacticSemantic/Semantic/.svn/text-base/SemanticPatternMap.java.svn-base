package com.myhexin.qparser.phrase.SyntacticSemantic.Semantic;

import java.util.HashMap;

public class SemanticPatternMap {

    public SemanticPatternMap() {
        semanticPatternMap_ = new HashMap<String, SemanticPattern>();
    }

    public SemanticPattern getSemanticPattern(String id, boolean create) {
    	if (id == null || id.trim().length() == 0) {
    		return null;
    	}
    	id = id.trim();
        SemanticPattern semanticPattern;
        semanticPattern = semanticPatternMap_.get(id);
        if(semanticPattern == null) {
            if(create == true) {
                semanticPattern = new SemanticPattern(id);
                semanticPatternMap_.put(id, semanticPattern);
            }
        }
        return semanticPattern;
    }
    
    private HashMap<String, SemanticPattern> semanticPatternMap_ = null;
}
