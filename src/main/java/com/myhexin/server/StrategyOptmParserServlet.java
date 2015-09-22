/**
 *
 */
package com.myhexin.server;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myhexin.qparser.except.ExceptionUtil;
import com.myhexin.qparser.strategyoptm.StrategyOptmParser;

/**
 * @author chenhao Add new servlet for stratrgy optimization
 *
 */
public class StrategyOptmParserServlet extends HttpServlet {
	private static Logger logger_ = LoggerFactory.getLogger(StrategyOptmParserServlet.class.getName());
	private static final long serialVersionUID = 4142622313343312230L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String result = "";
		OutputStream outputStream = null;
		try {
			//if (request.getParameter("qid") != null)
			//	logger_.info("qid=" + request.getParameter("qid"));

			request.setCharacterEncoding("utf-8");

			String query = request.getParameter("q");
			String debug = request.getParameter("debug");
			String channel = request.getParameter("channel");
			String logtime = request.getParameter("logtime");
			StrategyOptmParser strategyOptmParser = new StrategyOptmParser();
			result = strategyOptmParser.doParser(query, debug, channel, logtime);

			response.setStatus(HttpStatus.SC_OK);

		} catch (Exception e) {
			result = String.format("{\"exception\":\"%s\"}", e.getMessage());
			logger_.error(ExceptionUtil.getStackTrace(e));

			response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		} finally {
			outputStream = response.getOutputStream();
			response.setContentType("application/json;charset=utf-8");
			outputStream.write(result.getBytes("utf-8"));
			outputStream.close();

		}
	}
}
