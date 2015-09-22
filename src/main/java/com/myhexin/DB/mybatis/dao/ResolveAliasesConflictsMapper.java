package com.myhexin.DB.mybatis.dao;

import java.util.List;

import com.myhexin.DB.mybatis.mode.ResolveAliasesConflicts;

public interface ResolveAliasesConflictsMapper {
	
	public List<ResolveAliasesConflicts> selectByAliasesId(Integer aliasesId);
	public List<ResolveAliasesConflicts> selectAll();
	

}