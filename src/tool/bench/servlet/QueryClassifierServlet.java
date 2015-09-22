/**
 * Copyright©Zhejiang Hexin Flush Network Services Ltd. All rights reserved.
 * Zhejiang Hexin Flush Network Services Ltd.
 * 
 * http://www.10jqka.com.cn/
 */
package bench.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.gson.Gson;
import com.myhexin.qparser.Query;

import bench.BenchManager;
import bench.BenchQuery;
import bench.ParserAgent;
import bench.QueryClassResult;
import bench.db.DBBenchManager;

/**
 * the Class QueryClassifierServlet
 *
 */
public class QueryClassifierServlet extends HttpServlet{
	private BenchManager benchManager;
	/** */
	private static final long serialVersionUID = 1L;
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		final String database = "ontoask";
		String parseConf = getServletContext().getRealPath("/conf/qparser.conf");
		
		try {
			ParserAgent.init(parseConf);
	    	benchManager = new DBBenchManager(database, false);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
    public void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse){
    	doCommon(httpRequest, httpResponse);
    }

    @Override
    public void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse){
    	doCommon(httpRequest, httpResponse) ;
    }
    
	protected void doCommon(HttpServletRequest httpRequest, HttpServletResponse httpResponse){
    	final String encoding = "utf-8";
    	httpResponse.setCharacterEncoding(encoding);   
		httpResponse.setContentType("text/html; charset="+encoding);
    	try {
    		PrintWriter writer = httpResponse.getWriter();
    		httpRequest.setCharacterEncoding(encoding);
    		
    		String text = new String(httpRequest.getParameter("text").getBytes("ISO-8859-1"), "utf-8").trim();
    		String qid = new String(httpRequest.getParameter("qid").getBytes("ISO-8859-1"), "utf-8").trim();
    		
            BenchQuery bq = new BenchQuery(text);
            Query query = ParserAgent.parse(bq);
            ParserAgent.parseBenchQueryFileds(query, bq, benchManager) ;
            
            String tree = bq.generalPtn ;
            String sepecificPtn = bq.specificPtn ;
            String currDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			if (qid.equals("2")) {
				System.out.println(currDate + "\tquery: " + text+ "\tTo Get Log !");
				writer.println("<pre>" + query.getLog("\n") + "</pre>") ;
				return ;
			}
			
            System.out.println(currDate + "\tquery: " + text+ "\tpattern: " + sepecificPtn);
			
//            int treeCode = benchManager.addTree(tree);
//            int patternCode = benchManager.addPattern(sepecificPtn);
            int treeCode = benchManager.getTreeCode(tree) ;
            int patternCode = benchManager.getSpecificPtnCode(sepecificPtn) ;
            
            QueryClassResult classResult = new QueryClassResult();
            classResult.qid = qid;
            classResult.text = text;
//            classResult.tree = tree;
            classResult.treeCode = treeCode;
            classResult.patternCode = patternCode;
            classResult.queryPattern = sepecificPtn;
//            classResult.indexs = ParserAgent.parseIndexs(query);
//            classResult.operators = ParserAgent.parseOperators(query);
//            classResult.dates = ParserAgent.parseDates(query);
//            classResult.semanticPatterns = ParserAgent.parseSemanticPatterns(query);
//            classResult.synonyms = ParserAgent.parseTrans(query);
//            classResult.unknowns = ParserAgent.parseUnknowns(query);
//            classResult.isNew = !benchManager.getPattern2CodeMap().containsKey(classResult.queryPattern) ;
            
            String jsonResult = new Gson().toJson(classResult);
    		writer.println(jsonResult);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
