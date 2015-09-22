package com.myhexin.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.resource.Resource;
import com.myhexin.qparser.resource.ResourceInst;
import com.myhexin.qparser.resource.model.RefCodeInfo;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class ParserServlet extends TemplateServlet{
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(Parser.class.getName());

    
    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	super.doGet(request, response);
    	
        
        //if(request.getParameter("qid")!=null)
        //	logger_.info("qid="+request.getParameter("qid"));
        

        response.setContentType("text/html");     

        boolean bparser = true;
        String query = request.getParameter("q");
        query = query == null ? request.getParameter("question") : query;
        String breload = request.getParameter("reload");
        String qType = request.getParameter("qType");
        String logtime = request.getParameter("logtime");
        String detail = request.getParameter("detail");
        String skip = request.getParameter("skip");
        if (breload != null && breload.equals("1")) {
        	bparser = false;
        	String error = null;
        	try {
        		Resource.reloadData();
        		PrintWriter out = response.getWriter(); 
        		out.write("<pre>");
        		out.write("**********************************\n\n");
        		out.write("            SUCCESS !!               \n\n");
        		out.write("**********************************\n\n");
        		out.write("config reload from db success !!");
        		out.write("</pre>");
        		return;
        	} catch (DataConfException e) {
    			logger_.error("conf reload failed...");
    			logger_.error(e.getMessage());
    			error =  "conf reload failed...\n" + e.getMessage();
    		} catch (Exception e) {
    			logger_.error("conf reload failed...");
    			logger_.error(e.getMessage());
    			error =  "conf reload failed...\n" + e.getMessage();
    		}
        	if (error != null && error.trim().length() != 0) {
        		Template template =null;
    	        Map<String, Object> root = new HashMap<String, Object>();
    	        template = cfg_.getTemplate("error.ftl");
    	        root.put("error", error);
    	        PrintWriter out = response.getWriter(); 
    	        try {
    	            template.process(root, out);
    	        } catch (TemplateException e) {
    	            e.printStackTrace();
    	        }
    	        out.flush();
    	        return ;
        	}
        }
        if (query == null) {
        	query = "";
        }
        try {
			Template template =null;
	        Map<String, Object> root = new HashMap<String, Object>();
	        ParserItem item = new ParserItem(query, qType, detail, skip);
	        item.setLogtime(logtime);
	        String ret = "";
	        if (bparser) {
	        	ret = Parser.parserQuery(item, true);
	        }
	        template = cfg_.getTemplate("parser.ftl");
	        root.put("question", query);
	        root.put("qType", qType);
	//        root.put("result", ret);
	        root.put("logresult", ret);
	        root.put("representations", item.getStandardQueryList());
	        root.put("outputs", item.getStandardOutputList());
	        root.put("multResultLists", item.getMultResultLists());
	        root.put("IndexMultPossibilitys", item.getIndexMultPossibilitys());
	        root.put("jsonresults", item.getJsonResultList());
	        root.put("luaresults", item.getLuaResultList());
	        root.put("jsonResultsOfMacroIndustry", item.getJsonResultListOfMacroIndustry());
	        root.put("thematics", item.getThematicList());
	        root.put("lightparserresults", item.getLightParserResultList());
	        root.put("scores", item.getScores());
	        root.put("ip",  RefCodeInfo.getInstance().getIp());
	       
	        PrintWriter out = response.getWriter(); 
	        try {
	            template.process(root, out);
	        } catch (TemplateException e) {
	            e.printStackTrace();
	        }
	        out.flush();	        
        }catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static void main(String[] args){
    	String ret = "";
    	ParserItem item = new ParserItem("净利润<资产总计", null);
    	ret = Parser.parserQuery(item);
    	
    	 try {
 			Template template =null;
 	        Map<String, Object> root = new HashMap<String, Object>();
 	        Configuration cfg_ = new Configuration();
 	       HashSet<String> notExistsTempleteSet =new HashSet<String>();
 			cfg_.setClassForTemplateLoading(Thread.currentThread().getClass(), "/webapp/template");
 			cfg_.setObjectWrapper(new DefaultObjectWrapper());
 	        template = cfg_.getTemplate("parser.ftl");
 	        root.put("question", "净利润<资产总计");
 	//        root.put("result", ret);
 	        root.put("logresult", ret);
 	        root.put("representations", item.getStandardQueryList());
 	        root.put("outputs", item.getStandardOutputList());
 	        root.put("multResultLists", item.getMultResultLists());
 	        root.put("jsonresults", item.getJsonResultList());
 	        root.put("jsonResultsOfMacroIndustry", item.getJsonResultListOfMacroIndustry());
 	        root.put("thematics", item.getThematicList());
 	        root.put("lightparserresults", item.getLightParserResultList());
 	        root.put("scores", item.getScores());
 	        PrintWriter out = new PrintWriter(System.out); 
 	        try
 	        {
 	            template.process(root, out);
 	        } catch (TemplateException e)
 	        {
 	            e.printStackTrace();
 	        }
 	        out.flush();	        
         }catch (Exception e) {
 			e.printStackTrace();
 		}
    }
}
