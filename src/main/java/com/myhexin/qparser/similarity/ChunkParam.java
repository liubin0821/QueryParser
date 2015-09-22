package com.myhexin.qparser.similarity;

import com.myhexin.qparser.define.MiscDef;
import com.myhexin.qparser.define.EnumDef.ChunkType;


/**
 * CHUNK是用何种方式生成的
 */


public class ChunkParam {
	
	public enum ChunkBy {
	    UNKNOWN,
	    /** 特定标记，见{@link MiscDef#USER_CHUNK_MARK} */
	    SPEC_MARK, PATTERN, CHUNK_DICT, TECH_PARSER, CALC_PARSER, LEFTOVER_BY_CONFLICT,
	}
	
    public int begin;
    public int end;
    public ChunkBy by;
    public ChunkType chkType = ChunkType.UNKNOWN;
    public boolean fixLeft = false, fixRight = false, fixWhole = false;
    public OnConflict onConflict = OnConflict.FAIL;
    public FailReason failReason = FailReason.UNKNOWN;
    public double score = -1;
    
    public ChunkParam(int begin, int end, ChunkBy by) {
        this.begin = begin;
        this.end = end;
        this.by = by;
    }
    
    public static enum OnConflict {
        FAIL, 
        OVERWRITE,
    }
    
    public static enum FailReason {
        /** 某节点属于一个被锁定的边界 */
        BOUND_FIXED,
        /** 拆散一个整体被锁定的chunk */
        WHOLE_FIXED,
        /** 从大chunk中切出小chunk */
        BREAK_A_LOGNER_CHUNK,
        /** 与另一个冲突块冲突（目前尚不支持此种冲突） */
        CONFLICT_WITH_CONFLICT_CHUNK,
        UNKNOWN,
    }
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Begin:[").append(begin).append("] End:[").append(end).append("]").append("\n");
        sb.append("ChunkBy:").append(by.name()).append("\n");
        sb.append("FixLeft:[").append(fixLeft).append("] FixRight:[").append(fixRight).append("] FixWhole:[").append(fixWhole).append("]\n");
        sb.append("OnConflict:").append(onConflict.name()).append("\n");
        sb.append("FailReason:").append(failReason).append("\n");
        sb.append("Score:").append(score).append("\n");
        sb.append("Type:").append(chkType.name());
        return sb.toString();
    }
}
