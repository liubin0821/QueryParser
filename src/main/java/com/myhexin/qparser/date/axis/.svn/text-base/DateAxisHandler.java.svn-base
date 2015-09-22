package com.myhexin.qparser.date.axis;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.StrNode;
import com.myhexin.qparser.node.UnknownNode;

/**
 * 时间轴处理类 1. 先判断问句是不是时间轴问句 2. 找出时间分割词(后）所在的位置 3. 用以上分割词位置 ，把问句分成多个子问句 4.
 * 检查问句中有没有时间点SemanticNode 5. 根据有没有时间点及其所在的位置选择前推 还是 后推逻辑
 * 
 * @author 刘小峰 liuxiaofeng@myhexin.com
 * @Version 创建时间 2014-12-3
 *
 */
@Component
public class DateAxisHandler {
	//private final static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(DateAxisHandler.class.getName());

	public final static Pattern AXIS_SPLIT_PATTERN_WORD_ONLY = Pattern.compile("^(后|以后|之后)$");
	private final static Pattern AXIS_SPLIT_PATTERN = Pattern.compile("^(后|以后|之后)(连续)?(.*)$");

	public final static Pattern N_DAY_PATTERN = Pattern.compile("^([0-9]{1,5})(日|天)$");
	public final static Pattern CONTINUE_N_DAY_PATTERN = Pattern.compile("^(连续|持续)([0-9]{1,5})(日|天)$");
	public final static Pattern AFTRE_CONTINUE_N_DAY_PATTERN = Pattern.compile("^(后|以后|之后)(连续)([0-9]{1,5})(日|天)$");
	public final static Pattern AVG_ETC_PATTERN = Pattern.compile("^(日线|均线|线|区间)$");

	private boolean isDateAxisQuery(ArrayList<SemanticNode> nodes) {
		int before = 0;
		int after = 0;
		// 碰到时间轴关键字"后"的时候, inBefore=false
		boolean inBefore = true;
		for (int i = 0; i < nodes.size(); i++) {
			SemanticNode node = nodes.get(i);
			if (node.type == NodeType.ENV)
				continue;

			if (AXIS_SPLIT_PATTERN.matcher(node.getText()).matches()) {
				boolean isStrKey = false;
				if (node.isStrNode()) {
					StrNode strNode = (StrNode) node;
					if (strNode.subType != null && strNode.subType.size() > 0) {
						isStrKey = true;
					}
				}

				if (isStrKey == false) {
					inBefore = false;
					continue;
				}
			}

			if (inBefore) {
				before++;
			} else {
				after++;
			}

			if (after > 0)
				break;
		}

		// 是时间轴问句
		if (inBefore == false && before > 0 && after > 0) {
			return true;
		} else {
			return false; // 不是
		}

	}

	public static int getDateNodeIndex(List<SemanticNode> nodes) {

		int dateNodeIndex = -1;
		int index = 0;

		DateNode dateNode = null;
		boolean useTheDateNode = true;

		for (SemanticNode node : nodes) {
			if (node.isDateNode() && CONTINUE_N_DAY_PATTERN.matcher(node.getText()).matches() == false) {
				dateNode = (DateNode) node;
				dateNodeIndex = index;
			} else {
				// 当时5日均线...这种情况的时候不要使用这个时间节点
				if (dateNode != null && N_DAY_PATTERN.matcher(dateNode.getText()).matches()
						&& AVG_ETC_PATTERN.matcher(node.getText()).matches()) {
					useTheDateNode = false;
				}
			}

			index++;
		}

		if (useTheDateNode && dateNode != null && dateNodeIndex >= 0) {
			return dateNodeIndex;
		} else {
			return -1;
		}
	}

	private int getDateNodeIndex(ArrayList<ArrayList<SemanticNode>> list) {

		int dateNodeIndex = -1;
		int index = 0;
		for (ArrayList<SemanticNode> nodes : list) {

			dateNodeIndex = getDateNodeIndex(nodes);
			if (dateNodeIndex >= 0) {
				return index + dateNodeIndex;
			}

			/*
			 * DateNode dateNode = null; boolean useTheDateNode = true;
			 * 
			 * for(SemanticNode node : nodes) { if(node.isDateNode()) { dateNode
			 * = (DateNode) node; dateNodeIndex = index; }else{
			 * //当时5日均线...这种情况的时候不要使用这个时间节点 if(dateNode!=null && (
			 * (N_DAY_PATTERN.matcher(dateNode.text).matches() &&
			 * AVG_ETC_PATTERN.matcher(node.text).matches() ) ||
			 * CONTINUE_N_DAY_PATTERN.matcher(dateNode.text).matches() )) {
			 * useTheDateNode = false; } }
			 * 
			 * index++; }
			 * 
			 * if(useTheDateNode && dateNode!=null && dateNodeIndex>=0) { return
			 * dateNodeIndex; }
			 */

			index += nodes.size();
			index++; // 分歌词的份
		}
		return -1;
	}

