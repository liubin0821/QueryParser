package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePlugins.bind.SimpleBindStrToIndex1;

/**
 * 只是简单的附近的str到index
 */
public class PhraseParserPluginSimpleBindStrToIndex extends PhraseParserPluginAbstract{

	@Autowired(required=true)
	private SimpleBindStrToIndex1 simpleBindStrToIndex=null;
	
	
    public void setSimpleBindStrToIndex(SimpleBindStrToIndex1 simpleBindStrToIndex) {
    	this.simpleBindStrToIndex = simpleBindStrToIndex;
    }


	public PhraseParserPluginSimpleBindStrToIndex() {
        super("Simple_Bind_Props_to_Index");
    }


	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		
		Environment ENV = annotation.getEnv();
		ArrayList<SemanticNode> nodes = annotation.getNodes();
    	return dobind(nodes,ENV);
    }

    /**
     * 用于识别句中的周期节点
     * 
     * @param qlist
     * @throws NotSupportedException
     */
    private ArrayList<ArrayList<SemanticNode>> dobind(ArrayList<SemanticNode> nodes,Environment ENV)
    {
        ArrayList<ArrayList<SemanticNode>> qlist = new ArrayList<ArrayList<SemanticNode>>(1);
        try{
        	simpleBindStrToIndex.bind(nodes);
        }catch(UnexpectedException e){}
        
        qlist.add(nodes);
        return qlist;
    }
    
}
