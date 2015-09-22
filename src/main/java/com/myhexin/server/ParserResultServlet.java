package com.myhexin.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myhexin.qparser.ParseResult;
import com.myhexin.qparser.phrase.PhraseParser;
import com.myhexin.qparser.phrase.PhraseParserFactory;
import com.myhexin.qparser.phrase.PhraseParserUtil;
import com.myhexin.qparser.util.condition.ConditionBuilderUtil;

/**
 * 此类用于直接查看json字符串构建的结果
 * @author admin
 *
 */
public class ParserResultServlet extends TemplateServlet{
	
	private static final long serialVersionUID = 1L;	
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(Parser.class.getName());
    private static PhraseParser parser = PhraseParserFactory.createPhraseParser(PhraseParserFactory.ParserName_Condition);
    @Override
    public void init() throws ServletException {
        super.init();
    }
    
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	super.doGet(request,response);
    	
        String query = request.getParameter("q");
        query = query == null ? request.getParameter("question") : query;
        
        Map<String, String> result = new HashMap<String, String>();
        result.put("query", query!=null?query:"");
        if (query != null && query.trim().length()>0) {
        	ParseResult pr = PhraseParserUtil.parse(parser, query, "ALL", null);
        	result.put("score", String.valueOf(pr.getScore()) );
        	result.put("syntacticSemanticIds", pr.getSyntacticSemanticIds());
		}else{
			result.put("msg", "query is null");
		}
        
        PrintWriter out = response.getWriter(); 
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String s= ConditionBuilderUtil.unicodeEsc2Unicode(gson.toJson(result));
		out.println(s);
        out.close();
    }

    /*@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	super.doGet(request,response);
    	
        boolean bparser = true;
        String query = request.getParameter("q");
        query = query == null ? request.getParameter("question") : query;
        String breload = request.getParameter("reload");
        String qType = request.getParameter("qType");
        if (breload != null && breload.equals("1")) {
        	bparser = false;
        	String error = null;
        	try {
        		Resource.reloadData();
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
	        ParserItem item = new ParserItem(query, qType);
	        String ret = "";
	        if (bparser) {
	        	ret = Parser.parserQuery(item);
	        }
	        template = cfg_.getTemplate("parserresult.ftl");
	        root.put("question", query);
	        root.put("qType", qType);
	//        root.put("result", ret);
	        root.put("logresult", ret);
	        root.put("representations", item.getStandardQueryList());
	        root.put("outputs", item.getStandardOutputList());
	        root.put("multResultLists", item.getMultResultLists());
	        root.put("IndexMultPossibilitys", item.getIndexMultPossibilitys());
	        root.put("syntSmeanIdsList", item.getSyntSmeanIdsList());
	        List<String> jsonResultList = item.getJsonResultList();
	        if (jsonResultList != null)
				for (int i = 0; i < jsonResultList.size(); i++) {
					String jsonResult = jsonResultList.get(i);
					jsonResult = JsonTool.formatJson(jsonResult, "\t");
					jsonResultList.set(i, jsonResult);
				}
	        root.put("jsonresults", jsonResultList);
	        root.put("jsonResultsOfMacroIndustry", item.getJsonResultListOfMacroIndustry());
	        root.put("thematics", item.getThematicList());
	        root.put("lightparserresults", item.getLightParserResultList());
	        root.put("scores", item.getScores());
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
    	// 加载xml文件，初始spring容器
		ApplicationContextHelper.loadApplicationContext();
		// phraseParser配置于qphrase.xml文件
		PhraseParser parser = (PhraseParser) ApplicationContextHelper.getBean("phraseParser");
    	String query = "净利润<1千万元";
    	String qType = "stock";
    	ParserItem item = new ParserItem(query, qType);
    	String ret = Parser.parserQuery(item);
    	
    	 try {
			Template template = null;
			Map<String, Object> root = new HashMap<String, Object>();
			Configuration cfg_ = new Configuration();
			HashSet<String> notExistsTempleteSet =new HashSet<String>();
			cfg_.setClassForTemplateLoading(Thread.currentThread().getClass(), "/webapp/template");
			cfg_.setObjectWrapper(new DefaultObjectWrapper());
			template = cfg_.getTemplate("parserresult.ftl");
	        root.put("question", query);
	        root.put("qType", qType);
	        root.put("logresult", ret);
	        root.put("representations", item.getStandardQueryList());
	        root.put("outputs", item.getStandardOutputList());
	        root.put("multResultLists", item.getMultResultLists());
	        root.put("IndexMultPossibilitys", item.getIndexMultPossibilitys());
	        root.put("syntSmeanIdsList", item.getSyntSmeanIdsList());
	        List<String> jsonResultList = item.getJsonResultList();
	        if (jsonResultList != null)
				for (int i = 0; i < jsonResultList.size(); i++) {
					String jsonResult = jsonResultList.get(i);
					jsonResult = JsonTool.formatJson(jsonResult, "\t");
					jsonResultList.set(i, jsonResult);
				}
	        root.put("jsonresults", jsonResultList);
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
    }*/
}
