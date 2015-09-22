package com.myhexin.qparser.similarity;

import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.QueryNodes;
import com.myhexin.qparser.define.EnumDef.Direction;
import com.myhexin.qparser.node.SemanticNode;

/**
 * 本类记录语义节点信息。注意它没有chunk相关操作。有个例外：由于本类将其所有节点
 * 视为一个chunk，因此chunk边界判断相关的方法仍可用。
 */
public class SimpleQueryNodes extends QueryNodes {
    private static final long serialVersionUID = 8082720825610389678L;

    public SimpleQueryNodes(ArrayList<SemanticNode> nodes, Query.Type qtype) {
    	 this.nodes = nodes;
        this.qtype = qtype;
    }

    public Query.Type getQType(){ return qtype; }

    /**
     * 将pos处设为一个chunk的结尾
     */
    public void setChunkEnd(int pos) {
        assert(pos >= 0 && pos <= nodes.size()-1) : String.format("bad pos %d", pos);
        if(!endChkPos.contains(pos)) {
            endChkPos.add(pos);
        }
    }
    
    /**
     * 在{@code pos}处增加一个节点
     * @param sameChunkWithLeftNode 仅为保持与父类接口一致，没使用
     */
    public void add(int pos, SemanticNode node, boolean sameChunkWithLeftNode) {
        nodes.add(pos, node);
    }
    
    /**
     * 在{@code pos}处增加一序列节点
     * @param sameChunkWithLeftNode 仅为保持与父类接口一致，没使用
     */
    public void addAll(int pos, List<SemanticNode> newNodes, boolean sameChunkWithLeftNode) {
        nodes.addAll(pos, newNodes);
    }

    /**
     * 将src节点合并到dest节点成为一个节点。src节点的text拼接到dest前面或后面（取决于参数after）后，然后彻底消失。
     */
    public void mergeAsOne(int src, int dest, boolean after){
        mergeAsOne(src, dest, after, "");
    }
    
    /**
     * 与{@link #mergeAsOne(int, int, boolean)}同，但拼接时指定分割符。
     */
    public void mergeAsOne(int src, int dest, boolean after, String delimiter){
        ChunkUtil.checkIndexInRangeAndNotEqual(src, dest, nodes.size());
        
        SemanticNode snSrc = nodes.get(src), snDest = nodes.get(dest);
        if(after) { snDest.setText(snDest.getText() + delimiter + snSrc.getText()); }
        else { snDest.setText(snSrc.getText() + delimiter + snDest.getText()); }
        
        nodes.remove(src);
    }
    
    /**
     * 将src节点加入到dest节点。src节点保存到dest中并从序列中移除，dest本身不发生任何其他变化。
     */
    public void mergeAsGroup(int src, int dest) {
        ChunkUtil.checkIndexInRangeAndNotEqual(src, dest, nodes.size());
        
        SemanticNode snSrc = nodes.get(src), snDest = nodes.get(dest);
        snDest.addGroupNode(snSrc);
        nodes.remove(src);
    }

    /**
     * 将[beg:end)范围内的节点替换成给定节点。
     * @return 总是返回null。仅为保持父类接口一致
     */
    public ReplaceChunk replace(int beg, int end, List<SemanticNode> newNodes) {
        ChunkUtil.checkIndexInRangeAndFirstLessSecond(beg, end, nodes.size());

        int change = newNodes.size() - (end - beg);
        int commonLen = (change > 0 ? (end - beg) : newNodes.size());
        for (int i = 0; i < commonLen; i++) {
            nodes.set(beg + i, newNodes.get(i));
        }
        if (change > 0) {
            nodes.addAll(end, newNodes.subList(commonLen, newNodes.size()));
        }
        while (change < 0) {
            nodes.remove(beg+commonLen); change++;
        }
        return null;
    }
    
    /**
     * 将区间[beg,end)内的节点替换成指定结点。效果与{@link #replace(int, int, List)}相同
     */
    public ReplaceChunk replace(int beg, int end, SemanticNode newNode) {
        List<SemanticNode> newNodes = new ArrayList<SemanticNode>(1);
        newNodes.add(newNode);
        return replace(beg, end, newNodes);
    }
   
    /**
     * 将指定位置节点替换成指定新节点
     */
    public ReplaceChunk replace(int pos, SemanticNode newNode) {
        List<SemanticNode> oneNode = new ArrayList<SemanticNode>(1);
        oneNode.add(newNode);
        return replace(pos, pos + 1, oneNode);
    }
    
    /**
     * 将指定位置节点替换成一序列新节点
     */
    public ReplaceChunk replace(int pos, List<SemanticNode> newNodes) {
        return replace(pos, pos+1, newNodes);
    }
    
    /**
     * 交换两个节点的位置。
     * @return 总是返回true。仅为与父类保持接口一致。
     */
    public boolean swapNode(int src, int dest) {
        ChunkUtil.checkIndexInRangeAndNotEqual(src, dest, nodes.size());
        SemanticNode snSrc = nodes.get(src);
        SemanticNode snDest = nodes.get(dest);
        
        nodes.set(src, snDest);
        nodes.set(dest, snSrc);
        
        return true;
    }
    
   
    /**
     * 判断给定位置的节点是否是某个chunk的边界，可参数指定是左或右或任意边界。 特别地：
     * 1) pos越界时无论direction的值都返回true。
     * 2) direction == null时与direction == Direction.BOTH结果一致
     */
    public boolean isBoundOfChunk(int pos, Direction direction) {
        boolean isLeft  = pos <= 0;
        boolean isRight = pos >= nodes.size()-1;
        for(int endPos : endChkPos) {
            if(pos == endPos) isRight = true;
            else if(pos == endPos+1) isLeft = true;
        }
        if(direction == Direction.LEFT) return isLeft;
        if(direction == Direction.RIGHT) return isRight;
        return isLeft || isRight;
    }

    
    private Query.Type qtype;
    private ArrayList<Integer> endChkPos = new ArrayList<Integer>(4);
}
