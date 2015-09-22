package com.myhexin.qparser.frequency;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.myhexin.qparser.date.DateFrequencyInfo;
import com.myhexin.qparser.date.DateParser;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.define.EnumConvert;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.number.NumRange;

@Component
public class FrequencyParser {
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(FrequencyParser.class.getName());

	/**
	 * 处理时间频率
	 */
	public void parse(ArrayList<SemanticNode> nodes) {
		try {
			//String packetge = "com/myhexin/qparser/frequency/函数名称_";
			//UnitTestTools.writeToXml(packetge + query_.text+"_before.xml", query_);
			dealDateFrequency(nodes);
			//UnitTestTools.writeToXml(packetge + query_.text+"_after.xml", query_);
		} catch (NotSupportedException e) {
			logger_.debug(e.getLogMsg());
			//query_.getLog().logMsg(ParseLog.LOG_ERROR, e.getMessage());
		} catch (UnexpectedException e) {
			logger_.debug(e.getLogMsg());
			//query_.getLog().logMsg(ParseLog.LOG_ERROR, e.getMessage());
		}
	}

	private SemanticNode getLeftDateNode(ArrayList<SemanticNode> nodes, int i) {
		int j = i - 1;
		SemanticNode ldate = null;
		while (j >= 0 && ldate == null) {// 向前抓取一个时间节点
			// 可忽略
			if (FrequencyPatterns.FREQUENCY_IGNORE.matcher(nodes.get(j).getText())
					.matches()) {
				j--;
				continue;
			}// 有|出现 可忽略
			if (DatePatterns.SP_KEY_WORDS.matcher(nodes.get(j).getText()).matches()) {
				j--;
				continue;
			}
			// 处理 10月08日至10月09日 涨停1次 空格问题
			else if (nodes.get(j).getText().equals(" ")) {
				j--;
				continue;
			} else if (nodes.get(j).type == NodeType.DATE) {
				ldate = nodes.get(j);
			}
			break;
		}
		return ldate;
	}

	private SemanticNode getRightDateNode(ArrayList<SemanticNode> nodes, int i) {
		int j = i + 1;
		SemanticNode rdate = null;
		while (j < nodes.size() && rdate == null) {// 向前抓取一个时间节点
			// 可忽略
			if (FrequencyPatterns.FREQUENCY_IGNORE.matcher(nodes.get(j).getText())
					.matches()) {
				j++;
				continue;
			}
			if (DatePatterns.SP_KEY_WORDS.matcher(nodes.get(j).getText()).matches()) {
				j++;
				continue;
			} else if (nodes.get(j).getText().equals(" ")) {
				j++;
				continue;
			} else if (nodes.get(j).type == NodeType.DATE) {
				rdate = nodes.get(j);
			}
			break;
		}
		return rdate;
	}

