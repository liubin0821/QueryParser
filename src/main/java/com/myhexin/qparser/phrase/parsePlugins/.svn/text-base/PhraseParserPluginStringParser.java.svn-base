package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.tokenize.StringParser;

public class PhraseParserPluginStringParser extends PhraseParserPluginAbstract {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginStringParser.class.getName());
	
	@Autowired(required=true)
	private StringParser stringParser = null;

	
	public void setStringParser(StringParser stringParser) {
    	this.stringParser = stringParser;
    }

	public PhraseParserPluginStringParser() {
		super("String_Parser");
	}

	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		
		Environment ENV = annotation.getEnv();
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return stringParser(ENV,nodes);
	}

	private ArrayList<ArrayList<SemanticNode>> stringParser(Environment ENV,ArrayList<SemanticNode> nodes) {
		ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);
		
		nodes = stringParser.parse(ENV,nodes);
		
		if (nodes == null || nodes.size() == 0)
			return null;
		
		tlist.add(nodes);
		return tlist;
	}
}
