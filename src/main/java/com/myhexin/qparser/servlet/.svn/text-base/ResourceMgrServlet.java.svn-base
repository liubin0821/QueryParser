package com.myhexin.qparser.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.myhexin.qparser.resource.Resource;
import com.myhexin.qparser.resource.model.NodeMergeInfo;
import com.myhexin.server.TemplateServlet;

public class ResourceMgrServlet extends TemplateServlet{
	private static final long serialVersionUID = 1L;	
	@Override
    public void init() throws ServletException {
        super.init();
    }
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	super.doGet(request,response);
    	
    	String resourceName = request.getParameter("res");
    	String msg = null;
    	if(resourceName!=null) {
    		if(resourceName.equals("ALL")) {
    			try {
					Resource.reloadData();
				} catch (Exception e) {
					e.printStackTrace();
				}
    		}else if(resourceName.equals("dateconfig")) {
    			NodeMergeInfo.getInstance().reload();
    		}
    		msg = resourceName +" reload success.";
    	}else{
    		msg = "param res=null";
    	}
    	
    	
    	response.setContentType("text/plain; charset=utf-8");
    	PrintWriter out = response.getWriter(); 
    	out.println(msg);
    	out.close();
    }
}
