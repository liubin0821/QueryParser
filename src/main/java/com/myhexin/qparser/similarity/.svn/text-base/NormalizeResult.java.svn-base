package com.myhexin.qparser.similarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.myhexin.qparser.util.Pair;

public class NormalizeResult {
	public void assignResult(NormalizeTool normalizeTool) {
		this.text = normalizeTool.text() ;
		this.npattern = normalizeTool.nPattern() ;
		
		/** 因为改用将所有的chunk一起分词，下面的代码暂时注释 */
		if(normalizeTool.segValueList() != null) {
			List<HashMap<String, Double>> segMap = new ArrayList<HashMap<String,Double>>() ;
			for(Pair<String, Double> pair : normalizeTool.segValueList()){
				HashMap<String, Double> map = new HashMap<String, Double>() ;
				map.put(pair.first, pair.second) ;
				segMap.add(map) ;
			}
			this.segger = segMap ;
		}
		this.indexes = normalizeTool.indexList() ;
		this.nodelist = normalizeTool.pNodeList() ;
		this.keyWords = normalizeTool.getKeyWords();
		this.qtype = normalizeTool.getQType();
	}
	
	public static List<NormalizeResult> assignResultForAccurate(NormalizeTool normalizeTool){
		List<NormalizeResult> rltList = new ArrayList<NormalizeResult>() ;
		for(String npattern : normalizeTool.nPattern().split(ChunkSimilarity.SPLIT_TAG)){
			NormalizeResult rlt = new NormalizeResult() ;
			rlt.npattern = npattern ;
			rltList.add(rlt) ;
		}
		return rltList ;
	}
	
	public String text ;
	public String npattern ;
	public List<HashMap<String,Double>> segger ;
	public List<String> indexes ;
	public List<PatternNode> nodelist ;
	public List<String> keyWords;
	public String qtype;
	public Boolean isSuccess = true;
}
