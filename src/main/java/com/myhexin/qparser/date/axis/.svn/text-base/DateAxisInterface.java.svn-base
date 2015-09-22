package com.myhexin.qparser.date.axis;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.SemanticNode;

public abstract class DateAxisInterface {
	//public static Pattern DAY_PATTERN = Pattern.compile("$([1-9]{1,5})(日|天)^");
	
	public static Pattern SHOULD_NOT_REPLACE_NUM_PATTERN = Pattern.compile("^(连阴)$");
	protected boolean shouldReplaceNumWithDate(int currentIndex, List<SemanticNode> nodes) {
		int next = currentIndex+1;
		if(next>0 && next < nodes.size()) {
			SemanticNode node = nodes.get(next);
			if(node.isFocusNode() && SHOULD_NOT_REPLACE_NUM_PATTERN.matcher(node.getText()).matches() ) {
				return false;
			}
		}
		
		return true;
	}

	private int fetchDay(String text) {
		Matcher m = DateAxisHandler.N_DAY_PATTERN.matcher(text);
		if(m.matches()) {
			String day = m.group(1);
			try{
				return Integer.parseInt(day);
			}catch(Exception e) {
				
			}
		}else {
			m = DateAxisHandler.CONTINUE_N_DAY_PATTERN.matcher(text);
			if(m.matches()) {
				String day = m.group(2);
				try{
					return Integer.parseInt(day);
				}catch(Exception e) {
					
				}
			}
		}
		
		return -1;
	}
	
	
	static class DayNumInfo {
		protected int dayNum;
		protected int index;
	}
	
	
	
