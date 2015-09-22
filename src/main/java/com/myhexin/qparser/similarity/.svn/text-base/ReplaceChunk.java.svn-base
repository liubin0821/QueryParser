package com.myhexin.qparser.similarity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.myhexin.qparser.node.SemanticNode;

public class ReplaceChunk implements Serializable {
    private static final long serialVersionUID = 7874181560415798319L;
    
    public ReplaceChunk(List<SemanticNode> oldNodes) {
        this.oldNodes = new ArrayList<SemanticNode>(oldNodes);
    }
    
    public List<SemanticNode> getOldNodes(){
        return this.oldNodes;
    }
    
    /**
     * 当为了更好解析导致替换后的节点变得对用户不可读时，请设置此标志。设置后将使用原节点
     * 生成可读的表述方式。
     */
    public void useOldNodesForPubText() {
        needOldNodesForPubText = true;
    }
    
    public boolean needOldNodesForPubText() {
        return needOldNodesForPubText;
    }
    
    private List<SemanticNode> oldNodes;
    private boolean needOldNodesForPubText = false;
}
