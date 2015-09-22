package com.myhexin.DB.mybatis.dao;

import java.util.List;

import com.myhexin.DB.mybatis.mode.NodeMerge;

public interface NodesMergerMapper {
	public List<NodeMerge> selectNodeMergeList();
}
