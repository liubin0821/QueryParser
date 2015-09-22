package resouremanager.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class CronJobRcvServlet extends HttpServlet {
	/** */
	private static final long serialVersionUID = 1L;
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		System.out.println("CronJobRcvServlet 进入成功");
	}
	
	public void destroy(){
		
	}
	
	@Override
    public void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse){
		doCommon(httpRequest, httpResponse) ;
	}

    @Override
    public void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse){
    	doCommon(httpRequest, httpResponse) ;
    }
    
    protected void doCommon(HttpServletRequest httpRequest, HttpServletResponse httpResponse){

    	System.out.println("-------doPost------");
    	final String encoding = "utf-8";
    	httpResponse.setCharacterEncoding(encoding);   
		httpResponse.setContentType("text/html; charset="+encoding);
				
    	try {
    		PrintWriter writer = httpResponse.getWriter();
    		httpRequest.setCharacterEncoding(encoding);
    		
    		String oper = new String(httpRequest.getParameter("oper").getBytes("ISO-8859-1"), "utf-8");
    		String currDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    		System.out.println(currDate + "\tcronjob is " + oper + "!");
    		CronJobHandler job = new CronJobHandler(oper) ;
    		CronJobResult result = job.handle() ;
    		System.out.println(result.toString());
    		String jsonResult = new Gson().toJson(result);
    		writer.print(jsonResult) ;
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
