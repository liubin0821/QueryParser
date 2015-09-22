package com.myhexin.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;


public class PhraseViewServlet  extends HttpServlet {
	private static final org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(PhraseViewServlet.class.getName());


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
        
        //if(request.getParameter("qid")!=null)
        //	logger_.info("qid="+request.getParameter("qid"));
        

		request.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html");     

        String query = request.getParameter("q");
        query = query == null ? request.getParameter("query") : query;
        if (query != null && query.length() > 0)
        {
        	getRelatedPhrase(response, query);
        }
        else
        {
        	openPhrase(response, "data/stock/stock_phrase.xml");
        }
	}
	
	private String getRelatedPhrase(HttpServletResponse response, String query) throws IOException
	{
		StringBuffer sb = new StringBuffer();
		Document document = RelatePhrase.getRelatePhrases(query);
		try {
			String tempPath = "temp/temp.xml";
			
			writeXML(document, tempPath);
			openPhrase(response, tempPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	private void writeXML(Document doc, String file) 
			throws TransformerException, IOException
	{
	    File fileObject = new File(file);
	    File dir = new File(fileObject.getParent());
	    if (!dir.exists()) {
	        dir.mkdirs();
	    }
	    if (!fileObject.exists()) {
	        fileObject.createNewFile();
	    }
		Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty("indent", "yes");
        t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(file)));
    }
	
	private void openPhrase(HttpServletResponse response, String filePath)
	{		
		File f = new File(filePath); 
		byte[] buf = new byte[1024];
		int len = 0;
		response.reset(); 
		try {
			BufferedInputStream br = new BufferedInputStream(new FileInputStream(f));
			URL u = new URL("file:///" + filePath);
			response.setContentType(u.openConnection().getContentType());
//	        response.setContentType("text/xml");
			response.addHeader("Content-Disposition", "inline; filename=" + f.getName());
			OutputStream out = response.getOutputStream();
	        while ((len = br.read(buf)) > 0)
	            out.write(buf, 0, len);
	        br.close();
	        out.close();
			        
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
