package com.myhexin.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.except.ExceptionUtil;
import com.myhexin.qparser.node.Environment;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.util.freemarker.CreateTemplate;

public class MacroIndustryParser {
	private static ParserPlugins parserPlugins;
	
	static{
		// parserPluginsMi配置于qparser_plugins_mi.xml文件
		parserPlugins = (ParserPlugins) ApplicationContextHelper.getBean("parserPluginsMi");
	}
	
	public static String queryParser(String query, String type) {
		try {
			if (query == null || query.trim().length() == 0 || query.length() > 50)
				return "";
				//return getJsonResult(query, type, "NO_PARSER", "", "");
			String splitWords = "_&_";
	        Parser.parser.setSplitWords(splitWords);
			Query q = new Query(query.toLowerCase(), type);
			ParseResult ret = Parser.parser.parse(q, parserPlugins.pre_plugins_, parserPlugins.plugins_, parserPlugins.post_plugins_);
			if (ret != null) {
				String result = getJsonResults(ret.qlist);
				if (result != null && result.length() > 0)
					return result;
					//return getJsonResult(query, type, "OK", ret.processLog.replace("\"", "'").replace("\n", "<br>"), result);
				else
					return "";
					//return getJsonResult(query, type, "NO_PARSER", "", "");
			} else {
				return "";
				//return getJsonResult(query, type, "ERROR", "result is null", "");
			}
		} catch (Exception e) {
			String info = ExceptionUtil.getStackTrace(e);
			return "";
			//return getJsonResult(query, type, "ERROR", info, "");
		}
	}
	
	private static String getJsonResults(ArrayList<ArrayList<SemanticNode>> qlist) {
		if (qlist == null || qlist.size() == 0)
			return null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < qlist.size(); i++) {
			if (qlist.get(i).size() > 0 ) {
				Environment listEnv = (Environment) qlist.get(i).get(0);
				if (listEnv.containsKey("jsonResultOfMacroIndustry")) {
					if (sb.length() > 0)
						sb.append(",");
					sb.append(listEnv.get("jsonResultOfMacroIndustry", String.class, false));
				}
			}
		}
		return sb.toString();
	}

	public static String getJsonResult(String query, String type, String state, String info, String result) {
		HashMap<String, Object> root = new LinkedHashMap<String, Object>();
		root.put("query", query);
		root.put("type", type);
		root.put("state", state);
		root.put("info", info);
		root.put("result", result);
        return CreateTemplate.createTemplate("result.ftl", root);
	}
	
	public static void main(String[] args) {
    	///*
    	System.out.println(MacroIndustryParser.queryParser("近期创阶段新高 前复权", ""));
    	System.out.println(MacroIndustryParser.queryParser("20130901 市盈率", ""));
    	System.out.println(MacroIndustryParser.queryParser("2013年9月 涨跌幅 前复权", ""));
    	System.out.println(MacroIndustryParser.queryParser("市盈率 市净率", ""));
    	System.out.println(MacroIndustryParser.queryParser("PE>10 收盘价>10元", ""));
    	System.out.println(MacroIndustryParser.queryParser("", ""));
    	System.out.println(MacroIndustryParser.queryParser("123", ""));
    	//*/
    	//Parser.queryParser("2013年9月 涨跌幅 前复权", "");
    	//System.out.println(Parser.queryParser("2013年9月 涨跌幅 前复权", ""));
    }
}
