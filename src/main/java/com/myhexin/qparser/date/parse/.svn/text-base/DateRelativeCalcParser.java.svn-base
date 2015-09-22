package com.myhexin.qparser.date.parse;

import java.util.Calendar;

import com.myhexin.qparser.date.DateInfoNode;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.date.ParserInterface;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.SemanticNode;


/**
 * 5天前, 5天后这种相对时间，解析
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2015-7-6
 *
 */
public class DateRelativeCalcParser implements ParserInterface {

	@Override
	public void parse(SemanticNode node, Calendar backtestTime) {
		if(node==null || node.getNodeMerge()==null)  return;
		if(node.isDateNode()==false) return;
		
		DateNode datenode = (DateNode) node;
		String text = datenode.getText();
		
		boolean add = true;
		StringBuilder numStr = new StringBuilder();
		for(int i=0;i<text.length();i++) {
			char c = text.charAt(i);
			if(c>='0' && c<='9') {
				numStr.append(c);
			}else if(c=='前' || c=='后'){
				if(c=='前') {
					add = false;
				}else{
					add = true;
				}
			}/*else{
				break;
			}*/
		}
		
		int num = -1;
		try{
			num = Integer.parseInt(numStr.toString());
		}catch(Exception e) {
			
		}
		
		try{
			if(add==false) {
				num = 0-num;
			}
			
			DateInfoNode today = new DateInfoNode(backtestTime);
			DateInfoNode info = DateUtil.rollTradeDate(today,num);
			datenode.setDateinfo(info);
				
			//TODO 删除
			datenode.setSkipOldDateParser(true);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
