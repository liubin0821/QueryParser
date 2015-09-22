package com.myhexin.qparser.iterator;

public class ChunkInfos {
	public int start = -1;	// chunk开始位置，最左为0
	public int end = -1;	// chunk结束位置，左右为size-1
	
	public ChunkInfos() {
		
	}
	
	public ChunkInfos(int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	public String toString() {
		return "chunk-start: " + start + " end: " + end;
	}
}
