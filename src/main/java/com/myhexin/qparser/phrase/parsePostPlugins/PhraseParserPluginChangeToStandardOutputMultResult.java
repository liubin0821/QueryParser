package com.myhexin.qparser.phrase.parsePostPlugins;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.IndexMultPossibility;
import com.myhexin.qparser.phrase.OutputResult;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;
import com.myhexin.qparser.phrase.parsePostPlugins.output.imp.ChangeToStandardStatementMultResult;

public class PhraseParserPluginChangeToStandardOutputMultResult extends PhraseParserPluginAbstract {
	
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginChangeToStandardOutputMultResult.class.getName());
	private String split_ = "_&_";
	
	public PhraseParserPluginChangeToStandardOutputMultResult() {
		super("Mult_Result");
	}

	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		
		Environment ENV = annotation.getEnv();
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return changeToStandardStatement(nodes,ENV);
    }
    
    public ArrayList<ArrayList<SemanticNode>> changeToStandardStatement(ArrayList<SemanticNode> nodes,Environment ENV) {
    	if (nodes == null || nodes.size() == 0)
    		return null;
    	
    	ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>();
    	//List<String> standardStatements = ChangeToStandardStatement.changeToStandardStatement(nodes, split_);
    	ENV.put("IndexMultPossibilitys", new ArrayList<IndexMultPossibility>(), true);
    	OutputResult result = ChangeToStandardStatementMultResult.changeToStandardStatement(nodes, split_,ENV);
    	if (result!=null)
    		nodes.get(0).setMultResult(result);
    	nodes.get(0).setIndexMultPossibility( (List<IndexMultPossibility>)ENV.get("IndexMultPossibilitys",true));

    	rlist.add(nodes);
		return rlist;
    }
    
    public void logResult(StringBuilder sb, ArrayList<ArrayList<SemanticNode>> qlist) {
        for(int i=0 ; i<qlist.size(); i++) {
            sb.append(String.format("[match %d]: %s\n", i, qlist.get(i).get(0).getMultResult())+"");
            sb.append(String.format("%s\n", qlist.get(i).get(0).getIndexMultPossibility()));
        }
        //sb.append(logsb_.toString());
        //logsb_  = new StringBuilder();
    }
}
