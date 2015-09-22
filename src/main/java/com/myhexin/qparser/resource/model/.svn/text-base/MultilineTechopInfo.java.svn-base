package com.myhexin.qparser.resource.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.node.FocusNode;


/**
 * 多线指标和及其技术形态的对应关系
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-5-6
 *
 */
public class MultilineTechopInfo implements ResourceInterface{

	private static MultilineTechopInfo instance = new MultilineTechopInfo();
	private Map<String, Set<String>> multilineTechopMap;
	private Set<String> techOpSet;
	private MultilineTechopInfo() {
		
	}
	
	public static MultilineTechopInfo getInstance() {
		return instance;
	}
	
	public boolean isTechOpName(FocusNode focusNode) {
		if(techOpSet==null) return false;
		
		if(techOpSet.contains(focusNode.getText())) {
			return true;
		}
		
		if(focusNode.hasIndex() && focusNode.getIndex()!=null && techOpSet.contains(focusNode.getIndex().getText())) {
			return true;
		}
		
		return false;
		
	}
	
	public boolean isMultilineIndex(FocusNode focusNode) {
		if(multilineTechopMap==null) return false;
		
		if(multilineTechopMap.containsKey(focusNode.getText())) {
			return true;
		}
		
		if(focusNode.hasIndex() && focusNode.getIndex()!=null && multilineTechopMap.containsKey(focusNode.getIndex().getText())) {
			return true;
		}
		
		return false;
	}
	
	public boolean isIndexOpMatch(FocusNode index_name, FocusNode opName) {
		if(multilineTechopMap==null) return false;
		
		Set<String> opNames = multilineTechopMap.get(index_name.getText());
		if(opNames==null && index_name.hasIndex() && index_name.getIndex()!=null ) {
			opNames = multilineTechopMap.get(index_name.getIndex().getText());
		}
		
		if(opNames!=null ) {
			if(opNames.contains(opName.getText())) {
				return true;
			}
			
			if(opName.hasIndex() && opName.getIndex()!=null && opNames.contains(opName.getIndex().getText())) 
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 重新加载配置
	 * 
	 */
	@Override
	public void reload() {
		multilineTechopMap = new HashMap<String, Set<String>>(); 
		techOpSet = new HashSet<String>();
		
		DataSource ds = ApplicationContextHelper.getBean("dataSource");
		Connection con = null;
		try{
			con = ds.getConnection();
			PreparedStatement pstmt = con.prepareStatement("SELECT index_name,tech_op_name FROM parser_multilineindex_techop");
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				String index_name = rs.getString(1);
				String tech_op_name = rs.getString(2);
				
				Set<String> tech_op_names = multilineTechopMap.get(index_name);
				if(tech_op_names==null) {
					tech_op_names = new HashSet<String>();
					multilineTechopMap.put(index_name, tech_op_names);
				}
				tech_op_names.add(tech_op_name);
				techOpSet.add(tech_op_name);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(con!=null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
