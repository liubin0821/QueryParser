package com.myhexin.DB.mybatis.dao;

import java.util.List;

import com.myhexin.DB.mybatis.mode.IndexPropV2;

public interface IndexPropV2Mapper {
	public List<IndexPropV2> selectByIndexId(Integer indexId);
	public List<IndexPropV2> selectAll();
}