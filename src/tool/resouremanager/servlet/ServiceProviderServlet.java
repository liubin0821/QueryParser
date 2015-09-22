package resouremanager.servlet;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ServiceProviderServlet extends HttpServlet{
	/** */
	private static final long serialVersionUID = 1L;
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		
	}
	
	public void destroy(){
		
	}
	
	@Override
    public void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse){
    	final String encoding = "utf-8";
    	httpResponse.setCharacterEncoding(encoding);   
		httpResponse.setContentType("text/html; charset="+encoding);
    	try {
    		PrintWriter writer = httpResponse.getWriter();
    		httpRequest.setCharacterEncoding(encoding);
    		String oper = new String(httpRequest.getParameter("oper").getBytes("ISO-8859-1"), "utf-8");

		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    @Override
    public void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse){
    	System.out.println("-------doPost------");
    	final String encoding = "utf-8";
    	httpResponse.setCharacterEncoding(encoding);   
		httpResponse.setContentType("text/html; charset="+encoding);
    	try {
    		PrintWriter writer = httpResponse.getWriter();
    		httpRequest.setCharacterEncoding(encoding);
    		
    		String oper = new String(httpRequest.getParameter("oper").getBytes("ISO-8859-1"), "utf-8");
    		String text = httpRequest.getParameter("text");
    		
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
