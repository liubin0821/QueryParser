package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePlugins.bind.BindNumOrDateToIndex1;

public class PhraseParserPluginBindNumOrDateToIndex extends PhraseParserPluginAbstract{

	@Autowired(required=true)
	private BindNumOrDateToIndex1 bindNumOrDateToIndex;
	
	
    public void setBindNumOrDateToIndex(BindNumOrDateToIndex1 bindNumOrDateToIndex) {
    	this.bindNumOrDateToIndex = bindNumOrDateToIndex;
    }

	public PhraseParserPluginBindNumOrDateToIndex() {
        super("Bind_Num_And_Date_To_Index");
    }
    
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		return bindNumOrDateToIndex(annotation.getNodes(),annotation.getEnv());
    }

    private ArrayList<ArrayList<SemanticNode>> bindNumOrDateToIndex(ArrayList<SemanticNode> nodes,Environment ENV) {
        ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);		 
 
 
        try{
        	bindNumOrDateToIndex.bind(nodes);
        }catch(UnexpectedException e)
        {
        }
        tlist.add(nodes);
        return tlist;
    }	
    //把绑定信息,打印出来
    public void logResult(StringBuilder sb, ArrayList<ArrayList<SemanticNode>> qlist) {
        for(int i=0 ; i<qlist.size(); i++) {
            sb.append(String.format("[match %d]:<BR>%s\n", i, _nodesHtmlAllBindings(qlist.get(i)) ));
        }
    }
}
