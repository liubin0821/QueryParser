package com.myhexin.DB.mybatis.dao;

import java.util.List;

import com.myhexin.DB.mybatis.mode.CondUseNewapi;
import com.myhexin.DB.mybatis.mode.ConfigTextLine;
import com.myhexin.DB.mybatis.mode.DataType;
import com.myhexin.DB.mybatis.mode.IndexCalcExpr;
import com.myhexin.DB.mybatis.mode.IndexDefDate;
import com.myhexin.DB.mybatis.mode.IndexRenameRule;
import com.myhexin.DB.mybatis.mode.RefCode;
import com.myhexin.DB.mybatis.mode.SuperType;
import com.myhexin.qparser.util.cfgdate.CfgDateEntity;
import com.myhexin.qparser.util.cfgdate.CfgRptPeriodDefault;

public interface DateParserInfoMapper {
	
	//刘小峰,指数id和名称
	//TODO 删除IndexIdName类
	//public List<IndexIdName> selectIndexIdName();
	
	//使用新的接口的配置信息
	public List<CondUseNewapi> selectCondUserNewapiInfos();

	//读取配置文件的行
	public List<ConfigTextLine> selectConfigFileLines(String fileName);
	public List<ConfigTextLine> selectAllConfigFileLines();
	
	//取到指标计算公式
	public List<IndexCalcExpr> selectCalcExpr();
	
	//找到指标重命名规则
	public List<IndexRenameRule> getRenameRuleList();
	
	//RefCode信息
	public List<RefCode> getRefCode();
	
	
	//DataType树
	
	public List<DataType> getDataTypes();
	public List<SuperType> getSuperTypes();
	
	
	//时效性时间配置
	public List<CfgDateEntity> getDateTimePeriods();
	public List<CfgRptPeriodDefault> getDateTimeDefaults();
	
	//指标时间计算逻辑配置
	public List<IndexDefDate> selectIndexDefDate();
	
}
