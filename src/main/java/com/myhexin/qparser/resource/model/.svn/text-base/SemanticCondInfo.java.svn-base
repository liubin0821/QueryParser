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


/**
 * 语义转Condition 配置信息
 * 
 * 1. parser_semantic_ops 语义对应的父类级别的OP信息, 比如sort
 * 
 * 2. parser_semantic_index_ops指标级别的op信息,比如 pe>19
 * 
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-5-7
 *
 */
public class SemanticCondInfo implements ResourceInterface{

	private static SemanticCondInfo instance = new SemanticCondInfo();
	private Map<Integer, SemanticOpModel> semanticOpMap;
	private Set<String> allowOutputProps;
	private Set<String> alwaysOutputProps;
	
	private SemanticCondInfo() {
		
	}
	
	public boolean isAllowOutputProp(String name) {
		if(allowOutputProps==null) return false;
		
		return allowOutputProps.contains(name);
	}
	
	public boolean isAlwaysOutputProp(String name) {
		if(alwaysOutputProps==null) return false;
		
		return alwaysOutputProps.contains(name);
	}
	
	public static SemanticCondInfo getInstance() {
		return instance;
	}
	
	
	public boolean isCompareSemantic(int semanticId) {
		SemanticOpModel op = semanticOpMap.get(semanticId);
		if(op!=null && op.getOpClazzName()!=null && op.getOpClazzName().equals("COMP")) {
			return true;
		}else{
			return false;
		}
	}
	
	public SemanticOpModel getSemanticOpInfo(Integer semanticId) {
		return semanticOpMap.get(semanticId);
	}
	
	@Override
	public void reload() {
		semanticOpMap = new HashMap<Integer, SemanticOpModel>(); 
		
		
		DataSource ds = ApplicationContextHelper.getBean("dataSource");
		Connection con = null;
		try{
			con = ds.getConnection();
			PreparedStatement pstmt = con.prepareStatement("SELECT semantic_id,ch_fulltext,ex_word,opClazzName, opName,opProperty,opSonSize FROM parser_cond_ops");
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int semantic_id = rs.getInt(1);
				String ch_fulltext = rs.getString(2);
				String ex_word = rs.getString(3);
				String opClazzName = rs.getString(4);
				String opName = rs.getString(5);
				String opProperty = rs.getString(6);
				int opSonSize = rs.getInt(7);
				
				SemanticOpModel op = new SemanticOpModel();
				op.setSemanticId(semantic_id);
				op.setChineseText(ch_fulltext);
				op.setKeyWord(ex_word);
				op.setOpClazzName(opClazzName);
				op.setOpName(opName);
				op.setOpProperty(opProperty);
				op.setOpSonSize(opSonSize);
				
				semanticOpMap.put(semantic_id, op);
				
			}

			rs.close();
			pstmt.close();
			
			
			//indexOP
			pstmt = con.prepareStatement("SELECT semantic_id,index_op_nm,index_op_val FROM parser_cond_index_ops");
			rs = pstmt.executeQuery();
			while(rs.next()) {
				int semantic_id = rs.getInt(1);
				String index_op_nm = rs.getString(2);
				String index_op_val = rs.getString(3);
				SemanticOpModel op = semanticOpMap.get(semantic_id);
				if(op!=null) {
					op.addIndexOp(index_op_nm, index_op_val);
				}
			}
			rs.close();
			pstmt.close();
			
			//允许输出的属性
			allowOutputProps = new HashSet<String>();
			alwaysOutputProps = new HashSet<String>();
			pstmt = con.prepareStatement("SELECT prop_nm, flg_always FROM parser_cond_props");
			rs = pstmt.executeQuery();
			while(rs.next()) {
				String index_op_nm = rs.getString(1);
				int always = rs.getInt(2);
				allowOutputProps.add(index_op_nm);
				if(always==1) {
					alwaysOutputProps.add(index_op_nm);
				}
			}
			rs.close();
			pstmt.close();
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
