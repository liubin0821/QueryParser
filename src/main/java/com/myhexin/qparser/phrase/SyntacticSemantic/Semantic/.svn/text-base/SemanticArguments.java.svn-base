package com.myhexin.qparser.phrase.SyntacticSemantic.Semantic;

import com.myhexin.qparser.util.Util;

import java.util.ArrayList;

/**
 * stock_phrase_semantic.xml
 * SemanticPattern/Argument列表
 */
public class SemanticArguments {

    public SemanticArguments() {
    	
    }
    
    public ArrayList<SemanticArgument> getSemanticArguments() {
		return semanticArguments;
	}
    
    public void setSemanticArguments(ArrayList<SemanticArgument> semanticArguments) {
		this.semanticArguments = semanticArguments;
	}
    
    public int getSemanticArgumentsCount() {
	    return semanticArguments.size();
    }

    public SemanticArgument getSemanticArgument(String id, boolean create) {
        if(id == null || id.equals(""))
            return null;
        return getSemanticArgument(Util.parseInt(id, -1), create);
    }

	public SemanticArgument getSemanticArgument(int id, boolean create) {
        int size = semanticArguments.size();
        if(id < 1)
            return null;
        int ididx = id-1; // id 从1开始

        if(ididx < size)
        {
            return semanticArguments.get(ididx);
        }
        else if(create && ididx == size)
        {
            SemanticArgument gArgument = new SemanticArgument(id);
            semanticArguments.add(gArgument);
            return gArgument;
        }
        else
        {
            // TODO 必须顺序加入,是否抛出异常
            return null;
        }
    }

	// Argument列表
    private ArrayList<SemanticArgument> semanticArguments = new ArrayList<SemanticArgument>();
}
