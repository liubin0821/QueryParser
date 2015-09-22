package com.myhexin.qparser.servlet.op;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.myhexin.server.TemplateServlet;

public class TestCaseServlet extends TemplateServlet{
	private static final long serialVersionUID = 1L;
	public void init() throws ServletException {
		super.init();
	} 
	

	
	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {   
		super.doPost(request, response);
		String path=request.getContextPath();   
		PrintWriter pr = response.getWriter();
		String  query=request.getParameter("query");     //获取页面
		String  qType=request.getParameter("qType");        //获取关键字
		String  domain=request.getParameter("domain");
		String  postDataStr=request.getParameter("postDataStr");
		String  exp_result=request.getParameter("exp_result");
		String  clazzName=request.getParameter("clazzName");
		String  p1=request.getParameter("p1");
		
		if(query!=null && exp_result!=null && clazzName!=null) {
			if(domain!=null && domain.equals("null") ) domain=null;
			update_dev(query, qType, domain, postDataStr, exp_result, clazzName, p1);
			
		}
		
		  
		response.setContentType("text/html; charset=utf-8");
		pr.println("<html>");
		pr.println("<head><title>unknownNodeView</title></head>");
		pr.println("<body>");
		pr.println("<div style=\"width:100%;overflow:auto;background-color:#F1F1Fa;font-size:11pt;\">");
		displayForm(pr, path);   
		pr.println("<br><br>");
		displayTestCase(pr);
		pr.println("</div>");
		pr.println("<body>");
		pr.println("</html>");
		pr.close();
    }


	 
	private void displayForm(PrintWriter pr, String path) {
		pr.println("<form action='"+path+"/testcase' method='post'>");
		pr.println("<table border='1'>");
		pr.println("<tr>");
		pr.println("<td>");pr.println("query:");pr.println("</td>");
		pr.println("<td>");pr.println("<input size='100' type='text' name='query'>");pr.println("</td>");
		pr.println("</tr>");
		
		pr.println("<tr>");
		pr.println("<td>");pr.println("qType:");pr.println("</td>");
		pr.println("<td>");pr.println("<input size='100' type='text' name='qType' value='ALL'>");pr.println("</td>");
		pr.println("</tr>");
		
		pr.println("<tr>");
		pr.println("<td>");pr.println("domain:");pr.println("</td>");
		pr.println("<td>");pr.println("<input size='100' type='text' name='domain' value='null'>");pr.println("</td>");
		pr.println("</tr>");
		
		pr.println("<tr>");
		pr.println("<td>");pr.println("postDataStr:");pr.println("</td>");
		pr.println("<td>");pr.println("<input size='100' type='text' name='postDataStr' value='{\"length\":\"0\",\"dunit\":\"0\",\"start\":\"2015-06-25\"}'>");pr.println("</td>");
		pr.println("</tr>");
		
		pr.println("<tr>");
		pr.println("<td>");pr.println("exp_result:");pr.println("</td>");
		pr.println("<td>");pr.println("<input size='100' type='text' name='exp_result'>");pr.println("</td>");
		pr.println("</tr>");
		
		pr.println("<tr>");
		pr.println("<td>");pr.println("clazzName:");pr.println("</td>");
		pr.println("<td>");pr.println("<input size='100' type='text' name='clazzName' value='ConditionTestCase'>");pr.println("</td>");
		pr.println("</tr>");
		
		pr.println("<tr>");
		pr.println("<td>");pr.println("p1:");pr.println("</td>");
		pr.println("<td>");pr.println("<input size='100' type='text' name='p1' value=''>");pr.println("</td>");
		pr.println("</tr>");
		
		pr.println("<tr>");
		pr.println("<td>");pr.println("&nbsp;");pr.println("</td>");
		pr.println("<td>");pr.println("<input type='submit' value='submit'>");pr.println("</td>");
		pr.println("</tr>");
	    pr.println("</table>");
	    pr.println("</form>");    
	 }
	
	 
	 public int update_dev(String query, String qType, String domain, String postDataStr, String exp_result, String clazzName, String p1) {        //页对应的list<UnknownWord>
			Connection conn=null;
			String sql="INSERT INTO configFile.parser_test(query, qType, domain, postDataStr, exp_result, clazzName, p1) VALUES(?,?,?,?,?,?,?) ";
			try {
				conn=OpDb.getConnection();
				PreparedStatement pstmt=conn.prepareStatement(sql);
				pstmt.setString(1, query);
				pstmt.setString(2, qType);
				pstmt.setString(3, domain);
				pstmt.setString(4, postDataStr);
				pstmt.setString(5, exp_result);
				pstmt.setString(6, clazzName);
				pstmt.setString(7, p1);
				pstmt.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(conn!=null ) {
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			return 1;
		}
	
	 private void displayTestCase(PrintWriter pr) {
		 
		pr.println("<table border='1'>");
		Connection conn=null;
		String sql="select id, query, status, last_upd_dt, postDataStr, clazzName  from configFile.parser_test";//,exp_result 
		try {
			conn=OpDb.getConnection();
			PreparedStatement pstmt=conn.prepareStatement(sql);
			ResultSet resultset=pstmt.executeQuery();
			while(resultset.next()){
				pr.println("<tr>");
				pr.println("<td>");pr.println(resultset.getInt(1));pr.println("</td>");
				pr.println("<td>");pr.println(resultset.getString(2));pr.println("</td>");
				pr.println("<td>");pr.println(resultset.getString(3));pr.println("</td>");
				pr.println("<td>");pr.println(resultset.getString(4));pr.println("</td>");
				//pr.println("<td>");pr.println(resultset.getString(5));pr.println("</td>");
				pr.println("<td>");pr.println(resultset.getString(6));pr.println("</td>");
				//pr.println("<td>");pr.println(resultset.getString(7));pr.println("</td>");
				pr.println("</tr>");
		    }
		    pr.println("</table>");
    	}catch(Exception e){
    		
    	}finally {
    		if(conn!=null) {
    			try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
    		}
    	}
	 }

}
