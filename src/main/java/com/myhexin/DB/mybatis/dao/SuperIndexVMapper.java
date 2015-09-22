package com.myhexin.DB.mybatis.dao;

import java.util.List;

import com.myhexin.DB.mybatis.mode.SuperIndexV;

public interface SuperIndexVMapper {
	
	public List<SuperIndexV> selectByIndexId(Integer indexId);
	public List<SuperIndexV> selectAll();
}