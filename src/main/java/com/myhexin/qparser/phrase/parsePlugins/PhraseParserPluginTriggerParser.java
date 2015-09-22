package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.tokenize.TriggerParser;

public class PhraseParserPluginTriggerParser extends PhraseParserPluginAbstract{
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginTriggerParser.class);
	
	@Autowired(required=true)
	private TriggerParser triggerParser= null;
	
	public PhraseParserPluginTriggerParser() {
        super("Trigger_Parser");
    }
    
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		
		//Environment ENV = annotation.get(ParserKeys.EnvironmentKey.class);
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return stringParser(nodes);
    }

    public void setTriggerParser(TriggerParser triggerParser) {
    	this.triggerParser = triggerParser;
    }

	private ArrayList<ArrayList<SemanticNode>> stringParser(ArrayList<SemanticNode> nodes) {
        ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);
        
        nodes = triggerParser.parse(nodes);
        
        if (nodes == null || nodes.size() == 0)
        	return null;
        
        tlist.add(nodes);
        return tlist;
    }
}
