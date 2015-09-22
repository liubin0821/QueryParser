package com.myhexin.qparser.phrase.parsePostPlugins;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;
import com.myhexin.qparser.phrase.parsePostPlugins.output.imp.SolarIndexOutput;

/**
 * 创建语义树模板
 */
public class PhraseParserPluginCreateJsonResult extends PhraseParserPluginAbstract{
	
	
    public PhraseParserPluginCreateJsonResult() {
        super("Create_Json_Result");
    }
    
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		ArrayList<SemanticNode> nodes = annotation.getNodes();
    	return createTemplate(nodes);
    }
    
    public ArrayList<ArrayList<SemanticNode>> createTemplate(ArrayList<SemanticNode> nodes) {
    	if (nodes == null || nodes.size() == 0)
    		return null;
    	
    	ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>();
    	List<String> results = SolarIndexOutput.changeToStandardStatement(nodes, " ");
    	//String jsonResult = CreateTemplate.getJsonResult(nodes);
    	String jsonResult = results.size()>0 ? results.get(0):"";
    	if(nodes.get(0).type==NodeType.ENV){
    		Environment listEnv = (Environment) nodes.get(0);
    		listEnv.put("jsonResult", "\""+jsonResult+"\"", true);
    	}
    	//nodes.get(0).jsonResult = jsonResult;
    	rlist.add(nodes);
		return rlist;
    }
    
    public void logResult(StringBuilder sb, ArrayList<ArrayList<SemanticNode>> qlist) {
        for(int i=0 ; i<qlist.size(); i++) {
        	if(qlist.get(i).get(0).type==NodeType.ENV){
        		Environment listEnv = (Environment) qlist.get(i).get(0);
        		if (listEnv.containsKey("jsonResult") && listEnv.get("jsonResult", String.class, false) !=null)
            		sb.append(String.format("[match %d]: %s\n", i, listEnv.get("jsonResult", String.class, false).replace("\n", "\t")));
            	else
            		sb.append(String.format("[match %d]: %s\n", i, ""));
        	}
        	
        }
        //sb.append(logsb_.toString());
        //logsb_  = new StringBuilder();
    }
}

