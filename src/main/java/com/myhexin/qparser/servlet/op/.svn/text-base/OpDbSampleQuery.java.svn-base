package com.myhexin.qparser.servlet.op;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class OpDbSampleQuery {
	
	public int countSample() {
		Connection con = null;
		try {
			con = OpDb.getConnection();
			if (con == null) return 0;

			PreparedStatement pstmt = con.prepareStatement("SELECT count(ID) FROM configFile.sampling_queries");
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {

		}finally{
			if(con!=null) {
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return 0;
	}
	

	public static void main(String[] args) {
		OpDbSampleQuery q = new OpDbSampleQuery();
		q.getSampleQueries(0, 1000);
	}
	
	public void updateResult(List<Rt> rtList) {
		Connection con = null;
		try{
			con = OpDb.getConnection();
			if(con==null) return;
			con.setAutoCommit(false);
			PreparedStatement pstmt = con.prepareStatement("UPDATE configFile.sampling_queries set synt_semantic_info1=?,score1=?,standard_q1=?,run1_dt=now(), used_time1=? WHERE ID=?");
			for(Rt rt : rtList) {
				if(rt.syntSemanticInfo.length()>100) {
					rt.syntSemanticInfo = rt.syntSemanticInfo.substring(0,99);
				}
				pstmt.setString(1, rt.syntSemanticInfo);
				pstmt.setInt(2, rt.score);
				if(rt.stdQuery.length()>300) rt.stdQuery = rt.stdQuery.substring(0,299);
				pstmt.setString(3, rt.stdQuery);
				pstmt.setInt(4, rt.used_time);
				pstmt.setInt(5, rt.id);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(con!=null) {
				try {
					con.commit();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public String getQuery(int id) {
		Connection con = null;
		try {
			con = OpDb.getConnection();
			if (con == null) return null;

			String query1 = "SELECT query FROM configFile.sampling_queries WHERE ID=?";
			PreparedStatement pstmt = con.prepareStatement(query1);
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(con!=null) {
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	private final static SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd kk:mm:ss");
	public List<String[]> getSampleQueries(int start, int batchSize) {
		Connection con = null;
		try {
			con = OpDb.getConnection();
			if (con == null) return null;

			List<String[]> list = new ArrayList<String[]>();
			String query1 = "SELECT ID,query,synt_semantic_info1,score1,standard_q1,run1_dt, used_time1 FROM configFile.sampling_queries ORDER BY ID LIMIT "+ start + "," + batchSize;
			PreparedStatement pstmt = con.prepareStatement(query1);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String id = rs.getString(1);
				String query = rs.getString(2);
				String a = rs.getString(3);
				String b = rs.getString(4);
				String c = rs.getString(5);
				Timestamp d = rs.getTimestamp(6);
				String e1 = rs.getString(7);
				String ds = null;
				if(d!=null) {
					try{
					ds = format.format(d);
					}catch(Exception e){}
				}
				list.add(new String[] { id, query,a,b,c, ds, e1 });
			}
			//System.out.println("query=" + query1);
			//System.out.println("list=" + list.size());
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(con!=null) {
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public void deleteSampleQuery(int id) {
		Connection con = null;
		try {
			con = OpDb.getConnection();
			if (con == null) return;
			PreparedStatement pstmt = con.prepareStatement("DELETE FROM configFile.sampling_queries WHERE ID=?");
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void save(String[] qs) {
		Connection con = null;
		String sql = "INSERT INTO configFile.sampling_queries (query,status) VALUES(?,0)";
		try{
			con =  OpDb.getConnection();
			if(con==null) return;
			
			con.setAutoCommit(false);
			PreparedStatement pstmt = con.prepareStatement(sql);
			for(String s: qs){
				pstmt.setString(1, s);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(con!=null) {
				try {
					con.commit();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
