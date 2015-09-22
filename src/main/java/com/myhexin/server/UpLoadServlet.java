package com.myhexin.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class UpLoadServlet extends TemplateServlet{
	private static final org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(UpLoadServlet.class.getName());
    
    @Override
    public void init() throws ServletException
    {
        super.init();

    }
			   
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException 
	{
		super.doGet(request, response);
		
        
        //if(request.getParameter("qid")!=null)
        //	logger_.info("qid="+request.getParameter("qid"));
        

        response.setContentType("text/html");  
        try {
			Map<String, Object> root = new HashMap<String, Object>();
			Template template = cfg_.getTemplate("upload.ftl");
			PrintWriter out = response.getWriter(); 
			template.process(root, out);
	        out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException 
	{
		FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
        	String destpath = "data/stock/";
        	List<FileItem> items = upload.parseRequest(request);
            Iterator<FileItem> itr = items.iterator();
            String fieldname = "fileinput";
            while (itr.hasNext()) 
            {
                FileItem item = (FileItem) itr.next();
                if (!item.isFormField() && item.getFieldName().equals(fieldname))
                {
                	String filepath = item.getName();
                    if(filepath !=null)
                    {
                        File fullFile = new File(filepath);
                        backupOldFile(destpath, fullFile.getName());
                        File savedFile = new File(destpath, fullFile.getName());
                        item.write(savedFile);
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setCharacterEncoding("utf-8");
                        response.setContentType("text/html");
                        response.getWriter().write(
                        		"<script language='javascript'>alert('文件上传成功!');</script>");
                    }
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
        
	}
	
	private void backupOldFile(String destpath, String filename)
	{
		File oldFile = new File(destpath, filename);
        if (!oldFile.exists())
        {
        	return;            
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date();
        String datetemp = format.format(curDate);
        int idx = filename.lastIndexOf(".");
        String fn = filename.substring(0, idx);
        String backupname = String.format("%s%s.xml", fn, datetemp);
        File backup = new File(destpath, backupname);
        oldFile.renameTo(backup);
	}
	
	public static String convertReaderToString(Reader reader)
    {
        BufferedReader r = new BufferedReader(reader); 
        StringBuffer b = new StringBuffer(); 
        String line; 
        try{
            while((line = r.readLine()) != null) { 
                b.append(line); 
                b.append("\r\n"); 
            }
        }
        catch(Exception e)
        {
             e.printStackTrace();
        }
        return b.toString(); 
    }
}
