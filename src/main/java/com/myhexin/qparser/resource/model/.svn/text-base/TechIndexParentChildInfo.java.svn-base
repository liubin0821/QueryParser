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
 * 技术指标字母线关系
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-5-6
 *
 */
public class TechIndexParentChildInfo implements ResourceInterface{
	private static TechIndexParentChildInfo instance = new TechIndexParentChildInfo();
	private Map<String, Set<String>> parentChildMap;
	private Set<String> childTechIndexNameMap;
	private TechIndexParentChildInfo() {
		
	}
	
	public static TechIndexParentChildInfo getInstance() {
		return instance;
	}
	
	public boolean isChildTechIndexName(FocusNode fNode) {
		if(childTechIndexNameMap==null) return false;
		
		if(childTechIndexNameMap.contains(fNode.getText() ) ) {
			return true;
		}
		
		if(fNode.hasIndex() && fNode.getIndex()!=null && childTechIndexNameMap.contains(fNode.getIndex().getText())) {
			return true;
		}
		
		return false;
	}
	
	public boolean isParentChildMatch(FocusNode parent, FocusNode child) {
		if(parentChildMap==null) return false;
		Set<String> childNames = parentChildMap.get(parent);
		
		if(childNames==null && parent.hasIndex() && parent.getIndex()!=null) {
			childNames = parentChildMap.get(parent.getIndex().getText());
		}
		
		if(childNames!=null && childNames.contains(child.getText())) {
			return true;
		}
		
		if(child.hasIndex() && child.getIndex()!=null && childNames!=null && childNames.contains(child.getIndex().getText())) {
			return true;
		}
		
		
		return false;
	}
	
	@Override
	public void reload() {
		parentChildMap = new HashMap<String, Set<String>>(); 
		childTechIndexNameMap = new HashSet<String>();
		
		DataSource ds = ApplicationContextHelper.getBean("dataSource");
		Connection con = null;
		try{
			con = ds.getConnection();
			PreparedStatement pstmt = con.prepareStatement("SELECT index_name, child_index_nm FROM parser_techindex_zimu");
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				String index_name = rs.getString(1);
				String tech_op_name = rs.getString(2);
				
				Set<String> childNames = parentChildMap.get(index_name);
				if(childNames==null) {
					childNames = new HashSet<String>();
					parentChildMap.put(index_name, childNames);
				}
				childNames.add(tech_op_name);
				childTechIndexNameMap.add(tech_op_name);
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
