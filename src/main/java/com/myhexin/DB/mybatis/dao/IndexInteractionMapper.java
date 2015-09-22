package com.myhexin.DB.mybatis.dao;

import java.util.List;

import com.myhexin.DB.mybatis.mode.IndexInteraction;

public interface IndexInteractionMapper {
    List<IndexInteraction> selectAll();
}