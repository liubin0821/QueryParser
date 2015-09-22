package com.myhexin.DB.mybatis.dao;

import java.util.List;

import com.myhexin.DB.mybatis.mode.ObjectRelationV;

public interface ObjectRelationVMapper {
	public List<ObjectRelationV> selectByPropId(Integer propId);
	public List<ObjectRelationV> selectAll();
}