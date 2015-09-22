package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePlugins.bind.BindStrToIndex;

public class PhraseParserPluginBindStrToIndex extends PhraseParserPluginAbstract{

	@Autowired(required=true)
	private BindStrToIndex bindStrToIndex;
	
	
	
    public void setBindStrToIndex(BindStrToIndex bindStrToIndex) {
    	this.bindStrToIndex = bindStrToIndex;
    }

	public PhraseParserPluginBindStrToIndex() {
        super("Bind_Str_to_Index");
    }
    
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		
		Environment ENV = annotation.getEnv();
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return bindPropToIndex(nodes,ENV);
    }

    /**
     * 用于识别句中的周期节点
     * 
     * @param qlist
     * @throws NotSupportedException
     */
    private ArrayList<ArrayList<SemanticNode>> bindPropToIndex(ArrayList<SemanticNode> nodes,Environment ENV)
    {
        ArrayList<ArrayList<SemanticNode>> qlist = new ArrayList<ArrayList<SemanticNode>>(1);
        try{
        	bindStrToIndex.bind(ENV,nodes);
        }catch(UnexpectedException e)
        {
        }
        qlist.add(nodes);
        return qlist;
    }
    
    //把绑定信息,打印出来
    public void logResult(StringBuilder sb, ArrayList<ArrayList<SemanticNode>> qlist) {
        for(int i=0 ; i<qlist.size(); i++) {
            sb.append(String.format("[match %d]:<BR>%s\n", i, _nodesHtmlAllBindings(qlist.get(i)) ));
        }
    }
}
