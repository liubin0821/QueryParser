package com.myhexin.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.myhexin.qparser.Param;
import com.myhexin.qparser.chunk.ChunkQueryResult;
import com.myhexin.qparser.util.ThreadMonitor;

public class TotalParserServlet extends HttpServlet {

	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(ConfigCheckServlet.class.getName());
	private ThreadMonitor monitor = ThreadMonitor.getInstance();
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        //if(request.getParameter("qid")!=null)
        //	logger_.info("qid="+request.getParameter("qid"));
        

    	request.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json; charset=utf-8");
        
        String query = request.getParameter("q");
        Thread thread = Thread.currentThread();
        monitor.addThread(thread,"TotalParserServlet:"+ query);
        
        
        query = query == null ? request.getParameter("query") : query;
        String chunk = request.getParameter("chunk");
        String qType = request.getParameter("qType");
        String debug = request.getParameter("debug");
        boolean isDebug = ( debug!=null&&debug.equals("1") ) ? true:false;
        try {
	        if (query != null && query.length() > 0 && query.length() <= Param.PARSED_MAX_CHARS) {
	            ChunkQueryResult chunkQueryResult = TotalParser.parserQuery(query, chunk,qType, isDebug);
	            OutputStream os = response.getOutputStream();
	            HashMap<String, Object> map = new HashMap<String, Object>();
	            
	            ArrayList<Map<?, ?>> chunks = chunkQueryResult.getChunk();
	            map.put("errno", String.valueOf(0));
	            map.put("errmsg", "");
	            map.put("query", query);
	            map.put("chunk", chunks);	                        	            
	            
	            Gson json = new Gson();
	            os.write(json.toJson(map).getBytes("utf-8"));
	            os.close();
	        } else {
	        	// 日志1：问句超过50个字符不解析
	        	logger_.info("None parsed: len not in " + Param.PARSED_MAX_CHARS + "\tquery=" + query);
	        	OutputStream os = response.getOutputStream();
	            HashMap<String, Object> map = new HashMap<String, Object>();
	            map.put("errno", String.valueOf(1));
	            map.put("errmsg", "None parsed: len not in " + Param.PARSED_MAX_CHARS);
	            map.put("query", query);
	            map.put("chunk", chunk);
	            Gson json = new Gson();
	            os.write(json.toJson(map).getBytes("utf-8"));
	            os.close();
	        }
        } catch (Exception e) {
        	// 日志2：解析过程中出错，包一个try catch
        	logger_.error("Exception:" , e);
        	logger_.error("query=" + query);
        	logger_.info("None parsed: exception\tquery=" + query);
        	OutputStream os = response.getOutputStream();
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("errno", String.valueOf(1));
            map.put("errmsg", e.getStackTrace());
            map.put("query", query);
            map.put("chunk", chunk);
            Gson json = new Gson();
            os.write(json.toJson(map).getBytes("utf-8"));
            os.close();
        }finally {
        	monitor.removeThread(thread);
        }
    }
}
