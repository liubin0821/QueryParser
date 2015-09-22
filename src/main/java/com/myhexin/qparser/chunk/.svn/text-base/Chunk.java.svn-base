package com.myhexin.qparser.chunk;

import com.myhexin.qparser.define.EnumDef.ChunkType;
import com.myhexin.qparser.node.SemanticNode;

public class Chunk {
    public Chunk() {
        
    }
    
    public void setType(ChunkType type) { this.type_ = type; }
    public ChunkType getType() { return type_; }
    
    private ChunkType type_ = ChunkType.UNKNOWN;
    
    public Chunk copy() {
    	Chunk c = new Chunk();
    	c.type_ = type_;
    	return c;
    }
    
    /**
     * 所有{@link SemanticNode}在构造时属于此chunk
     */
    public static final Chunk NaC = new Chunk();
}
