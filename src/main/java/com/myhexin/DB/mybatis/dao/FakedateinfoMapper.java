package com.myhexin.DB.mybatis.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.myhexin.DB.mybatis.mode.Fakedateinfo;


public interface FakedateinfoMapper {
	public List<Fakedateinfo> selectAll();
}