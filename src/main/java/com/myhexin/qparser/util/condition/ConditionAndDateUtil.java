package com.myhexin.qparser.util.condition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.onto.PropNodeFacade;
import com.myhexin.qparser.phrase.util.Consts;
import com.myhexin.qparser.util.cfgdate.CfgDateLogicInstance;
import com.myhexin.qparser.util.condition.model.ConditionIndexModel;

public class ConditionAndDateUtil {
	private final static Pattern Continue_Days_Pattern = Pattern.compile("^(最近|近|连续)[0-9]{1,2}(日|天)$");
	
	/*
	 * 拆分日期的事情,已经在插件PhraseParserPluginBusinessAdjustDateNumByReportType做了
	 * 所以这里只做duplicate index的事情
	 */
	static List<ConditionIndexModel> checkAndDupIndex(ConditionIndexModel indexCond, List<PropNodeFacade> props) {
		List<ConditionIndexModel> models = null;
		if(props!=null) {
			models = new ArrayList<ConditionIndexModel>();
			for(PropNodeFacade prop : props) {
				if(prop.isDateProp() && prop.getValue()!=null && prop.getValue().isDateNode()) {
					DateNode dateNode = (DateNode)prop.getValue();
					DateRange dateRange = dateNode.getDateinfo();
					if(dateRange!=null && dateRange.getDateinfos()!=null) {
						for(DateInfoNode dateInfo : dateRange.getDateinfos()) {
							ConditionIndexModel dup = indexCond.copy();
							dup.replaceTradeDayProp(dateInfo.toString(Consts.STR_BLANK));
							models.add(dup);
						}
					}
					break;
				}
			}
		}
		if(models==null || models.size()==0) {
			models.add(indexCond);
		}
			
		return models;
	}
	
	
	//判断是不是"连续3天涨跌幅"类型的问句,如果是,要把连续3天拆成3个condition
	private static List<ConditionIndexModel> checkAndDupIndex2(ConditionIndexModel indexCond, List<PropNodeFacade> props) {
		List<ConditionIndexModel> models = null;
		if(props!=null) {
			models = new ArrayList<ConditionIndexModel>();
			//models.add(indexCond);
			
			boolean hasRangeDateProp = false; //有没有区间属性
			boolean hasTradeDateProp = false; //有没有交易日期属性
			boolean isDateRange = false; //时间值是不是区间
			boolean hasContinueDaysText = false; //时间节点中有没有连续n日/天, 近n日/天2个字                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
			DateNode datePropValue = null;
			for(PropNodeFacade prop : props) {
				if(prop.isDateProp() && prop.getValue()!=null && prop.getValue().isDateNode()) {
					if(ConditionBuilderAbstract.DAY_TRADE_DAY.equals(prop.getText())) {
						hasTradeDateProp = true;
					}else if(ConditionBuilderAbstract.DAY_RANGE_START.equals(prop.getText()) || ConditionBuilderAbstract.DAY_RANGE_END.equals(prop.getText())) {
						hasTradeDateProp = true;
					}
					datePropValue = (DateNode)prop.getValue();
					if(datePropValue.getText()!=null && Continue_Days_Pattern.matcher(datePropValue.getText()).matches()) {
						hasContinueDaysText = true;
					}
					
					if(datePropValue.getDateinfo()!=null && datePropValue.getDateinfo().getFrom()!=null
							&& datePropValue.getDateinfo().getTo()!=null 
							&& !datePropValue.getDateinfo().getFrom().equals(datePropValue.getDateinfo().getTo())) {
						isDateRange = true;
					}
				}
			}
			//4个条件都满足,那么要复制
			if(hasRangeDateProp==false && hasTradeDateProp && isDateRange && hasContinueDaysText) {
				
				DateInfoNode from  = datePropValue.getDateinfo().getFrom();
				DateInfoNode to  = datePropValue.getDateinfo().getTo();
				Calendar calFrom = Calendar.getInstance();
				Calendar calTo = Calendar.getInstance();
				calFrom.set(Calendar.YEAR, from.getYear());
				calFrom.set(Calendar.MONTH, from.getMonth()-1);
				calFrom.set(Calendar.DAY_OF_MONTH, from.getDay());
				//calFrom.add(Calendar.DAY_OF_MONTH, 1); //加一天,因为indexCond本身已经占用了一天
				
				calTo.set(Calendar.YEAR, to.getYear());
				calTo.set(Calendar.MONTH, to.getMonth()-1);
				calTo.set(Calendar.DAY_OF_MONTH, to.getDay());
				int count = 0;
				while(calFrom.before(calTo)) {
					/*
					 * 防止死循环,这里为什么是100, liuxiaofeng
					 * 1. 取100天的指标的数据, 页面显示不下
					 * 2. 取100天的数据容易出现性能问题
					 * 3. 如果真的要取100天的数据,应该想别的办法,而不是出100个condition去取
					 */
					if(count++>100) {
						break;
					}
					
					if(DateUtil.isTradeDate(calFrom)==false){
						calFrom.add(Calendar.DAY_OF_MONTH, 1);
						continue;
					}
					
					ConditionIndexModel dup = indexCond.copy();
					dup.replaceTradeDayProp(DateInfoNode.toString(calFrom, Consts.STR_BLANK));
					models.add(dup);
					calFrom.add(Calendar.DAY_OF_MONTH, 1);
				}
				//把边界加上
				ConditionIndexModel dup = indexCond.copy();
				dup.replaceTradeDayProp(DateInfoNode.toString(calTo, Consts.STR_BLANK));
				models.add(dup);
				
			}else{
				models.add(indexCond);
			}
		}
		return models;
	}
	
	
	
