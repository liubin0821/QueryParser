package com.myhexin.DB.mybatis.dao;

import java.util.List;

import com.myhexin.DB.mybatis.mode.Aliases;

public interface AliasesMapper {
	
	public List<Aliases> selectByIndexId(Integer indexId);
	public List<Aliases> selectAll();
	
	public Integer setDefault(Integer aliasesId, Boolean isdefault);
	
	public Integer deleteByIndexId(Integer indexId);
	
	public Integer deleteByAliasesId(Integer aliasesId);
	
	public Integer insertAndGetId(Aliases aliases);
	
	public Integer updateLabel(Integer aliasesId,String lable);
}