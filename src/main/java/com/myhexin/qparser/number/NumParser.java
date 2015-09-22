package com.myhexin.qparser.number;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.conf.SpecialWords;
import com.myhexin.qparser.date.DatePatterns;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.EnumDef.HiddenType;
import com.myhexin.qparser.define.EnumDef.LogicType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.OperatorType;
import com.myhexin.qparser.define.EnumDef.SpecialWordType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.define.MiscDef;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.define.OperDef;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.QPException;
import com.myhexin.qparser.except.TBException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.LogicNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.NumNode.MoveType;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.SortNode;
import com.myhexin.qparser.node.TimeNode;
import com.myhexin.qparser.node.UnknownNode;
import com.myhexin.qparser.tech.TechMisc;
import com.myhexin.qparser.time.TimeMerger;
import com.myhexin.qparser.time.parse.TimePatterns;
import com.myhexin.qparser.util.Util;

/**
 * 数字归类，格式统一化 调用tagNumType(Query query)，返回Query
 * 
 */

@Component
public class NumParser {
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(NumParser.class.getName());
	
	private static final double NUM_MORE_VALUE = 0.05; // 多出多少
	private static final double NUM_AROUND_VALUE = 0.05; // 波动范围是多少
	private static final double TOLERATE_RATE = 3000; //单位补全时，前后两个数容忍 比例  ，超过这个比例连同万，亿一起补全

	@Autowired(required=true)
	private TimeMerger timeMerger = null;
		
	
	public void setTimeMerger(TimeMerger timeMerger) {
    	this.timeMerger = timeMerger;
    }

	public ArrayList<SemanticNode> parse(ArrayList<SemanticNode> nodes) {
		try {
			
				tagHiddenNumNodes(nodes);
				removeSuperfluousBlankAndConbinator(nodes);
				changeChineseNumToArabic(nodes);
				
			if (!Param.DEBUG_MERGE) {
				mergeToDateWithYMDFromNum(nodes);
				mergeNumsByOp(nodes);
				mergeNumsByOp2(nodes);

				// 时间合并
				mergePureNumAsTime(nodes);

				addUnitToNum(nodes);

				// 时间合并
				mergeNumAsTime(nodes);

				mergeOnlyCompareToNum(nodes);
				addDayWithoutUnit2Month(nodes);

				addSubtractionSign(nodes); // 只合并可能的负值情况
				// getWholeNum();
				// 将getWholeNum函数分隔成两部分，每部分有明确的功能
				getWholeMoney(nodes); // 先处理5块4等情况
				getNumSeries(nodes); // 再处理处理2-3块钱等情况
				separateDateFromNum(nodes);
			}
			
			getNumRange(nodes);
			changeUnit(nodes);
			// changeRangeByCompare();
			changeByLogic(nodes);
			// isBetween();
			makeFirstNumBigSecondNum(nodes);
			checkNum(nodes);
		} catch (QPException e) {
			logger_.debug(e.getLogMsg());
			//query_.getLog().logMsg(ParseLog.LOG_ERROR, e.getMessage());
		}
		return nodes;
	}
	
	 /**
     * 用于区分比较明显的“点”歧义，如“上升10点”，“10点半上涨”
     * @throws UnexpectedException 
     */
    private void mergeNumAsTime(ArrayList<SemanticNode> nodes) throws UnexpectedException {
    	timeMerger.lightMerge(nodes);
    }

    /**
     * 根据冒号(:)将前后数字链接为时间{@link TimeNode}
     * @throws UnexpectedException 
     */
    private void mergePureNumAsTime(ArrayList<SemanticNode> nodes) throws UnexpectedException {
    	timeMerger.mergePureNumAsTime(nodes);
    }
	
	/**
     * 对同义词转换或正则补充的数值，进行Hidden属性标记，并删除“{{”、“}}”标记
     * @param query_ 
     */
    public void tagHiddenNumNodes(ArrayList<SemanticNode> nodes) {
    	ArrayList<SemanticNode> words = nodes;
    	for(int idx = 0; idx < words.size(); idx++){
    		SemanticNode tmpNode = words.get(idx);
    		if(tmpNode.type != NodeType.UNKNOWN){
    			continue ;
    		}
    		if(SpecialWords.hasWord(tmpNode.getText(), SpecialWordType.HIDDEN_RIGHT_TAG)){
    			words.remove(idx);
    			idx--;
    		}else if(SpecialWords.hasWord(tmpNode.getText(), SpecialWordType.HIDDEN_LEFT_TAG)){
    			int right = -1;
    			for(int j = idx + 1; j < words.size();j++){
    				SemanticNode tmpNode2 = words.get(j);
    				if(SpecialWords.hasWord(tmpNode2.getText(), SpecialWordType.HIDDEN_LEFT_TAG)){
    					//删除中间的“{{”
    					words.remove(j--);
    				}else if(SpecialWords.hasWord(tmpNode2.getText(), SpecialWordType.HIDDEN_RIGHT_TAG)){
    					//删除右边的“}}”
    					words.remove(j);
    					right = j ;
    					break ;
    				}else{
    					//标记“{{”后面的节点的Hidden
    					tmpNode2.hiddenType = HiddenType.HIDDEN;
    				}
    			}
    			//删除第一个“{{”
    			words.remove(idx);
    			right--;
    			//如果后面一直没有找到“}}”，则退出
    			if(right <= -1){
    				return ;
    			}else{
    				idx = right;
    			}
    		}
    	}
	}

