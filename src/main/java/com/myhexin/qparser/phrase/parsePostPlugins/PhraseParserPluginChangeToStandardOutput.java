package com.myhexin.qparser.phrase.parsePostPlugins;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePlugins.PhraseParserPluginAbstract;
import com.myhexin.qparser.phrase.parsePostPlugins.output.imp.ChangeToStandardStatement;

public class PhraseParserPluginChangeToStandardOutput extends PhraseParserPluginAbstract {
	
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginChangeToStandardOutput.class.getName());
	private String split_ = "_&_";
	
	public PhraseParserPluginChangeToStandardOutput() {
		super("Standard_Output_(线上使用)");
	}

	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return changeToStandardStatement(nodes);
    }
    
    public ArrayList<ArrayList<SemanticNode>> changeToStandardStatement(ArrayList<SemanticNode> nodes) {
    	if (nodes == null || nodes.size() == 0)
    		return null;
    	
    	ArrayList<ArrayList<SemanticNode>> rlist = new ArrayList<ArrayList<SemanticNode>>();
    	List<String> standardStatements = ChangeToStandardStatement.changeToStandardStatement(nodes, split_);
    	if (standardStatements!=null && standardStatements.size() > 0)
    		addToListEnv(nodes, "standardStatement", standardStatements.get(0), true);
    		//nodes.get(0).setStandardStatement(standardStatements.get(0));
    	rlist.add(nodes);
		return rlist;
    }
    
    public void logResult(StringBuilder sb, ArrayList<ArrayList<SemanticNode>> qlist) {
        for(int i=0 ; i<qlist.size(); i++) {
            //sb.append(String.format("[match %d]: %s\n", i, qlist.get(i).get(0).getStandardStatement()));
            sb.append(String.format("[match %d]: %s\n", i, getFromListEnv(qlist.get(i), "standardStatement",String.class, false)));
        }

    }
}