	/**
	 * 处理时间轴问句
	 * 
	 * @param nodes
	 * @return
	 */
	// TODO 面向接口编程, 把ArrayList => List
	public ArrayList<ArrayList<SemanticNode>> parse(ArrayList<SemanticNode> nodes, Calendar backtestTime) {
		ArrayList<ArrayList<SemanticNode>> list = new ArrayList<ArrayList<SemanticNode>>(1);
		if (isDateAxisQuery(nodes) == false) {
			// logger_.info("非时间轴问句");
			list.add(nodes);
			return list;
		}

		List<Integer> splitWordIndexList = new ArrayList<Integer>();

		int dateNodeIndex = -1;
		int firstSplitIndex = -1;

		// 第一次遍历，找出时间轴分割词,和已有时间节点的index
		int i = 0;
		for (i = 0; i < nodes.size(); i++) {
			SemanticNode node = nodes.get(i);
			if (AXIS_SPLIT_PATTERN.matcher(node.getText()).matches()) {
				if (i < nodes.size() - 1) {
					splitWordIndexList.add(i);
					if (firstSplitIndex == -1)
						firstSplitIndex = i;
				}
			}
		}

		if (splitWordIndexList.size() == 0) { // 不是时间轴问句,直接返回
			// logger_.info("非时间轴问句");
			list.add(nodes);
			return list;
		} else {
			// logger_.info("是时间轴问句 : " + splitWordIndexList.toString());
			// 分割问句
			int fromIndex = 1;
			for (i = 0; i < splitWordIndexList.size(); i++) {
				int toIndex = splitWordIndexList.get(i);
				if (toIndex >= fromIndex) {
					List<SemanticNode> subList = nodes.subList(fromIndex, toIndex);
					ArrayList<SemanticNode> newSubList = new ArrayList<SemanticNode>(subList);
					if (newSubList != null && newSubList.size() > 0)
						list.add(newSubList);
				}
				fromIndex = toIndex + 1;
			}
			if (fromIndex < nodes.size()) {
				List<SemanticNode> subList = nodes.subList(fromIndex, nodes.size());
				ArrayList<SemanticNode> newSubList = new ArrayList<SemanticNode>(subList);
				if (newSubList != null && newSubList.size() > 0)
					list.add(newSubList);
			}

			// 查看有没有已知的时间节点，以这个时间节点为基点，前推或后推
			dateNodeIndex = getDateNodeIndex(list);
			// logger_.info("dateNodeIndex=" + dateNodeIndex);

			// 对没有时间节点的那部分问句，添加时间节点
			// 比如10月24日涨停后连跌,对连跌这部分问句添加时间节点
			DateAxisInterface handler = null;
			if (dateNodeIndex >= 0 && dateNodeIndex < firstSplitIndex) { // 后推逻辑
				// logger_.info("后推逻辑");
				handler = new DateAxisBackward();
			} else { // 前推逻辑
				// logger_.info("前推逻辑");
				handler = new DateAxisForward();
			}
			DateNode theDateNode = null;
			if (dateNodeIndex >= 0 && dateNodeIndex < nodes.size()) {
				SemanticNode node = nodes.get(dateNodeIndex);
				if (node.isDateNode()) {
					theDateNode = (DateNode) node;
				} else {
					node = nodes.get(dateNodeIndex + 1);
					if (node.isDateNode()) {
						theDateNode = (DateNode) node;
					}
				}

			}

			// logger_.info("theDateNode=" + theDateNode);
			handler.process(list, theDateNode, backtestTime);

			ArrayList<SemanticNode> list2 = new ArrayList<SemanticNode>();
			// 先放Env node
			list2.add(nodes.get(0));

			i = 0;
			for (ArrayList<SemanticNode> n1 : list) {
				if (i == 0) {
					list2.addAll(n1);
				} else {
					// 时间轴的用;unknown分割
					_unknownNode.isCombined = true;
					list2.add(_unknownNode);
					list2.addAll(n1);
				}
				i++;
			}
			list.clear();
			list.add(list2);
			return list;
		}
	}

	private final static UnknownNode _unknownNode = new UnknownNode("_&_");
}
