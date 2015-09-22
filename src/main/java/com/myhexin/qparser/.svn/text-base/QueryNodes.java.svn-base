package com.myhexin.qparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.myhexin.qparser.chunk.Chunk;
import com.myhexin.qparser.define.EnumDef.Direction;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.NodeUtil;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.similarity.ChunkUtil;
import com.myhexin.qparser.similarity.IntIntPair;
import com.myhexin.qparser.similarity.ReplaceChunk;
import com.myhexin.qparser.similarity.SimpleQueryNodes;

public class QueryNodes {
    /** 解析出的语义节点 */
    protected ArrayList<SemanticNode> nodes = new ArrayList<SemanticNode>();
    private List<SemanticNode> backupNodes = null;
    
    public QueryNodes(){}
    public QueryNodes(ArrayList<SemanticNode> nodes) {
    	  this.nodes = nodes;
    	  this.backupNodes = new ArrayList<SemanticNode>(nodes);
    }
    
    public void setNodes(ArrayList<SemanticNode> nodes) {
        this.nodes = nodes;
    }

    public ArrayList<SemanticNode> getNodes() {
        return nodes;
    }
    
    
    //B-A句式转换代码
    
    public int size() { 
    	if(nodes!=null)
    		return nodes.size(); 
    	else
    		return 0;
    }
    
    
    public ArrayList<SemanticNode> subListOfBackNodes(int beg, int end){
        ChunkUtil.checkIndexInRangeAndFirstLessSecond(beg, end, backupNodes.size());
        ArrayList<SemanticNode> rtn = new ArrayList<SemanticNode>();
        for(int i = beg; i < end; i++){
            rtn.add(  NodeUtil.copyNode(backupNodes.get(i))); //.copy());
        }
        return rtn;
    }
    
    
    /**
     * 返回boundOfNodes区间内的节点在{@link Query.parseText}文本中的位置.
     * @param boundOfNodes
     * @return
     */
    public IntIntPair getBacknodeTextBound(IntIntPair boundOfNodes) {
        int preStrLen = 0;
        int strLen = 0;
        for(int i = 0; i<backupNodes.size() && i < boundOfNodes.first; i++){
            preStrLen += backupNodes.get(i).getText().length();
        }
        for(int i = boundOfNodes.first; i>=0 && i < boundOfNodes.second && i <backupNodes.size(); i++){
            strLen += backupNodes.get(i).getText().length();
        }       
        return new IntIntPair(preStrLen, preStrLen + strLen);
    }
    
    /**
     * 将beg, end之间的位置的节点回滚。<br>
     * 
     * @param pos 需要回滚的节点位置
     * @return chunk的节点在原始节点中的起始和截止索引
     * 
     */
    public IntIntPair getRollbackBoundsInRange(int beg, int end, Query.Type qType){
        SimpleQueryNodes originNodes = rollbackQueryNodesByBegEnd(beg, end, null, false, qType);
        return getRollbackBoundsOfSimpleQueryNodes(originNodes);
    }
    
    private SimpleQueryNodes rollbackQueryNodesByBegEnd(int beg, int end, NodeType[] nodeTypes, boolean toClone, Query.Type qType){
        List<NodeType> types = nodeTypes == null ? null : Arrays.asList(nodeTypes);
        ArrayList<SemanticNode> rollbackNodes = new ArrayList<SemanticNode>();
        SimpleQueryNodes rtn = new SimpleQueryNodes(rollbackNodes, qType);
        
        IntIntPair bounds = new IntIntPair(beg, end);
        
        int iNode = bounds.first;
        Set<SemanticNode> origNodes = new LinkedHashSet<SemanticNode>();
        Set<ReplaceChunk> rollbackedRplChks = new LinkedHashSet<ReplaceChunk>();
        while(iNode < bounds.second) {
            SemanticNode curNode = nodes.get(iNode);
            
            if(types != null && !types.contains(curNode.type) ){ //TODO && !rollbackedRplChks.contains(curNode.getReplacedFrom())) {
                rollbackNodes.add(toClone ?  NodeUtil.copyNode(curNode) : curNode); //.copy()
            } else {
                origNodes.clear();
                getOrigNodes(origNodes, curNode, rollbackedRplChks);
                while(iNode+1 < bounds.second &&
                        (types == null || types.contains(nodes.get(iNode+1).type))) {
                    getOrigNodes(origNodes, nodes.get(iNode+1), rollbackedRplChks);
                    iNode++;
                }

                List<SemanticNode> orderedOrigNodes = orderOrigNodes(origNodes);
                for(SemanticNode sn : orderedOrigNodes) {
                    rollbackNodes.add(toClone ? NodeUtil.copyNode(sn) : sn); //.copy()
                }
            }
            
            if(isBoundOfChunk(iNode, Direction.RIGHT)) {
                rtn.setChunkEnd(rtn.size()-1);
            }
            
            iNode++;
        }
        
        return rtn;
    }
    
