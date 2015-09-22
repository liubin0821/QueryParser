package com.myhexin.qparser.similarity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConfigFileMysqlInsert {
	
	private final static String mysql_driverClass = "com.mysql.jdbc.Driver";
	private final static String mysql_jdbcUrl = "jdbc:mysql://172.20.201.147/configFile";
	private final static String mysql_username = "qnateam";
	private final static String mysql_password = "qnateam";

	private static Connection getConnection() throws Exception {
		Class.forName(mysql_driverClass);
		Connection con = DriverManager.getConnection(mysql_jdbcUrl,
				mysql_username, mysql_password);
		return con;
	}
	
	/**
	 * 取到是测试环境的机器的IP
	 * 
	 * @return
	 */
	private static List<String> insertLines(String fileName, List<String> list) {
		String sql = "INSERT INTO configFile.parser_configfiles (file_name, line, seq) VALUES (?,?,?)";
		Connection con = null;
		List<String> nodes = new ArrayList<String>();
		try {
			con = getConnection();
			con.setAutoCommit(false);
			PreparedStatement pstmt = con.prepareStatement(sql);
			int i=1;
			for(String s : list) {
				pstmt.setString(1, fileName);
				pstmt.setString(2, s);
				pstmt.setInt(3, i);
				pstmt.addBatch();
				i++;
				if(i%500==0) {
					pstmt.executeBatch();
					con.commit();
					System.out.println(i);
				}
				
			}
			pstmt.executeBatch();
			con.commit();
			System.out.println(i);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.setAutoCommit(true);
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nodes;
	}

	public static void readFile(String fileName, String filePath) {
		//StringBuilder builder = new StringBuilder();
		BufferedReader br = null;
		try {
		br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));
		String s = null;
		
		List<String> list = new ArrayList<String>();
		
		while ((s = br.readLine()) != null) {
			list.add(s);
		}
		System.out.println(list.size());
		insertLines(fileName, list);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(br!=null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		//return builder.toString();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//readFile("./src/test/java/com/myhexin/qparser/similarity/npatternInfo.txt");
		
		/*readFile("fund_code_baseinfo.txt", "./src/test/java/com/myhexin/qparser/similarity/fund_code_baseinfo.txt");
		readFile("fundmanager_code_baseinfo.txt", "./src/test/java/com/myhexin/qparser/similarity/fundmanager_code_baseinfo.txt");
		readFile("report_code_baseinfo.txt", "./src/test/java/com/myhexin/qparser/similarity/report_code_baseinfo.txt");
		readFile("stock_code_baseinfo.txt", "./src/test/java/com/myhexin/qparser/similarity/stock_code_baseinfo.txt");
		*/
		
		//readFile("ignorable_words.txt", "./src/test/java/com/myhexin/qparser/similarity/ignorable_words.txt");
		
		String[] files = new String[]{
		"dp1CharsChinese.txt",
		"dp1CharsLading.txt",
		"dp2DictsChinese.txt",
		"dp2DictsEnglish.txt",
		"dp2DictsIgnore.txt",
		"dp2DictsJapanese.txt",
		"dp2DictsSystem.txt",
		"dp3Patterns.txt",
		};
		
		for(String s  : files) {
			readFile(s, "./src/test/java/com/myhexin/qparser/similarity/dynamic/" + s);
		}
		
	}

}
