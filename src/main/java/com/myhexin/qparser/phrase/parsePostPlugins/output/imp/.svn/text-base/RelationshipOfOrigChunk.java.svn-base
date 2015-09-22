/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-5-13 下午1:42:09
 * @description:   	
 * 
 */
package com.myhexin.qparser.phrase.parsePostPlugins.output.imp;

import java.util.Iterator;
import java.util.List;

import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.iterator.SyntacticIteratorImpl;
import com.myhexin.qparser.node.BoundaryNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.parsePostPlugins.output.Output;

public class RelationshipOfOrigChunk extends Output  {
	protected static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(RelationshipOfOrigChunk.class.getName());

	//private static Environment  ENV;
	/*
	 * 问句回写
	 * 1、没有匹配上句式，则直接返回原句，这里返回null；
	 * 2、匹配上句式，则只将boundary内部的nodes回写。
	 */
    public static String relationshipOfOrigChunk(List<SemanticNode> nodes, String split) {
    	if (nodes == null || nodes.size() == 0 )
    		return null; 	
    	String result = null;
        int boundaryStart = 0;
        int boundaryEnd = nodes.size() - 1;
        Iterator iterator = new SyntacticIteratorImpl(nodes);
        if (!iterator.hasNext())
			return "";
    	while (iterator.hasNext()) {
    		BoundaryInfos boundaryInfos = (BoundaryInfos) iterator.next();
        	boundaryStart = boundaryInfos.bStart; // 句式左边界
        	boundaryEnd = boundaryInfos.bEnd; // 句式右边界
            String origChunk = transOrigChunkFromSyntactic(nodes, boundaryStart, boundaryEnd);
            if(result==null){
            	result = origChunk;
            }else{
            	result = result + split + origChunk;
            } 
    	}
    	
       return result;
    }
    
    /**
     * 用于得到该节点在nodes节点列表中的对应位置。
     * @param node
     * @param nodes
     * @return
     */
    private static int getPositionFromNodes(SemanticNode node,List<SemanticNode> nodes){
    	for(int i=0;i<nodes.size();i++){
    		if(node.equals(nodes.get(i))) return i;
    	}
    	return 0;
    }
    
    /**
     * 针对每一个句式来推算原始chunk问句信息
     */
    private static String transOrigChunkFromSyntactic(List<SemanticNode> nodes, int start, int end) {
        String origChunk = "";
        
    	for(int i=start;i<end;i++){
        	if(nodes.get(i) instanceof BoundaryNode) continue;
        	if(nodes.get(i) instanceof NumNode){
        		//针对是NumNode节点的需要找到对应的原问句信息 即通过oldStr。
        		NumNode numNode = (NumNode) nodes.get(i);
        		origChunk = origChunk + numNode.oldStr_;
        	}else if(nodes.get(i) instanceof FocusNode){
        		origChunk = origChunk + getOrigChunkFromFocusNode((FocusNode) nodes.get(i),nodes);
        	}else{
        		origChunk = origChunk + nodes.get(i).getText();
        	}
        }
        return origChunk;
    }
    
    /**
     * 针对FocusNode需要再等到前后绑定的信息
     * @param fNode
     * @param nodes
     * @return
     */
    private static String getOrigChunkFromFocusNode(FocusNode fNode,List<SemanticNode> nodes){
    	String origChunk = fNode.getText();
    	ClassNodeFacade index = fNode.getIndex();
    	if(index==null)
    		return origChunk;
    	if(!index.isBoundValueToThis())
    		return origChunk;
    	for(PropNodeFacade prop:index.getAllProps()){
    		SemanticNode node =prop.getValue();
    		if(node == null) continue;
    		int nodePosition = getPositionFromNodes(node,nodes);
    		int fNodePosition = getPositionFromNodes(fNode,nodes);
    		if(nodePosition > fNodePosition){
    			origChunk = origChunk + node.getText();
    		}else{
    			origChunk =  node.getText() + origChunk;
    		}
    		
    	}
    	return origChunk;
    }
 
}