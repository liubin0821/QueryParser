package com.myhexin.qparser.servlet.op;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.myhexin.server.TemplateServlet;

public class UnknownNodeViewServlet extends TemplateServlet{
	private static final long serialVersionUID = 1L;
	private static  org.slf4j.Logger logger_=org.slf4j.LoggerFactory.getLogger(UnknownNodeViewServlet.class.getName());
	
	static class UnknownWord{
		int id;
		String text;
		String query;
	}
	
	public void init() throws ServletException {
		super.init();
	} 
	
	 protected void closeAll(Connection connection, Statement statement, ResultSet resultSet) {   //关连接
			try {
				if (resultSet != null && !resultSet.isClosed()) {
					resultSet.close();
				}
				if (statement != null && !statement.isClosed()){
					statement.close();
				}
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	
	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		    doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {   
		super.doPost(request, response);
		String path=request.getContextPath();   
		PrintWriter pr = response.getWriter();
		int page=1;                                        //默认为第一页
		String  pages=request.getParameter("newPage");     //获取页面
		String  worda=request.getParameter("word");        //获取关键字
		String  action=request.getParameter("act");
		String  id=request.getParameter("id");
		
		if(action!=null && action.equals("del") && id!=null) {
			delete(id);
		}else if(action!=null && action.equals("dev") && id!=null) {
			update_dev(id);
		}
		
		UnknownWord  findword=null;
		if(worda!=null){
		      findword=findwords(worda);          //查询关键字
		}
		  if(pages!=null){
			  page=Integer.parseInt(pages);        
		 }
		response.setContentType("text/html; charset=utf-8");
		pr.println("<html>");
		pr.println("<head><title>unknownNodeView</title></head>");
		pr.println("<body>");
		pr.println("<div style=\"width:100%;overflow:auto;background-color:#F1F1Fa;font-size:11pt;\">");
		   if(findword!=null){
		      showResult(pr,findword,path);          //查询的结果
			}else{
				if(action==null){
					List<UnknownWord> words=findPages(page);
					displayUnknowWordResults(pr,page,path, words);     //页面显示
				}else if(action!=null && action.equals("viewdev")) {
					List<UnknownWord> words=findDevNodes();
					displayUnknowWordResults(pr,page,path, words);     //页面显示
				}
			}
		pr.println("</div>");
		pr.println("<body>");
		pr.println("</html>");
      }
	
	 private static final Integer pageSize=100;
	 private Integer countRecode;//总条数
	 private Integer countPage;//页数
	 private PreparedStatement pstmt;
	 private ResultSet resultset;
	 
	 
	 private void showResult(PrintWriter pr,UnknownWord word,String path) {
		 pr.println("<a href= "+path+"/unknownNodeView?newPage="+1+">首页</a>"); 
		 pr.println("搜索结果：");
		 pr.println("<table border='1' style=\"width:100%;overflow:auto;background-color:#F1F1Fa;font-size:11pt;\">");
		    pr.println("<tr>");
		    pr.println("<td>");pr.println(word.id);pr.println("</td>");
			pr.println("<td>");pr.println(word.text);pr.println("</td>");
			pr.println("</tr>");
		 pr.println("</table>");
		 
	 }
	 
	 private void displayPaginationButtons(PrintWriter pr,int pages,String path) {
		 
		 int rePage=pages-1<=1?1:pages-1;         //上一页
		 int laPage=pages+1>=countPage?countPage:pages+1;  //下一页
		 
		 pr.println("<a href= "+path+"/unknownNodeView?newPage="+1+">首页</a>");  
		  pr.println("<a href= "+path+"/unknownNodeView?newPage="+rePage+">上一页</a>"); 
		    pr.println("<a href= "+path+"/unknownNodeView?newPage="+laPage+">下一页</a>"); 
		    pr.println("<a href= "+path+"/unknownNodeView?newPage="+countPage+">末页</a>"); 
		    pr.println("第"+pages+"/"+countPage+"页");
	 }
	 
	 
	 private void displayUnknowWordResults(PrintWriter pr,int pages,String path, List<UnknownWord> words) {
		 //List<UnknownWord> words=findPages(pages);
		 countRecode=getCountRecord();
		 countPage=getCountPage();
		
		 pr.println(" <A HREF='" + path + "/unknownNodeView?act=viewdev'>开发要处理的unknown text</A><BR><BR>");
		 
		 displayPaginationButtons(pr,pages,path);
		  
		    pr.println("<table border='1'>");
		    for(int i=0;i<words.size();i++){
		    pr.println("<tr>");
			pr.println("<td>");pr.println(words.get(i).id);pr.println("</td>");
			pr.println("<td>");pr.println(words.get(i).text);pr.println("</td>");
			pr.println("<td>");
			try {
				pr.println("<A HREF='" + path + "/parser?question=" + URLEncoder.encode(words.get(i).query, "utf-8") + "&qType=all' target='_blank'>" );
				pr.println(words.get(i).query);
				pr.println("</A>");
				
			} catch (Exception e) {
				pr.println("null");
			}
			pr.println("<td><A HREF='" + path + "/unknownNodeView?act=del&id="+words.get(i).id + "&newPage=" +pages + "'>删除</A></td>");
			pr.println("<td><A HREF='" + path + "/unknownNodeView?act=dev&id="+words.get(i).id + "&newPage=" +pages + "'>开发处理</A></td>");
			pr.println("</td>");
			pr.println("</tr>");
		    }
		    pr.println("</table>");
		    
		    displayPaginationButtons(pr,pages,path);
    	}
	
	 public Integer getCountRecord() {                  //总条数
		   Integer count = 0;
		   Connection conn=null;
		   String sql="select  count(*) from  sampling_unknown_text";
	    try {
		    conn=OpDb.getConnection();
			pstmt=conn.prepareStatement(sql);
			resultset=pstmt.executeQuery();
			if(resultset.next()){
	           count=resultset.getInt(1);	
			}
			this.countPage=((count%pageSize)!=0 ?(count/pageSize +1):(count/pageSize));
		   } catch (Exception e) {
			 e.printStackTrace();
		 }finally{
			  closeAll(conn, pstmt, resultset);
		   }
			 return  count;
		}

	 
	 public int update_dev(String id) {        //页对应的list<UnknownWord>
			Connection conn=null;
			String sql="UPDATE sampling_unknown_text set is_dev=1 WHERE ID=? ";
			try {
				conn=OpDb.getConnection();
				pstmt=conn.prepareStatement(sql);
				pstmt.setInt(1, new Integer(id));
				pstmt.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				closeAll(conn, pstmt, null);
			}
			return 1;
		}
	
	 public int delete(String id) {        //页对应的list<UnknownWord>
			Connection conn=null;
			String sql="DELETE from sampling_unknown_text WHERE ID=? ";
			try {
				conn=OpDb.getConnection();
				pstmt=conn.prepareStatement(sql);
				pstmt.setInt(1, new Integer(id));
				pstmt.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				closeAll(conn, pstmt, null);
			}
			return 1;
		}
	 
	 public List<UnknownWord> findDevNodes() {        //页对应的list<UnknownWord>
			List<UnknownWord> words=new ArrayList<UnknownWord>();
			Connection conn=null;
			String sql="select id,txt, original_query  from sampling_unknown_text WHERE is_dev IS NOT NULL AND is_dev=1 ORDER BY ID";
			try {
				conn=OpDb.getConnection();
				pstmt=conn.prepareStatement(sql);
				resultset=pstmt.executeQuery();
				while(resultset.next()){
					UnknownWord word=new UnknownWord();
					word.id=(resultset.getInt(1));
					word.text=(resultset.getString(2));
					word.query=(resultset.getString(3));
					words.add(word);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				closeAll(conn, pstmt, resultset);
			}
			return words;
		}
	 
		public List<UnknownWord> findPages(Integer newPage) {        //页对应的list<UnknownWord>
			List<UnknownWord> words=new ArrayList<UnknownWord>();
			Connection conn=null;
			String sql="select id,txt, original_query  from sampling_unknown_text WHERE is_dev IS NULL OR is_dev=0 ORDER BY ID limit ?,? ";
			try {
				conn=OpDb.getConnection();
				pstmt=conn.prepareStatement(sql);
				pstmt.setObject(1, (newPage-1)*pageSize);
				pstmt.setObject(2, pageSize);
				resultset=pstmt.executeQuery();
				while(resultset.next()){
					UnknownWord word=new UnknownWord();
					word.id=(resultset.getInt(1));
					word.text=(resultset.getString(2));
					word.query=(resultset.getString(3));
					words.add(word);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				closeAll(conn, pstmt, resultset);
			}
			return words;
		}
		
			public Integer getCountPage() {      //页数
			return countPage;
		}
	
			
			
	
		public UnknownWord findwords(String word) {         //查询
			Connection conn=null;
		    String sql="select id,txt, original_query from  sampling_unknown_text where  is_dev IS NULL OR is_dev=0 AND txt like %" + word + "%  ORDER BY ID";
		    UnknownWord uword=new UnknownWord();
			try {
				conn=OpDb.getConnection();
				pstmt=conn.prepareStatement(sql);
				pstmt.setObject(1, word);
				resultset=pstmt.executeQuery();
				while(resultset.next()){
					uword.id=(resultset.getInt(1));
					uword.text=(resultset.getString(2));
					uword.query=(resultset.getString(3));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				closeAll(conn, pstmt, resultset);
			}
			return uword;
		}
	
	
	
	
	
	
	
	

}
