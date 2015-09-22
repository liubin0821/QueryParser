package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.parsePlugins.MatchSyntacticPatterns.MatchSyntacticPatterns1;

/**
 * 句式匹配
 * 采用广度优先遍历的思想进行
 * 
 * @author admin
 *
 */
public class PhraseParserPluginMatchSyntacticPatterns extends PhraseParserPluginAbstract{
	
	@Autowired(required=true)
	private MatchSyntacticPatterns1 matchSyntacticPatterns=null;
	
    public PhraseParserPluginMatchSyntacticPatterns() {
        super("Match_Syntactic_Patterns_Breadth_First");
    }
    
    
    
    public void setMatchSyntacticPatterns(MatchSyntacticPatterns1 matchSyntacticPatterns) {
    	this.matchSyntacticPatterns = matchSyntacticPatterns;
    }



	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		
		Environment ENV = annotation.getEnv();
		ArrayList<SemanticNode> nodes = annotation.getNodes();
    	return matchSyntacticPatterns.matchSyntacticPatterns(nodes, ENV);
    }
}
