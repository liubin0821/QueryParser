package com.myhexin.qparser.servlet.op;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.myhexin.server.TemplateServlet;

/*
 * 
ALTER TABLE `sampling_queries` ADD COLUMN synt_semantic_info1 VARCHAR(100) NULL;
ALTER TABLE `sampling_queries` ADD COLUMN score1 INT NULL;
ALTER TABLE `sampling_queries` ADD COLUMN standard_q1 VARCHAR(300) NULL;
ALTER TABLE `sampling_queries` ADD COLUMN run1_dt DATETIME;
ALTER TABLE `sampling_queries` ADD COLUMN used_time1 INT AFTER run1_dt;

ALTER TABLE `sampling_queries` ADD COLUMN synt_semantic_info2 VARCHAR(100) NULL;
ALTER TABLE `sampling_queries` ADD COLUMN score2 INT NULL;
ALTER TABLE `sampling_queries` ADD COLUMN standard_q2 VARCHAR(300) NULL;
ALTER TABLE `sampling_queries` ADD COLUMN run2_dt DATETIME;
ALTER TABLE `sampling_queries` ADD COLUMN used_time2 INT AFTER run2_dt;


 */
public class SampleQueryViewServlet extends TemplateServlet {
	private static final long serialVersionUID = 1L;
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(SampleQueryViewServlet.class.getName());
	
	@Override
	public void init() throws ServletException {
		super.init();
	}
	

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public final static int batchSize  = 100;
	private OpDbSampleQuery db = new OpDbSampleQuery();
	private SampleRuner runner = new SampleRuner();
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doGet(request, response);
		PrintWriter pr = response.getWriter();
		String startStr = request.getParameter("start");
		String action = request.getParameter("a");
		String id = request.getParameter("id");
		String q = request.getParameter("q");
		String batch = request.getParameter("batch");
		if(action!=null && action.equals("d") && id!=null) {
			try{
			db.deleteSampleQuery(Integer.parseInt(id));
			}catch(Exception e){}
		}else if(action!=null && action.equals("add") && q!=null) {
			try{
				String[] qs = q.split("\r\n");
				db.save(qs);
			}catch(Exception e){}
		}else if(action!=null && action.equals("r") && id!=null) {
			runner.runSingle(id);
		}else if(action!=null && action.equals("r") && batch!=null) {
			runner.runBatch(batch, startStr);
		}
		
		
		if(startStr==null) startStr = "0";
		//String batchSizeStr = request.getParameter("batchSize");
		int start = Integer.parseInt(startStr);
		//int batchSize = Integer.parseInt(batchSizeStr);
		String contextPath = request.getContextPath();
		
		
		int count = db.countSample();
		
		List<String[]> list = db.getSampleQueries(start*batchSize, batchSize);
		
		response.setContentType("text/html; charset=utf-8");
		pr.println("<html>");
		pr.println("<head><title>抽样问句列表</title></head>");
		pr.println("<body>");
		
		displayForm(pr, contextPath);
		
		displayPagination(pr, contextPath, start, count);
		
		pr.println("<div style=\"width:100%;overflow:auto;background-color:#F1F1Fa;font-size:11pt;\">");
		displaySamplingResults(pr,list,contextPath, start);
		pr.println("<BR><BR>");
		
