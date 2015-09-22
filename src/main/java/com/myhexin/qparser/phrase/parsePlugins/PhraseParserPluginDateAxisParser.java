package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.myhexin.qparser.date.axis.DateAxisHandler;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;

public class PhraseParserPluginDateAxisParser extends PhraseParserPluginAbstract{
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginNumParser.class.getName());
	
	@Autowired(required=true)
	private DateAxisHandler dateAxisHandler = null;
	
	
    public void setDateAxisHandler(DateAxisHandler dateAxisHandler) {
    	this.dateAxisHandler = dateAxisHandler;
    }

	public PhraseParserPluginDateAxisParser() {
        super("Date_Axis_Parser");
    }
    
	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		ArrayList<ArrayList<SemanticNode>> tlist = dateAxisHandler.parse(nodes,annotation.getBacktestTime());
		return tlist;
    }
}
