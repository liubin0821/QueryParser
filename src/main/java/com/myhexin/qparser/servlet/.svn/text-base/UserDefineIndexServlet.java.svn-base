package com.myhexin.qparser.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.ExceptionUtil;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.PhraseParser;
import com.myhexin.qparser.phrase.PhraseParserFactory;
import com.myhexin.qparser.phrase.PhraseParserUtil;

public class UserDefineIndexServlet  extends HttpServlet {
	private static Logger logger_ = LoggerFactory.getLogger(UserDefineIndexServlet.class.getName());
	private static final long serialVersionUID = 4142622313343312230L;
	private final static PhraseParser parser = PhraseParserFactory.createPhraseParser(PhraseParserFactory.ParserName_Condition);
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doGet(request, response);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		OutputStream outputStream = null;
		
		String jsonResult = null;
		try {
			//if (request.getParameter("qid") != null)
			//	logger_.info("qid=" + request.getParameter("qid"));
			request.setCharacterEncoding("utf-8");

			String index = request.getParameter("index");
			String q = request.getParameter("q");
			String unit = request.getParameter("unit");
			ParseResult pr = PhraseParserUtil.parse(parser, q, "ALL", null);
			
			String result = null;
			if(pr!=null && pr.qlist!=null && pr.qlist.size()>0) {
				result = getParserResult(pr.qlist.get(0));
			}
			Map<String, String> dataResult = new HashMap<String, String>();
			dataResult.put("index_name", index!=null?index:"");
			dataResult.put("query", q!=null?q:"");
			dataResult.put("unit", unit!=null?unit:"UNKNOWN");
			dataResult.put("result", result!=null?result:"");
			jsonResult = new Gson().toJson(dataResult);

		} catch (Exception e) {
			jsonResult = String.format("{\"exception\":\"%s\"}", e.getMessage());
			logger_.error(ExceptionUtil.getStackTrace(e));

			response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		} finally {
			outputStream = response.getOutputStream();
			response.setContentType("application/json;charset=utf-8");
			response.setStatus(HttpStatus.SC_OK);
			outputStream.write(jsonResult.getBytes("utf-8"));
			outputStream.close();

		}
	}
	
	private String getParserResult(ArrayList<SemanticNode> nodes) {
		StringBuffer buf = new StringBuffer();
		
		ArrayList<SemanticNode> new_nodes = new ArrayList<SemanticNode>();
		for(SemanticNode node : nodes) {
			if(node.isBoundaryNode() || node.getType() == NodeType.ENV) continue;
			new_nodes.add(node);
		}
		
		for(int i=0;i<new_nodes.size();i++) {
			
			SemanticNode node = new_nodes.get(i);
			if(node.isFocusNode()) {
				FocusNode fNode = (FocusNode) node;
				if(fNode.hasIndex() && fNode.getIndex()!=null) {
					buf.append(fNode.getIndex().getText()).append("#").append(node.getType());
				}	else{
					buf.append(fNode.getText()).append("#").append(node.getType());
				}
			}else{
				buf.append(node.getText()).append("#").append(node.getType());
			}
			if(i<new_nodes.size()-1)
				buf.append("__#__");
		}
		
		return buf.toString();
	}

}
