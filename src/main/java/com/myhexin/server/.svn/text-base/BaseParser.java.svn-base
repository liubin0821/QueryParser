/**
 * 
 */
package com.myhexin.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.except.ExceptionUtil;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.PhraseParser;

/**
 * @author chenhao
 *
 */
public abstract class BaseParser {
	private static Logger logger_ = LoggerFactory.getLogger(BaseParser.class);
	protected boolean isDebug = false;
	protected static PhraseParser phraseParser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");

	public abstract ParserPlugins getPlugins();

	public abstract ParserPlugins getPluginsDebug();

	public String doParser(String... params) {
		try {
			Query query = beforeParser(params);
			ParseResult parseResult = parser(query);
			String result = afterParser(parseResult);
			return result;
		} catch (Exception e) {
			logger_.error(ExceptionUtil.getStackTrace(e));
			throw e;
		}
	}

	protected Query beforeParser(String... params) {
		String queryString = params[0];
		isDebug = Boolean.valueOf(params[1]);
		// no longer used?
		// String channel = params[2];
		// String logtime = params[3];
		Query q = new Query(queryString.toLowerCase());
		return q;
	}

	protected ParseResult parser(Query q) {
		try {
			ParserAnnotation annotation = new ParserAnnotation();
			annotation.setQueryText(q.text);
			annotation.setQuery(q);
			annotation.setQueryType(q.getType());
			annotation.setWriteLog(true);
			if (!isDebug) {
				return phraseParser.parse(annotation, getPlugins().pre_plugins_, getPlugins().plugins_,
						getPlugins().post_plugins_, null, null);
			} else {
				return phraseParser.parse(annotation, getPluginsDebug().pre_plugins_, getPluginsDebug().plugins_,
						getPluginsDebug().post_plugins_, null, null);
			}
		} catch (Exception e) {
			logger_.error(e.getMessage());
			throw e;
		}
	}

	protected abstract String afterParser(ParseResult parseResult);

}
