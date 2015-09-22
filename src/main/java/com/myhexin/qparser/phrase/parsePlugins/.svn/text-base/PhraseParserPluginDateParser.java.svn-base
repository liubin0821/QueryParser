package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;

import com.myhexin.qparser.date.DateParser;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;

public class PhraseParserPluginDateParser extends PhraseParserPluginAbstract{
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginNumParser.class.getName());
	
	@Autowired(required=true)
	private DateParser dateParser = null;
	
	
    public void setDateParser(DateParser dateParser) {
    	this.dateParser = dateParser;
    }

	public PhraseParserPluginDateParser() {
        super("Date_Parser");
    }
    
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return dateParser(nodes, annotation.getBacktestTime());
    }

    private ArrayList<ArrayList<SemanticNode>> dateParser(ArrayList<SemanticNode> nodes, Calendar backtestTime) {
        ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);
        
        nodes = dateParser.parse(nodes, backtestTime);
        
        if (nodes == null || nodes.size() == 0)
        	return null;
        
        tlist.add(nodes);
        return tlist;
    }
}
