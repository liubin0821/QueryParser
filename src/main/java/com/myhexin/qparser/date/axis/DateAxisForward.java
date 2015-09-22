package com.myhexin.qparser.date.axis;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.SemanticNode;


/**
 * 
 * 前推逻辑：chunkB为最新交易日发生的行为（图中黑点），
 * 先要算出chunkB所用的时间（n个交易日），
 * 然后由最新交易日往前推n个交易日（注意是交易日，非自然日）。新推倒出来时间点（图中绿点）为chunkA的行为发生点。
 * 主要的query类型有：
 * chunkA +后+chunkB
 * 分析：chunkA和chunkB均为一个交易日完成的行为。
 * Eg：涨停后收阴线最新交易日（即11.19）【收阴线】，【收阴线】所用的时间为1个交易日。前推1个交易日后，为【涨停】的发生时间点（即11.18）。所以解析出来的结果为：11.18涨停；11.19收阴线。
 * 
 * 其他类似的问句有：
 * 一字板后阴线11.18一字板；11.19阴线
 * 20日小于60日均线后股价回调10日均线11.18 20日线小于60日线；11.19股价回调10日线
 * chunkA +后+（date区间）chunkB
 * 
 * 
 * 分析：chunkA为一个交易日完成的行为，chunkB为一段时间完成的行为。
 * Eg：涨停后横盘15日的股票 10.30涨停；11.19横盘15日。
 * 涨停后连续3天缩量11.14涨停；11.17缩量；11.18缩量；11.19缩量
 * 阳线之后2连阴11.17阳线；11.18阴线；11.19阴线
 * 创新高后连续3日下跌11.14创新高；11.17下跌；11.18下跌；11.19下跌
 * 
 * （date区间）chunkA +后+chunkB
 * 分析：chunkA为一段时间完成的行为，chunkB为一个交易日完成的行为。
 * Eg：5日区间涨幅>30%后下跌11.12-11.18区间涨跌幅大于30%；11.19下跌
 * 连涨3天后回调11.14涨；11.17涨；11.18涨；11.19回调
 * 连续下跌之后又站上10日均线11.14跌；11.17跌；11.18跌；11.19站上10日线
 * 
 * （date区间）chunkA +后+（date区间）chunkB
 * 分析：chunkA和chunkB均为一段时间完成的行为。
 * Eg：两个阴线后连续三个小阳线11.13阴线；11.14阴线；11.17小阳线；11.18小阳线；11.19小阳线
 * 连续下跌六日后，再连续上涨二日11.10至11.17连续下跌；11.19连涨2日。
 * 
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2014-12-1
 *
 */
public class DateAxisForward extends DateAxisInterface{
	//private final static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(DateAxisForward.class.getName());

	
	
	
	@Override
	public void process(ArrayList<ArrayList<SemanticNode>> list, DateNode dateParam, Calendar backtestTime) {
		int len = list.size();
		if(len==1) return;
		
		
		//chunkB节点没有日期节点，添加一个今天交易日的节点
		DateNode datePointInfo = dateParam;
		if(datePointInfo==null) {
			List<SemanticNode> subList = list.get(len-1);
			int dateNodeIndex = DateAxisHandler.getDateNodeIndex(subList);
			if(dateNodeIndex<0) { //没有时间节点, 添加一个
				//logger_.info("没有时间节点, 添加一个");
				DateInfoNode fromDate = null;
				DateInfoNode toDate = null;
				DayNumInfo dayNumInfo = fetchDayNumber(subList);
				if(dayNumInfo!=null && dayNumInfo.dayNum>0) {
					toDate = this.getForwardDateInfo(datePointInfo, backtestTime);
					try {
						fromDate = DateUtil.rollTradeDate(toDate, 0- dayNumInfo.dayNum+1);
					} catch (UnexpectedException e) {
						e.printStackTrace();
					}
				}else {
					fromDate = this.getForwardDateInfo(datePointInfo, backtestTime);
					toDate = fromDate;
				}
						
				DateNode theNode = new DateNode();
				DateRange dateRange = new DateRange(fromDate, toDate);
				theNode.setDateinfo(dateRange);
				theNode.setText(this.getDateNodeText(dateRange) );
				if(dayNumInfo!=null && dayNumInfo.dayNum>0 && dayNumInfo.index>=0 && shouldReplaceNumWithDate(dayNumInfo.index, subList)) {
					subList.set(dayNumInfo.index, theNode);
				}else{
					subList.add(0, theNode);
				}
				
				
				datePointInfo = theNode;
			}else{
				datePointInfo = (DateNode) subList.get(dateNodeIndex);
			}
			
		}
		
		for(int i=len-2;i>=0;i--) {
			List<SemanticNode> subList = list.get(i);
			int dateNodeIndex = DateAxisHandler.getDateNodeIndex(subList);
			
			if(dateNodeIndex<0) { //没有时间节点, 添加一个
				DateInfoNode fromDate = null;
				DateInfoNode toDate = null;
				DayNumInfo dayNumInfo = fetchDayNumber(subList);
				if(dayNumInfo!=null && dayNumInfo.dayNum>0) {
					toDate = this.getForwardDateInfo(datePointInfo,backtestTime);
					try {
						fromDate = DateUtil.rollTradeDate(toDate, 0-dayNumInfo.dayNum+1);
					} catch (UnexpectedException e) {
						e.printStackTrace();
					}
				}else {
					fromDate = this.getForwardDateInfo(datePointInfo,backtestTime);
					toDate = fromDate;
				}
						
				DateNode theNode = new DateNode();
				DateRange dateRange = new DateRange(fromDate, toDate);
				theNode.setDateinfo(dateRange);
				theNode.setText(this.getDateNodeText(dateRange));
				
				if(dayNumInfo!=null && dayNumInfo.dayNum>0 && dayNumInfo.index>=0 && shouldReplaceNumWithDate(dayNumInfo.index, subList)) {
					subList.set(dayNumInfo.index, theNode);
				}else{
					subList.add(0, theNode);
				}
				
				datePointInfo = theNode;
			}else{
				datePointInfo = (DateNode) subList.get(dateNodeIndex);
			}
			
		}
		
	}

	
}
