package com.myhexin.qparser.util.itoperation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.myhexin.qparser.util.Util;
import com.myhexin.qparser.xmlreader.XmlReader;

public class DataSqlUtil {

	public static void main(String[] args) throws Exception {
		//generateDefaultDatesSql();
		generateTechOpSql();
	}
	
	public static void generateTechOpSql() throws Exception {
		FileOutputStream fos = new FileOutputStream("C:/sql.log");
		PrintWriter out = new PrintWriter(fos);
		Document doc = Util.readXMLFile("C:/stock_index.xml");
		Element root = doc.getDocumentElement();
		ArrayList<Node> nodes = XmlReader.getChildElementsByTagName(root, "index");
		for(int i=0;i<nodes.size();i++) {
			Node node = nodes.get(i);
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			
			Element elem  = (Element) node;
			String id = elem.getAttribute("id");
			String name =  elem.getAttribute("title");
			String sql  = "INSERT INTO configFile.legacy_idxId_names(idxId, idxName) VALUES('" + id + "','" + name + "');";
			System.out.println(sql);
			out.println(sql);
		}
		out.close();
	}
	
	/**
	 * 生成时效时间的SQL
	 * @throws IOException 
	 */
	public static void generateDefaultDatesSql() throws IOException {
		String sql1 = "INSERT  INTO parser_ref_code(attr_id, code_value, code_short_desc, code_long_desc) VALUES(1003, '";
		String sql2 = "', 'hhmmss', NULL);";
		
		
		String s1 = "INSERT INTO parser_datetime_defaults(key_name, dt_format, dt_from,dt_to,dt_val,offset_val,sign_val) VALUES ('";
		String s2 = "','hhmmss','000001','153000','yyMMdd',1,'-');";
		
		String q1 = "INSERT INTO parser_datetime_defaults(key_name, dt_format, dt_from,dt_to,dt_val,offset_val,sign_val) VALUES ('";
		String q2  ="','hhmmss','153000','235959','yyMMdd',0,'+');";
		
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("C:/indexes.txt")));
		String s = null;
		System.out.println("start");
		while( (s=br.readLine())!=null) {
			System.out.println(sql1 + s + sql2);
			System.out.println(s1 + s + s2);
			System.out.println(q1 + s + q2);
			System.out.println();
			
		}
		System.out.println("end");
		br.close();
	}
}
