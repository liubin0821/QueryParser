package com.myhexin.qparser.iterator;

import com.myhexin.qparser.phrase.PhraseInfo;
import com.myhexin.qparser.phrase.SyntacticSemantic.Syntactic.SyntacticPattern;

public class BoundaryInfos {
	public int start = -1;	// 上一句式结束位置，最左为0
	public int bStart = -1;	// 本句式开始位置
	public int bEnd = -1;	// 本句式结束位置
	public int end = -1;	// 下一句是开始位置，左右为size-1
	public String syntacticPatternId = "";
	
	public BoundaryInfos() {
		
	}
	
	public BoundaryInfos(int start, int bStart, int bEnd, int end) {
		this.start = start;
		this.bStart = bStart;
		this.bEnd = bEnd;
		this.end = end;
	}
	
	public final SyntacticPattern getSyntacticPattern(){
		if(syntacticPatternId == "")
			return null;		
		return PhraseInfo.getSyntacticPattern(syntacticPatternId);
	}
}
