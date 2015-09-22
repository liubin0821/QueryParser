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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import freemarker.template.Template;

public class TestServlet extends TemplateServlet {

	private static final long serialVersionUID = 1L;
	public static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(ConfigCheckServlet.class.getName());
	private static Object lock = new Object();

	@Override
	public void init() throws ServletException {
		super.init();
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		super.doGet(request, response);
		response.setContentType("text/html");
		try {
			Map<String, Object> root = new HashMap<String, Object>();
			Template template = cfg_.getTemplate("test.ftl");
			PrintWriter out = response.getWriter();
			template.process(root, out);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		try {
			String destpath = "temp_test/";
			List<FileItem> items = upload.parseRequest(request);
			Iterator<FileItem> itr = items.iterator();
			String fieldname = "fileinput";
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				if (!item.isFormField()
						&& item.getFieldName().equals(fieldname)) {
					String filepath = item.getName();
					if (filepath != null) {
						File fullFile;
						String backName;
						synchronized (lock) {
							fullFile = new File(filepath);
							backName = backupOldFile(destpath,
									fullFile.getName());
							File savedFile = new File(destpath, backName);
							item.write(savedFile);
						}
						String name;
						if(backName.contains("knowledge")){
							name = new TestKnowledge().test(destpath, backName);
						}else if(backName.contains("hghy")){
							name = new TestHghy().test(destpath, backName);
						}else if(backName.contains("hkstock")){
							name = new TestHkstock().test(destpath, backName);
						}else{
							name = new TestDetail().test(destpath, backName);
						}
						response.setStatus(HttpServletResponse.SC_OK);
						response.setCharacterEncoding("utf-8");
						response.setContentType("text/html");
						response.getWriter()
								.write("<script language='javascript'>alert('文件上传成功!');window.location.href='../temp_test/"
										+ name + "';</script>");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger_.error(e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html");
			response.getWriter().write("<script language='javascript'>alert('文件解析失败，请联系开发查看问题！');</script>");
		}
	}

	private String backupOldFile(String destpath, String filename) {
		File oldFile = new File(filename);
		if (!oldFile.exists()) {
			return filename;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date();
		String datetemp = format.format(curDate);
		int idx = filename.lastIndexOf(".");
		String fn = filename.substring(0, idx);
		String backupname = String.format("%s%s.txt", fn, datetemp);
		File backup = new File(destpath, backupname);
		oldFile.renameTo(backup);
		return backupname;
	}

	public static String convertReaderToString(Reader reader) {
		BufferedReader r = new BufferedReader(reader);
		StringBuffer b = new StringBuffer();
		String line;
		try {
			while ((line = r.readLine()) != null) {
				b.append(line);
				b.append("\r\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b.toString();
	}
}
