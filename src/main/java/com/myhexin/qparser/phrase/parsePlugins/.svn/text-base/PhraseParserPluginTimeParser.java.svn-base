package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.time.TimeParser;

public class PhraseParserPluginTimeParser extends PhraseParserPluginAbstract{
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginTimeParser.class.getName());
	
	@Autowired(required = true)
	private TimeParser timeParser = null;
	
	public void setTimeParser(TimeParser timeParser) {
    	this.timeParser = timeParser;
    }

	public PhraseParserPluginTimeParser() {
        super("Time_Parser");
    }
    
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		ArrayList<SemanticNode> nodes = annotation.getNodes();
    	return timeParser(nodes);
    }

    private ArrayList<ArrayList<SemanticNode>> timeParser(ArrayList<SemanticNode> nodes) {
        ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);
        
        nodes = timeParser.parse(nodes);
        
        if (nodes == null || nodes.size() == 0)
        	return null;
        
        tlist.add(nodes);
        return tlist;
    }
}
