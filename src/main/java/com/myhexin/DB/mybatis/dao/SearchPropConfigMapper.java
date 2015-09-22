package com.myhexin.DB.mybatis.dao;

import java.util.List;

import com.myhexin.DB.mybatis.mode.SearchPropConfig;

public interface SearchPropConfigMapper {
	List<SearchPropConfig> selectAll();
}