package com.myhexin.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

public class MacroIndustryParserServlet extends TemplateServlet{
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(MacroIndustryParserServlet.class.getName());


	private static final long serialVersionUID = 1L;
    
    @Override
    public void init() throws ServletException
    {
        super.init();

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	super.doGet(request, response);
    	
        response.setContentType("application/json; charset=utf-8");

		String query = request.getParameter("q");
		query = query == null ? request.getParameter("query") : query;
		String type = request.getParameter("qType");
        if (query == null) query = "";
        if (type == null) type = "hghy";
        try {
        	OutputStream os = response.getOutputStream();
            String result = MacroIndustryParser.queryParser(query, type);
            os.write(result.getBytes("utf-8"));
            os.close();
        }catch (Exception e) {
			e.printStackTrace();
		}
    }
}
