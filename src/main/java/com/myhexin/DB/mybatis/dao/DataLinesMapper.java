package com.myhexin.DB.mybatis.dao;

import java.util.List;

import com.myhexin.DB.mybatis.mode.DataLine;

public interface DataLinesMapper {
	public List<DataLine> getDataLines();
	
	public List<DataLine> listTypeIdTexts();
	
	public int countListTypeIdTexts();
}
