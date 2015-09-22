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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class ChunkParserServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    private static final Logger logger_ = LoggerFactory.getLogger(ChunkParserServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        //if(request.getParameter("qid")!=null)
        //	logger_.info("qid="+request.getParameter("qid"));
        
        response.setContentType("application/json; charset=utf-8");
        
        
        String query = request.getParameter("q");
        query = query == null ? request.getParameter("query") : query;
        
        if (query != null && query.length() > 0) {
            Gson json = new Gson();
            String res = ChunkParser.parserQuery(query);

            List<String> standard = new ArrayList<String>();
            standard.add(res);
            OutputStream os = response.getOutputStream();
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("errno", String.valueOf(0));
            map.put("errmsg", "");
            map.put("query", query);
            map.put("result", standard);
            os.write(json.toJson(map).getBytes("utf-8"));
            os.close();
        }
    }
}
