package com.myhexin.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.ServletException;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TestWebPage {
	private static Configuration cfg_;
	private static HashSet<String> notExistsTempleteSet ;
    
    static {
        cfg_ = new Configuration();
        notExistsTempleteSet = new HashSet<String>();
		cfg_.setClassForTemplateLoading(Thread.currentThread().getClass(), "/webapp/template");
		cfg_.setObjectWrapper(new DefaultObjectWrapper());
    }
    
	public static void main(String[] args) {
		boolean bparser = true;
        String question = "同花顺新闻";
        String qType = "All";
        try {
			Template template =null;
	        Map<String, Object> root = new HashMap<String, Object>();
	        ParserItem item = new ParserItem(question, "ALL");
	        String ret = "";
	        if (bparser)
	        {
	        	ret = Parser.parserQuery(item);
	        }
	        template = cfg_.getTemplate("parser.ftl");
	        root.put("question", question);
	        root.put("qType", qType);
	//        root.put("result", ret);
	        root.put("logresult", ret);
	        root.put("representations", item.getStandardQueryList());
	        root.put("outputs", item.getStandardOutputList());
	        root.put("multResultLists", item.getMultResultLists());
	        root.put("IndexMultPossibilitys", item.getIndexMultPossibilitys());
	        root.put("jsonresults", item.getJsonResultList());
	        root.put("jsonResultsOfMacroIndustry", item.getJsonResultListOfMacroIndustry());
	        root.put("thematics", item.getThematicList());
	        root.put("lightparserresults", item.getLightParserResultList());
	        root.put("scores", item.getScores());
	        Writer out = new StringWriter();
	        try
	        {
	            template.process(root, out);
	        } catch (TemplateException e)
	        {
	            e.printStackTrace();
	        }
	        System.out.println(out.toString());	        
        }catch (Exception e) {
			e.printStackTrace();
		}
	}
}
