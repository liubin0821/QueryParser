package com.myhexin.qparser.chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class ChunkQueryResult {
	private String queryStr;
	private ArrayList<Map<?, ?>> chunk;
	
	public ChunkQueryResult() {
		
	}
	
	public ChunkQueryResult(String queryStr) {
		this.queryStr = queryStr;
	}
	
	public ChunkQueryResult(String queryStr, ArrayList<Map<?, ?>> chunk) {
		this.queryStr = queryStr;
		this.chunk = chunk;
	}
	
	public String getQueryStr() {
		return queryStr;
	}
	
	public void setQueryStr(String queryStr) {
		this.queryStr = queryStr;
	}

	public ArrayList<Map<?, ?>> getChunk() {
		return chunk;
	}

	public void setChunk(ArrayList<Map<?, ?>> chunk) {
		this.chunk = chunk;
	}
	
	public String toString() {
		HashMap<String, Object> map = new HashMap<String, Object>();
        ArrayList<Map<?, ?>> chunks = chunk;
        map.put("query", queryStr);
        map.put("chunk", chunks);
        //JSONObject json = JSONObject.fromObject(map); 
		return new Gson().toJson(map);
	}
}
