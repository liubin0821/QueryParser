package com.myhexin.qparser.similarity;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.chunk.Chunk;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.SemanticNode;

public class ChunkUtil {
    public static boolean isSameChunk(List<SemanticNode> nodes) {
        if(nodes.size() == 0) return true;
        Chunk chk = nodes.get(0).getChunk();
        for(int i = 1; i < nodes.size(); i++) {
            if(nodes.get(i).getChunk() != chk) return false;
        }
        return chk == null;// || chk.hasAllInSameChunk(nodes);
    }
    
    public static boolean isSameChunk(SemanticNode... nodes ) {
        List<SemanticNode> snodes = new ArrayList<SemanticNode>(nodes.length);
        for(SemanticNode sn : nodes) { snodes.add(sn); }
        return isSameChunk(snodes);
    }
    
    /*public static boolean isSameChunk(TreeNode... nodes) {
        List<SemanticNode> snodes = new ArrayList<SemanticNode>(nodes.length);
        for (TreeNode sn : nodes) {
            try {
                SemanticNode realNode = TreeBuilderUtil.getRealNodeFromTreeNode(sn);
                snodes.add(realNode);
            } catch (QPException e) {
                return false;
            }
        }
        return isSameChunk(snodes);
    }*/
    
    /*public static void assertSameChunk(List<SemanticNode> nodes) throws NotSameChunkException {
        if(!isSameChunk(nodes)) {
            throw new NotSameChunkException();
        }
    }*/
    
    /*public static void assertSameChunk(SemanticNode...nodes) throws NotSameChunkException {
        if(!isSameChunk(nodes)) {
            throw new NotSameChunkException();
        }
    }*/
    
    public static  void checkIndexInRangeAndNotEqual(int src, int dest, int size) {
        if(src < 0 || dest < 0 || src >= size || dest >= size || src == dest) {
            throw new IllegalArgumentException(String.format(
                    "bad node index src=%d, dest=%d; expected range [0:%d) and not equal",
                    src, dest, size));
        }
    }
    
    public static void checkIndexInRangeAndFirstLessSecond(int beg, int end, int size) {
        if(beg < 0 || end < 0 || beg >= size || end > size || beg >= end) {
            throw new IllegalArgumentException(String.format(
                    "bad node index src=%d, dest=%d; expected range [0:%d] and no greater",
                    beg, end, size));
        }
    }   
    
    
    /*public static void assertChunkSanity(QueryNodes nodes) {
        int nNodeWithChk = 0; //有chunk的节点数
        int nNodeInChunk = 0; //所有chunk含有的节点数
        for(int i = 0; i < nodes.size(); i++) {
            SemanticNode sn = nodes.get(i);
            if(sn.getChunk() != null) {
                assert(sn.getChunk().hasNode(sn)) : String.format("node [%s] not in its chunk", sn.text);
                nNodeWithChk++;
                if(nodes.isBoundOfChunk(i, Direction.RIGHT)) {
                    nNodeInChunk += sn.getChunk().countNode();
                }
            }
        }
        assert(nNodeInChunk == nNodeWithChk) : String.format(
                "nNodeInChunk=%d but nNodeWithChk=%d", nNodeInChunk, nNodeWithChk);
    }*/
    
    /**
     * 检查新的节点，或不存在，或在指定区间内
     * @throws UnexpectedException 节点存在，且不在指定区间内
     */
    /*public static void checkNodesNotExistOrInRange(QueryNodes nodes, int beg, int end, List<SemanticNode> newNodes)
    throws UnexpectedException {
        for(SemanticNode sn : newNodes) {
            int ind = nodes.indexOf(sn);
            if(ind >=0 && !(ind >= beg && ind < end)) {
                throw new UnexpectedException("node [%s] exists at %d, expected in [%d,%d)",
                        sn.text, ind, beg, end);
            }
        }
    }*/
    
    /**
     * 检查节点不存在
     * @throws UnexpectedException 节点已经存在
     */
    /*public static void checkNodeNotExist(QueryNodes nodes, SemanticNode node)
    throws UnexpectedException {
        int ind = nodes.indexOf(node);
        if(ind >= 0) {
            throw new UnexpectedException("node [%s] exists at %d", node.text, ind);
        }
    }*/

    public static void checkIndexInRange(int pos, int size) {
        if(pos<0||pos>=size){
            throw new IllegalArgumentException(String.format(
                    "bad node index pos=%d; expected range [0:%d] and no greater",
                    pos, size));
        }
    }
}