	static boolean shouldCheckReportPeriodAndDupIndex(String semanticInfo, List<ConditionIndexModel> models) {
		//没有过滤条件的才复制
		if(semanticInfo!=null && (semanticInfo.equals("FREE_VAR") || semanticInfo.equals("STR_INSTANCE") || semanticInfo.equals("KEY_VALUE"))){
			//前面的连续3天这种已经复制了,不再复制
			if(models==null || models.size()==1) {
				return true;
			}
		}
		return false;
	}
	
	

	/*public ConditionIndexModel toCondition(FocusNode fNode, String domain) {
		return toCondition(fNode, domain);
	}*/
	private final static CfgDateLogicInstance cfgDateInstance = CfgDateLogicInstance.getInstance();
	private static List<ConditionIndexModel> checkReportPeriodAndDupIndex(ConditionIndexModel indexCond, List<PropNodeFacade> props) {
		List<ConditionIndexModel> models = null;
		//检查是不是报告期的指标
		if(indexCond.getReportType()!=null && indexCond.getReportType().equals("QUARTER")) {
			if(props!=null) {
				String indexName = indexCond.getIndexName();
				for(PropNodeFacade prop : props) {
					if(prop.isDateProp() && prop.getText()!=null && prop.getText().equals("报告期")) {
						if(prop.getValue()==null){ // || isThisYear(prop.getValue()
							String dates = cfgDateInstance.getDatePeriod(indexName, prop.getText() + "_optimize", null);
							if(dates!=null) {
								String[] arr_dates = dates.split(",");
								if(arr_dates!=null && arr_dates.length>0) {
									for(String date : arr_dates) {
										ConditionIndexModel dup = indexCond.copy();
										dup.replaceTradeDayProp(date);
										
										if(models==null) models = new ArrayList<ConditionIndexModel>();
										models.add(dup);
									}
								}
							}
						}
						break;
					}
				}
			}
		}
		return models;
	}
	

	
	
	private static boolean quarterReportTypeDupIndex(ConditionIndexModel indexCond, ClassNodeFacade cNode, List<PropNodeFacade> props) {
		for(PropNodeFacade prop : props) {
			if(prop.isDateProp() && prop.getValue()!=null && prop.getValue().isDateNode()) {
				
			}
		}
		
		return false;
	}
	
}
