package com.myhexin.DB.mybatis.dao;

import java.util.List;

import com.myhexin.DB.mybatis.mode.ToLuaExpression;

public interface ToLuaExpressionMapper {

	public List<ToLuaExpression> selectAll();
	
	public String getLuaExpression(String syntacticId,String semanticIds,String indexs);

}