    /**
     * 判断给定位置的节点是否是某个chunk的边界，可参数指定是左或右或任意边界。
     * <em>特别地：</em><br>
     * 1) pos越界时无论direction的值都返回true。<br>
     * 2) {@code get(pos).getChunk() == null}时返回true。<br>
     * 3) direction == null时与direction == Direction.BOTH结果一致
     */
    public boolean isBoundOfChunk(int pos, Direction direction) {
        if(pos < 0 || pos > nodes.size()-1) return true;
        Chunk curChk = nodes.get(pos).getChunk();
        if(curChk == null) return true;
        
        boolean isLeftBound = pos == 0 || curChk != nodes.get(pos-1).getChunk();
        boolean isRightBound = pos == nodes.size()-1  || curChk != nodes.get(pos+1).getChunk();
        if(direction == Direction.LEFT) return isLeftBound;
        if(direction == Direction.RIGHT) return isRightBound;
        return isLeftBound || isRightBound;
    }
    
    private void getOrigNodes(Set<SemanticNode> origNodes, SemanticNode node, Set<ReplaceChunk> rollbackedRplChks){
        /*for(SemanticNode sn : node.getAsOneNodes()) {
            getOrigNodes(origNodes, sn, rollbackedRplChks);
        }*/
        for(SemanticNode sn : node.getGroupNodes()) {
            getOrigNodes(origNodes, sn, rollbackedRplChks);
        }

        ReplaceChunk rplChk = null; //node.getReplacedFrom();
        if(rplChk == null) {
            origNodes.add(node);
        } else if(!rollbackedRplChks.contains(rplChk)){
     	   rollbackedRplChks.add(rplChk);
            for(SemanticNode sn : rplChk.getOldNodes()) {
                getOrigNodes(origNodes, sn, rollbackedRplChks);
            }
        } else {
     	   ;//this node has been roll back
        }
    }
    
    private IntIntPair getRollbackBoundsOfSimpleQueryNodes(SimpleQueryNodes originNodes){
        Set<SemanticNode> trueOriginNodes = new LinkedHashSet<SemanticNode>();
        for(SemanticNode sn : originNodes.nodes){
            if(backupNodes.indexOf(sn) >= 0){
                trueOriginNodes.add(sn);
            }
        }
        List<SemanticNode> orderedNodes = orderOrigNodes(trueOriginNodes);
        assert(orderedNodes.size()>0);
        int sIdx = backupNodes.indexOf(orderedNodes.get(0));
        int eIdx = backupNodes.indexOf(orderedNodes.get(orderedNodes.size() - 1))+1;
        return new IntIntPair(sIdx, eIdx);
    }
    
    private List<SemanticNode> orderOrigNodes(Set<SemanticNode> origNodes) {
        List<SemanticNode> rtn = new ArrayList<SemanticNode>(origNodes);
        
        Collections.sort(rtn, new Comparator<SemanticNode>() {
             @Override
             public int compare(SemanticNode o1, SemanticNode o2) {
                 Integer indO1 = backupNodes.indexOf(o1);
                 Integer indO2 = backupNodes.indexOf(o2);
                 assert(indO1 >= 0 && indO2 >=0);
                 return indO1.compareTo(indO2);
             }
        });
        
        return rtn;
    }
    
}
