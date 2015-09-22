package com.myhexin.qparser.phrase.parsePlugins;

import java.util.ArrayList;
import java.util.Iterator;

import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.iterator.BoundaryInfos;
import com.myhexin.qparser.iterator.SyntacticIteratorImpl;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.phrase.ParserAnnotation;
import com.myhexin.qparser.phrase.PhraseUtil;

public class PhraseParserPluginCombineOnePhraseDate extends
		PhraseParserPluginAbstract {
	
	public PhraseParserPluginCombineOnePhraseDate() {
		super("Combine_One_Phrase_Date");
	}

	public ArrayList<ArrayList<SemanticNode>> process(ParserAnnotation annotation) {
		
		ArrayList<SemanticNode> nodes = annotation.getNodes();
		return dealWithDateCombine(nodes);
	}

	private ArrayList<ArrayList<SemanticNode>> dealWithDateCombine(
			ArrayList<SemanticNode> nodes) {
		if (nodes == null || nodes.size() == 0)
    		return null;
    	
    	ArrayList<ArrayList<SemanticNode>> tlist = new ArrayList<ArrayList<SemanticNode>>(1);
        Iterator iterator = new SyntacticIteratorImpl(nodes);
        if (!iterator.hasNext()) {
        	tlist.add(nodes);
			return tlist; // 没有句式
		}
        
    	while (iterator.hasNext()) {
    		BoundaryInfos boundaryInfos = (BoundaryInfos) iterator.next();
    		// 处理时间节点的合并
			CombineOnePhraseDate.process(nodes, boundaryInfos.start, boundaryInfos.bEnd);
    	}
    	
    	tlist.add(nodes);
		return tlist;
	}
}

class CombineOnePhraseDate {

	public static void process(ArrayList<SemanticNode> nodes, int start, int end) {
		ArrayList<DateNode> dl = new ArrayList<DateNode>();
		int indexNum = 0;
		for (int i = start; i <= end; i++) {
			// 提取时间节点 没有绑定到句式才可用 
			if (nodes.get(i).type == NodeType.DATE && !nodes.get(i).isBoundToSynt) {
				dl.add((DateNode) nodes.get(i));
			} else if (PhraseUtil.isIndex(nodes.get(i)))
				indexNum++;
		}
		if (indexNum > 1)
			return;

		// 对相邻时间节点进行合并
		for (int i = 0; i < dl.size(); i++) {
			DateNode dn = dl.get(i);
			if (dn.isCombined) {// 已经被合并
				continue;
			}
			for (int j = i + 1; j < dl.size(); j++) {
				DateNode dnNext = dl.get(j);
				if (dnNext.isCombined) {
					continue;
				}
				doCombine(dn, dnNext);
				doCombine(dnNext, dn);
			}

		}

	}

	/**
	 * @author: 吴永行
	 * @dateTime: 2013-10-11 上午10:34:04
	 * @description:
	 * @param dn
	 * @param dnNext
	 * 
	 */
	private static void doCombine(DateNode dn, DateNode dnNext) {
		if (dn.isCombined || dnNext.isCombined) {
			return;
		}
		switch (getDateCombineType(dn, dnNext)) {
		case IGNORE_BEFORE:
			dnNext.setText(dn.getText() + dnNext.getText() );
			dn.isCombined = true;
			break;
		case AS_ONE_DATE:
			// 留待以后处理
			DateRange dr1 = null;
			String time = dn.getText() + dnNext.getText();
			/*if (dn.isSequence) {
				time =  time;
			}*/
			try {
				dr1 = DateCompute.getDateInfoFromStr(time, null);
			} catch (NotSupportedException e) {

			}
			if (dr1 != null) {
				dn.setText(time );
				dn.setDateinfo(dr1);
				dn.oldNodes.addAll(dnNext.oldNodes);
				dnNext.isCombined = true;
			}
			break;
		// 中间加上连续
		case AS_ONE_DATE_ADD_SEQ:
			// 留待以后处理
			DateRange dr2 = null;
			String time2 = dn.getText() +  dnNext.getText();
			try {
				dr2 = DateCompute.getDateInfoFromStr(time2, null);
			} catch (NotSupportedException e) {

			}
			if (dr2 != null) {
				dn.setText(time2);
				dn.setDateinfo(dr2);
				dn.oldNodes.addAll(dnNext.oldNodes);
				dnNext.isCombined = true;
			}
			break;
		default:
			break;
		}
	}

	/**
	 * @author: 吴永行
	 * @dateTime: 2013-10-11 上午9:59:39
	 * @description:
	 * @param dn
	 * @param dnNext
	 * @return
	 * 
	 */
	private static EnumDef.DateCombineType getDateCombineType(DateNode cur,DateNode curNext) {
		// TODO Auto-generated method stub
		Unit curDateUnit = null;
		Unit curMaxUnit = null;
		
		if(cur!=null && cur.getDateinfo()!=null) {
			curDateUnit = cur.getDateinfo().getDateUnit();
			curMaxUnit = cur.getDateinfo().getMaxUnit();
		}
		
		Unit curNextDateUnit = null;
		Unit curNextMaxUnit = null;
		if(curNext!=null && curNext.getDateinfo()!=null) {
			curNextDateUnit = curNext.getDateinfo().getDateUnit();
			curNextMaxUnit = curNext.getDateinfo().getMaxUnit();
		}
		
		
		

		// 非连续合并
		if (curNext != null && !curNext.isSequence && !cur.isSequence && cur.getDateinfo() != null
				&& cur.getDateinfo().getLength() <= 1// 前一个是时间区间不合并
				&& !DatePatterns.REPORT_NO41.matcher(cur.getText()).matches() // 前一个是报告期不处理
		) {
			if (DateUtil.isBigerUnit(curDateUnit, curNextDateUnit)
					&& !DatePatterns.THAT_REGION_NO48.matcher(curNext.getText())
							.matches()) {
				return EnumDef.DateCombineType.AS_ONE_DATE;
			}
			// 12月4日至今
			else if (DatePatterns.TO_TODAY.matcher(curNext.getText()).matches()) {
				return EnumDef.DateCombineType.AS_ONE_DATE;
			}

		}		
		// 形如 2013年一季度前连续三个季度
		if ((DatePatterns.IN_ONE_DAY_BEFORE_NO43.matcher(cur.getText())
				.matches() || DatePatterns.IN_ONE_DAY_AFTER_NO42.matcher(
				cur.getText()).matches())
				&& curNext.isSequence) {
			return EnumDef.DateCombineType.AS_ONE_DATE_ADD_SEQ;
		} 		
		// 处理时间加报告期
		 if ((DatePatterns.REPORT_NO41.matcher(curNext.getText()).matches())
				&& (curDateUnit == curNextDateUnit || DateUtil.isBigerUnit(
						curDateUnit, curNextDateUnit))) {
			return EnumDef.DateCombineType.AS_ONE_DATE;
		}
		 if (cur!=null && !cur.isSequence && curNext.isSequence) {
			// 前一个包含后一个范围 可忽略的
			if (cur.getDateinfo().contains(curNext.getDateinfo())) {
				return EnumDef.DateCombineType.IGNORE_BEFORE;
			}
			//其他不做处理
		}

		return EnumDef.DateCombineType.DO_NOTHING;
	}

}
