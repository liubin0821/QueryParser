package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;

import com.myhexin.qparser.iterator.ChunkIteratorImpl;
import com.myhexin.qparser.iterator.ChunkSemicolonIteratorImpl;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;

/**
 * 
 * 以_&_或;来分chunk的句式匹配
 * 
 * @author liuxiaofeng
 *
 */
public class PhraseParserPluginMatchSyntacticPatternsBySemicolonChunk extends PhraseParserPluginMatchSyntacticPatternsByChunk{
	
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginMatchSyntacticPatternsBySemicolonChunk.class.getName());

    public PhraseParserPluginMatchSyntacticPatternsBySemicolonChunk() {
    	strTitle = "Match_Syntactic_Patterns_Breadth_First_By_SemicolonChunk";
    }
    
    // 新增的代码，分chunk句式匹配，并构建笛卡尔积
    @Override
    protected ArrayList<ArrayList<SemanticNode>> matchSyntacticPatternsByChunk(ArrayList<SemanticNode> nodes, Environment ENV) {
    	ChunkIteratorImpl iterator = new ChunkSemicolonIteratorImpl(nodes);
    	return matchSyntacticPatternsByChunk(nodes, ENV, iterator);
    }
    
}
