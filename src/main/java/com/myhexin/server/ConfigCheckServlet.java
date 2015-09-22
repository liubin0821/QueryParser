package com.myhexin.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

import org.w3c.dom.*;

import com.google.gson.Gson;
import com.myhexin.server.ConfigCheck.Result;

public class ConfigCheckServlet extends TemplateServlet {
	private static final long serialVersionUID = 1L;
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(ConfigCheckServlet.class.getName());


	@Override
	public void init() throws ServletException {
		super.init();
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		super.doGet(request, response);
		logger_.warn("doGet");
		//doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request, response);
		
		logger_.info("doPost");
		
		response.setContentType("application/json; charset=utf-8");
		
		try {
			// 解析对方发来的xml数据，获得EventID节点的值
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document xmldoc = db.parse(request.getInputStream());
			xmldoc.setXmlStandalone(true);
			/*
	        Document doc = ConfigCheck.checkConfig(xmldoc);
			StringWriter writer = new StringWriter();
	        javax.xml.transform.TransformerFactory.newInstance().newTransformer().transform(new javax.xml.transform.dom.DOMSource(doc), new javax.xml.transform.stream.StreamResult(writer));
			String xmlData = writer.toString();
			logger_.warn(xmlData);
			ServletOutputStream os = response.getOutputStream();
			os.write(xmlData.getBytes("utf-8"));
			os.flush();
			os.close();
			*/
			List<Result> results = ConfigCheck.checkConfigList(xmldoc);
			OutputStream os = response.getOutputStream();
            Gson json = new Gson();
            os.write(json.toJson(results).getBytes("utf-8"));
            os.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			logger_.error(e.getMessage());
		} 
	}
}