		displayPagination(pr, contextPath, start, count);
		pr.println("</div>");
		pr.println("<body>");
		pr.println("</html>");
	}
	
	private void displayForm(PrintWriter pr, String contextPath) {
		pr.println("<form action='" +contextPath  + "/sampleView' method='post'>"  );
		pr.println("<input type='hidden' name='a' value='add'>"  );
		pr.println("添加问句:<BR> <textarea name='q' cols='50' rows='3'></textarea><BR>"  );
		pr.println("<input type='submit' name='submit' value='添加'>"  );
		pr.println("</form>"  );
		pr.println("<BR>");
	}

	private void displayPagination(PrintWriter pr, String contextPath,  int start, int total) {
		int prev = start - 1;
		if(prev<0) prev=0;
		int next = start+1;
		if(next*batchSize > total) {
			next = total/batchSize + 1;
		}
		
		int last =  total/batchSize;
		pr.println("<A  HREF='" +contextPath+ "/sampleView?start=" +0 + "'>首页</A>&nbsp;");
		pr.println("<A  HREF='" +contextPath+ "/sampleView?start=" +prev + "'>上一页</A>&nbsp;");
		pr.println("<A  HREF='" +contextPath+ "/sampleView?start=" +start + "'>当前页</A>&nbsp;");
		pr.println("<A  HREF='" +contextPath+ "/sampleView?start=" +next + "'>下一页</A>&nbsp;");
		pr.println("<A  HREF='" +contextPath+ "/sampleView?start=" +last + "'>尾页</A>&nbsp;");
		pr.println("<BR>");
		pr.println("<A  HREF='" +contextPath+ "/sampleView?batch=1&a=r&start=" +start + "'>批量跑着一页(时间会比较长)</A>&nbsp;");
		pr.println("<BR><BR>");
	}
	
	private void displaySamplingResults(PrintWriter pr, List<String[]> list, String contextPath, int start) {
			pr.println("<table border='1' style='font-size:10pt;'>");
			pr.println("<tr>");
			pr.println("<th>");pr.println("ID");pr.println("</th>");
			pr.println("<th>");pr.println("问句");pr.println("</th>");
			pr.println("<th>");pr.println("句式语义");pr.println("</th>");
			pr.println("<th>");pr.println("分数");pr.println("</th>");
			pr.println("<th>");pr.println("标准句");pr.println("</th>");
			pr.println("<th>");pr.println("时间");pr.println("</th>");
			pr.println("<th>");pr.println("&nbsp;");pr.println("</th>");
			pr.println("<th>");pr.println("&nbsp;");pr.println("</th>");
			pr.println("<th>");pr.println("&nbsp;");pr.println("</th>");
			pr.println("</tr>");
			int total = 0;
			int correct = 0;
			
			
			if(list!=null) {
				
				for(String[] q : list){
					total++;
					String query = null;
					try {
						query =  URLEncoder.encode(q[1], "utf-8");
					} catch (UnsupportedEncodingException e) {
						query = q[1];
					}
					pr.println("<tr>");
					pr.println("<td>");pr.println(q[0]);pr.println("</td>");
					if(q[1]!=null && q[1].length()>40) q[1] = q[1].substring(0,40) + "...";
					pr.println("<td>");pr.println("<A target='_blank' HREF='http://192.168.201.164:9100/parser?question=" + query+ "'>" + q[1] + "</A>");pr.println("</td>");
					
					if(q[2]!=null && q[2].length()>25) q[2] = q[2].substring(0,25) + "...";
					pr.println("<td>");pr.println(q[2]==null?"&nbsp;":q[2]);pr.println("</td>");
					
					
					pr.println("<td>");pr.println(q[3]==null?"&nbsp;":q[3]);pr.println("</td>");
					if(q[3]!=null && q[3].equals("100")) {
						correct ++ ;
					}
					
					if(q[4]!=null && q[4].length()>50) q[4] = q[4].substring(0,50) + "...";
					pr.println("<td>");pr.println(q[4]==null?"&nbsp;":q[4]);pr.println("</td>");
					
					
					pr.println("<td width='120'>");pr.println(q[5]==null?"&nbsp;":q[5]);pr.println("</td>");
					pr.println("<td>");pr.println(q[6]==null?"&nbsp;":(q[6] +"ms"));pr.println("</td>");
					pr.println("<td>");pr.println("<A HREF='" +contextPath+ "/sampleView?a=r&id=" +q[0] + "&start=" +start +"'>Parse</A>");pr.println("</td>");
					pr.println("<td>");pr.println("<A HREF='" +contextPath+ "/sampleView?a=d&id=" +q[0] + "'>DEL</A>");pr.println("</td>");
					pr.println("</tr>");
				}
			}
			
			pr.println("</table>");
			
			pr.println("总数: " + total + "<br>");
			pr.println("正确数: " + correct + "<br>");
			if(total==0) {
				pr.println("正确率: " + 0 + "%<br>");
			}else{
				pr.println("正确率: " + (correct*1.0/total)*100 + "%<br>");
			}
	}
}
