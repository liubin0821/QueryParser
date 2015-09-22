package com.myhexin.qparser.matcher;

import java.util.List;
import java.util.Map;

/**
 * 根据文件名和文件类型loader出dict，返回出list入参和dict的匹配结果
 * @author wangjiajia
 *
 */
public class Matcher {
	public MatchResult match(List<String> list,String fname,int type){
		MatchResult ret = new MatchResult(); 
		ret.setSuccess(false);
		KeywordDict dict = new KeywordDict(fname, type);
		KeywordMatch match = new KeywordMatch();
		List<String[]> matchers = match.keywordMatcher(list, dict.getKeywordDict());
		ret.setMatchers(matchers);
		ret.setNodes(list);
		if (!matchers.isEmpty()) {
			ret.setSuccess(true);
		}
		return ret;
	}
}
