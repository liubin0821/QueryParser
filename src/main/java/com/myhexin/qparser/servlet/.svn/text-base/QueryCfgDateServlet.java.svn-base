package com.myhexin.qparser.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myhexin.qparser.util.cfgdate.CfgDateLogicInstance;
import com.myhexin.server.TemplateServlet;

/**
 * 根据指标,拿到时间的接口
 * 比如
 * 输入:[{"index":"净利润","prop":"报告期","dt":"20131231,20121231,20111231"},{"index":"龙头股","prop":"报告期"}]
 * 输出:[{"index":"净利润","prop":"报告期","dt":"20131231,20121231,20111231,20141231,20140930,20140630"},{"index":"龙头股","prop":"报告期","dt":"20141231,20140930,20140630"}]

 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-01-15
 *
 */
public class QueryCfgDateServlet extends TemplateServlet{
	private static final long serialVersionUID = 1L;	
	//public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(BacktestCondServlet.class.getName());
    
	static class Param {
		private String index;
		private String prop;
		private String dt;
		public String getIndex() {
			return index;
		}
		public void setIndex(String index) {
			this.index = index;
		}
		public String getProp() {
			return prop;
		}
		public void setProp(String prop) {
			this.prop = prop;
		}
		public String getDt() {
			return dt;
		}
		public void setDt(String dt) {
			this.dt = dt;
		}
		
		public String toString() {
			return index + "," + prop + "," + dt;
		}
	}
	
	/*public static void main(String[] args) {
		QueryCfgDateServlet serv = new QueryCfgDateServlet();
		String str = serv.getJsonParam();
		System.out.println(str);
	}
	
	public String getJsonParam() {
		List<Param> pList = new ArrayList<Param>();
    	Param p = new Param();
    	p.index = "净利润";
    	p.prop="报告期";
    	p.dt="20140604";
    	pList.add(p);
    	
    	Param p1 = new Param();
    	p1.index = "龙头股";
    	p1.prop="报告期";
    	p1.dt=null;
    	pList.add(p1);
    	
    	Gson gson = new Gson();
        String str = gson.toJson(pList);
        System.out.println(str);
        return str;
	}*/
	
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
    	
    	String resultOutputStr = null;
    	PrintWriter out = null;
    	List<Param> pList = null;
    	String paramStr = null;
    	try{
	        //[{"index":"净利润","prop":"报告期"},{"index":"龙头股","prop":"报告期"}]
	    	paramStr = request.getParameter("param");
	    	//param = "[{\"index\":\"净利润\",\"prop\":\"报告期\"},{\"index\":\"龙头股\",\"prop\":\"报告期\"}]";
	    	CfgDateLogicInstance instance = CfgDateLogicInstance.getInstance();
	    	//instance.print();
	    	Gson gson = new Gson();
	    	pList = gson.fromJson(paramStr, new TypeToken<List<Param>>(){}.getType());
	    	if(pList!=null) {
	    		for(Param p : pList) {
	    			String retStr = instance.getDatePeriod(p.index, p.prop, p.dt);
	    			//System.out.println("retStr=" + retStr);
	    			p.setDt(retStr);
	    		}
	    	}
    	}catch(Exception e) {
    		e.printStackTrace();
    		resultOutputStr = paramStr;
    	}
    	
    	try{
	    	if(pList!=null) {
	    		Gson gson = new Gson();
	    		resultOutputStr = gson.toJson(pList);
	    	}else{
	    		resultOutputStr = paramStr;
	    	}
	    	response.setContentType("text/plain; charset=utf-8");
	    	out = response.getWriter(); 
	    	out.println(resultOutputStr);
    	}catch(Exception e) {
    		e.printStackTrace();
		}finally {
			if(out!=null ) {
				out.flush();
				out.close();
			}
		}
    }
}
