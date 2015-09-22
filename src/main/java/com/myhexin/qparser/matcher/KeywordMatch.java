package com.myhexin.qparser.matcher;

/**
 * 匹配关键字
 * @author wangjiajia
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KeywordMatch {

	/**
	 * 匹配关键字，支持最多拼接5个字符串进行匹配
	 * @param nodes 需要进行匹配的字符串集合
	 * @param dict 匹配规则
	 * @return
	 */
	public List<String[]> keywordMatcher(List<String> nodes,
			Map<String, String> dict) {
		String matcher = null;
		List<String[]> matchers = new ArrayList<String[]>();
		try {
			// 多个入参拼接，最多拼接5个字符串
			for (int i = 0; i < nodes.size(); i++) {
				String node = "";
				for (int j = 0; (j + i) < nodes.size(); j++) {
					String[] matchs = new String[2];
					// 遍历得到入参
					node += nodes.get(i + j);
					// 比较关键字和入参，如果相等，则取出关键字和入参
					matcher = dict.get(node);
					if (matcher != null) {
						matchs[0] = node;
						matchs[1] = matcher;
						matchers.add(matchs);
					}
					if (j > 4) {
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return matchers;
	}
}
