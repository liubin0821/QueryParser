package com.myhexin.qparser.date;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import com.myhexin.qparser.date.bean.DateNodeParserInfo;
import com.myhexin.qparser.date.bean.DateSyntaxInfo;
import com.myhexin.qparser.date.bean.DateWordInfo;

public class DatabaseUtil {

	public static Connection getConnection() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		String url="jdbc:mysql://172.20.201.147:3306/configFile";

		Connection con = DriverManager.getConnection(url, "qnateam", "qnateam");
		return con;
	}
	
	public static void clearAllDB()  throws Exception {
		Connection con =  getConnection();
		con.setAutoCommit(false);
		Statement stmt = con.createStatement();
		stmt.executeUpdate("DELETE FROM dateparser_pattern");
		con.commit();
		
		stmt = con.createStatement();
		stmt.executeUpdate("DELETE FROM dateparser_keywords");
		con.commit();
		con.setAutoCommit(true);
		con.close();
	}
	
	public static void loadNodeParserInfoXML() throws Exception {
		List<DateNodeParserInfo> list = null;//DateTimeNodeProcessor.patterns;
		Connection con =  getConnection();
		con.setAutoCommit(false);
		PreparedStatement pstmt = con.prepareStatement("INSERT INTO dateparser_pattern (nm, regex, typ, parser) VALUES (?,?,?,?)");
		for(DateNodeParserInfo node :list) {
			pstmt.setString(1, "NodeParserInfo");
			pstmt.setString(2, node.getPatternStr()) ;
			pstmt.setString(3, node.getType()+"");
			pstmt.setString(4, node.getParserName());
			pstmt.addBatch();
		}
		pstmt.executeBatch();
		con.commit();
		con.setAutoCommit(true);
		con.close();
	}
	

	public static void loadSyntaxParserXML() throws Exception {
		List<DateSyntaxInfo> list = null;//DateTimeParserUtil.syntaxPatterns;
		Connection con =  getConnection();
		con.setAutoCommit(false);
		PreparedStatement pstmt = con.prepareStatement("INSERT INTO dateparser_pattern (nm, regex, typ, parser) VALUES (?,?,?,?)");
		for(DateSyntaxInfo node :list) {
			pstmt.setString(1, "Syntax");
			pstmt.setString(2, node.getPatternRegex());
			pstmt.setString(3, "");
			pstmt.setString(4, node.getClazzName());
			pstmt.addBatch();
		}
		pstmt.executeBatch();
		con.commit();
		con.setAutoCommit(true);
		con.close();
	}
	
	
	public static void loadKeywords() throws Exception {
		List<DateWordInfo> list = null;//DateTimeParserUtil.wordList;
		Connection con =  getConnection();
		con.setAutoCommit(false);
		PreparedStatement pstmt = con.prepareStatement("INSERT INTO dateparser_keywords (word, value, len) VALUES (?,?,?)");
		for(DateWordInfo node :list) {
			pstmt.setString(1, node.getWord());
			pstmt.setInt(2, node.getRelativeNum());
			pstmt.setInt(3,node.getLen());
			pstmt.addBatch();
		}
		pstmt.executeBatch();
		con.commit();
		con.setAutoCommit(true);
		con.close();
	}
	
	public static void syncFromDBtoXml() throws Exception {
		
	}
	
	public static void syncFromXmltoDB() throws Exception {
		clearAllDB();
		loadNodeParserInfoXML();
		loadSyntaxParserXML();
		loadKeywords();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		//syncFromXmltoDB();
	}

}
