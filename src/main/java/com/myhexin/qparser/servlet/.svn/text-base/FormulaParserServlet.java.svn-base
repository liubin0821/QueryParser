package com.myhexin.qparser.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.myhexin.qparser.util.backtest.expr.SuffixExprBuilder;
import com.myhexin.server.TemplateServlet;

public class FormulaParserServlet extends TemplateServlet{
	private static final long serialVersionUID = 1L;	
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(FormulaParserServlet.class.getName());
    
	@Override
    public void init() throws ServletException {
        super.init();
    }
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doGet(request, response);
    }
    
    
    protected void displayForm(PrintWriter out ,String contextPath) throws ServletException, IOException {
        try{
        	
        	
        	
        	out.println("<header>");
        	out.println("<title>中缀表达式转后缀表达式</title>");
        	out.println("</header>");
        	out.println("<body>");
        	out.println("<div style=\"width:100%;overflow:auto;\">");
         	out.println("<form action=\""+contextPath+"/formula\" method=\"post\">");
        	out.println("公式:<textarea name=\"formula_txt\"  style=\"width:100%;\" rows=\"3\">(a+b)*c</textarea><BR>");
        	//out.println("<input type=\"checkbox\" name=\"xml\" value=\"1\" >");
        	out.println("<input type=\"submit\" name=\"submit\" value=\"submit\" >");
        	out.println("</form>");
        	
        }catch (Exception e) {
    		e.printStackTrace();
    	}
        
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	super.doGet(request,response);
    	
    	response.setContentType("text/html; charset=utf-8");
    	PrintWriter out = null;
    	
        try{
        	out = response.getWriter(); 
        	out.println("<html>");
        	displayForm(out, request.getContextPath());
        	
        	String formula_txt = request.getParameter("formula_txt");
        	logger_.info("formula_txt=" +formula_txt);
        	String suffix_formula = null;
        	if(formula_txt!=null && formula_txt.length()>0) {
        		suffix_formula = "";//SuffixExprBuilder.infixToSuffix(formula_txt);
        	}
        	logger_.info("suffix_formula=" +suffix_formula);
    	    out.println("<BR>输入公式:");
    	    out.println(formula_txt);
    	    out.println("<BR>后缀表达式:<B>");
    	    out.println(suffix_formula);
    	    out.println("</B><BR>");
    	    out.println("</body>");
        	out.println("</html>");
        }catch (Exception e) {
    		e.printStackTrace();
    	}finally {
    		if(out!=null ) {
    			out.flush();
    			out.close();
    		}
    	}
        
    }
}
