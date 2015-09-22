package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.number.NumParser;
import com.myhexin.qparser.phrase.ParserAnnotation;

public class PhraseParserPluginNumParser extends PhraseParserPluginAbstract {
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginNumParser.class.getName());

	@Autowired(required = true)
	private NumParser numParser = null;

	public void setNumParser(NumParser numParser) {
    	this.numParser = numParser;
    }

	public PhraseParserPluginNumParser() {
		super("Num_Parser");
	}

	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return numParser(nodes);
	}

	private ArrayList<ArrayList<SemanticNode>> numParser(ArrayList<SemanticNode> nodes) {
		ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);
		nodes = numParser.parse(nodes);
		
		if (nodes == null || nodes.size() == 0)
			return null;
		
		tlist.add(nodes);
		return tlist;
	}
}
