package com.myhexin.DB.mybatis.dao;

import java.util.List;

import com.myhexin.DB.mybatis.mode.SearchIdConfig;

public interface SearchIdConfigMapper {
	List<SearchIdConfig> selectAll();
	List<SearchIdConfig> selectByChannel(String channel);
}