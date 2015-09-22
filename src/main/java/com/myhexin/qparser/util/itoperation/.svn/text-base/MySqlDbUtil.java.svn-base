package com.myhexin.qparser.util.itoperation;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.myhexin.qparser.util.condition.ConditionBuilderUtil;

/**
 * 抽样跑问句 1. 从sampling_queries表随机抽取n个问句, n=args[0] 2.
 * 跑解析,拿到句式语义,分数,存入sampling_results表 3. 请通过servlet, xxx:9100/sampling 查看结果
 * 
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-4-16
 * 
 */
public class MySqlDbUtil {
	static{
		initProp();
		
	}
	public static void initProp() {
		InputStream ins = null;
		try {
			ins = MySqlDbUtil.class.getClassLoader()
					.getResourceAsStream("DB/DB.properties");
			Properties prop = new Properties();
			prop.load(ins);
			mysql_driverClass = prop.getProperty("mysql.driverClass");
			mysql_jdbcUrl = prop.getProperty("mysql.jdbcUrl");
			mysql_username = prop.getProperty("mysql.user");
			mysql_password = prop.getProperty("mysql.password");

		} catch (Exception e) {
			System.out.println("Load JDBC Properties failed :  "
					+ e.getMessage());
			e.printStackTrace();

		} finally {
			if (ins != null) {
				try {
					ins.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	// 测试用的，不要写到mybatis中去
	private static String mysql_driverClass = null;
	private static String mysql_jdbcUrl = null;
	private static String mysql_username = null;
	private static String mysql_password = null;

	private static Connection getConnection() throws Exception {
		if (mysql_driverClass == null || mysql_jdbcUrl == null
				|| mysql_username == null || mysql_password == null) { return null; }

		Class.forName(mysql_driverClass);
		Connection con = DriverManager.getConnection(mysql_jdbcUrl,
				mysql_username, mysql_password);
		return con;
	}

	
	public static void deleteSamplingResult() {
		Connection con = null;
		try{
			con = getConnection();
			if(con==null) return;
			PreparedStatement pstmt = con.prepareStatement("DELETE FROM configFile.sampling_results");
			pstmt.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(con!=null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void insertSemanticOps(int id, String text, String word, String opName, String opProp, String sonSize) {
		Connection con = null;
		String sql = "INSERT INTO parser_semantic_ops (semantic_id, ch_fulltext, ex_word, opName, opProperty, opSonSize) VALUES(?,?,?,?,?,?)";
		try{
			con = getConnection();
			if(con==null) return;
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.setString(2, text);
			pstmt.setString(3, word);
			pstmt.setString(4, opName);
			pstmt.setString(5, opProp);
			pstmt.setString(6, sonSize);
			pstmt.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(con!=null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void insertIndexOps(int id,  String text, String indexOp, String indexOpVal) {
		Connection con = null;
		String sql = "INSERT INTO parser_semantic_index_ops (semantic_id,ch_fulltext, index_op_nm, index_op_val) VALUES(?,?,?,?)";
		try{
			con = getConnection();
			if(con==null) return;
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.setString(2, text);
			pstmt.setString(3, indexOp);
			pstmt.setString(4, indexOpVal);
			pstmt.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(con!=null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args){
		initProp();
		generateMultiTechOp();
	}
	
	public static void generateMultiTechOp() {
		System.out.println("IN");
		Connection con = null;
		String sql = "SELECT name,tech_op FROM ontologydb.tech_idxes_info WHERE idx_type='TECH_LINES'";
		try{
			con = getConnection();
			if(con==null) return;
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				String s1 = rs.getString(1);
				String ss = rs.getString(2);
				//System.out.println(s1 + ", " + ss);
				
				//["上穿","下穿","卖出信号","高位金叉","拐头向下","顶背离","底背离","买入信号","二次金叉"]
				if(ss!=null) {
					if(ss.charAt(0)=='[') ss= ss.substring(1);
					if(ss.charAt(ss.length()-1)==']') ss= ss.substring(0, ss.length()-1);
					
					String[] a = ss.split(",");
					for(String s2 : a) {
						if(s2.charAt(0)=='"') s2= s2.substring(1);
						if(s2.charAt(s2.length()-1)=='"') s2= s2.substring(0, s2.length()-1);
						System.out.println(String.format("INSERT INTO parser_multilineindex_techop(index_name, tech_op_name) VALUES('%s','%s');", s1, s2));
					}
				}
				
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(con!=null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("OUT");
	}
	
	public static void getTechIndexAndOps() {
		System.out.println("IN");
		Connection con = null;
		String sql = "SELECT name,tech_op FROM ontologydb.tech_idxes_info WHERE tech_op IS NOT NULL";
		try{
			con = getConnection();
			if(con==null) return;
			
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			
			Map<String, List<String> > map = new HashMap<String, List<String>>();
			
			while(rs.next()) {
				String s1 = rs.getString(1);
				String ss = rs.getString(2);
				//System.out.println(s1 + ", " + ss);
				
				//["上穿","下穿","卖出信号","高位金叉","拐头向下","顶背离","底背离","买入信号","二次金叉"]
				if(ss!=null) {
					if(ss.charAt(0)=='[') ss= ss.substring(1);
					if(ss.charAt(ss.length()-1)==']') ss= ss.substring(0, ss.length()-1);
					
					String[] a = ss.split(",");
					for(String s2 : a) {
						if(s2.charAt(0)=='"') s2= s2.substring(1);
						if(s2.charAt(s2.length()-1)=='"') s2= s2.substring(0, s2.length()-1);
						
						List<String> list = map.get(s2);
						if(list==null) {
							list = new ArrayList<String>();
							map.put(s2, list);
						}
						if(list.contains(s1)==false)
							list.add(s1);
					}
				}
				
				
			}
			rs.close();
			pstmt.close();
			
			//子线
			pstmt = con.prepareStatement("SELECT subline_tech_op FROM ontologydb.tech_idxes_info WHERE subline_tech_op IS NOT NULL");
			rs = pstmt.executeQuery();
			while(rs.next()) {
				String s1 = rs.getString(1);
				//System.out.println(s1);
				JSONObject json = new JSONObject(s1);
				Set<String> keys = json.keySet();
				for(Iterator<String> it = keys.iterator(); it.hasNext();) {
					String k= it.next();
					JSONArray arr = json.getJSONArray(k);
					for(int j=0;j<arr.length();j++) {
						String s2 = (String)arr.get(j);
						List<String> list = map.get(s2);
						if(list==null) {
							list = new ArrayList<String>();
							map.put(s2, list);
						}
						if(list.contains(k)==false)
							list.add(k);
					}
					
				}
				
			}
			//<index-group title="市净率" default="市净率(pb,最新)">
	        //	<member>市净率(pb)</member>
	        //	<member>市净率(pb,最新)</member>
	        //	</index-group>
			//<IndexGroup id="37">
		    //<Index>货币资金</Index>
		    //<Description>货币资金</Description>
		  //</IndexGroup>
			//打印结果
			int i =0;
			int id = 45;
			List<String> ll = new ArrayList<String>();
			for(Iterator<String> it = map.keySet().iterator();it.hasNext();) {
				String k = it.next();
				List<String> v = map.get(k);
				StringBuilder buf  = new StringBuilder();
				i++;
				/*buf.append("<IndexGroup id=\""+id+"\">\n");
				for(String s : v) {
					buf.append("<Index>"+s.toLowerCase()+"</Index>\n");
				}
				buf.append("<Description>"+k.toLowerCase()+"</Description>\n");
				buf.append("</IndexGroup>\n");*/
				buf.append("insert into indexgroup(description, indexs, created_at, updated_at, created_by, updated_by) values(");
				buf.append('\'').append(k.toLowerCase()).append('\'').append(',');
				
				Gson gson = new Gson();
				String s= ConditionBuilderUtil.unicodeEsc2Unicode(gson.toJson(v));
				buf.append('\'').append(s.toLowerCase()).append('\'').append(',');
				buf.append(0).append(',');
				buf.append(0).append(',');
				buf.append("'script'").append(',');
				buf.append("NULL").append(");");
				
				//System.out.println(buf);
				ll.add(buf.toString());
				id++;
			}
			System.out.println(i);
			/*Set<String> list = map.keySet();
			for(String s : list) {
				System.out.print("\""  +s + "\",");
			}*/
			
			for(i=ll.size()-1;i>=0;i--) {
				System.out.println(ll.get(i));
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(con!=null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("OUT");
	}
}
