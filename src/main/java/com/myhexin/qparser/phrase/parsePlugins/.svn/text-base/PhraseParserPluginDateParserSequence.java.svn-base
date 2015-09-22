package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;

import com.myhexin.qparser.date.DateSequence;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;

public class PhraseParserPluginDateParserSequence extends PhraseParserPluginAbstract{
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginNumParser.class.getName());
	
    public PhraseParserPluginDateParserSequence() {
        super("Date_Sequence");
    }
    
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		
		Environment ENV = annotation.getEnv();
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return dateParser(nodes);
    }

    private ArrayList<ArrayList<SemanticNode>> dateParser(ArrayList<SemanticNode> nodes) {
        ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);
        
        DateSequence.parse(nodes, null);

        tlist.add(nodes);
        return tlist;
    }
}
