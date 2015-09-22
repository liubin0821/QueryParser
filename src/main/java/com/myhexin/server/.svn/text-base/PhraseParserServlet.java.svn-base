package com.myhexin.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.myhexin.qparser.Query;

public class PhraseParserServlet extends HttpServlet {
	private static final org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseParserServlet.class.getName());

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        
        //if(request.getParameter("qid")!=null)
        //	logger_.info("qid="+request.getParameter("qid"));
        

        request.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html");     

        String query = request.getParameter("q");
        query = query == null ? request.getParameter("query") : query;
        String type = request.getParameter("type");
        type = type == null ? request.getParameter("qType") : type;
        if (query != null && query.length() > 0)
        {
            Gson json = new Gson();
            Query queryInst = parse(query, type);
            int errorCode = queryInst.hasFatalError() ? 1 : 0;
            String errorMsg = errorCode == 0 ? "" : queryInst.getErrorMsg();
            List<String> retStandard = queryInst.getParseResult().standardQueries;
            List<String> standard = new ArrayList<String>();
            List<Integer> scores = queryInst.getParseResult().standardQueriesScore;
            for(int i=0; i<retStandard.size(); i++)
            {
                int score = scores.get(i);
                if(score >= 80)
                    standard.add(retStandard.get(i));
            }
            if(standard.size() == 0)
                standard.add(query);
            OutputStream os = response.getOutputStream();
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("errno", String.valueOf(errorCode));
            map.put("errmsg", errorMsg);
            map.put("result", standard);
            os.write(json.toJson(map).getBytes());
            os.close();
        }
    }
    
    
    private Query parse(String query) {
        Query q = new Query(query);
        Parser.parser.parse(q);
        return q;
    }
    
    private Query parse(String query, String type) {
        Query q = new Query(query, type);
        Parser.parser.parse(q);
        return q;
    }
}
