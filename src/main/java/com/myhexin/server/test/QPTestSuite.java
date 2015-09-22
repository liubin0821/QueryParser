package com.myhexin.server.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;


public class QPTestSuite {

	private List<QPTestCase> testcaseList = new ArrayList<QPTestCase>();
	public QPTestSuite(){
		testcaseList.add(new ConditionTestCase());
	}
	
	static class TestVO {
		public int id;
		public String query;
		public String status;
		public String qType;
		public String domain;
		public String postDataStr;
		public String exp_result;
		public String clazzName;
		public String p1;
		public String p2;
		public String p3;
		public String p4;
		public String p5;
	}
	
	public void update(List<TestVO> list) {
		DataSource ds = ApplicationContextHelper.getBean("dataSource");
		Connection con = null;
		try{
			con = ds.getConnection();
			con.setAutoCommit(false);
			PreparedStatement pstmt = con.prepareStatement("UPDATE configFile.parser_test SET status=?, last_upd_dt=now() WHERE ID=?");
			for(TestVO vo : list) {
				pstmt.setString(1, vo.status);
				pstmt.setInt(2, vo.id);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			con.commit();
		}catch(Exception e) {
			
		}finally {
			if(con!=null) {
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public List<TestVO> getTestList() {
		System.out.println("start load testcase.");
		DataSource ds = ApplicationContextHelper.getBean("dataSource");
		Connection con = null;
		List<TestVO> list = new ArrayList<TestVO>();
		try{
			con = ds.getConnection();
			PreparedStatement pstmt = con.prepareStatement("SELECT ID, query, qType, domain, postDataStr, exp_result, clazzName, p1, p2, p3, p4, p5 from configFile.parser_test");
			ResultSet rs = pstmt.executeQuery();
			while( rs.next()) {
				TestVO v = new TestVO();
				list.add(v);
				v.id = rs.getInt(1);
				v.query = rs.getString(2);
				v.qType = rs.getString(3);
				v.domain = rs.getString(4);
				v.postDataStr = rs.getString(5);
				v.exp_result = rs.getString(6);
				v.clazzName = rs.getString(7);
				v.p1 = rs.getString(8);
				v.p2 = rs.getString(9);
				v.p3 = rs.getString(10);
				v.p4 = rs.getString(11);
				v.p5 = rs.getString(12);
			}
		}catch(Exception e) {
			
		}finally {
			if(con!=null) {
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println("load testcase completed.");
		return list;
	}
	
	
	public void testsuite()  {
		List<String> errors= new ArrayList<String>();
		int totalCase = 0;
		int ok = 0;
		
		List<TestVO> list = getTestList();
		totalCase = list.size();
		for(TestVO testVo : list) {
			try{
				System.out.println("runing test " + testVo.query + "," + testVo.clazzName);
				QPTestCase test = (QPTestCase)Class.forName("com.myhexin.server.test." + testVo.clazzName).newInstance();
				if(test.test(testVo) ) {
					ok++;
					testVo.status="Pass";
				}else{
					testVo.status="Failed";
				}
				errors.addAll(test.errors());
			}catch(Exception e){
				errors.add(String.format("[exception] query=%s, e=%s", testVo.query, e.getMessage()));
			}
		}
		update(list);
		System.out.println();
		System.out.println("###TEST SUITE REPORT###");
		System.out.println(String.format("###total=%d, ok=%d, failed=%d",  totalCase, ok, (totalCase-ok)) );
		
		for(String error : errors) {
			System.out.println("[error]" + error);
		}
		
		
		
		System.out.println(String.format("###total=%d, ok=%d, failed=%d",  totalCase, ok, (totalCase-ok)) );
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("START");
		ApplicationContextHelper.loadApplicationContext();
		System.out.println("Spring init completed");
		new QPTestSuite().testsuite();
		
		System.out.println("All done.");
	}

}
