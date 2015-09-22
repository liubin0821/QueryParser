package com.myhexin.qparser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;

import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.PhraseParser;
import com.myhexin.qparser.phrase.util.CommonUtil;
import com.myhexin.qparser.phrase.util.Traditional2simplified;
import com.myhexin.qparser.tokenize.Tokenizer;

public class TestNormalizeNum {
	public static PhraseParser parser;// = new PhraseParser("./conf/qparser.conf", "./data");
	static{
		// 加载xml文件，初始spring容器
		ApplicationContextHelper.loadApplicationContext();

		// phraseParser配置于qphrase.xml文件
		parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
		
	}
	public static String getNormalizeString(String queryString) {
		Query query = new Query(queryString);
		query.getParseResult().standardQueries = new ArrayList<String>();
		try {
			ArrayList<SemanticNode> nodes = new ArrayList<SemanticNode>();
			textNormalize(query);
			nodes = tokenize(query, query.text);
			String content = getSentence(nodes);
			//System.out.println(content);
			content = normalizeNum(content);
			return content;
		} catch (Exception e) {
			return queryString;
		}
	}

	/**
	 * 字符串的标准化
	 * 
	 * @param Query
	 */
	private static void textNormalize(Query query) {
		// 繁体转简体
		query.text = Traditional2simplified.toSimplified(query.text);
		// 全角转半角
		query.text = CommonUtil.toLowerAndHalf(query.text);
	}

	/**
	 * 单分词可能性的分词模块
	 * 
	 * @param query
	 */
	private static ArrayList<SemanticNode> tokenize(Query query,String ltpRlt) {
		ArrayList<SemanticNode> nodes = new ArrayList<SemanticNode>();
		if (query.text.length() == 0)
			return null;
		//new Tokenizer(query).tokenize(new Environment(),ltpRlt);
		
		ParserAnnotation annotation = new ParserAnnotation();
    	annotation.setQueryText( ltpRlt);
    	annotation.setQueryType( query.getType());
    	annotation.setQuery( query);
		Tokenizer.tokenize(annotation);
		
		//if (!query.hasFatalError())
		nodes = annotation.getNodes();
		return nodes;
	}
	
	// 获得分词及时间解析和数值解析的结果
	private static String getSentence(ArrayList<SemanticNode> nodes) {
        if (nodes == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (SemanticNode sn : nodes) {
            sb.append(sn.getText());
        }
        return sb.toString().trim();
    }
	
	// 归一化数值
	private static String normalizeNum(String content) {
		Pattern YEAR_MONTH_DAY_WITH_SPLIT_SIGN = Pattern
	            .compile("(199\\d|200\\d|201\\d)[年\\-\\./、—]?" +
	            		"(1[0-2]|0?[1-9])[月\\-\\./、—]" +
	            		"([12]\\d|0?[1-9]|3[01])[日号]?");
		Matcher matcher = YEAR_MONTH_DAY_WITH_SPLIT_SIGN.matcher(content);  
		while (matcher.find()) {
			content = content.replace(matcher.group(0), "DATE");
		}
		Pattern pattern = Pattern.compile("(\\d+)(\\.\\d+)?");
		matcher = pattern.matcher(content);
		StringBuilder sb = new StringBuilder();
		int last = 0;
		while (matcher.find()) {
			double num = Double.parseDouble(matcher.group(0));
			sb.append(content.substring(last, matcher.start()));
			last = matcher.end();
			if (num>=1000) 
				sb.append("BIG_NUM");
			else 
				sb.append("SMALL_NUM");
		}
		sb.append(content.substring(last, content.length()));
		return sb.toString();
	}
	
	public static void main(String[] args) {
		String queryString = "8月1日股权登记日，流通盘<1亿股";
		//System.out.println(normalizeNum(queryString));
		System.out.println(getNormalizeString(queryString));
	}
}