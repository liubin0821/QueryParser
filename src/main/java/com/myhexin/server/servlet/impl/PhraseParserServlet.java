package com.myhexin.server.servlet.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.myhexin.qparser.Query;
import com.myhexin.server.service.Service;
import com.myhexin.server.servlet.ParserAbstractServlet;
import com.myhexin.server.vo.impl.ParserParam;
import com.myhexin.server.vo.impl.ParserResult;

public class PhraseParserServlet extends ParserAbstractServlet {

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		OutputStream os = response.getOutputStream();

		String query = request.getParameter("q");
		query = query == null ? request.getParameter("query") : query;
		String type = request.getParameter("type");
		type = type == null ? request.getParameter("qType") : type;

		if (query != null && query.length() > 0) {

			ParserParam param = new ParserParam();
			param.setQueryText(query);
			param.setQueryType(type);

			ParserResult result = (ParserResult) service
					.serve(param);

			String strResult = (String) result.getResult();
			os.write(strResult.getBytes());
		}
		
		os.close();
	}
}