	/**
	 * @author: 吴永行
	 * @dateTime: 2013-10-14 下午3:20:33
	 * @description:
	 * 
	 */
	private void dealDateFrequency(ArrayList<SemanticNode> nodes) throws UnexpectedException,
			NotSupportedException {

		ArrayList<SemanticNode> words = nodes;
		// 日期后的数值节点做合并 超过+num num+之上
		for (int i = 0; i < words.size(); i++) {
			if(words.get(i)!=null && words.get(i).isSkipOldDateParser()) continue;
			
			if (words.get(i).type == NodeType.NUM
					&& (((NumNode) words.get(i)).getUnit() == Unit.CI || ((NumNode) words
							.get(i)).getUnit() == Unit.GE)) {
				// 合并数值节点 date + 超过|低于 num(次)
				// 向前抓取一个时间节点
				SemanticNode ldate = getLeftDateNode(words, i - 1);
				if (ldate != null) {
					if (FrequencyPatterns.FREQUENCY_COMPARE_UP.matcher(// 超过
							words.get(i - 1).getText()).matches()) {
						((NumNode) words.get(i)).getNuminfo().setTo(
								NumRange.MAX_);
						words.get(i).setText(words.get(i - 1).getText()+ words.get(i).getText() );
						words.remove(i - 1);
						i--;

					} else if (FrequencyPatterns.FREQUENCY_COMPARE_LOW.matcher(// 低于
							words.get(i - 1).getText()).matches()) {
						((NumNode) words.get(i)).getNuminfo().setFrom(
								NumRange.MIN_);
						words.get(i).setText(words.get(i - 1).getText() + words.get(i).getText() );
						words.remove(i - 1);
						i--;
					}
				}
				// 合并数字节点 date + num + 以上|以下
				ldate = getLeftDateNode(words, i);
				if (ldate != null && i + 1 < words.size()) {// 以上
					if (FrequencyPatterns.FREQUENCY_UP.matcher(
							words.get(i + 1).getText()).matches()) {
						((NumNode) words.get(i)).getNuminfo().setTo(
								NumRange.MAX_);
						words.get(i).setText(words.get(i).getText() + words.get(i + 1).getText() );  
						words.remove(i + 1);

					} else if (FrequencyPatterns.FREQUENCY_LOW.matcher(// 以下
							words.get(i + 1).getText()).matches()) {
						((NumNode) words.get(i)).getNuminfo().setFrom(
								NumRange.MIN_);
						words.get(i).setText(words.get(i).getText() + words.get(i + 1).getText() );
						words.remove(i + 1);
					}

				}
			}
		}

		// 真正开始处理时间频率
		// date + num(次)
		for (int i = 0; i < words.size(); i++) {
			if (words.get(i).type == NodeType.NUM
					&& (((NumNode) words.get(i)).getUnit() == Unit.CI || ((NumNode) words
							.get(i)).getUnit() == Unit.GE)) {
				// date + num
				SemanticNode ldate = getLeftDateNode(words, i);
				if (ldate != null
						&& addFrequencyInfoToDateByOtherDateOrNumber(
								(DateNode) ldate, words.get(i))) {
					//int j=i;
					while(i>=0 && words.get(i)!=ldate) words.remove(i--);
					//words.remove(words.get(i));
					//i--;
				}
			}
		}

		// 没有 num(次) 出现 如近5日有3天
		for (int i = 0; i < words.size(); i++) {
			SemanticNode curNode = words.get(i);
			// 不是 “有|出现”关键字
			if (!DatePatterns.SP_KEY_WORDS.matcher(curNode.getText()).matches()) {
				continue;
			}
			SemanticNode ldate = getLeftDateNode(words, i);
			SemanticNode num;
			// 出现 的前两个节点和后两个节点
			if (ldate != null) {
				num = getRightDateNode(words, i);
				if (ldate != null) {
					if (num == null) {
						NumNode nn = new NumNode("", 1, 1);
						nn.setNuminfo(makeNumRange());
						num = nn;
					} else if (addFrequencyInfoToDateByOtherDateOrNumber(
							(DateNode) ldate, num)) {
						words.remove(curNode);
						words.remove(num);
						i = i - 2;
					}
				}
			}
		}
	}

	private boolean addFrequencyInfoToDateByOtherDateOrNumber(
			DateNode leftDate, SemanticNode rightNode)
			throws UnexpectedException, NotSupportedException {
		if (leftDate.getFrequencyInfo() != null) {
			// 前面已经存在Frequency不能再进行 下一步操作
			return false;
		}
		if (rightNode.type != NodeType.NUM && rightNode.type != NodeType.DATE) {
			throw new UnexpectedException("Unexpected node type:%s",
					rightNode.type);
		}

		// Unit leftUnit = leftDate.getUnitOfDate();
		Unit rightUnit = rightNode.type == NodeType.NUM ? ((NumNode) rightNode)
				.getUnit() : ((DateNode) rightNode).getUnitOfDate();
		// 不需要
		// boolean notNeedCheck = !isLeftGERight(leftDate, rightNode);
		// if (notNeedCheck) {
		// return false;
		// }
		NumRange lengthRange = rightNode.type == NodeType.NUM ? ((NumNode) rightNode)
				.getNuminfo() : makeNumRangeByDateNode((DateNode) rightNode);
		boolean isSequence = rightNode.type == NodeType.NUM ? false
				: ((DateNode) rightNode).isSequence;
 		DateFrequencyInfo frequencyInfo = new DateFrequencyInfo(rightUnit,
   				lengthRange, isSequence);
 		leftDate.setFrequencyInfo(frequencyInfo);
		return true;
	}


	private NumRange makeNumRangeByDateNode(DateNode rightNode)
			throws UnexpectedException {
		Unit unit = rightNode.getUnitOfDate();
		String unitStr = EnumConvert.getStrFromUnit(unit);
		int length = rightNode.getRangeLen(unit);
		NumRange numRange = new NumRange();
		switch (rightNode.compare) {
		case EQUAL:
			numRange.setNumRange((double) length, (double) length);
			numRange.setBothInclude(true);
			break;
		case LONGER:
			numRange.setNumRange((double) length, null);
			numRange.setIncludeFrom(true);
			break;
		case SHORTER:
			numRange.setNumRange(null, (double) length);
			numRange.setIncludeTo(true);
			break;
		default:
			throw new UnexpectedException("Unexpected compare :%s",
					rightNode.compare);
		}
		numRange.setUnit(unitStr);
		return numRange;
	}

	private NumRange makeNumRange() {
		NumRange nr = new NumRange();
		nr.setNumRange((double) 1, NumRange.MAX_);
		nr.setBothInclude(true);
		nr.setUnit("次");
		return nr;
	}
}
