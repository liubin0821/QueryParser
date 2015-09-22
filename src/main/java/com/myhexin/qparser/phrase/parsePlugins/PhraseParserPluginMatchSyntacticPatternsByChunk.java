package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.myhexin.qparser.iterator.ChunkInfos;
import com.myhexin.qparser.iterator.ChunkIteratorImpl;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.NodeUtil;
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
public class PhraseParserPluginMatchSyntacticPatternsByChunk extends PhraseParserPluginAbstract{
	
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserPluginMatchSyntacticPatternsByChunk.class.getName());
	public static final int SYNTACTIC_LIST_MAX_SIZE = 64;
	public static final int SYNTACTIC_LIST_MAX_SIZE_SAME_POS = 8;
	
	@Autowired(required=true)
	protected MatchSyntacticPatterns1 matchSyntacticPatterns=null;
	
    public PhraseParserPluginMatchSyntacticPatternsByChunk() {
        super("Match_Syntactic_Patterns_Breadth_First_By_Chunk");
    }
    
    
    public void setMatchSyntacticPatterns(MatchSyntacticPatterns1 matchSyntacticPatterns) {
    	this.matchSyntacticPatterns = matchSyntacticPatterns;
    }


    /*static class BindNodeInfo {
    	private SemanticNode node;
    	private int index;
    }*/


	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		
		Environment ENV = annotation.getEnv();
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		
		/*
	     * 已经被绑定的node,不参加句式匹配
	     * 比如kdj的k值>10 
	     * kdj被绑定到k值, 所以kdj不参加句式匹配
	     * 
	     */
		/*ArrayList<SemanticNode> new_nodes = new ArrayList<SemanticNode>();
		ArrayList<BindNodeInfo> bind_nodes = new ArrayList<BindNodeInfo>();
    	for(int i=0;i<nodes.size();i++) {
    		SemanticNode node = nodes.get(i);
    		if(node.isBoundToIndex()==false) {
    			new_nodes.add(node);
    		}else{
    			BindNodeInfo info = new BindNodeInfo();
    			info.node = node;
    			info.index = i;
    			bind_nodes.add(info);
    		}
    	}*/
		
    	//句式匹配
    	ArrayList<ArrayList<SemanticNode>> list = matchSyntacticPatternsByChunk(nodes, ENV);
    	
    	//如果有被绑定的node,把他们放回到nodes list中取
    	/*if(bind_nodes.size()>0) {
    		for(ArrayList<SemanticNode> synt_nodes : list) {
    			for(BindNodeInfo info : bind_nodes) {
    				if(info.index==0 || info.index==1) {
    					synt_nodes.add(info.index, info.node);
    				}else{
    					int bNodeCount = 0;
    					for(int j=info.index;j>=0;j--) {
    						SemanticNode node = synt_nodes.get(j);
    						if(node.isBoundaryNode()) bNodeCount++;
    					}
    					
    					synt_nodes.add(info.index + bNodeCount, info.node);
    				}
    			}
    		}
    	}*/
    	
		return list;
    }
	
	
	protected ArrayList<ArrayList<SemanticNode>> matchSyntacticPatternsByChunk(ArrayList<SemanticNode> nodes, Environment ENV) {
    	ChunkIteratorImpl iterator = new ChunkIteratorImpl(nodes);
    	return matchSyntacticPatternsByChunk(nodes, ENV, iterator);
    }
    
    // 新增的代码，分chunk句式匹配，并构建笛卡尔积
    protected ArrayList<ArrayList<SemanticNode>> matchSyntacticPatternsByChunk(ArrayList<SemanticNode> nodes, Environment ENV, ChunkIteratorImpl iterator) {
    	ArrayList<ArrayList<SemanticNode>> matchedList = new ArrayList<ArrayList<SemanticNode>>();
    	
    	//这个什么意思?
    	matchedList.add(new ArrayList<SemanticNode>());
        
    	
    	while (iterator.hasNext()) {
        	ChunkInfos ci = iterator.next();
        	int start = ci.start;
        	int end = ci.end;
        	
        	ArrayList<ArrayList<SemanticNode>> tempAllList = new ArrayList<ArrayList<SemanticNode>>();
        	if(start>=0 && start<nodes.size() && end>=start && end+1<=nodes.size()) {
        		ArrayList<SemanticNode> subList = new ArrayList<SemanticNode>(nodes.subList(start, end+1));
        		ArrayList<ArrayList<SemanticNode>> tempList = matchSyntacticPatterns.matchSyntacticPatterns(subList, ENV);
        		
        		if (tempList != null) {
        			//笛卡尔积
        			//前面的是matchedList, 后面的是newmatched
    	        	for (ArrayList<SemanticNode> matched : matchedList) {
    	    			for (ArrayList<SemanticNode> newmatched : tempList) {
    	    				ArrayList<SemanticNode> tempNodes = new ArrayList<SemanticNode>();
    	    				for (SemanticNode sn : matched)
    	    					tempNodes.add(sn); //.clone()
    	    				for (SemanticNode sn : newmatched)
    	    					tempNodes.add(sn); //.clone()
    	    				tempAllList.add(tempNodes);
    	    			}
    	    		}
            	}
            	if (tempAllList != null && tempAllList.size() > 0)
            		matchedList = tempAllList;
        	}
        }
        if (matchedList == null || (matchedList.size() == 1 && matchedList.get(0).size() == 0))
        	return null;
        
        ArrayList<ArrayList<SemanticNode>> finalList = new ArrayList<ArrayList<SemanticNode>>(matchedList.size());
        for(ArrayList<SemanticNode> list : matchedList) {
        	ArrayList<SemanticNode> newList = new ArrayList<SemanticNode>();
        	for(SemanticNode node : list) {
        		newList.add(  NodeUtil.copyNode(node)); //.copy());
        	}
        	finalList.add(newList);
        }
        
        return finalList;
    }
    
}
