package com.myhexin.qparser.phrase;

import java.util.Calendar;

import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.Query.Type;

public class PhraseParserUtil {
	// 解析
	
	public static ParseResult parse(PhraseParser parser, String query, String qType, String domain, Calendar backtestTime) {
		return parse(parser, query, qType, domain, backtestTime, null);
	}
	
	public static ParseResult parse(PhraseParser parser, String query, String qType, String domain) {
		return parse(parser, query, qType, domain, null, null);
	}
	
	public static ParseResult parse(PhraseParser parser, String query, String qType, String domain, Calendar backtestTime, String stop_plugin_name) {
		Query q = new Query(query);
		if (qType == null) {
			q.setType(Type.STOCK);
		} else {
			Type theQType = null;
			try {
				theQType = Type.valueOf(Type.class, qType);
			} catch (Exception e) {
				theQType = Type.STOCK;
			}
			if (theQType == null) theQType = Type.STOCK;
			q.setType(theQType);
		}
		q.setDomain(domain);

		ParserAnnotation annotation = new ParserAnnotation();
		annotation.setQueryText(q.text);
		annotation.setQuery(q);
		annotation.setQueryType(q.getType());
		annotation.setWriteLog(false);
		annotation.setBacktestTime(backtestTime);

		ParseResult pr = parser.parse(annotation, stop_plugin_name);
		pr.standardQueries = ParseResult.toStandardQueries(q, pr.qlist, PhraseParserFactory.split_);
		pr.standardQueriesScore = ParseResult.getQueryScores(pr.qlist);
		return pr;
	}
}
