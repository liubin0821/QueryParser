package com.myhexin.qparser.matcher;

/**
 * 把关系词与关键词用map中的key和value关联起来
 * @author wangjiajia
 */
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class KeywordDict {
	private KeywordDictLoader keywordDictLoader = new KeywordDictLoader();
	private Map<String, String> keywordDict = new HashMap<String, String>();

	public KeywordDict() {

	}

	public KeywordDict(String fname, int type) {
		keywordDict = keywordDictLoader.keywordDictLoader(fname, type);
	}

	public void AddKeywordDict(String fname, int type,
			Map<String, String> keywordDict) {
		this.keywordDict = keywordDict;
		Map<String, String> addKeywordDict = keywordDictLoader
				.keywordDictLoader(fname, type);
		keywordDict.putAll(addKeywordDict);
	}

	public void AddOrUpdateKeywordDict(String relation, String keyword,
			Map<String, String> keywordDict) {
		this.keywordDict = keywordDict;
		keywordDict.put(keyword, relation);
	}

	public void DeleteKeywordDictForKeyword(String keyword,
			Map<String, String> keywordDict) {
		this.keywordDict = keywordDict;
		keywordDict.remove(keyword);
	}

	public void ClearKeywordDict(Map<String, String> keywordDict) {
		this.keywordDict = keywordDict;
		keywordDict.clear();
	}

	public Map<String, String> getKeywordDict() {
		return keywordDict;
	}

	public void setKeywordDict(Map<String, String> keywordDict) {
		this.keywordDict = keywordDict;
	}
}
