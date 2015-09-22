package com.myhexin.qparser.server.suggest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Document;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IfindSuggestionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static IndicatorSort aIndicatorSort_;

	static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
			.getLogger(IfindSuggestionServlet.class.getName());

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		aIndicatorSort_ = new IndicatorSort();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int sugWordSearchBeginLen = 10;
		int maxIndicateCount = 15;

		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("GBK");
		response.setContentType("text/html;charset=gbk");

		String sugWords = new String(request.getParameter("q").getBytes(
				"ISO-8859-1"), "UTF-8");
		sugWords = sugWords.trim();

		String anstring = "";
		String queryString = "";

		// test if the input is empty
		if (sugWords.isEmpty()) {
			response.getWriter().print(anstring);
			return;
		}
		try {
			// test if the sentence end with alpbet letters
			int alpbetPos = aIndicatorSort_.getAlphabetBegPos(sugWords);
			if ((alpbetPos >= 0) && (alpbetPos < sugWords.length())) {
				queryString = sugWords.substring(alpbetPos);
				Document tempDocument = aIndicatorSort_
						.formDocument(queryString);
				if (tempDocument != null) {
					ArrayList<String> resArrayList = aIndicatorSort_.indiSort(
							tempDocument, true);
					anstring = "";
					if (resArrayList.size() > 0) {
						for (int i = 0; i < maxIndicateCount
								&& i < resArrayList.size(); i++) {
							anstring += aIndicatorSort_.mergeSentence(sugWords,
									resArrayList.get(i), queryString);
						}
					}
				}
				response.getWriter().print(anstring);
				return;
			}

			// try to find some stop words
			int stopWordPos = aIndicatorSort_.getStopWordPos(sugWords);
			// if the sentence end with stop words return direct
			if ((stopWordPos + 1) >= sugWords.length()) {
				response.getWriter().print(anstring);
				return;
			}
			// test if the stop word pos is to long
			if (sugWords.length() - stopWordPos > sugWordSearchBeginLen) {
				// it is to long
				if (sugWords.length() <= sugWordSearchBeginLen) {
					queryString = sugWords;
				} else {
					queryString = sugWords.substring(sugWords.length()
							- sugWordSearchBeginLen);
				}
			} else {
				// it is in the test limitation
				queryString = sugWords.substring(stopWordPos + 1);
			}

			// try to find some words that may can skip by context first
			int skipLen = aIndicatorSort_.contextSkip(sugWords, queryString);
			// add back code here
			queryString = sugWords.substring(sugWords.length()
					- queryString.length() + skipLen);

			// now we entry the common area
			if (!aIndicatorSort_.seggerCandWords(queryString)) {
				response.getWriter().print(anstring);
				return;
			}
			boolean hasRes = false;
			while (hasRes == false) {
				Document tempDocument = aIndicatorSort_
						.formDocument(queryString);
				if (tempDocument != null) {
					ArrayList<String> resArrayList = aIndicatorSort_.indiSort(
							tempDocument, false);
					anstring = "";
					if (resArrayList.size() > 0) {
						hasRes = true;
						for (int i = 0; i < maxIndicateCount
								&& i < resArrayList.size(); i++) {
							anstring += aIndicatorSort_.mergeSentence(sugWords,
									resArrayList.get(i), queryString);
						}
					} else {
						if (queryString.length() > 1) {
							queryString = queryString.substring(aIndicatorSort_
									.getJumpStep());
						} else {
							break;
						}
					}
				} else {
					if (queryString.length() > 1) {
						queryString = queryString.substring(aIndicatorSort_
								.getJumpStep());
					} else {
						break;
					}
				}
			}
			response.getWriter().print(anstring);
		} catch (Exception e) {
			response.getWriter().print("");
		}
	}
}