	/**
	 * 取到天数的数量词,用于计算时间区间
	 * 有日期节点要返回日期节点的日期
	 * 有数字节点,要返回数字
	 * 有连续,默认返回3天
	 * 
	 * @param nodes
	 * @return
	 */
	protected DayNumInfo fetchDayNumber(List<SemanticNode> nodes) {
		DateNode dateNode = null;
		//NumNode numNode = null;
		boolean useTheDateNode = true;
		int dateIndex = -1;
		//int numIndex = -1;
			
		int i =0;
		for(SemanticNode node : nodes) {
			if(node.isDateNode()) {
				dateNode = (DateNode) node;
				dateIndex = i;
			}
			//2015-9-6, 刘小峰,数字节点不能作为时间轴日期计算的目的
			//问句上证指数量比大于1.3后振幅小于3%
			/*else if(node.isNumNode() && (i+1<nodes.size() && DateAxisHandler.AVG_ETC_PATTERN.matcher(nodes.get(i+1).getText()).matches() )==false ) {
				numNode = (NumNode) node;
				numIndex = i;
				if(numNode.getNuminfo()!=null && "%".equals(numNode.getNuminfo().getUnit() ) ) {
					//百分比不应该被看成是天数
					numNode=null;
					numIndex = -1;
				}
			}*/else{
				//处理后连续3天，这种情况
				//TODO 跟wyh确认,为什么后连续3天会被DateParser放在一起， 问句:涨停后连续3天缩量
				/*Matcher matcher = DateAxisHandler.CONTINUE_N_DAY_PATTERN.matcher(node.text);
				if(matcher.matches() ) {
					String dayNumStr = matcher.group(3);
					int day = -1;
					try{
						day = Integer.parseInt(dayNumStr);
					}catch(Exception r){}
					if(day>0) {
						return day;
					}
				}else */
				if(dateNode!=null && DateAxisHandler.N_DAY_PATTERN.matcher(dateNode.getText()).matches() && DateAxisHandler.AVG_ETC_PATTERN.matcher(node.getText()).matches()) {				
					//当时5日均线...这种情况的时候不要使用这个时间节点
					useTheDateNode = false;
				}
			}
			i++;
		}
			
		
		
		if(useTheDateNode && dateNode!=null ) {
			DayNumInfo info = new DayNumInfo();
			int day = fetchDay(dateNode.getText());
			info.dayNum = day;
			info.index = dateIndex;
			return info;
		}/* 2015-9-6, 刘小峰 else if(numNode!=null){
			DayNumInfo info = new DayNumInfo();
			info.dayNum = (int) numNode.getNuminfo().getLongFrom();;
			info.index = numIndex;
			return info;
		}*/else{
			return null;
		}
	}
	

	
	protected DateInfoNode getDateInfo(Calendar cal) {
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		
		return new DateInfoNode(year, month, day);
	}

	
	protected Calendar getCal(DateInfoNode dateInfo) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, dateInfo.getYear());
		cal.set(Calendar.MONTH, dateInfo.getMonth()-1);
		cal.set(Calendar.DAY_OF_MONTH, dateInfo.getDay());
		//cal.add(Calendar.DAY_OF_MONTH, 1);
		
		return cal;
	}
	
	
	/*protected int getDateNodeIndex(List<SemanticNode> nodes) {
		
		int dateNodeIndex = -1;
		int index = 0;
		
		DateNode dateNode = null;
		boolean useTheDateNode = true;
			
		for(SemanticNode node : nodes) {
			if(node.isDateNode()) {
				dateNode = (DateNode) node;
				dateNodeIndex = index;
			}else{
				//当时5日均线...这种情况的时候不要使用这个时间节点
				if(dateNode!=null && DateAxisHandler.N_DAY_PATTERN.matcher(dateNode.text).matches() && DateAxisHandler.AVG_ETC_PATTERN.matcher(node.text).matches()) {
					useTheDateNode = false;
				}
			}
		
			index++;
		}
			
		if(useTheDateNode && dateNode!=null && dateNodeIndex>=0) {
			return dateNodeIndex;
		}else{
			return -1;
		}
	}*/
	
	
	/*
	 * 从时间轴的前面节点， 往后面节点， 推时间
	 * 上证指数8月12号涨跌幅>1%后振幅大于3% 
	 * 8月12号=>8月13号
	 * @param datePointInfo
	 * @param backtestTime
	 * @return
	 */
	protected DateInfoNode getBackwardDateInfo(DateNode datePointInfo, Calendar backtestTime) {
		DateInfoNode toDateInfo = null;
		if(datePointInfo!=null && datePointInfo.getDateinfo()!=null) {
			DateRange range = datePointInfo.getDateinfo();
			toDateInfo =  range.getTo();
			try {
				toDateInfo = DateUtil.rollTradeDate(toDateInfo, 1);
			} catch (UnexpectedException e) {
				e.printStackTrace();
			}
		}else{
			//Calendar cal = Calendar.getInstance();
			//toDateInfo = getDateInfo(cal);
			
			Calendar cal = null;
			if(backtestTime!=null) cal = backtestTime;
			else cal = Calendar.getInstance();
			toDateInfo = getDateInfo(cal);
			try {
				toDateInfo = DateUtil.rollTradeDate(toDateInfo, 0);
			} catch (UnexpectedException e) {
				e.printStackTrace();
			}
		}
		
		return toDateInfo;
	}
	
	protected DateInfoNode getForwardDateInfo(DateNode datePointInfo, Calendar backtestTime) {
		DateInfoNode fromDateInfo = null;
		if(datePointInfo!=null && datePointInfo.getDateinfo()!=null) {
			DateRange range = datePointInfo.getDateinfo();
			fromDateInfo =  range.getFrom();
			try {
				fromDateInfo = DateUtil.rollTradeDate(fromDateInfo, -1);
			} catch (UnexpectedException e) {
				e.printStackTrace();
			}
		}else{
			Calendar cal = null;
			if(backtestTime!=null) cal = backtestTime;
			else cal = Calendar.getInstance();
			fromDateInfo = getDateInfo(cal);
			try {
				fromDateInfo = DateUtil.rollTradeDate(fromDateInfo, 0);
			} catch (UnexpectedException e) {
				e.printStackTrace();
			}
		}
		
		return fromDateInfo;
	}
	
	
	
	protected String getDateNodeText(DateRange range) {
		if(range!=null) {
			DateInfoNode from = range.getFrom();
			DateInfoNode to = range.getTo();
			if(from!=null && to!=null) {
				if(from.equals(to))
					return from.toString();
				else{
					return from.toString() + "到" + to.toString();
				}
			}else if(from !=null) {
				return from.toString();
			}else if(to!=null) {
				return to.toString();
			}
		}
		return null;
	}
	
	public abstract void process(ArrayList<ArrayList<SemanticNode>> list, DateNode dateNode, Calendar backtestTime);
}
