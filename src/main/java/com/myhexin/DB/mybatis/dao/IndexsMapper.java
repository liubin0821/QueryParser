package com.myhexin.DB.mybatis.dao;

import java.util.List;

import com.myhexin.DB.mybatis.mode.Indexs;

public interface IndexsMapper {

	public Integer insertAndGetId(Indexs indexs);
	
	public Integer updateLabel(Indexs indexs);
	
	public Integer updateIndexNotInTree(Indexs indexs);
	
	public Integer delete(Integer id);
	
	public List<Indexs> selectAll();
	
	public Indexs selectByIndexId(Integer indexId);
	
	public List<Indexs> selectByIndex(Indexs indexs);
}