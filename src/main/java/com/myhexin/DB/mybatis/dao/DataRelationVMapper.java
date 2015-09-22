package com.myhexin.DB.mybatis.dao;

import java.util.List;

import com.myhexin.DB.mybatis.mode.DataRelationV;
import com.myhexin.DB.mybatis.mode.IndexPropV2;

public interface DataRelationVMapper {
	
	public List<DataRelationV> selectByPropId(Integer propId);
	public List<DataRelationV> selectAll();
}