	/**
	 * @author: 吴永行
	 * @dateTime: 2013-10-18 下午7:18:41
	 * @description: 遇到 num1至num2情况, 如果num1>num2 交换他们
	 * 				 增加功能，num1至num2前面没有单位， 补全前面的单位	
	 * 
	 */
	private void makeFirstNumBigSecondNum(ArrayList<SemanticNode> nodes) {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 1; i + 1 < words.size(); ++i) {
			// 有连词且连接两个数字
			if (NumPatterns.NUM_CONNECT_NUM_MARK.matcher(words.get(i).getText())
					.matches()
					&& words.get(i - 1).type == NodeType.NUM
					&& words.get(i + 1).type == NodeType.NUM) {
				NumNode preNn = (NumNode)words.get(i - 1);
				NumNode postNn = (NumNode)words.get(i + 1);
				// 前面数值节点单位补全
				if (preNn.getUnit() == Unit.UNKNOWN) {
					String secNumText = postNn.oldStr_;
					boolean combined = false;
					
					Matcher thousand = NumPatterns.CHINESE_THOUSAND.matcher(secNumText);//万 或者 亿    //检测是否符合逻辑，排除1000至1万这样的情况 
					//不是合法的数值范围 且后一个数值节点带有 万或者亿        -->  连带 万 亿 做单位补全
					if (thousand.find() && !isReasonableRange(preNn.getNuminfo(),postNn.getNuminfo())) {
						String text = preNn.getText() + thousand.group(1) ;
						NumRange nr = null;
						try {
							nr = getNumRangeFromStr(text);
							//是合法数值范围
							if (nr != null
									&& isReasonableRange(nr,postNn.getNuminfo())) {
								preNn.setNuminfo(nr);
								preNn.setText(NumUtil.getArabic(text));
								combined = true;
							}
						} catch (NotSupportedException e) {}
					}	
										
					Matcher matcher = NumPatterns.NUM_WITH_UNIT.matcher(secNumText);
					if (matcher.matches()) {											
						//连同万 亿单位补全未进行
						if (!combined && matcher.matches() && preNn.getNuminfo()!= null) {
							preNn.getNuminfo().setUnit(matcher.group(2));
							preNn.setText( preNn.getText() +  matcher.group(2) );
						}
					}
				}
				//第一个大于第二个， 交换
				if (NumUtil.big(preNn,postNn)) {
					words.set(i - 1, words.get(i + 1));
					words.set(i + 1, preNn);
				}
			}
		}
	}

	//大小不超过TOLERATE_RATE（3000）倍，认为是合理的数值范围
	private boolean isReasonableRange(NumRange first, NumRange second) {
		//如数字节点没有解析出来, 下面步骤不进行
		if(first ==null || second==null) return true;
		double firstNum = first.getDoubleFrom();
		double SeondNum = second.getDoubleFrom();
		if (firstNum == 0 || SeondNum == 0)
			return true;
		else if (firstNum / SeondNum >= TOLERATE_RATE
				|| SeondNum / firstNum >= TOLERATE_RATE) {
			return false;
		}
		return true;
	}

	/**
	 * @author: 吴永行
	 * @dateTime: 2013-10-17 上午10:51:54
	 * @description: 9块多 9快左右
	 * 
	 */
	private void mergeOnlyCompareToNum(ArrayList<SemanticNode> nodes) {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i + 1 < words.size(); ++i) {
			if (words.get(i).type == NodeType.NUM
					&& (NumPatterns.NUM_MORE.matcher(words.get(i + 1).getText())
							.matches() || NumPatterns.NUM_AROUND.matcher(
							words.get(i + 1).getText()).matches())) {
				NumNode nn = (NumNode) words.get(i);
				nn.setText(nn.getText() + words.get(i + 1).getText() );
				nn.oldStr_ += words.get(i + 1).getText();
				words.remove(i + 1);
				i--;
			}
		}

	}

	/**
	 * 处理不带单位的由分隔符分开的年月日的情况 类型1：2013-6-9 分词结果：2013/onto_num: -/onto_keyword:
	 * 6/onto_num: -/onto_keyword: 9/onto_num: 类型2：2013.6.9 分词结果：2013/onto_num:
	 * ./ 6.9/onto_num:
	 */
	private void mergeToDateWithYMDFromNum(ArrayList<SemanticNode> nodes) {
		//ArrayList<SemanticNode> nodes = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); i++) {
			// 处理2013-6-9的分词结果：2013/onto_num: -/onto_keyword: 6/onto_num:
			// -/onto_keyword: 9/onto_num:
			if (words.get(i).type == NodeType.NUM
					&& i + 1 < words.size()
					&& NumPatterns.COMBINATOR.matcher(words.get(i + 1).getText()).matches()
					&& i + 2 < words.size()
					&& words.get(i + 2).type == NodeType.NUM
					&& i + 3 < words.size()
					&& NumPatterns.COMBINATOR.matcher(words.get(i + 3).getText()).matches() && i + 4 < words.size()
					&& words.get(i + 4).type == NodeType.NUM) {
				String texts = "";
				for (int j = i; j <= i + 4; j++)
					texts += words.get(j).getText();
				if (NumPatterns.DATE_NEED_SEPARATE_1.matcher(texts).matches()
						|| NumPatterns.DATE_NEED_SEPARATE_3.matcher(texts).matches()) {
					DateNode dn = new DateNode(texts);
					words.set(i, dn);
					for (int j = i + 1; j <= i + 4; j++) {
						dn.oldNodes.add(words.get(j));
						// nodes.remove(i+1);
					}
					for (int j = i + 4; j >= i + 1; j--) {
						words.remove(j);
					}
				}
			}
			// 处理2013.6.9的分词结果：2013/onto_num: ./ 6.9/onto_num:
			if (words.get(i).type == NodeType.NUM
					&& i + 1 < words.size()
					&& NumPatterns.COMBINATOR.matcher(words.get(i + 1).getText()).matches() && i + 2 < words.size()
					&& words.get(i + 2).type == NodeType.NUM) {
				String texts = "";
				for (int j = i; j <= i + 2; j++)
					texts += words.get(j).getText();
				if (NumPatterns.DATE_NEED_SEPARATE_1.matcher(texts).matches()
						|| NumPatterns.DATE_NEED_SEPARATE_3.matcher(texts).matches()) {
					DateNode dn = new DateNode(texts);
					words.set(i, dn);
					for (int j = i + 1; j <= i + 2; j++) {
						dn.oldNodes.add(words.get(j));
						// nodes.remove(i+1);
					}
					for (int j = i + 2; j >= i + 1; j--) {
						words.remove(j);
					}
				}
			}
			
			
			//liuxiaofeng 2015/5/21
			// 处理2013.12/onto_date, . , 31/onto_num
			if (words.get(i).type == NodeType.DATE
					&& i + 1 < words.size()
					&& NumPatterns.COMBINATOR.matcher(words.get(i + 1).getText()).matches() && i + 2 < words.size()
					&& words.get(i + 2).type == NodeType.NUM) {
				StringBuilder newText = new StringBuilder();
				for (int j = i; j <= i + 2; j++)
					newText.append(words.get(j).getText());
				
				String texts = newText.toString();
				if (NumPatterns.DATE_NEED_SEPARATE_1.matcher(texts).matches()
						|| NumPatterns.DATE_NEED_SEPARATE_3.matcher(texts).matches()) {
					DateNode dn = new DateNode(texts);
					words.set(i, dn);
					for (int j = i + 1; j <= i + 2; j++) {
						dn.oldNodes.add(words.get(j));
						// nodes.remove(i+1);
					}
					for (int j = i + 2; j >= i + 1; j--) {
						words.remove(j);
					}
				}
			}
		}
	}

	/**
	 * 处理“三分之一”
	 */
	private void mergeNumsByOp(ArrayList<SemanticNode> nodes) {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 1; i < words.size(); i++) {
			SemanticNode node = words.get(i);
			SemanticNode leftN = NumUtil.getLastOrNextNode(i, words, true);
			SemanticNode rightN = NumUtil.getLastOrNextNode(i, words, false);
			boolean needCheck = NumPatterns.OP_DIV_BETWEEN_NUM_RIGHT_ON_LEFT
					.matcher(node.getText()).matches();
			needCheck &= leftN != null && leftN.type == NodeType.NUM
					&& NumPatterns.LONG.matcher(leftN.getText()).matches();
			needCheck &= rightN != null && rightN.type == NodeType.NUM
					&& NumPatterns.LONG.matcher(rightN.getText()).matches();
			if (!needCheck) {
				continue;
			}
			// "三分之一"
			NumNode demominator = (NumNode) leftN;
			NumNode numerator = (NumNode) rightN;
			String newText = String.format("%f", Double.valueOf(numerator.getText())
					/ Double.valueOf(demominator.getText()));
			String newOldText = String.format("%s%s%s", demominator.oldStr_,
					node.getText(), numerator.oldStr_);
			NumNode repNum = new NumNode(newText);
			repNum.oldStr_ = newOldText;
			words.set(i, repNum);
			words.remove(demominator);
			words.remove(numerator);
			i--;
		}
	}

	/**
	 * 处理“1/2”
	 */
	private void mergeNumsByOp2(ArrayList<SemanticNode> nodes) {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 1; i < words.size(); i++) {
			SemanticNode node = words.get(i);
			SemanticNode leftN = NumUtil.getLastOrNextNode(i, words, true);
			SemanticNode rightN = NumUtil.getLastOrNextNode(i, words, false);
			boolean needCheck = NumPatterns.OP_FRACTION_BETWEEN_NUM_RIGHT_ON_LEFT
					.matcher(node.getText()).matches();
			needCheck &= leftN != null && leftN.type == NodeType.NUM
					&& NumPatterns.LONG.matcher(leftN.getText()).matches();
			needCheck &= rightN != null && rightN.type == NodeType.NUM
					&& NumPatterns.LONG.matcher(rightN.getText()).matches();
			if (!needCheck) {
				continue;
			}
			// "1/2"
			NumNode demominator = (NumNode) rightN;
			NumNode numerator = (NumNode) leftN;
			String newText = String.format("%f", Double.valueOf(numerator.getText())
					/ Double.valueOf(demominator.getText()));
			String newOldText = String.format("%s%s%s", demominator.oldStr_,
					node.getText(), numerator.oldStr_);
			NumNode repNum = new NumNode(newText);
			repNum.oldStr_ = newOldText;
			words.set(i, repNum);
			words.remove(demominator);
			words.remove(numerator);
			i--;
		}
	}

	/**
	 * 为了避免歧义，先将既可以表示负号，又可以表示连接符的“-”整合到数字内 2013.08.29 linpeikun 修改这段代码
	 * 可将连接符“-”整合到数字内变为负值的情况： 1、num为纯数字，且前一个不为num 2、num包含数字单位，且负号前一个不为num
	 */
	private void addSubtractionSign(ArrayList<SemanticNode> nodes) {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 1; i < words.size(); i++) {
			SemanticNode node = words.get(i);
			if (node.type != NodeType.NUM) {
				continue;
			}

			SemanticNode leftNode = words.get(i - 1);
			boolean isNegative = false;
			// leftNode.text.equals("-")
			// NumPatterns.NEGATIVE_SIGN.matcher(leftNode.text).matches()
			isNegative |= i - 2 < 0
					&& NumPatterns.NEGATIVE_SIGN.matcher(leftNode.getText())
							.matches()
					&& NumPatterns.DOUBLE.matcher(node.getText()).matches();
			isNegative |= i - 2 >= 0
					&& words.get(i - 2).type != NodeType.NUM
					&& NumPatterns.NEGATIVE_SIGN.matcher(leftNode.getText())
							.matches()
					&& NumPatterns.DOUBLE.matcher(node.getText()).matches();
			isNegative |= i - 2 < 0
					&& NumPatterns.NEGATIVE_SIGN.matcher(leftNode.getText())
							.matches()
					&& NumPatterns.NUM_WITH_UNIT.matcher(node.getText()).matches();
			isNegative |= i - 2 >= 0
					&& words.get(i - 2).type != NodeType.NUM
					&& NumPatterns.NEGATIVE_SIGN.matcher(leftNode.getText())
							.matches()
					&& NumPatterns.NUM_WITH_UNIT.matcher(node.getText()).matches();

			if (isNegative) {
				String left = "-";
				node.setText( left + node.getText());
				((NumNode) node).oldStr_ = left + ((NumNode) node).oldStr_;
				words.remove(i - 1);
				i--;
			}
		}

	}

	/**
	 * 特殊处理如“5月31”这种后面日期无单位“日”导致日期无法整合完全 这部分必须在单位整合后，数字整合前处理,以防止无法识别此类特征
	 */
	private void addDayWithoutUnit2Month(ArrayList<SemanticNode> nodes) {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size() - 1; ++i) {
			boolean isOnlyMonth = words.get(i).type == NodeType.DATE
					&& NumPatterns.ONLY_MONTH_WITH_UNIT.matcher(
							words.get(i).getText()).matches();
			boolean maybeOnlyDay = words.get(i + 1).type == NodeType.NUM
					&& NumPatterns.ONLY_DAY.matcher(words.get(i + 1).getText())
							.matches();
			if (!isOnlyMonth || !maybeOnlyDay) {
				continue;
			}
			DateNode onlyMonthN = (DateNode) words.get(i);
			NumNode onlyDay = (NumNode) words.get(i + 1);
			Matcher midMonth = NumPatterns.ONLY_MONTH_WITH_UNIT
					.matcher(onlyMonthN.getText());
			midMonth.matches();
			int month = Integer.valueOf(midMonth.group(1));
			int day = Integer.valueOf(onlyDay.getText());
			if (day > DateUtil.getMonthDayCount(2012, month)) {
				// 若可能为“日”的数字大于 闰年 该月份的最大日期，则不予合并
				// 如“4月31”
				continue;
			}
			onlyMonthN.setText(month + "月" + day + "日");
			onlyMonthN.oldNodes.add(onlyDay);
			words.remove(i + 1);
		}
	}

	/**
	 * 将单位与数字分开的数字整合到一起
	 */
	private void addUnitToNum(ArrayList<SemanticNode> nodes) {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); ++i) {
			if (words.get(i).type != NodeType.NUM) {
				continue;
			}
			NumNode curNode = (NumNode) words.get(i);
			if (NumPatterns.NUM_WITH_UNIT.matcher(curNode.getText()).matches()) {
				// 当前数字必须没有单位
				continue;
			}
			SemanticNode leftN = NumUtil.getLastOrNextNode(i, words, true);
			SemanticNode rightN = NumUtil.getLastOrNextNode(i, words, false);
			String left = leftN == null ? null : leftN.getText();
			String right = rightN == null ? null : rightN.getText();
			if (left != null
					&& NumPatterns.NUM_UNITS_LEFT.matcher(left).matches()) {
				// 优先寻找左侧单位
				if (Param.DEBUG_NUM) {
					System.err.println("NUM_UNITS_LEFT");
				}
				curNode.oldStr_ = left + curNode.oldStr_;
				curNode.setText(curNode.getText() +  "%");
				words.remove(i - 1);
				i--;
			} else if (right != null
					&& NumPatterns.NUM_UNITS_RIGHT_TEN_PER.matcher(right)
							.matches()
					&& NumPatterns.LONG.matcher(curNode.getText()).matches()) {
				// 1成、两成等成当作单位进行解析
				// 再寻找右侧单位
				if (Param.DEBUG_NUM) {
					System.err.println("NUM_UNITS_RIGHT_TEN_PER");
				}
				curNode.oldStr_ = left + curNode.oldStr_;
				curNode.setText(curNode.getText() + "0%");
				words.remove(i + 1);
			} else if (right != null
					&& NumPatterns.NUM_UNITS_RIGHT.matcher(right).matches()) {
				if (Param.DEBUG_NUM) {
					System.err.println("NUM_UNITS_RIGHT");
				}
				curNode.oldStr_ += right;
				curNode.setText(curNode.getText() +  right);
				words.remove(i + 1);
			} else if (right != null
					&& NumPatterns.DATE_UNITS.matcher(right).matches()) {
				// - 若数字不以单位结尾，将后面的时间单位合并进来
				if (Param.DEBUG_NUM) {
					System.err.println("NUM_P5");
				}
				if (NumPatterns.UNITS_AFTER_DECIMALS.matcher(right).matches()
						&& Pattern.matches(
								"^(0?[1-9]|1[0-2])\\.(0?[1-9]|[12]\\d|3[01])$",
								curNode.getText())) {
					// 可以合并，如：5.27日
				} else if (Pattern.matches("^(\\-?\\d+?\\.\\d*?)$",
						curNode.getText()))
					return; // 不可以合并
				else if (curNode.getText().length() > 4)
					return;
				String text = curNode.getText() + right;
				DateNode dn = new DateNode(text);
				dn.oldNodes.add(curNode);
				dn.oldNodes.add(words.get(i + 1));
				words.set(i, dn);
				words.remove(i + 1);
			}
			//time 小时
			else if ( right != null
                    && NumPatterns.TIME_HOUR_UNITS_AFTER_NUM.matcher(right)
                            .matches()) {
                // - 若数字不以单位结尾，将后面的时间单位合并进来
                if (Param.DEBUG_NUM) {
                    System.err.println("HMS_UNIT");
                }
                if(!TimePatterns.HOUR_NUM.matcher(curNode.getText()).matches()){
                    continue;
                }
                String text = curNode.getText() + right;
                TimeNode timeNode = new TimeNode(text);
                words.set(i, timeNode);
                words.remove(i + 1);
            }
			//time 分
			else if ( right != null
                    && NumPatterns.TIME_MIN_UNIT_AFTER_NUM.matcher(right)
                            .matches()) {
                // - 若数字不以单位结尾，将后面的时间单位合并进来
                if (Param.DEBUG_NUM) {
                    System.err.println("TIME MIN");
                }
                if(!TimePatterns.MIN_SEC_NUM.matcher(curNode.getText()).matches()){
                    continue;
                }
                String text = curNode.getText() + right;
                TimeNode timeNode = new TimeNode(text);
                words.set(i, timeNode);
                words.remove(i + 1);
            }
			//time 秒
			else if ( right != null
                    && NumPatterns.TIME_SEC_UNIT_AFTER_NUM.matcher(right)
                            .matches()) {
                // - 若数字不以单位结尾，将后面的时间单位合并进来
                if (Param.DEBUG_NUM) {
                    System.err.println("TIME SEC");
                }
                if(!TimePatterns.MIN_SEC_NUM.matcher(curNode.getText()).matches()){
                    continue;
                }
                String text = curNode.getText() + right;
                TimeNode timeNode = new TimeNode(text);
                words.set(i, timeNode);
                words.remove(i + 1);
            }
		}
	}

	/**
	 * 清除句子中多余的空格
	 * 
	 * @param query
	 */
	private void removeSuperfluousBlankAndConbinator(ArrayList<SemanticNode> nodes) {
		removeSuperfluousSigns(nodes,NumPatterns.BLANK);
		removeSuperfluousSigns(nodes,NumPatterns.SINGLE_HORIZONTAL);
	}

	private void removeSuperfluousSigns(ArrayList<SemanticNode> nodes,Pattern pattern) {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); i++) {
			SemanticNode curNode = words.get(i);
			if (!pattern.matcher(curNode.getText()).matches()) {
				continue;
			}
			for (int k = i + 1; k < words.size(); k++) {
				SemanticNode nodeK = words.get(k);
				if (!pattern.matcher(nodeK.getText()).matches()) {
					break;
				}
				words.remove(k);
				k--;
			}
		}
	}

	/**
	 * 根据上下文，对数字是否在解析出的数值范围和是否需要浮动进行判断 处理不等于。应当合并到 {@link changeRangeByCompare}
	 * 
	 * @param query
	 */
	private void isBetween(ArrayList<SemanticNode> nodes) {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); i++) {
			if (words.get(i).type != NodeType.NUM) {
				continue;
			}
			NumNode curNode = (NumNode) words.get(i);
			SemanticNode leftN = NumUtil.getLastOrNextNode(i, words, true);
			SemanticNode rightN = NumUtil.getLastOrNextNode(i, words, false);
			String leftStr = leftN != null ? leftN.getText() : null;
			String rightStr = rightN != null ? rightN.getText() : null;
			if (leftN == null && rightN == null) {
				break;
			}
			boolean canCheckLeft = leftN != null;
			boolean canCheckRight = rightN != null;
			if (canCheckLeft
					&& (leftN.type == NodeType.NEGATIVE
							|| leftStr.equals("不等于") || leftStr.equals("不为"))) {
				curNode.isBetween = false;
				curNode.setText(leftStr + curNode.getText() );
				curNode.oldStr_ = leftStr + curNode.oldStr_;
				// 在不等于时，将是否包含From 和To 反过来
				// 例如，不等于 5-8,应该是<5或>8 ,非<=5或>=8
				curNode.getNuminfo().setIncludeFrom(
						!curNode.getNuminfo().isIncludeFrom());
				curNode.getNuminfo().setIncludeTo(
						!curNode.getNuminfo().isIncludeTo());
				words.remove(leftN);
				i--;
			} else if (canCheckLeft
					&& NumPatterns.MOVABLE_LEFT.matcher(leftStr).matches()) {
				checkNeedMovable(curNode);
				curNode.setText(leftStr + curNode.getText() );
				curNode.oldStr_ = leftStr + curNode.oldStr_;
				words.remove(leftN);
				i--;
			} else if (canCheckLeft
					&& NumPatterns.MOVABLE_DOWN_LEFT.matcher(leftStr).matches()) {
				checkNeedMovable(curNode);
				curNode.moveType = MoveType.DOWN;
				curNode.setText( leftStr + curNode.getText() );
				curNode.oldStr_ = leftStr + curNode.oldStr_;
				words.remove(leftN);
				i--;
			}

			if (canCheckRight
					&& NumPatterns.MOVABLE_RIGHT.matcher(rightStr).matches()) {
				checkNeedMovable(curNode);
				curNode.setText(curNode.getText() + rightStr );
				curNode.oldStr_ += rightStr;
				words.remove(rightN);
			} else if (canCheckRight
					&& NumPatterns.MOVABLE_UP_RIGHT.matcher(rightStr).matches()) {
				checkNeedMovable(curNode);
				curNode.moveType = MoveType.UP;
				curNode.setText(curNode.getText() + rightStr );
				curNode.oldStr_ += rightStr;
				words.remove(rightN);
			}
			/*
			 * else if (rightN != null &&
			 * NumPatterns.SHOULD_REMOVE_RIGHT.matcher(rightStr).matches()) {
			 * curNode.text += rightStr; curNode.oldStr_ += rightStr;
			 * words.remove(rightN); }
			 */
		}
	}

	/**
	 * 对数字是否需要浮动进行判断
	 */
	private void checkNeedMovable(NumNode curNode) {
		if (curNode.getNuminfo() == null || curNode.needMove) {
			return;
		}
		String rangeType = curNode.getRangeType();
		if (rangeType.equals(OperDef.QP_EQ)) {
			// 现只对确定值非范围进行调整
			curNode.needMove = true;
		}
	}

	/**
	 * 根据NumNode左右Operator节点，改变NumNode.numinfo
	 * 
	 * @param query
	 */
	private void changeRangeByCompare(ArrayList<SemanticNode> nodes) {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); i++) {
			if (words.get(i).type != NodeType.NUM
					|| ((NumNode) words.get(i)).getNuminfo() == null) {
				continue;
			}
			NumRange nr = ((NumNode) words.get(i)).getNuminfo();
			NumNode curNode = (NumNode) words.get(i);
			SemanticNode leftN = NumUtil.getLastOrNextNode(i, words, true);
			SemanticNode rightN = NumUtil.getLastOrNextNode(i, words, false);
			String type = curNode.getRangeType();
			if (leftN != null
					&& NumPatterns.COMPARISON_LEFT.matcher(leftN.getText())
							.matches()) {
				if (Param.DEBUG_NUM) {
					System.out.println("^<|>|=|<=|>=$");
				}
				String textL = leftN.getText();
				Double newFrom = NumUtil.isLessType(type) ? nr.getDoubleTo()
						: nr.getDoubleFrom();
				Double newTo = NumUtil.isGreaterType(type) ? nr.getDoubleFrom()
						: nr.getDoubleTo();

				curNode.setText ( textL + curNode.getText() );
				curNode.oldStr_ = textL + curNode.oldStr_;
				if (textL.matches(">=?") && !type.equals(OperDef.QP_EQ)) {
					// "达到3元以上","每股收益增长率连续5年大于20%至25%的上市公司"
					;// No Op
				} else if (textL.equals("=")) {
					curNode.needMove = false;
					nr.setBothInclude(true);
				} else if (textL.matches(">=?")) {
					nr.setNumRange(newTo, NumRange.MAX_);
					nr.setIncludeFrom(textL.matches(">="));
					nr.setIncludeTo(false);
				} else if (textL.matches("<=?")) {
					nr.setNumRange(NumRange.MIN_, newFrom);
					nr.setIncludeTo(textL.matches("<="));
					nr.setIncludeFrom(false);
				} else {
					;// No Op
				}
				words.remove(leftN);
				i--;
			} else if (rightN != null
					&& NumPatterns.COMPARISON_RIGHT.matcher(rightN.getText())
							.matches()) {
				if (Param.DEBUG_NUM) {
					System.out.println("^上|以上|以下|以内|内|之上|之下$");
				}
				if (leftN != null && leftN.getText().matches("^在$")) {
					curNode.setText ( leftN.getText() + curNode.getText() );
					curNode.oldStr_ = leftN.getText() + curNode.oldStr_;
					words.remove(leftN);
					i--;
				}
				String textR = rightN.getText();
				curNode.setText(curNode.getText() + textR);
				curNode.oldStr_ += textR;
				words.remove(rightN);
				if (type.equals(OperDef.QP_IN)) {
					// 若已为数字范围，则不再调整数值
					continue;
				}
				Double newFrom = NumUtil.isLessType(type) ? nr.getDoubleTo()
						: nr.getDoubleFrom();
				Double newTo = NumUtil.isGreaterType(type) ? nr.getDoubleFrom()
						: nr.getDoubleTo();
				if (textR.equals("以上") || textR.equals("之上")
						|| textR.equals("上")) {
					nr.setNumRange(newTo, NumRange.MAX_);
					nr.setIncludeFrom(false);
					nr.setIncludeTo(false);
				} else if (textR.matches("[以之]下|[以之]?内")) {
					nr.setNumRange(NumRange.MIN_, newFrom);
					nr.setIncludeTo(textR.matches("[以之]?内"));
					nr.setIncludeFrom(false);
				} else {
					assert (false);
				}
			}
		}
	}

	/**
	 * 从给定数字表达方式生成数字节点
	 * 
	 * @param text
	 * @return 数字节点
	 * @throws NotSupportedException
	 * @throws UnexpectedException
	 */
	public static NumNode getNumNodeFromStr(String text)
			throws NotSupportedException, UnexpectedException {
		NumRange range = getNumRangeFromStr(text);
		NumNode numNode = new NumNode(text);
		numNode.setNuminfo(range);
		return numNode;
	}

	private static NumRange getNumRangeFromChineseRangeStr(
			String chineseRangeStr) throws NotSupportedException {
		boolean isChineseRange = NumPatterns.NUM_RANGE_CHINESE.matcher(
				chineseRangeStr).matches();
		if (!isChineseRange) {
			return null;
		}
		if (Param.DEBUG_NUM) {
			System.err.println("NUM_RANGE_CHINESE");
		}
		Matcher numMatcher = NumPatterns.NUM_RANGE_CHINESE
				.matcher(chineseRangeStr);
		numMatcher.matches();

		String fromFirstPart = numMatcher.group(1);
		String fromSecPart = numMatcher.group(2);
		String fromUnit = numMatcher.group(3);
		Double fromFirst = Double.valueOf(fromFirstPart);
		Double from = !fromSecPart.isEmpty() ? fromFirst
				* Double.valueOf(NumUtil.parseChineseNumber(fromSecPart))
				: fromFirst;

		String toFirstPart = numMatcher.group(4);
		String toSecPart = numMatcher.group(5);
		String toUnit = numMatcher.group(6);
		Double toFirst = Double.valueOf(toFirstPart);
		Double to = toFirst
				* Double.valueOf(NumUtil.parseChineseNumber(toSecPart));

		boolean canParse = fromFirst < toFirst || fromUnit.isEmpty();
		boolean fromCanParse = fromSecPart.isEmpty();
		fromCanParse |= !fromSecPart.isEmpty()
				&& toSecPart.charAt(toSecPart.length() - 1) != fromSecPart
						.charAt(fromSecPart.length() - 1);
		canParse &= fromCanParse;
		if (!canParse) {
			return null;
		}
		String unit = toUnit.isEmpty() ? fromUnit : toUnit;

		if (toSecPart.endsWith("万亿")) {
			from = fromSecPart.endsWith("万") ? from * 100000000.0 : fromSecPart
					.isEmpty() ? from * 1000000000000.0 : null;
		} else if (toSecPart.endsWith("千亿")) {
			from = fromSecPart.endsWith("千") ? from * 100000000.0 : fromSecPart
					.isEmpty() ? from * 100000000000.0 : null;
		} else if (toSecPart.endsWith("百亿")) {
			from = fromSecPart.endsWith("百") ? from * 100000000.0 : fromSecPart
					.isEmpty() ? from * 10000000000.0 : null;
		} else if (toSecPart.endsWith("十亿")) {
			from = fromSecPart.endsWith("十") ? from * 100000000.0 : fromSecPart
					.isEmpty() ? from * 1000000000.0 : null;
		} else if (toSecPart.endsWith("亿")) {
			from = fromSecPart.isEmpty() ? from * 100000000.0 : null;
		} else if (toSecPart.endsWith("千万")) {
			from = fromSecPart.endsWith("千") ? from * 10000.0 : fromSecPart
					.isEmpty() ? from * 10000000.0 : null;
		} else if (toSecPart.endsWith("百万")) {
			from = fromSecPart.endsWith("百") ? from * 10000.0 : fromSecPart
					.isEmpty() ? from * 1000000.0 : null;
		} else if (toSecPart.endsWith("十万")) {
			from = fromSecPart.endsWith("十") ? from * 10000.0 : fromSecPart
					.isEmpty() ? from * 100000.0 : null;
		} else if (toSecPart.endsWith("万")) {
			from = fromSecPart.isEmpty() ? from * 10000.0 : null;
		} else if (toSecPart.endsWith("千") && fromFirst < 10) {
			from = fromSecPart.isEmpty() ? from * 1000.0 : null;
		} else if (toSecPart.endsWith("百") && fromFirst < 10) {
			from = fromSecPart.isEmpty() ? 100.0 : null;
		}
		if (from == null) {
			// “如5千至5十万”，不处理
			return null;
		}
		NumRange numRange = new NumRange();
		numRange.setNumRange(from, to);
		numRange.setBothInclude(true);
		numRange.setUnit(unit);
		return numRange;
	}

	/**
	 * 根据数字文字表达方式解析出NumRange
	 * 
	 * @param numStr
	 *            数字文字表达
	 * @return 解析出的NumRange
	 * @throws NotSupportedException
	 * @throws UnexpectedException
	 */
	public static NumRange getNumRangeFromStr(String numStr)
			throws NotSupportedException {
		if (numStr == null) {
			return null;
		}
		NumRange range = getNumRangeFromRegularNumStr(numStr);
		range = range == null ? getNumRangeFromNumWithOpStr(numStr) : range;
		range = range == null ? getNumRangeFromRegularChineseNumStr(numStr): range;
		range = range == null ? getNumRangeFromNumWithCompare(numStr) : range;
		range = range == null ? getNumRangeFromMoneyNumStr(numStr) : range;
		range = range == null ? getNumRangeFromChineseRangeStr(numStr) : range;
		range = range == null ? getNumRangeFromNumRangeStr(numStr) : range;
		range = range == null ? getNumRangeFromNumWithOnlyCompareMore(numStr): range;
		range = range == null ? getNumRangeFromNumWithOnlyCompareAround(numStr): range;
		/*
		 * if (range == null) { throw new
		 * NotSupportedException(MsgDef.UNKNOW_NUM_FMT, numStr); }
		 */
		return range;
	}

	/**
	 * @author: 吴永行
	 * @dateTime: 2013-10-17 上午10:43:55
	 * @description: 处理 num左右 类型
	 * @param numStr
	 * @return
	 * @throws NotSupportedException
	 * 
	 */
	private static NumRange getNumRangeFromNumWithOnlyCompareAround(
			String numStr) throws NotSupportedException {
		Matcher numMatcher = NumPatterns.NUM_AROUND.matcher(numStr);
		if (!numMatcher.matches()) {
			return null;
		}
		if (Param.DEBUG_NUM) {
			System.err.println("NUM_AROUND");
		}
		NumRange nr = getNumRangeFromStr(numMatcher.group(1));
		if (nr != null && nr.getUnit() != null) {
			nr.setTo(nr.getDoubleTo() * (1 + NUM_AROUND_VALUE));
			nr.setFrom(nr.getDoubleFrom() * (1 - NUM_AROUND_VALUE));
			nr.setBothInclude(true);
		}
		return nr;
	}

	/**
	 * @author: 吴永行
	 * @dateTime: 2013-10-17 上午9:53:06
	 * @description: 处理 num多 类型
	 * @param numStr
	 * @return
	 * @throws NotSupportedException
	 * 
	 */
	private static NumRange getNumRangeFromNumWithOnlyCompareMore(String numStr)
			throws NotSupportedException {
		Matcher numMatcher = NumPatterns.NUM_MORE.matcher(numStr);
		if (!numMatcher.matches()) {
			return null;
		}
		if (Param.DEBUG_NUM) {
			System.err.println("NUM_MORE");
		}
		NumRange nr = getNumRangeFromStr(numMatcher.group(1));
		if (nr != null && nr.getUnit() != null) {
			nr.setTo(nr.getDoubleTo() * (1 + NUM_MORE_VALUE));
			nr.setBothInclude(true);
		}
		return nr;
	}

	private static NumRange getNumRangeFromNumWithOpStr(String numStr) {
		Matcher numMatcher = NumPatterns.NUM_WITH_OP.matcher(numStr);
		if (!numMatcher.matches()) {
			return null;
		}
		if (Param.DEBUG_NUM) {
			System.err.println("NUM_WITH_OP");
		}
		Double demominator = Double.valueOf(numMatcher.group(1));
		Double numerator = Double.valueOf(numMatcher.group(3));
		Double numVal = demominator / numerator;
		String unit = numMatcher.group(2).isEmpty() ? numMatcher.group(4)
				: numMatcher.group(2);
		NumRange range = new NumRange();
		range.setNumRange(numVal, numVal);
		range.setUnit(unit);
		range.setBothInclude(true);
		return range;
	}

	private static NumRange getNumRangeFromRegularChineseNumStr(String numStr)
			throws NotSupportedException {
		String unit = null;
		Double value = null;
		if (NumPatterns.NUM_REGULAR_CHINESE_WITH_DOUBLE_HEAD.matcher(numStr)
				.matches()) {
			if (Param.DEBUG_NUM) {
				System.err.println("NUM_REGULAR_CHINESE_WITH_DOUBLE_HEAD");
			}
			Matcher numMatcher = NumPatterns.NUM_REGULAR_CHINESE_WITH_DOUBLE_HEAD
					.matcher(numStr);
			numMatcher.matches();
			Double headVal = Double.valueOf(numMatcher.group(1));
			Double chineseVal = Double.valueOf(NumUtil.parseChineseNumber("一"
					+ numMatcher.group(2)));
			value = headVal * chineseVal;
			unit = numMatcher.group(3);
		} else if (NumPatterns.NUM_REGULAR_CHINESE.matcher(numStr).matches()) {
			if (Param.DEBUG_NUM) {
				System.err.println("NUM_REGULAR_CHINESE");
			}
			Matcher numMatcher = NumPatterns.NUM_REGULAR_CHINESE
					.matcher(numStr);
			numMatcher.matches();
			value = Double.valueOf(NumUtil.parseChineseNumber(numMatcher
					.group(1)));
			unit = numMatcher.group(2);
		} else {
			return null;
		}
		NumRange numRange = new NumRange();
		numRange.setNumRange(value, value);
		numRange.setBothInclude(true);
		numRange.setUnit(unit);
		return numRange;
	}

	/**
	 * 处理普通不带单位的数字
	 * 
	 * @param numStr
	 * @return
	 */
	private static NumRange getNumRangeFromRegularNumStr(String numStr) {
		if (!NumPatterns.NUM_REGULAR.matcher(numStr).matches()) {
			return null;
		}
		/** 处理普通数字，形如“2.3” */
		if (Param.DEBUG_NUM) {
			System.err.println("NUM_REGULAR");
		}
		Matcher numMatcher = NumPatterns.NUM_REGULAR.matcher(numStr);
		numMatcher.matches();
		String numValStr = numMatcher.group(1);
		String unit = numMatcher.group(2);
		NumRange numrange = new NumRange();
		numrange.setNumRange(numValStr, numValStr);
		numrange.setBothInclude(true);
		numrange.setUnit(unit);
		return numrange;
	}

	private static NumRange getNumRangeFromNumRangeStr(String numStr)
			throws NotSupportedException {
		if (!NumPatterns.NUM_RANGE.matcher(numStr).matches()) {
			return null;
		}
		if (Param.DEBUG_NUM) {
			System.err.println("NUM_RANGE");
		}
		Matcher numMatcher = NumPatterns.NUM_RANGE.matcher(numStr);
		numMatcher.matches();
		String num1 = numMatcher.group(1);
		String num2 = numMatcher.group(2);
		NumRange range1 = null;
		NumRange range2 = null;
		try {
			range1 = getNumRangeFromStr(num1);
			range2 = getNumRangeFromStr(num2);
		} catch (NotSupportedException e) {
			throw new NotSupportedException(MsgDef.UNKNOW_NUM_FMT, numStr);
		}
		String unit = range2.getUnit() == null ? range1.getUnit() : range2
				.getUnit();
		NumRange range = new NumRange();
		range.setNumRange(range1.getDoubleFrom(), range2.getDoubleTo());
		range.setIncludeFrom(range1.isIncludeFrom());
		range.setIncludeTo(range2.isIncludeTo());
		range.setUnit(unit);
		return range;
	}

	private static NumRange getNumRangeFromNumWithCompare(String numStr)
			throws NotSupportedException {
		if (!NumPatterns.NUM_WITH_COMPARE.matcher(numStr).matches()) {
			return null;
		}
		if (Param.DEBUG_NUM) {
			System.err.println("NUM_WITH_COMPARE");
		}
		Matcher numMatcher = NumPatterns.NUM_WITH_COMPARE.matcher(numStr);
		numMatcher.matches();
		String compare = numMatcher.group(1);
		String numRangeStr = numMatcher.group(2);
		NumRange originalRange;
		try {
			originalRange = getNumRangeFromStr(numRangeStr);
		} catch (NotSupportedException e) {
			throw new NotSupportedException(MsgDef.UNKNOW_NUM_FMT, numStr);
		}
		NumRange range = new NumRange();
		range.setUnit(originalRange.getUnit());
		String rangeType = originalRange.getRangeType();
		Double originalFrom = NumUtil.isLessType(rangeType) ? originalRange.getDoubleTo() : originalRange.getDoubleFrom();
		Double originalTo = NumUtil.isGreaterType(rangeType) ? originalRange.getDoubleFrom() : originalRange.getDoubleTo();
		if (compare.matches(">=?|大于(等于)?") && !rangeType.equals(OperDef.QP_IN)) {
			range.setFrom(originalFrom);
			range.setIncludeFrom(compare.matches(">=|大于等于"));
		}else if (compare.matches("!=") && ( rangeType.equals(OperDef.QP_NE) || rangeType.equals(OperDef.QP_EQ) ) ) {
			range.setFrom(originalFrom);
			range.setIncludeFrom(true);
		}  else if (compare.matches("<=?|小于(等于)?")) {
			range.setTo(originalTo);
			range.setIncludeTo(compare.matches("<=|小于等于"));
		} else {
			range.setNumRange(originalFrom, originalTo);
			range.setBothInclude(true);
		}
		return range;
	}

	private static NumRange getNumRangeFromMoneyNumStr(String numStr) {
		boolean canParseAsMoney = NumPatterns.NUM_MONEY.matcher(numStr)
				.matches()
				|| NumPatterns.NUM_MONEY_1.matcher(numStr).matches()
				|| NumPatterns.NUM_MONEY_2.matcher(numStr).matches();
		if (!canParseAsMoney) {
			return null;
		}
		if (Param.DEBUG_NUM) {
			System.err.println("NUM_MONEY");
		}

		NumRange range = new NumRange();
		Double num = null;
		if (NumPatterns.NUM_MONEY.matcher(numStr).matches()) {
			Matcher numMatcher = NumPatterns.NUM_MONEY.matcher(numStr);
			numMatcher.matches();
			String num1 = numMatcher.group(1);
			String num2 = numMatcher.group(2);
			String num3 = numMatcher.group(3);
			Double numVal1 = num1.isEmpty() ? 0.0 : Double.valueOf(num1);
			Double numVal2 = num2.isEmpty() ? 0.0 : Double.valueOf(num2);
			Double numVal3 = num3.isEmpty() ? 0.0 : Double.valueOf(num3);
			num = numVal1 + 0.1 * numVal2 + 0.01 * numVal3;
		} else {
			boolean isMatch2Type1 = NumPatterns.NUM_MONEY_1.matcher(numStr)
					.matches();
			Matcher numMatcher = isMatch2Type1 ? NumPatterns.NUM_MONEY_1
					.matcher(numStr) : NumPatterns.NUM_MONEY_2.matcher(numStr);
			numMatcher.matches();
			String num1 = numMatcher.group(1);
			String num2 = isMatch2Type1 ? numMatcher.group(2).replaceAll("毛|角",
					"") : numMatcher.group(2).replaceAll("分", "");

			Double numVal1 = Double.valueOf(num1);
			Double numVal2 = num2.isEmpty() ? 0.0 : Double.valueOf(num2);

			num = isMatch2Type1 ? numVal1 + 0.1 * numVal2 : 0.1 * numVal1
					+ 0.01 * numVal2;
		}
		range.setNumRange(num, num);
		range.setUnit("元");
		range.setBothInclude(true);
		return range;
	}

	private static NumRange getNumRangeFromMoneyNumStrA(String numStr) {
		boolean canParseAsMoney = NumPatterns.NUM_MONEY_1.matcher(numStr)
				.matches() || NumPatterns.NUM_MONEY_2.matcher(numStr).matches();
		if (!canParseAsMoney) {
			return null;
		}
		if (Param.DEBUG_NUM) {
			System.err.println("NUM_MONEY");
		}
		NumRange range = new NumRange();
		Double num = null;
		boolean isMatch2Type1 = NumPatterns.NUM_MONEY_1.matcher(numStr)
				.matches();
		Matcher numMatcher = isMatch2Type1 ? NumPatterns.NUM_MONEY_1
				.matcher(numStr) : NumPatterns.NUM_MONEY_2.matcher(numStr);
		numMatcher.matches();
		String num1 = numMatcher.group(1);
		String num2 = isMatch2Type1 ? numMatcher.group(2).replaceAll("毛|角", "")
				: numMatcher.group(2).replaceAll("分", "");

		Double numVal1 = Double.valueOf(num1);
		Double numVal2 = num2.isEmpty() ? 0.0 : Double.valueOf(num2);

		num = isMatch2Type1 ? numVal1 + 0.1 * numVal2 : 0.1 * numVal1 + 0.01
				* numVal2;
		range.setNumRange(num, num);
		range.setUnit("元");
		range.setBothInclude(true);
		return range;
	}

	/**
	 * 根据规则更改数字单位。已处理的单位是：手、[12]股
	 * 
	 * @param query
	 * @throws UnexpectedException
	 */
	private void changeUnit(ArrayList<SemanticNode> nodes) throws UnexpectedException {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); i++) {
			if (words.get(i).type != NodeType.NUM
					|| ((NumNode) words.get(i)).getNuminfo() == null) {
				continue;
			}
			NumNode nn = (NumNode) words.get(i);
			String rangeType = nn.getRangeType();
			NumRange nr = nn.getNuminfo();
			Unit unit = ((NumNode) words.get(i)).getUnit();
			if (unit == Unit.SHOU) {
				NumUtil.changeNRByOp(nn, OperatorType.MULTIPLY, 100);
				nr.setUnit("股");
			} else if (((NumNode) words.get(i)).getUnit() == Unit.GU) {
				if (NumUtil.isGreaterType(rangeType)
						&& nr.getDoubleFrom() < 2.0
						|| NumUtil.isLessType(rangeType)
						&& nr.getDoubleTo() < 2.0
						|| rangeType.equals(OperDef.QP_EQ)
						&& nr.getDoubleFrom() < 2.0) {
					// 对单位为股的数字进行特殊处理，可能需考虑
					nr.setUnit(null);
					nn.setNuminfo(nr);
				}
			} else {
				;// No Op
			}
		}
	}

	/**
	 * 根据NumNode间LogicNode合并NumNode
	 * 
	 * @param query
	 */
	private void changeByLogic(ArrayList<SemanticNode> nodes) {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 1; i < words.size() - 1; i++) {
			SemanticNode nodeI = words.get(i);
			SemanticNode left = NumUtil.getLastOrNextNode(i, words, true);
			SemanticNode right = NumUtil.getLastOrNextNode(i, words, false);

			boolean isLogic = nodeI.type == NodeType.LOGIC;
			boolean isNumAndSign = NumPatterns.NUM_AND_SIGN.matcher(nodeI.getText())
					.matches();
			boolean leftIsNum = left != null && left.type == NodeType.NUM;
			boolean rightIsNum = right != null && right.type == NodeType.NUM;
			if (!isLogic && !isNumAndSign || !leftIsNum || !rightIsNum) {
				continue;
			}
			String leftRangeType = ((NumNode) left).getRangeType();
			String rightRangeType = ((NumNode) right).getRangeType();
			boolean leftCanNotJoin = leftRangeType == null
					|| !NumUtil.isLessType(leftRangeType)
					&& !NumUtil.isGreaterType(leftRangeType);
			boolean rightCanNotJoin = rightRangeType == null
					|| !NumUtil.isLessType(rightRangeType)
					&& !NumUtil.isGreaterType(rightRangeType);
			if (leftCanNotJoin || rightCanNotJoin) {
				continue;
			}

			NumNode numL = (NumNode) left;
			NumNode numR = (NumNode) right;
			LogicType lt = isLogic ? ((LogicNode) nodeI).logicType
					: LogicType.AND;

			try {
				NumNode newN = getNewNumByLogic(numL, numR, lt);
				if (newN != null) {
					words.set(i, newN);
					words.remove(numL);
					words.remove(numR);
					i--;
				}
			} catch (TBException e) {
				//query_.getLog().logMsg(ParseLog.LOG_TIP, e.getMessage());
			}
		}

		for (int i = 0; i < words.size() - 1; i++) {
			if (words.get(i).type != NodeType.NUM) {
				continue;
			}
			NumNode numL = (NumNode) words.get(i);
			SemanticNode nodeR = NumUtil.getLastOrNextNode(i, words, false);
			if (nodeR == null || nodeR.type != NodeType.NUM) {
				continue;
			}
			NumNode numR = (NumNode) nodeR;
			String leftRangeType = numL.getRangeType();
			String rightRangeType = numR.getRangeType();
			boolean leftCanNotJoin = leftRangeType == null
					|| !NumUtil.isLessType(leftRangeType)
					&& !NumUtil.isGreaterType(leftRangeType);
			boolean rightCanNotJoin = rightRangeType == null
					|| !NumUtil.isLessType(rightRangeType)
					&& !NumUtil.isGreaterType(rightRangeType);
			if (leftCanNotJoin || rightCanNotJoin) {
				continue;
			}
			try {
				NumNode newN = getNewNumByLogic(numL, numR, LogicType.AND);
				if (newN != null) {
					words.set(i, newN);
					words.remove(numR);
				}
			} catch (TBException e) {
				continue;
			}
		}
	}

	/**
	 * 解析Query中的NumNode
	 * 
	 * @param query
	 * @throws NotSupportedException
	 * @throws UnexpectedException
	 * @throws NotSupportedException
	 */
	private void getNumRange(ArrayList<SemanticNode> nodes) throws UnexpectedException,
			NotSupportedException {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); i++) {
			if (words.get(i).type != NodeType.NUM) {
				continue;
			}
			NumNode curNode = (NumNode) words.get(i);
			boolean notNeedParse = curNode.isFake();
			notNeedParse |= curNode.getText().matches(MiscDef.NUM_TO_PERIOD_REGEX);
			if (notNeedParse) {
				// 不需解析伪数字
				continue;
			}
			NumRange numrange = getNumRangeFromStr(curNode.getText());
			numrange = numrange == null ? getNumRangeFromStr(curNode.oldStr_)
					: numrange; // 可能转变过的字符串解析不了，解析旧字符串没成功也没关系
			if (numrange != null) {
				curNode.setNuminfo(numrange);
			} else {
				// 解析不了，则将其转化为UnknownNode而不是抛出异常
				UnknownNode uNode = new UnknownNode(curNode.getText());
				words.set(i, uNode);
			}
		}
	}

	/**
	 * 识别分词未识别的数字或日期节点，并将中文数字转为阿拉伯数字。此处仅 构建NumNode或DateNode
	 * 
	 * @param query
	 * @throws UnexpectedException
	 * @throws NumberFormatException
	 * @throws NotSupportedException
	 */
	public void changeChineseNumToArabic(ArrayList<SemanticNode> nodes) throws NotSupportedException {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		// - 有些数字未被分词标为Num，如4.7等
		for (int i = 0; i < words.size(); ++i) {
			String str = words.get(i).getText();
			if (words.get(i).type != NodeType.UNKNOWN
					|| !NumPatterns.DOUBLE.matcher(str).matches()) {
				// 对分词未识别的阿拉伯数字进行再次识别
				continue;
			}
			NumNode nn = new NumNode(str);
			nn.oldStr_ = str;
			words.set(i, nn);
		}

		for (int i = 0; i < words.size(); ++i) {
			// 对未识别的其他包含中文的数字进行再次识别，并转化为阿拉伯数字
			if (words.get(i).type == NodeType.STR_VAL
					|| words.get(i).type == NodeType.SPECIAL) {
				continue;
			}
			String str = words.get(i).getText();
			if (NumPatterns.CHINESE_NUM.matcher(str).matches()) {
				// 对没有单位的包含中文的数字进行再次识别，并转化为阿拉伯数字，如“十亿”
				NumNode nn = new NumNode(NumUtil.getArabic(str));
				nn.oldStr_ = words.get(i).getText();
				words.set(i, nn);
			} else if (NumPatterns.DOUBLE_WITH_CHINESE_NUM.matcher(str)
					.matches()) {
				// 对没有单位的包含中文的数字进行再次识别，并转化为阿拉伯数字，如“1.2亿”
				String text = str;
				Matcher mid = NumPatterns.DOUBLE_WITH_CHINESE_NUM.matcher(text);
				mid.matches();
				Double head = Double.valueOf(mid.group(1));
				String tail = "一" + mid.group(2);
				Double body = Double.valueOf(NumUtil.getArabic(tail));
				String all = String.format("%f", body * head);
				NumNode nn = new NumNode(all);
				nn.oldStr_ = words.get(i).getText();
				words.set(i, nn);
			} else if (NumPatterns.CHINESE_NUM_TYPE_SORT.matcher(str).matches()) {
				// 对未识别的、有中文数字的排序进行解析,如“前三”
				Matcher mid = NumPatterns.CHINESE_NUM_TYPE_SORT.matcher(str);
				mid.matches();
				String numStr = NumUtil.getArabic(mid.group(2));
				SortNode sn = new SortNode(mid.group(1) + numStr);
				sn.setDescending_(!mid.group(1).equals("后"));
				sn.k_ = Double.valueOf(numStr);
				sn.isTopK_ = !mid.group(1).equals("第")
						|| mid.group(1).equals("第") && sn.k_ == 1.0;
				words.set(i, sn);
			} else if (NumPatterns.CHINESE_DATE_WITH_UNIT.matcher(str)
					.matches()) {
				// 对中文时间进行识别，但不解析
				Matcher mid = NumPatterns.CHINESE_DATE_WITH_UNIT.matcher(str);
				mid.matches();
				String text = NumUtil.getArabic(mid.group(1));
				text += mid.group(2);
				DateNode dn = new DateNode(text);
				dn.oldNodes.add(words.get(i));
				words.set(i, dn);
			} else if (NumPatterns.CHINESE_NUM_WITH_UNIT.matcher(str).matches()) {
				// 对有单位的包含中文的数字进行再次识别，如“十手”
				Matcher mid = NumPatterns.CHINESE_NUM_WITH_UNIT.matcher(str);
				mid.matches();
				String text = NumUtil.getArabic(mid.group(1));
				text += mid.group(2);
				NumNode nn = new NumNode(text);
				nn.oldStr_ = str;
				words.set(i, nn);
			}
		}
	}

	/**
	 * 根据规则合并Query中的数字。 经过此函数，所有以连接符相连的数字/时间已合并；类似“4块3”的钱也合并
	 * 
	 * @param query
	 * @throws UnexpectedException
	 * @throws NotSupportedException
	 */
	public void getWholeNum(ArrayList<SemanticNode> nodes) throws NotSupportedException {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		// - 若数字不以单位结尾，将后面的数字单位合并进来
		boolean hasConnect = false;
		for (int i = 0; i < words.size(); ++i) {
			boolean needCheck = checkNodeAsNumToParse(words.get(i));
			if (!needCheck) {
				continue;
			}
			// - 将表达一个数字的多个数字/时间节点合并成一个数字或时间节点
			NumNode curNum = (NumNode) words.get(i);
			for (int j = i + 1; j < words.size(); j++) {
				if (NumPatterns.COMBINATOR.matcher(words.get(j).getText()).matches() // 连接符
						&& j < words.size() - 1 && words.get(j + 1) != null) {
					// 当前节点为连接符，并且后面还有节点，且后接节点为时间或者数字
					if (Param.DEBUG_NUM) {
						System.err.println("连接符");
					}
					SemanticNode nextNode = NumUtil.getLastOrNextNode(j, words,
							false);

					boolean nextIsDateAndCanConnect = !NumPatterns.NUM_WITH_UNIT
							.matcher(curNum.getText()).matches()
							&& nextNode.type == NodeType.DATE;
					boolean nextIsNumAndCanConnect = checkNodeAsNumToParse(nextNode)
							&& numsUnitAreMatch(curNum.getText(), nextNode.getText());

					if (nextIsDateAndCanConnect || nextIsNumAndCanConnect) {
						String textJ = words.get(j).getText();
						((NumNode) curNum).oldStr_ += textJ;
						curNum.setText( curNum.getText() + textJ );
						words.remove(j);
						j--;
						hasConnect = true;
					}
				} else if (checkNodeAsNumToParse(words.get(j))) {
					// - 两个连续数字。目前实质只将连续2个钱的数字合并。
					if (Param.DEBUG_NUM) {
						System.err.println("连续数字");
					}
					boolean isWhole = numsUnitAreMatch(curNum.getText(),
							words.get(j).getText());
					if (NumPatterns.NUM_WITH_UNIT.matcher(curNum.getText())
							.matches()
							&& NumPatterns.LONG.matcher(words.get(j).getText())
									.matches()) {
						// 本身有单位，后面的无单位，如5块2
						if (Param.DEBUG_NUM) {
							System.err.println("first has unit,sec doesn't");
						}
						Matcher str1 = NumPatterns.NUM_WITH_UNIT
								.matcher(curNum.getText());
						str1.matches();
						String unit1 = str1.group(2);
						// 只有钱的单位才允许省略后面的单位
						if (!NumPatterns.UNIT_FOR_MONEY_1.matcher(unit1)
								.matches()
								&& !NumPatterns.UNIT_FOR_MONEY_2.matcher(unit1)
										.matches()) {
							isWhole = false;
						}
					}

					if (!isWhole) {
						// 不能合并就不再继续下去
						break;
					}
					String textJ = words.get(j).getText();
					String old = ((NumNode) words.get(j)).oldStr_;
					if (words.get(j).getText().equals("0")
							&& NumPatterns.CHINESE_NUM.matcher(old).matches()) {
						// 处理分词错误或连接符错误的数字，如“1.亿”
						String newStr = NumUtil.getArabic(curNum.getText() + old);
						curNum.setText(newStr );
					} else {
						curNum.setText (curNum.getText() + textJ);
					}
					((NumNode) curNum).oldStr_ += ((NumNode) words.get(j)).oldStr_;
					words.remove(j);
					j--;
				} else if (!NumPatterns.NUM_WITH_UNIT.matcher(curNum.getText())
						.matches()
						&& words.get(j).type == NodeType.DATE
						&& (NumPatterns.CHINESE_DATE_WITH_UNIT.matcher(
								words.get(j).getText()).matches() || hasConnect)) {
					// 当前数字无单位，后面是时间，合成一个时间;或者数字无单位，有连接词，后面接时间，合成一个时间
					if (Param.DEBUG_NUM) {
						System.err.println("后接时间");
					}
					DateNode dn = new DateNode(curNum.getText() + words.get(j).getText());
					words.set(i, dn);
					words.remove(j);
					j--;
					break;
				} else {
					break;
				}
			}
		}
	}

	/**
	 * 根据规则合并Query中的数字，用于处理5元4角、4块3等金钱单位从大到小的情况
	 * 
	 * @param query
	 * @throws UnexpectedException
	 * @throws NotSupportedException
	 */
	public void getWholeMoney(ArrayList<SemanticNode> nodes) throws NotSupportedException {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); ++i) {
			// 当前为数字且非伪数字
			if (!checkNodeAsNumToParse(words.get(i))) {
				continue;
			}
			// 获得当前节点后第N个非空白节点位置
			int nextNodePos = NumUtil.getLastOrNextNNodePos(i, 1, words, false);
			int next2NodePos = NumUtil
					.getLastOrNextNNodePos(i, 2, words, false);
			boolean isNextNum = nextNodePos == -1 ? false
					: checkNodeAsNumToParse(words.get(nextNodePos));
			boolean isNext2Num = next2NodePos == -1 ? false
					: checkNodeAsNumToParse(words.get(next2NodePos));
			NumNode curNum = (NumNode) words.get(i);
			NumNode nextNum = !isNextNum ? null : (NumNode) words
					.get(nextNodePos);
			NumNode next2Num = !isNext2Num ? null : (NumNode) words
					.get(next2NodePos);
			if (nextNum != null && next2Num != null
					&& moneysUnitAreMatch(curNum.getText(), nextNum.getText())
					&& moneysUnitAreMatch(nextNum.getText(), next2Num.getText())) {
				// 圆角分齐全
				String text = nextNum.getText() + next2Num.getText();
				curNum.oldStr_ += text;
				curNum.setText( curNum.getText() + text);
				for (int j = next2NodePos; j > i; j--)
					words.remove(j);
			} else if (nextNum != null
					&& moneysUnitAreMatch(curNum.getText(), nextNum.getText())) {
				// 圆角 角分
				String text = nextNum.getText();
				curNum.oldStr_ += text;
				curNum.setText(curNum.getText() +  text);
				for (int j = nextNodePos; j > i; j--)
					words.remove(j);
			} else if (nextNum != null
					&& NumPatterns.NUM_WITH_MONEY_UNIT.matcher(curNum.getText()).matches()
					&& NumPatterns.LONG.matcher(nextNum.getText()).matches()) {
				// 本身有单位，后面的无单位，如5块2
				String text = nextNum.getText();
				curNum.oldStr_ += text;
				curNum.setText(curNum.getText() +  text );
				for (int j = nextNodePos; j > i; j--)
					words.remove(j);
			}
		}
	}

	/**
	 * 并列逻辑
	 */
	private boolean isJuxtaposeLogic(String text) {
		if (text.equals("和")) {
			return true;
		}
		return false;
	}

	/**
	 * case：20、40、60、250日均线多头排列
	 */
	private boolean isTechLineClosing(int startPos,
			ArrayList<SemanticNode> words) {
		for (int i = startPos; i < words.size(); i++) {
			SemanticNode sNode = words.get(i);
			if (sNode.type == NodeType.DATE
					&& DatePatterns.TECH_PARAM_PATTERN.matcher(sNode.getText())
							.matches()) {
				continue;
			}
			if (sNode.type == NodeType.NUM) {
				NumNode nNode = (NumNode) sNode;
				if (nNode.getUnit() == Unit.UNKNOWN
						&& NumPatterns.LONG.matcher(sNode.getText()).matches()) {
					continue;
				}
			}
			if (sNode.getText().matches(TechMisc.USELESS_CHARS)
					|| isJuxtaposeLogic(sNode.getText())) {
				continue;
			}
			if (Util.StringInTechLine(sNode.getText())) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/*
	 * 处理由连接符相连的数字/时间 将表达一个数字的多个数字/时间节点合并成一个数字或时间节点
	 * 注：暂不合并时间节点，因为之前已有的时间节点类型还无法确定
	 */
	public void getNumSeries(ArrayList<SemanticNode> nodes) throws NotSupportedException {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); ++i) {
			// 当前为数字且非伪数字
			if (!checkNodeAsNumToParse(words.get(i))) {
				continue;
			}
			if (words.get(i).type == NodeType.NUM
					&& isTechLineClosing(i, words)) {
				continue;
			}
			int nextNodePos = NumUtil.getLastOrNextNNodePos(i, 1, words, false);
			int next2NodePos = NumUtil
					.getLastOrNextNNodePos(i, 2, words, false);
			NumNode curNum = (NumNode) words.get(i);
			SemanticNode nextNode = nextNodePos == -1 ? null : words
					.get(nextNodePos);
			SemanticNode next2Node = next2NodePos == -1 ? null : words
					.get(next2NodePos);

			if (nextNode != null && next2Node != null
					&& NumPatterns.COMBINATOR.matcher(nextNode.getText()).matches()) { // 连接符
				// 情况：当前节点为Num，下一个节点为连接符，并且后面还有节点，且后接节点为数字或者时间
				if (Param.DEBUG_NUM) {
					System.err.println("连接符");
				}
				boolean nextIsNumAndCanConnect = checkNodeAsNumToParse(next2Node)
						&& numsUnitAreMatch(curNum.getText(), next2Node.getText());
				boolean nextIsDateAndCanConnect = !NumPatterns.NUM_WITH_UNIT
						.matcher(curNum.getText()).matches()
						&& next2Node.type == NodeType.DATE
						&& datesUnitAreMatch(curNum.getText(), next2Node.getText());
				if (nextIsNumAndCanConnect) {
					// 数字+连接符+数字的处理
					/*
					 * curNum.oldStr_ += nextNode.text + next2Node.text;
					 * curNum.text += nextNode.text + next2Node.text; for (int
					 * j=next2NodePos; j > i; j--) words.remove(j);
					 */
				} else if (nextIsDateAndCanConnect) {
					String text = curNum.getText() + nextNode.getText() + next2Node.getText();
					DateNode dn = new DateNode(text);
					dn.oldNodes.add(nextNode);
					dn.oldNodes.add(next2Node);
					words.set(i, dn);
					for (int j = next2NodePos; j > i; j--)
						words.remove(j);
				}
			}
		}
	}

	/*
	 * 判断是否为NUM类型，且非伪数字
	 */
	private boolean checkNodeAsNumToParse(SemanticNode curNode) {
		boolean needCheck = curNode.type == NodeType.NUM;
		needCheck = needCheck ? !((NumNode) curNode).isFake() : needCheck;
		return needCheck;
	}

	/*
	 * 判断两个NumNode的文本是否可以合并 可合并的情况： 1、其中一个没有单位 2、两个文本单位相同 3、钱的不同单位，前面的单位大于后面的
	 */
	private boolean numsUnitAreMatch(String text, String text2) {
		boolean isMatch = !NumPatterns.NUM_WITH_UNIT.matcher(text).matches()
				|| !NumPatterns.NUM_WITH_UNIT.matcher(text2).matches();
		if (NumPatterns.NUM_WITH_UNIT.matcher(text).matches()
				&& NumPatterns.NUM_WITH_UNIT.matcher(text2).matches()) {
			Matcher str1 = NumPatterns.NUM_WITH_UNIT.matcher(text);
			str1.matches();
			String unit1 = str1.group(2);
			Matcher str2 = NumPatterns.NUM_WITH_UNIT.matcher(text2);
			str2.matches();
			String unit2 = str2.group(2);
			boolean unitsMatchesForMoney = unitsMatchesForMoney(unit1, unit2);
			if (unitsMatchesForMoney || unit1.equals(unit2)) {
				// 钱的不同单位才可能合并，如4元3角
				// 单位相同的数字也可合并
				isMatch = true;// No Op
			}
		}
		return isMatch;
	}

	/*
	 * 判断NumNode+分隔符+DateNode的文本是否可以合并 可合并的情况： 1、前后单位相同：在这里不可能发生
	 * 2、前一个没有单位，后一个有单位 3、前一个单位为个，后一个单位中包含个：这种情况还没有处理
	 */
	private boolean datesUnitAreMatch(String text1, String text2) {
		boolean isMatch = false;
		if (NumPatterns.CHINESE_DATE_WITH_UNIT.matcher(text1).matches()
				&& NumPatterns.CHINESE_DATE_WITH_UNIT.matcher(text2).matches()) {
			Matcher str1 = NumPatterns.CHINESE_DATE_WITH_UNIT.matcher(text1);
			str1.matches();
			String unit1 = str1.group(2);
			Matcher str2 = NumPatterns.CHINESE_DATE_WITH_UNIT.matcher(text2);
			str2.matches();
			String unit2 = str2.group(2);
			if (unit1.equals(unit2)) {
				isMatch = true; // No Op
			}
		} else if (NumPatterns.LONG.matcher(text1).matches()
				&& NumPatterns.CHINESE_DATE_WITH_UNIT.matcher(text2).matches()) {
			isMatch = true;
		}
		return isMatch;
	}

	/*
	 * 判断两个钱类型的NumNode的文本是否可以合并 可合并的情况：钱的不同单位，前面的单位大于后面的
	 */
	private boolean moneysUnitAreMatch(String text1, String text2) {
		boolean isMatch = false;
		if (NumPatterns.NUM_WITH_MONEY_UNIT.matcher(text1).matches()
				&& NumPatterns.NUM_WITH_MONEY_UNIT.matcher(text2).matches()) {
			Matcher str1 = NumPatterns.NUM_WITH_MONEY_UNIT.matcher(text1);
			str1.matches();
			String unit1 = str1.group(2);
			Matcher str2 = NumPatterns.NUM_WITH_MONEY_UNIT.matcher(text2);
			str2.matches();
			String unit2 = str2.group(2);
			boolean unitsMatchesForMoney = unitsMatchesForMoney(unit1, unit2);
			if (unitsMatchesForMoney) {
				// 钱的不同单位才可能合并，如4元3角
				isMatch = true;
			}
		}
		return isMatch;
	}

	/**
	 * 判断两个单位是否符合钱的单位合并规则
	 * 
	 * @param unit1
	 *            第一个单位
	 * @param unit2
	 *            第二个单位
	 * @return
	 */
	private boolean unitsMatchesForMoney(String unit1, String unit2) {
		return NumPatterns.UNIT_FOR_MONEY_1.matcher(unit1).matches()
				&& NumPatterns.UNIT_FOR_MONEY_2.matcher(unit2).matches()
				|| NumPatterns.UNIT_FOR_MONEY_2.matcher(unit1).matches()
				&& NumPatterns.UNIT_FOR_MONEY_3.matcher(unit2).matches();
	}

	/**
	 * 将被误判为数字的时间分离出来
	 * 
	 * @param query
	 * @throws UnexpectedException
	 */
	public void separateDateFromNum(ArrayList<SemanticNode> nodes) throws UnexpectedException {
		separateDateByUnitFromNum(nodes);
		separateDateWithYMDFromNum(nodes);
		separateDateWithWeekSignFromNum(nodes);
		separateDateWithOnlyYearFromNum(nodes);
		separateDateRangeWithYMDFromNum(nodes);
		separateDateRangeWithOnlyYearFromNum(nodes);
	}

	private void separateDateRangeWithOnlyYearFromNum(ArrayList<SemanticNode> nodes) {
		//ArrayList<SemanticNode> nodes = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); i++) {
			if (words.get(i).type != NodeType.NUM) {
				continue;
			}
			NumNode curNum = (NumNode) words.get(i);
			if (NumPatterns.DATE_NEED_SEPARATE_RANGE_3.matcher(curNum.getText())
					.matches()) {
				// 若匹配“2011-2012”，将其转换为时间
				String text = curNum.getText() + "年";
				DateNode repDate = new DateNode(text);
				repDate.oldNodes.add(curNum);
				words.set(i, repDate);
			}
		}
	}

	private void separateDateRangeWithYMDFromNum(ArrayList<SemanticNode> nodes) throws UnexpectedException {
		//ArrayList<SemanticNode> nodes = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); i++) {
			if (words.get(i).type != NodeType.NUM) {
				continue;
			}
			NumNode curNum = (NumNode) words.get(i);
			boolean matchType1 = NumPatterns.DATE_NEED_SEPARATE_RANGE_1
					.matcher(curNum.getText()).matches()
					&& NumPatterns.DATE_NEED_SEPARATE_RANGE_1.matcher(
							curNum.oldStr_).matches();
			boolean matchType2 = NumPatterns.DATE_NEED_SEPARATE_RANGE_2
					.matcher(curNum.getText()).matches()
					&& NumPatterns.DATE_NEED_SEPARATE_RANGE_2.matcher(
							curNum.oldStr_).matches();
			boolean needCheck = matchType1 || matchType2;
			if (!needCheck) {
				continue;
			}
			Matcher numMatcher = matchType1 ? NumPatterns.DATE_NEED_SEPARATE_RANGE_1
					.matcher(curNum.getText())
					: NumPatterns.DATE_NEED_SEPARATE_RANGE_2
							.matcher(curNum.getText());
			numMatcher.matches();
			String yearStr1 = numMatcher.group(1);
			String monthStr1 = numMatcher.group(2);
			String dayStr1 = numMatcher.group(3);
			String yearStr2 = numMatcher.group(4);
			String monthStr2 = numMatcher.group(5);
			String dayStr2 = numMatcher.group(6);
			boolean notDate = !NumUtil.checkAsYMD(yearStr1, monthStr1, dayStr1)
					|| !NumUtil.checkAsYMD(yearStr2, monthStr2, dayStr2);
			if (notDate) {
				// 若日期不合法，则不再处理
				continue;
			}
			DateNode dn = turnANodeToDateNode(curNum);
			words.set(i, dn);
		}
	}

	/**
	 * 将被分词分开的如“3个 月”此种时间以后接时间单位从数字中分离
	 * 此部分需要在数字整合之后处理，是由于“2个至3个月”此种不符合数字整合的单位匹配逻辑 而数字后直接接时间单位的已经处理，此处不需考虑
	 * 后续时间单位的完善可能会使此处逻辑无用
	 * 
	 * @throws UnexpectedException
	 */
	private void separateDateByUnitFromNum(ArrayList<SemanticNode> nodes) throws UnexpectedException {
		//ArrayList<SemanticNode> nodes = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); i++) {
			boolean notNeedCheck = words.get(i).type != NodeType.NUM
					|| !NumPatterns.NUM_WITH_UNIT.matcher(words.get(i).getText())
							.matches();
			SemanticNode rightN = NumUtil.getLastOrNextNode(i, words, false);
			notNeedCheck |= rightN == null
					|| !NumPatterns.DATE_UNITS.matcher(rightN.getText()).matches();
			if (notNeedCheck) {
				continue;
			}

			NumNode curNum = (NumNode) words.get(i);
			Matcher numMatch = NumPatterns.NUM_WITH_UNIT.matcher(curNum.getText());
			numMatch.matches();
			String unit = numMatch.group(2);
			if (!unit.equals("个")) {
				continue;
			}
			DateNode dn = makeANewDateNodeByTwoNode(curNum, rightN);
			words.set(i, dn);
			words.remove(i + 1);
		}
	}

	private void separateDateWithOnlyYearFromNum(ArrayList<SemanticNode> nodes) {
		//ArrayList<SemanticNode> nodes = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); i++) {
			boolean needCheck = words.get(i).type == NodeType.NUM;
			needCheck &= NumPatterns.DATE_YEAR_1.matcher(words.get(i).getText())
					.matches()
					|| NumPatterns.DATE_YEAR_2.matcher(words.get(i).getText())
							.matches();
			if (!needCheck) {
				continue;
			}
			NumNode curNum = (NumNode) words.get(i);
			boolean isWhole = NumPatterns.DATE_YEAR_1.matcher(curNum.getText())
					.matches();
			// 若匹配“2011”形式，将其转换为时间
			if (Param.DEBUG_NUM) {
				System.err.println("DATE_YEAR");
			}
			SemanticNode leftN = NumUtil.getLastOrNextNode(i, words, true);
			SemanticNode rightN = NumUtil.getLastOrNextNode(i, words, false);
			String left = leftN != null ? leftN.getText() : null;
			String right = rightN != null ? rightN.getText() : null;
			boolean notChange = left != null
					&& NumPatterns.COMPARISON_LEFT.matcher(left).matches();
			notChange |= right != null
					&& NumPatterns.COMPARISON_RIGHT.matcher(right).matches();
			notChange &= isWhole;
			// "09"这种时间表示暂定为无歧义的
			if (notChange) {
				continue;
			}
			String text = isWhole ? curNum.getText() : "20" + curNum.getText();
			// 若前后无数字范围标志，这转换为时间
			text = text + "年";
			DateNode dateNode = new DateNode(text);
			dateNode.oldNodes.add(curNum);
			words.set(i, dateNode);
		}
	}

	private void separateDateWithWeekSignFromNum(ArrayList<SemanticNode> nodes) throws UnexpectedException {
		//ArrayList<SemanticNode> nodes = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); i++) {
			SemanticNode curNode = words.get(i);
			SemanticNode lastNode = NumUtil.getLastOrNextNode(i, words, true);
			boolean needCheck = NumPatterns.DATE_WEEK_SIGN_2.matcher(
					curNode.getText()).matches()
					&& lastNode != null
					&& NumPatterns.DATE_WEEK_SIGN_1.matcher(lastNode.getText())
							.matches();
			if (!needCheck) {
				continue;
			}
			DateNode dateNode = makeANewDateNodeByTwoNode(lastNode, curNode);
			words.set(i, dateNode);
			words.remove(lastNode);
			i--;
		}
	}

	/**
	 * 由两个其他节点合成一个时间节点,这两个节点可不同时为空
	 * 
	 * @param firstNode
	 * @param secNode
	 * @return
	 * @throws UnexpectedException
	 */
	private DateNode makeANewDateNodeByTwoNode(SemanticNode firstNode,
			SemanticNode secNode) throws UnexpectedException {
		if (firstNode == null && secNode == null) {
			throw new UnexpectedException("Nothing to turn");
		}
		String firstText = firstNode == null ? "" : firstNode.getText();
		String secText = secNode == null ? "" : secNode.getText();
		String text = firstText + secText;
		DateNode dateNode = new DateNode(text);
		if (firstNode != null) {
			dateNode.oldNodes.add(firstNode);
		}
		if (secNode != null) {
			dateNode.oldNodes.add(secNode);
		}
		return dateNode;
	}

	private DateNode turnANodeToDateNode(SemanticNode onlyNode)
			throws UnexpectedException {
		return makeANewDateNodeByTwoNode(onlyNode, null);
	}

	/**
	 * 处理被错认为数字的，年月日均有的，形如“20100908”或“2010.09.08”或“10.09.08”的日期
	 * 此处不进行解析，只将其转为时间节点
	 * 
	 * @throws UnexpectedException
	 */
	private void separateDateWithYMDFromNum(ArrayList<SemanticNode> nodes) throws UnexpectedException {
		//ArrayList<SemanticNode> nodes = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); i++) {
			boolean needCheck = words.get(i).type == NodeType.NUM;
			if (!needCheck) {
				continue;
			}
			NumNode curNum = (NumNode) words.get(i);
			boolean needCheckType1 = NumPatterns.DATE_NEED_SEPARATE_1.matcher(
					curNum.getText()).matches() // 带连接符的时间
					&& NumPatterns.DATE_NEED_SEPARATE_1.matcher(curNum.oldStr_)
							.matches();
			boolean needCheckType2 = NumPatterns.DATE_NEED_SEPARATE_2.matcher(
					curNum.getText()).matches()
					&& NumPatterns.DATE_NEED_SEPARATE_2.matcher(curNum.oldStr_)
							.matches();
			boolean needCheckType3 = NumPatterns.DATE_NEED_SEPARATE_3.matcher(
					curNum.getText()).matches()
					&& NumPatterns.DATE_NEED_SEPARATE_3.matcher(curNum.oldStr_)
							.matches();
			needCheck = needCheckType1 || needCheckType2 || needCheckType3;
			if (!needCheck) {
				continue;
			}
			Matcher numMatcher = needCheckType1 ? NumPatterns.DATE_NEED_SEPARATE_1
					.matcher(curNum.getText())
					: needCheckType2 ? NumPatterns.DATE_NEED_SEPARATE_2
							.matcher(curNum.getText())
							: NumPatterns.DATE_NEED_SEPARATE_3
									.matcher(curNum.getText());
			numMatcher.matches();
			String yearStr = numMatcher.group(1);
			yearStr = needCheckType3 ? yearStr.startsWith("9") ? "19" + yearStr
					: "20" + yearStr : yearStr;
			String monthStr = numMatcher.group(2);
			String dayStr = numMatcher.group(3);
			if (!NumUtil.checkAsYMD(yearStr, monthStr, dayStr)) {
				continue;
			}
			// curNum.text = needCheckType3 ? curNum.text.startsWith("9") ? "19"
			// + curNum.text : "20" + curNum.text : curNum.text;
			SemanticNode rN = NumUtil.getLastOrNextNode(i, words, false);
			if (rN != null
					&& NumPatterns.DATE_UNITS_AFTER_NUM.matcher(rN.getText())
							.matches()) {
				// 若后有“日、号”等时间单位，则合并
				if (Param.DEBUG_NUM) {
					System.err.println("NUM_P6");
				}
				DateNode dn = makeANewDateNodeByTwoNode(curNum, rN);
				words.set(i, dn);
				words.remove(i + 1);
			} else {
				DateNode dn = new DateNode(curNum.getText());
				words.set(i, dn);
			}
		}
	}

	/**
	 * 根据两个NumNode的LogicType合并NumNode
	 * 
	 * @param left
	 *            Logic左侧NumNode
	 * @param right
	 *            Logic右侧NumNode
	 * @param lt
	 *            中间的LogicType
	 * @return
	 * @throws TBException
	 */
	public static NumNode getNewNumByLogic(NumNode left, NumNode right,
			LogicType lt) throws TBException {
		NumNode nn = null;
		String rangeTypeLeft = left.getRangeType();
		String rangeTypeRight = right.getRangeType();
		boolean typeMatch = NumUtil.isGreaterType(rangeTypeLeft)
				|| NumUtil.isLessType(rangeTypeLeft);
		typeMatch &= NumUtil.isGreaterType(rangeTypeRight)
				|| NumUtil.isLessType(rangeTypeRight);

		boolean unitMatch = left.getUnit() == right.getUnit()
				|| left.getUnit() == Unit.UNKNOWN
				|| right.getUnit() == Unit.UNKNOWN;
		if (typeMatch && unitMatch) {

			String unit = left.getNuminfo().getUnit() == null ? right
					.getNuminfo().getUnit() : left.getNuminfo().getUnit();
			String logicText = lt == LogicType.AND ? "并且" : "或者";
			String newNumText = String.format("%s%s%s", left.getText(), logicText,
					right.getText());
			String newNumTextOldStr = String.format("%s%s%s", left.oldStr_,
					logicText, right.oldStr_);

			boolean isBetween = true;
			NumRange nr = null;
			boolean includeFromL = left.getNuminfo().isIncludeFrom();
			boolean includeToL = left.getNuminfo().isIncludeTo();
			boolean includeFromR = right.getNuminfo().isIncludeFrom();
			boolean includeToR = right.getNuminfo().isIncludeTo();
			boolean includeFrom = false;
			boolean includeTo = false;

			Double from = NumUtil.isGreaterType(rangeTypeLeft) ? left.getFrom()
					: null;
			Double to = NumUtil.isGreaterType(rangeTypeLeft) ? null : left
					.getTo();

			if (NumUtil.isGreaterType(rangeTypeRight) && from != null) {
				Double biger = right.getFrom() > from ? right.getFrom() : from;
				Double smaller = right.getFrom() > from ? from : right
						.getFrom();
				boolean bigerIncludeFrom = right.getFrom() > from ? includeFromR
						: includeFromL;
				boolean smallerIncludeFrom = right.getFrom() > from ? includeFromL
						: includeFromR;
				nr = new NumRange();
				Double rangeFrom = lt == LogicType.AND ? biger : smaller;
				includeFrom = lt == LogicType.AND ? bigerIncludeFrom
						: smallerIncludeFrom;
				nr.setFrom(rangeFrom);
			} else if (NumUtil.isLessType(rangeTypeRight) && to != null) {
				Double biger = right.getTo() > to ? right.getTo() : to;
				Double smaller = right.getTo() > to ? to : right.getTo();
				boolean bigerIncludeTo = right.getTo() > to ? includeToR
						: includeToL;
				boolean smallerIncludeTo = right.getTo() > to ? includeToL
						: includeToR;

				nr = new NumRange();
				Double rangeTo = lt == LogicType.AND ? smaller : biger;
				includeTo = lt == LogicType.AND ? smallerIncludeTo
						: bigerIncludeTo;
				nr.setTo(rangeTo);
			} else if (NumUtil.isLessType(rangeTypeRight) && from != null
					|| NumUtil.isGreaterType(rangeTypeRight) && to != null) {// ><
				from = from == null ? right.getFrom() : from;
				to = to == null ? right.getTo() : to;
				includeFrom = from == null ? includeFromR : includeFromL;
				includeTo = to == null ? includeToR : includeToL;

				if (from <= to) {
					nr = new NumRange();
					nr.setNumRange(from, to);
				} else if (lt == LogicType.OR) {
					nr = new NumRange();
					nr.setNumRange(to, from);
					boolean tmp = includeFrom;
					includeFrom = includeTo;
					includeTo = tmp;
					isBetween = false;
				} else {
					String fail = String.format("“%s”与“%s”在“%s”逻辑下无法合并",
							left.getText(), right.getText(), logicText);
					throw new TBException(fail);
				}
			} else {
				;// No Op
			}
			if (nr != null) {
				nr.setIncludeFrom(includeFrom);
				nr.setIncludeTo(includeTo);
				nr.setUnit(unit);
				nn = new NumNode(newNumText);
				nn.setNuminfo(nr);
				nn.isBetween = isBetween;
				nn.oldStr_ = newNumTextOldStr;
			}
		}
		return nn;
	}

	/**
	 * 对未解析出的数字在query中加tip
	 * 
	 * @param query
	 * @throws NotSupportedException
	 * @throws TBException
	 */
	private void checkNum(ArrayList<SemanticNode> nodes) throws NotSupportedException {
		//ArrayList<SemanticNode> nodes = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); i++) {
			SemanticNode curNode = words.get(i);
			boolean checkPass = words.get(i).type != NodeType.NUM;
			boolean numCheckPass = words.get(i).type == NodeType.NUM;
			numCheckPass = numCheckPass
					&& (((NumNode) curNode).isFake() || ((NumNode) curNode)
							.getNuminfo() != null);
			numCheckPass |= curNode.getText().matches(MiscDef.NUM_TO_PERIOD_REGEX);
			checkPass |= numCheckPass;
			if (checkPass) {
				continue;
			}
			throw new NotSupportedException(MsgDef.NOT_SUPPORTED_NUM_FMT,
					words.get(i).getText());
		}
	}

	public static void main(String[] args) {
		NumRange nr = null;
		try {
			nr = getNumRangeFromStr("4分");
		} catch (NotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(nr);
	}
}
