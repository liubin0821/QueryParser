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
 * 后推逻辑：chunkA为历史上某个交易日发生的行为，先要算出chunkB所用的时间（n个交易日），
 * 然后由chunkA的发生时点往后推n个交易日（注意是交易日，非自然日），新推倒出来时间点（图中黑点）为chunkB的行为发生时点。
 * 
 * 主要的query类型有：
 * （date时间点）chunkA+后+chunkB
 * 分析：（date时间点）为chunkA的发生时间点，且chunkA和chunkB均为一个交易日完成的行为。
 * Eg：9月4日小阴线后小阳线9月4日小阴线；9月5日小阳线。
 * 2014年8月22日大跌3%以上后上涨20140822跌幅大于3%；20140825上涨
 * 20131114 涨停后量缩小20131114涨停；20131115缩量
 * 
 * （date时间点）chunkA+后+（date区间）chunkB
 * 分析：（date时间点）为chunkA的发生时间点，且chunkA为一个交易日完成的行为，chunkB为一段时间完成的行为。
 * Eg：8月4日遇到支撑位后二连阳8.4遇到支撑位；8.5阳线；8.6阳线；
 * 10月24日涨停后连跌10.24涨停；10.27下跌；10.28下跌；10.29下跌
 * 
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2014-12-1
 *
 */
public class DateAxisBackward extends DateAxisInterface{
	
	/**
	 * 后推逻辑
	 * 
	 */
	@Override
	public void process(ArrayList<ArrayList<SemanticNode>> list, DateNode dateParam, Calendar backtestTime) {
		//
		DateNode datePointInfo = dateParam;
		for(int i=1;i<list.size();i++) {
			List<SemanticNode> subList = list.get(i);
			int dateNodeIndex = DateAxisHandler.getDateNodeIndex(subList);
			if(dateNodeIndex<0) { //没有时间节点, 添加一个
				DateInfoNode fromDate = null;
				DateInfoNode toDate = null;
				DayNumInfo dayNumInfo = fetchDayNumber(subList);
				if(dayNumInfo!=null && dayNumInfo.dayNum>0) {
					fromDate = this.getBackwardDateInfo(datePointInfo, backtestTime);
					try {
						toDate = DateUtil.rollTradeDate(fromDate, dayNumInfo.dayNum-1);
					} catch (UnexpectedException e) {
						e.printStackTrace();
					}
				}else {
					fromDate = this.getBackwardDateInfo(datePointInfo, backtestTime);
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
	}
}
