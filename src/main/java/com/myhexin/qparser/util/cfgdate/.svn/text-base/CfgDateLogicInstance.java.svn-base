package com.myhexin.qparser.util.cfgdate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.myhexin.DB.mybatis.MybatisHelp;
import com.myhexin.DB.mybatis.mode.RefCode;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.ApplicationContext.BeanNameConsts;
import com.myhexin.qparser.resource.model.RefCodeInfo;
import com.myhexin.qparser.resource.model.ResourceInterface;
//import com.myhexin.qparser.phrase.util.Consts;

public class CfgDateLogicInstance implements ResourceInterface{
	private static MybatisHelp mybatisHelp = ApplicationContextHelper.getBean(BeanNameConsts.MYBATIS_HELP);
	private CfgDateLogicInstance() {
		//reloadDateCfg();
	}
	
	private List<CfgDateEntity> dateLogiclist = new ArrayList<CfgDateEntity>();
	//private List<CfgRptPeriodDefault> defaultRptPeriodList = new ArrayList<CfgRptPeriodDefault>();
	private Map<String, List<CfgRptPeriodDefault>> defaultRptPeriodMap = new HashMap<String, List<CfgRptPeriodDefault>>();
	
	
	private Map<String, String> nameFormatMap = new HashMap<String, String>();
	private List<String> propNames = new ArrayList<String>(); 
	
	public List<CfgDateEntity> getDateLogiclist() {
		return dateLogiclist;
	}
	
	/*public List<CfgRptPeriodDefault> getDefaultRptPeriodList() {
		return defaultRptPeriodList;
	}*/
	
	private static CfgDateLogicInstance instance = new CfgDateLogicInstance();
	public static CfgDateLogicInstance getInstance() {
		return instance;
	}
	
	public List<CfgRptPeriodDefault> getDefaultPeriodObj(String indexName, String propName) {
		return defaultRptPeriodMap.get(indexName + "@" + propName);
	}
	
	public List<CfgRptPeriodDefault> getDefaultPeriodObj(String name) {
		return defaultRptPeriodMap.get(name);
	}
	
	
	public void print() {
		System.out.println("dateLogiclist=" + dateLogiclist);
		System.out.println("defaultRptPeriodMap=" + defaultRptPeriodMap);
		System.out.println("propNames=" + propNames);
	}

	@Override
	public void reload() {
		//List<CfgDateEntity> dtPeriods = ResourceInst.getInstance().get(ResourceKeys.KeyDateTimePeriods.class);
		//List<CfgRptPeriodDefault> dtDefaults = ResourceInst.getInstance().get(ResourceKeys.KeyDateTimeDefaults.class);
		List<CfgRptPeriodDefault> dtDefaults = mybatisHelp.getDateParserInfoMapper().getDateTimeDefaults();
		List<CfgDateEntity> dtPeriods = mybatisHelp.getDateParserInfoMapper().getDateTimePeriods();
		
		if(dtPeriods!=null) {
			for(CfgDateEntity cfg : dtPeriods) {
				cfg.reloadDtFieldsByFromat();
				if(!propNames.contains(cfg.getKey_name())) {
					propNames.add(cfg.getKey_name());
				}
				dateLogiclist.add(cfg);
			}
		}
			
		if(dtDefaults!=null) {
			for(CfgRptPeriodDefault defaultRpt : dtDefaults) {
				defaultRpt.reloadDtFieldsByFromat();
				//defaultRptPeriodList.add(defaultRpt);
				List<CfgRptPeriodDefault> list = defaultRptPeriodMap.get(defaultRpt.getKey_name());
				if(list==null) {
					list = new ArrayList<CfgRptPeriodDefault>();
					defaultRptPeriodMap.put(defaultRpt.getKey_name(), list);
				}
				list.add(defaultRpt);
				//defaultRptPeriodMap.put(defaultRpt.getKey_name(), defaultRpt);
				/*if(Consts.DEBUG) {
					System.out.println(defaultRpt);
				}*/
			}
		}
		
		
		List<RefCode> codes = RefCodeInfo.getInstance().get(1003);
		if(codes!=null) {
			for(RefCode code : codes) {
				nameFormatMap.put(code.getCode_value(), code.getCode_short_desc());
			}
		}
	}
	
	
	/* 
	 * 根据指标和属性名字 取得时间解析器
	 * 
	 * @param indexName
	 * @param propName
	 * @return
	 */
	private DateTimeCfgGetter getGetterObject(String indexName, String propName) {
		String format = null;
		if(indexName!=null && propName!=null) {
			format = nameFormatMap.get(indexName + "@" + propName); 
		}
		
		if(format==null) {
			format = nameFormatMap.get(propName); 
		}
		
		if(format==null) {
			format = nameFormatMap.get(indexName); 
		}
		
		if(format==null) {
			return null;
		}else{
			if(format.equals("MMdd")) {
				return new DateTimeCfgReportPeriodGetter();
			}else if(format.equals("MMdd_optimize")) {
				return new DateTimeCfgReportPeriodOptimizeGetter();
			}else if(format.equals("hhmmss")) {
				return new DateTimeCfgDragonTigerListGetter();
			}else{
				return null;
			}
		}
	}
	
	/**
	 * 取报告期接口
	 * 如果传入时间符合如下情况,返回最新报告期 
	 * 1. 20111231,20121231,20131231
	 * 2. blank
	 * 
	 * 
	 * @param indexName 指标名称,现在没有用
	 * @param propName 属性名称:报告期,龙头股
	 * @param dateTime 传入时间
	 * @return
	 */
	public String getDatePeriod(String indexName, String propName, String dateTime) {
		DateTimeCfgGetter getter =  getGetterObject(indexName, propName);
		if(getter!=null) {
			return getter.getDatePeriod(indexName, propName, dateTime);
		}else{
			return dateTime;
		}
		
	}
	
	/*public String getDatePeriod(String indexName, String propName, Calendar dateTime) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String time = format.format(dateTime.getTime());
		return getDatePeriod(indexName, propName, time);
		
	}*/
 }
