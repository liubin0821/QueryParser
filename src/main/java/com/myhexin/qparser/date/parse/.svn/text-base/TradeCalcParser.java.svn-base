package com.myhexin.qparser.date.parse;

import java.util.Calendar;

import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.date.ParserInterface;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.SemanticNode;

public class TradeCalcParser implements ParserInterface {

	@Override
	public void parse(SemanticNode node, Calendar backtestTime) {
		if(node==null || node.getNodeMerge()==null)  return;
		if(node.isDateNode()==false) return;
		
		DateNode datenode = (DateNode) node;
		
		String calcexpr = node.getNodeMerge().getCalc_expr();
		if(calcexpr!=null) {
			try{
				DateInfoNode today = new DateInfoNode(backtestTime);
				Integer value = Integer.parseInt(calcexpr);
				DateInfoNode info = DateUtil.rollTradeDate(today,value);
				datenode.setDateinfo(info);
				
				//TODO 删除
				datenode.setSkipOldDateParser(true);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}

}
