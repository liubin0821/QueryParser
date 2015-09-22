/**
 * @author: 	    吴永行 
 * @dateTime:	  2014-7-31 下午6:21:09
 * @description:   	
 * 
 */
package com.myhexin.qparser.date;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.DB.mybatis.MybatisHelp;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.ApplicationContext.BeanNameConsts;
import com.myhexin.qparser.define.EnumDef.Direction;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.number.NumUtil;

public class DateSequence {
	
	static HashSet<String> stockChange = null;//老系统中原来的changeNode
	
	static {
		if(stockChange==null)
			loadDate();
	}

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-8-8 上午11:10:48
     * @description:   	
     * 
     */
    public static void loadDate() {
	    //数据库读取
    	MybatisHelp mybatisHelp = ApplicationContextHelper.getBean(BeanNameConsts.MYBATIS_HELP);
		stockChange = mybatisHelp.getStockchangeMapper().selectAll();
    }

	public static void parse(ArrayList<SemanticNode> nodes,String backtestTime) {
		try {
	        sequence(nodes, backtestTime);
        } catch (NotSupportedException e) {
	        
	        e.printStackTrace();
        } catch (UnexpectedException e) {
	        
	        e.printStackTrace();
        }
	}
	
	/**
	 * 根据DateNode附近节点，判断DateNode.isSequence
	 * 
	 * @param query
	 * @throws NotSupportedException
	 * @throws UnexpectedException
	 */
	private static void sequence(ArrayList<SemanticNode> nodes,String backtestTime) throws NotSupportedException, UnexpectedException {
		//ArrayList<SemanticNode> nodes = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		/*
		 * boolean isTag = tryTagDateAsOnlyDate2SequenceDate(nodes); if (isTag)
		 * { //若句中只有一个时间，也只有一个“连续”，标记后就不用再继续处理了 return; }
		 */
		for (int i = 0; i < words.size(); i++) {
			i = tryTagDate2SequenceDate(i, words, Direction.LEFT, false, backtestTime);
		}
		for (int i = 0; i < words.size(); i++) {
			i = tryTagDate2SequenceDate(i, words, Direction.RIGHT, false, backtestTime);
		}
		for (int i = 0; i < words.size(); i++) {
			i = tryTagDate2SequenceDate(i, words, Direction.LEFT, true, backtestTime);
		}
		for (int i = 0; i < words.size(); i++) {
			i = tryTagDate2SequenceDate(i, words, Direction.RIGHT, true, backtestTime);
		}
	}
	
	//分隔符分割的是不同的句式块,处理时间区间不应该跨句式块
	private final static Pattern SEP_PATTERN = Pattern.compile("(_&_|,|;|\\.|。)");

	private static int tryTagDate2SequenceDate(int i, ArrayList<SemanticNode> nodes,
			Direction dir, boolean isSkipBreak, String backtestTime) throws NotSupportedException,
			UnexpectedException {
		if (nodes.get(i).type != NodeType.DATE) {
			return i;
		}
		DateNode dn = (DateNode) nodes.get(i);
		if (dir == Direction.BOTH) {
			int nowPos = tryTagDate2SequenceDate(i, nodes, Direction.LEFT,
					isSkipBreak, backtestTime);
			if (nowPos == i) {
				// 若向左标记成功，则nowPos一定不等于i，因为有节点被删掉了
				return tryTagDate2SequenceDate(i, nodes, Direction.RIGHT,
						isSkipBreak, backtestTime);
			}
			return nowPos;
		}

		int tag = i;
		boolean passChange = false;
		boolean passIndex = false;
		while ((dir == Direction.LEFT ? --tag > -1 : ++tag < nodes.size())
				&& !(SEP_PATTERN.matcher(nodes.get(tag).getText()).matches())) {
			// change目前有18个词语，例如：上涨、下跌、涨、跌、跌幅、涨幅等，一般具有“连续/连”+(涨、跌、上涨、下跌、跌幅、涨幅)+时间
			// 特征
			//if (nodes.get(tag).type == NodeType.CHANGE) {
			if (stockChange.contains(nodes.get(tag).getText())) {
				// 不允许跨过两次Change
				if (passChange) {
					break;
				}
				passChange = true;
			} else if (nodes.get(tag).type == NodeType.FOCUS && ((FocusNode)nodes.get(tag)).hasIndex()) {
				// 不允许跨过两次Class
				if (passIndex) {
					break;
				}
				passIndex = true;
			} else if (nodes.get(tag).type == NodeType.LOGIC
					|| DatePatterns.SEQUENCE_BREAK_TAG.matcher(
							nodes.get(tag).getText()).matches()) {
				break;
			} else if (isSkipBreak == false
					&& DatePatterns.SEQUENCE_MAY_BREAK_TAG.matcher(
							nodes.get(tag).getText()).matches()) {
				break;
			} 
			//change by wyh 2014.08.07  
			//else if (nodes.get(tag).type != NodeType.UNKNOWN) {
			//	break;
			//}
			String text = nodes.get(tag).getText();
			if (text.matches("连续|连|持续") && dir == Direction.RIGHT) {
				if (dn.isSequence) {
					break;
				}
				changeDateBySequence(dn, dir, backtestTime);
				dn.oldNodes.add(nodes.get(tag));
				nodes.remove(tag);
				i = dir == Direction.LEFT ? i - 1 : i;
				break;
			} else if (text.matches("连续|连|持续") && dir == Direction.LEFT) {
				changeDateBySequence(dn, dir, backtestTime);
				dn.oldNodes.add(0, nodes.get(tag));
				nodes.remove(tag);
				i = dir == Direction.LEFT ? i - 1 : i;
				// tryTagDate2SequenceDate(i, nodes, Direction.RIGHT,
				// isSkipBreak);
				break;
			} else if (DatePatterns.SEQUENCE_DATE.matcher(text).matches()
					&& dir == Direction.RIGHT
					&& isLeftGERight(nodes.get(i), nodes.get(tag))) {
				changeDateBySequence(dn, dir, backtestTime);
				dn.setText(dn.getText()+ text);
				dn.oldNodes.add(nodes.get(tag));
				nodes.remove(tag);
				i = dir == Direction.LEFT ? i - 1 : i;
				break;
			}
		}
		return i;
	}
	
	private static void changeDateBySequence(DateNode dateN, Direction dir, String backtestTime)
			throws NotSupportedException, UnexpectedException {
		dateN.isSequence = true;
		if(dir==Direction.RIGHT)
			dateN.setText("连续");
		else
			dateN.setText("连续" + dateN.getText());
		boolean isOneOldNode = dateN.oldNodes.size() == 1;
		boolean needChange = isOneOldNode
				&& DatePatterns.SEQUENCE_SP_DATE.matcher(
						dateN.oldNodes.get(0).getText()).matches();
		if (!needChange) {
			return;
		}
		String oldStr = dateN.oldNodes.get(0).getText();
		String newStr = String.format("近%s", oldStr);
		DateRange newRange = DateCompute.getDateInfoFromStr(newStr, backtestTime);
		dateN.setDateinfo(newRange);
	}
	
	/**
	 * 左右节点值大小比较，根据单位。目前只支持左节点为DateNode 右节点为DateNode，NumNode，或者匹配每年(月)的格式
	 * 
	 * @param leftNode
	 * @param rightNode
	 * @return
	 * @throws UnexpectedException
	 */
	private static boolean isLeftGERight(SemanticNode leftNode, SemanticNode rightNode)
			throws UnexpectedException {
		if (leftNode.type != NodeType.DATE) {
			throw new UnexpectedException("Unexpected node type:%s",
					leftNode.type);
		}
		boolean isleftGTRight = false;
		Unit leftUnit = ((DateNode) leftNode).getUnitOfDate();
		Unit rightUnit;
		if (rightNode.type == NodeType.DATE) {
			rightUnit = ((DateNode) rightNode).getUnitOfDate();
			isleftGTRight = ((DateNode) leftNode).getRangeLen(rightUnit) >= ((DateNode) rightNode)
					.getRangeLen(rightUnit) ? true : false;
		} else if (rightNode.type == NodeType.NUM) {
			rightUnit = ((NumNode) rightNode).getUnit();
			if (NumUtil.isEQType(((NumNode) rightNode).getRangeType())
					|| NumUtil.isInType(((NumNode) rightNode).getRangeType())) {
				isleftGTRight = true;
			}
		} else if (DatePatterns.SEQUENCE_DATE.matcher(rightNode.getText()).matches()) {
			Matcher matchers = DatePatterns.SEQUENCE_DATE
					.matcher(rightNode.getText());
			matchers.matches();
			String rightUnitStr = matchers.group(0);
			String units[] = new String[] { "年", "(季度|季)", "月", "周", "日" };
			int unitPos = 0;
			for (int i = 0; i < units.length; i++) {
				Pattern unitPattern = Pattern.compile(units[i]);
				if (unitPattern.matcher(rightUnitStr).matches()) {
					unitPos = i;
				}
			}
			if (leftUnit == Unit.YEAR) {
				if (unitPos >= 0) {
					isleftGTRight = true;
				}
			} else if (leftUnit == Unit.QUARTER) {
				if (unitPos >= 1) {
					isleftGTRight = true;
				}
			} else if (leftUnit == Unit.MONTH) {
				if (unitPos >= 2) {
					isleftGTRight = true;
				}
			} else if (leftUnit == Unit.WEEK) {
				if (unitPos >= 3) {
					isleftGTRight = true;
				}
			} else if (leftUnit == Unit.DAY) {
				if (unitPos >= 4) {
					isleftGTRight = true;
				}
			}
		}
		return isleftGTRight;
	}
}
