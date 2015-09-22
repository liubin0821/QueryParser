package com.myhexin.qparser.date;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.date.axis.DateAxisHandler;
import com.myhexin.qparser.define.EnumDef.CompareType;
import com.myhexin.qparser.define.EnumDef.Direction;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.define.OperDef;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.TBException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.frequency.FrequencyParser;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.node.TimeNode;
import com.myhexin.qparser.node.UnknownNode;
import com.myhexin.qparser.number.NumUtil;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.tech.TechMisc;
import com.myhexin.qparser.time.TimeParser;
import com.myhexin.qparser.time.info.TimeRange;
import com.myhexin.qparser.util.Util;

@Component
public class DateParser {
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(DateParser.class.getName());

	private static HashSet<String> DATE_HEAD_WORDS = new HashSet<String>();
	private static HashSet<String> DATE_BETWEEN_WORDS = new HashSet<String>();
	private static HashSet<String> DATE_END_WORDS = new HashSet<String>();
	private static HashSet<String> DATE_AMBI_WORDS = new HashSet<String>();
	private static HashSet<String> NO_PARSER_DATE = new HashSet<String>();
	
	@Autowired(required=true)
	private TimeParser timeParser = null;
	
	@Autowired(required=true)
	private FrequencyParser frequencyParser = null;

	public void setTimeParser(TimeParser timeParser) {
    	this.timeParser = timeParser;
    }

	public void setFrequencyParser(FrequencyParser frequencyParser) {
    	this.frequencyParser = frequencyParser;
    }

	static {
		// 2013.08.23 时间修改：使之更加规范化
		String[] heads = { "最近", "过去", "以往", "过往", "前", "上", "下", "截止", "截止到",
				"从", "持续", "未来", "近", "自", "自从" };
		for (String word : heads) {
			DATE_HEAD_WORDS.add(word);
		}
		String[] betweens = { "至", "到", "~", "-", "、", "几个", "几", "与", "和" };
		for (String between : betweens) {
			DATE_BETWEEN_WORDS.add(between);
		}
		// 为什么不包括前？
		String ends[] = { "为止", "以来","以后", "来", "以内", "之前",  "内", "以前",
		        "即将", "将要", "将" ,"之间","底","开始","起"/*未来时间*/};
		//"后", "之后","以后", 时间轴需求, 去掉后,之后,以后
		for (String end : ends) {
			DATE_END_WORDS.add(end);
		}
		// 这些词可放在日期前和日期后，位置不同表示的日期不同
		String ambis[] = { "前", "之前", "以前" }; //时间轴需求, 去掉后,之后,以后
		for (String ambi : ambis) {
			DATE_AMBI_WORDS.add(ambi);
		}
		//不用解析的时间,只需要识别
		String noParserDate[] = { "即时","分时","开盘以来" };
		for (String npd : noParserDate) 
			NO_PARSER_DATE.add(npd);
	}
	
	/**
	 * 解析Query中的时间
	 * 
	 * @param Query
	 * @throws UnexpectedException
	 */
	public ArrayList<SemanticNode> parse(ArrayList<SemanticNode> nodes, Calendar backtestTimeCal) {
		
		
		try {
			String backtestTime = DateInfoNode.toString(backtestTimeCal, "");
			//nodes = dateParserNew.parse(nodes);
 			mergeConj(nodes);
			DateCompute.getDateInfo(nodes, backtestTime);
			
			if(!Param.DEBUG_MERGE){
				addDate(nodes,backtestTime);
				nodes = tagDate(nodes,backtestTime);
			}
			
			DateCompute.getDateInfo(nodes, backtestTime);
			
			if(!Param.DEBUG_MERGE){
				mergeDate(nodes, backtestTime);
			}
			//sequence(nodes);
			compareWithLength(nodes,backtestTime); // 这个可以删除么
			reparseSequenceDateOfUnitDay(nodes,backtestTime);
			dealWithReportDateWithOutYear(nodes);
			checkDate(nodes);
			connectRelativeDateWithOtherDate(nodes); // 把诸如 "每年" 处理伪时间
			dealWithNoParserDate(nodes);//不需要解析的时间
			timeParser.deepParse(nodes);
			frequencyParser.parse(nodes); // 处理时间后跟频率
			DateCompute.adjustHqDateToTradeDate(nodes);//将abs_常用行情指标的时间调整为交易日
			mergeDateForSequence(nodes); //处理既有时间区间，又有连续的情况，问句：2015年8月10日至2015年8月11日的每日涨幅大于5％
			adjustDateForRelated(nodes); //两个时间有关联的情况，问句：7月20日大宗交易溢价率，后5日涨跌幅
		} catch (TBException e) {
			logger_.debug(e.getLogMsg());
			//query_.getLog().logMsg(ParseLog.LOG_ERROR, e.getMessage());
		} catch (NotSupportedException e) {
			logger_.debug(e.getLogMsg());
			//query_.getLog().logMsg(ParseLog.LOG_ERROR, e.getMessage());
		} catch (UnexpectedException e) {
			logger_.debug(e.getLogMsg());
			//query_.getLog().logMsg(ParseLog.LOG_ERROR, e.getMessage());
		}
		return nodes;
	}
	
	/**
	 * 两个时间前后有关联。
	 * 
	 * @author huangmin
	 *
	 * @param nodes
	 */
	private void adjustDateForRelated(ArrayList<SemanticNode> nodes) {
    	ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); i++) {
			if(i > 2 &&  words.get(i) instanceof DateNode && 
					(DatePatterns.DAY_OFFSET.matcher(words.get(i - 1).getText() + words.get(i).getText()).matches() 
							|| DatePatterns.DAY_OFFSET.matcher(words.get(i).getText()).matches())){
				String prefix = words.get(i - 1).getText();
				DateNode curDnode = (DateNode)(words.get(i));
				String curText = curDnode.getText();
				String nDay = curText.replaceAll("日", "").replaceAll("天", "")
						.replaceAll("前", "").replaceAll("后", "").replaceAll("后面", "");
				for(int j = i - 1; j >=0; j--) {
					SemanticNode snode = nodes.get(j);
					if(snode instanceof DateNode) {
						boolean isSep = false;
						for(int k = j + 1;k < i - 1; k++) {
							SemanticNode tsnode = nodes.get(k);
							if(isSeparator(tsnode)) {
								isSep = true;
								break;
							}
						}
						if(!isSep) {
							continue;
						}
						DateNode dnode = (DateNode)snode;
						try {
							if(!dnode.isSequence() && "日".equals(dnode.getUnitStrOfDate()) && dnode.getLength() == 1) { //某个具体的时间
								DateRange drange = dnode.getDateinfo();
								if(drange != null) {
									DateInfoNode from = drange.getFrom();
									DateInfoNode to = drange.getTo();
									if(from != null && from.equals(to)) { //时间点，时间范围应该是同一天
										try {
											SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
											Calendar startCd = Calendar.getInstance();
											Calendar endCd = Calendar.getInstance();
											startCd.setTime(sdf.parse(from.toString()));
											endCd.setTime(sdf.parse(from.toString()));
											if(prefix.contains("后") || curDnode.getText().contains("后") ) {
												startCd.add(Calendar.DATE, 1); //新起点应该增加1天
												endCd.add(Calendar.DATE, Integer.parseInt(nDay)); //新结束应该增加n天
											}else if(prefix.contains("前") || curDnode.getText().contains("前") ) {
												startCd.add(Calendar.DATE, -Integer.parseInt(nDay)); //新起点应该减少n天
												endCd.add(Calendar.DATE, -1); //新结束应该减少一天
											}
											DateInfoNode startDnode = new DateInfoNode(startCd);
											DateInfoNode endDnode = new DateInfoNode(endCd);
											curDnode.setDateinfo(startDnode, endDnode); //设置真正的时间范围
											curDnode.setText(startDnode.toString()+ "至" + endDnode.toString());
											words.set(i, curDnode);
											if(DatePatterns.DAY_OFFSET.matcher(prefix + curText).matches() ) {
												words.remove(i - 1);
											}
											break;
										} catch (Exception e) {
											logger_.error(e.getMessage());
										}
									}
								}
							}
						} catch (UnexpectedException e) {
							logger_.error("DateNode can not get unit:%s", e.getMessage());
						}
					}
				}
			}
		}
	}
	
    // 判断是否为分隔符
	private boolean isSeparator(SemanticNode sn) {
		Pattern SEQUENCE_MAY_BREAK_TAG = Pattern.compile("^(\\s|,|;|\\.|。|，)$");
		boolean rtn = false;
		if (sn != null && SEQUENCE_MAY_BREAK_TAG.matcher(sn.getText()).matches()) {
			rtn = true;
		}
		return rtn;
	}

	/**
	 * 对于多个区间和连续的情况的处理。
	 * 
	 * @author huangmin
	 *
	 * @param nodes
	 */
	private void mergeDateForSequence(ArrayList<SemanticNode> nodes) {
    	ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); i++) {
			//2015年8月10日至2015年8月11日的每日涨幅大于5％
			if ((i + 2 < words.size() && (words.get(i+1).getText().equals("连续") || words.get(i+1).getText().equals("每天") 
					|| (words.get(i+1).getText() + words.get(i+2).getText()) .equals("的连续") ||  (words.get(i+1).getText() + words.get(i+2).getText()).equals("的每天")  )
					&& (DatePatterns.SEQUENCE_SP3.matcher(words.get(i).getText() + words.get(i + 1).getText()).matches()
					|| DatePatterns.SEQUENCE_SP3.matcher(words.get(i).getText() + words.get(i+1).getText() + words.get(i+2).getText()).matches()) )){
				String text = words.get(i).getText() + words.get(i + 1).getText();
				if(i + 2 < words.size() &&  (words.get(i+2).getText().equals("连续") || words.get(i+2).getText().equals("每天"))  ) {
					text += "每日";
				}
				DateNode dn = (DateNode)(words.get(i));
				dn.setText("连续"+ dn.getLength() +"日");
				dn.setForShow(text);
				dn.isSequence = true;
				if(i + 2 < words.size() &&  (words.get(i+2).getText().equals("连续") || words.get(i+2).getText().equals("每天"))  ) {
					words.remove(i + 1);
					words.remove(i + 1);
				}else {
					words.remove(i + 1);
				}
			}
			
			//8.12日至8.17日3日
			else if((i + 1 < words.size()
					&& DatePatterns.SEQUENCE_SP4.matcher(words.get(i).getText()+words.get(i+1).getText()).matches()) ||
					(i + 2 < words.size()
							&& DatePatterns.SEQUENCE_SP4.matcher(words.get(i).getText()+words.get(i+1).getText()+words.get(i+2).getText()).matches())){
				if(DatePatterns.SEQUENCE_SP4.matcher(words.get(i).getText()+words.get(i+1).getText()).matches()) {
					words.remove(i + 1);
				}else {
					words.remove(i + 1);
					words.remove(i + 1);
				}
			}
			
			//近期每天
			else if(i + 1 < words.size() && "近期每天".equals(words.get(i).getText()+words.get(i+1).getText())){
				DateNode dn = new DateNode("连续3日");
				dn.isSequence = true;
				words.set(i, dn);
				words.remove(i + 1);
			}
			
			//10天每天,10天7日
			else if(i + 2 < words.size() && DatePatterns.NUM_TYPE_PAST_GROUP2.matcher(words.get(i).getText()).matches()
					&& (DatePatterns.SEQUENCE_SP5.matcher(words.get(i).getText()+words.get(i+1).getText()).matches()
							 || DatePatterns.SEQUENCE_SP5.matcher(words.get(i).getText()+words.get(i+1).getText()+words.get(i+2).getText()).matches())){
				if(DatePatterns.SEQUENCE_SP5.matcher(words.get(i).getText()+words.get(i+1).getText()+words.get(i+2).getText()).matches()) {
					words.remove(i + 1);
					words.remove(i + 2);
				}else {
					words.remove(i + 1);
				}
			}
			
			//这个星期三复牌
			else if (words.get(i) instanceof DateNode){
				try {
					String unit = ((DateNode)words.get(i)).getUnitStrOfDate();
					if ( words.get(i) instanceof DateNode && "周".equals(unit) && i + 1 < words.size() 
							&& (words.get(i + 1) instanceof NumNode || words.get(i + 1) instanceof UnknownNode)
							&& DatePatterns.WEEK_DAY.matcher(words.get(i + 1).getText()).matches()){
						String text = words.get(i).getText() + words.get(i + 1).getText();
						DateNode dn = new DateNode(text);
						words.set(i, dn);
						words.remove(i + 1);
					}
				}
				catch (UnexpectedException e) {
					logger_.error("DateNode can not get unit:",e.getMessage());
				}
			}
		}
	}

	/**
     * @author: 	    吴永行 
     * @dateTime:	  2014-9-22 下午3:49:34
     * @description:   	
     */
    private void dealWithNoParserDate(ArrayList<SemanticNode> nodes) {
    	ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); i++) {
			if(words.get(i)!=null && words.get(i).isSkipOldDateParser()) continue;
			
			if (NO_PARSER_DATE.contains(words.get(i).getText())) {
				DateNode dn = new DateNode(words.get(i).getText());
				words.set(i, dn);
			}
		}
    }

	/**
	 * 目前仅支持如“2013,一季度业绩大幅度预增”这种年和季度分开的例子 2012年三季度业绩增长率大于2012年二季度业绩增长率
	 * 
	 * @throws UnexpectedException
	 * @throws NotSupportedException
	 */
	private void mergeDate(ArrayList<SemanticNode> nodes, String backtestTime) throws UnexpectedException, NotSupportedException {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		boolean bchange = mergeQuarterDataByPattern(nodes);

		for (int i = 0; i < words.size(); ++i) {
			if(words.get(i)!=null && words.get(i).isSkipOldDateParser()) continue;
			
			if (words.get(i).type != NodeType.DATE) {
				continue;
			}
			DateNode dn = (DateNode) words.get(i);
			
			//处理  "2013,一季度"
			for (int j = i + 1; j < words.size(); j++) {
				if (words.get(j).getText().matches(TechMisc.USELESS_CHARS)) {
					continue;
				} 
				else if (words.get(j).type == NodeType.DATE) {
					DateNode ndn = (DateNode) words.get(j);
					DateRange ndnr = ndn.getDateinfo();
					//第一个单位是年  第二个单位是季度 
					if (dn.getUnitOfDate() == Unit.YEAR
					        && ndn.getUnitOfDate() == Unit.QUARTER
					        && ndnr != null && ndnr.isExplicitYear()) {
						dn.setText( dn.getText() + words.get(j).getText() );
						dn.oldNodes.addAll(ndn.oldNodes);
						for (int k = i + 1; k <= j; k++) {
							words.remove(i + 1);
						}
						bchange = true;
						break;
					} else
						break;

				} 
				else
					break;
			}
			
			//即将在2014年
			for (int j = i-1; j >= 0 ; j--) {
				if(words.get(j).getText().equals("在"))
					continue;				
				if(DatePatterns.FURURE_DATE.matcher(words.get(j).getText()).matches()){
					dn.setText(words.get(j).getText()  + dn.getText() );
					bchange = true;
					//结束设置已经合并的节点
					while(j<i) words.get(j++).isCombined=true;
				}
				break;
			}
			
			if(DatePatterns.DATE_CONJ_LENGTH2.matcher(dn.getText()).matches()){
				if(i-1>=0 && words.get(i-1).type == NodeType.DATE){
					dn.setText (words.get(i-1).getText()+dn.getText() );
					bchange = true;
					words.get(i-1).isCombined=true;
				}
			}
			
		}
		if (bchange) {
			DateCompute.getDateInfo(words,backtestTime);
		}
	}

	/**
	 * 按照字符串格式匹配年+季度 如“2012年三季度业绩增长率大于2012年二季度业绩增长率”
	 */
	private boolean mergeQuarterDataByPattern(ArrayList<SemanticNode> nodes) {
		boolean bchange = false;
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < words.size(); i++) {
			sb.append(words.get(i).getText());
		}
		Pattern ptn = Pattern.compile("(\\d\\d)?\\d\\d年?第?[1234一二三四]季度?");
		Matcher matcher = ptn.matcher(sb.toString());
		List<String> matchStrs = new ArrayList<String>();
		while (matcher.find()) {
			matchStrs.add(matcher.group());
		}
		for (int j = 0; j < matchStrs.size(); j++) {
			int start = -1;
			int end = -1;
			String str = "";
			for (int i = 0; i < words.size(); i++) {
				String txt = words.get(i).getText();
				if (start < 0) {
					if (matchStrs.get(j).startsWith(txt)) {
						start = i;
						str += txt;
						if (matchStrs.get(j).equals(txt)) {
							end = i;
							break;
						}
					}
				} else {
					String temp = str + txt;
					if (matchStrs.get(j).startsWith(temp)) {
						str = temp;
						if (matchStrs.get(j).equals(temp)) {
							end = i;
							break;
						}
					} else {
						i = start;
						start = -1;
						str = "";
					}
				}
			}
			if (start >= 0 && end > start) {
				bchange = true;
				int length = end - start;
				DateNode dNode = new DateNode(str);
				for (int i = 0; i <= length; i++) {
					dNode.oldNodes.add(words.get(start));
					words.remove(start);
				}
				words.add(start, dNode);
			}
		}
		return bchange;
	}

	/**
	 * 左右节点值大小比较，根据单位。目前只支持左节点为DateNode 右节点为DateNode，NumNode，或者匹配每年(月)的格式
	 * 
	 * @param leftNode
	 * @param rightNode
	 * @return
	 * @throws UnexpectedException
	 */
	private boolean isLeftGERight(SemanticNode leftNode, SemanticNode rightNode)
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
	
	/**
	 * 现在只处理没有明确年份的报告期，其他年份不确定的，暂不处理
	 */
	private void dealWithReportDateWithOutYear(ArrayList<SemanticNode> nodes) {
		//ArrayList<SemanticNode> nodes = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 1; i < words.size(); i++) {
			if(words.get(i)!=null && words.get(i).isSkipOldDateParser()) continue;
			
			// 不需自初始位置开始
			boolean notNeedCheck = words.get(i).type != NodeType.DATE;
			if (notNeedCheck) {
				continue;
			}
			DateNode curNode = (DateNode) words.get(i);
			DateRange dr = curNode.getDateinfo();
			// 若时间非报告期，或已经有确定的年份，或最大单位已经是YEAR,则不需继续处理
			notNeedCheck |= dr == null || !dr.isReport() || dr.hasYear()
					|| dr.getMaxUnit() == Unit.YEAR;
			if (notNeedCheck) {
				continue;
			}
			DateNode hitDate = findLastDateNodeForReportDateWithOutYear(nodes,i);
			if (hitDate == null || hitDate.getDateinfo() == null) {
				continue;
			}
			DateRange hitDR = hitDate.getDateinfo();
			int year = hitDate.getRangeType().equals(OperDef.QP_GT) ? hitDR
					.getFrom().getYear() : hitDR.getTo().getYear();
			addYear2ReportDate(year, dr);

		}
	}

	private void addYear2ReportDate(int year, DateRange dr) {
		DateInfoNode from = dr.getFrom();
		DateInfoNode to = dr.getTo();
		from.setDateInfo(year, from.getMonth(), from.getDay());
		to.setDateInfo(year, to.getMonth(), to.getDay());
	}

	private DateNode findLastDateNodeForReportDateWithOutYear(ArrayList<SemanticNode> nodes,int i) {
		//ArrayList<SemanticNode> nodes = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		while (--i > -1) {
			// 只向左找
			// TODO:找的过程暂无其他规则，以后添加
			if (words.get(i).type != NodeType.DATE) {
				continue;
			}

			DateNode curNode = (DateNode) words.get(i);
			if (curNode.isFake()) {
				// 伪时间，如"最近"无法作为标杆时间
				break;
			} else if (checkNodeIsRelativeDate(curNode)) {
				// 相对时间也无法作为标杆时间
				break;
			} else if (curNode.isSequence) {
				break;
			} else if (!curNode.getDateinfo().hasYear()
					|| curNode.getDateinfo().getMaxUnit() != null
					&& curNode.getDateinfo().getMaxUnit() != Unit.YEAR) {
				// 对没有确定年份的，也无法作为标杆时间
				break;
			}
			return (DateNode) words.get(i);
		}
		return null;
	}

	/**
	 * 将如“前N天”这种时间与句中其标杆时间相关联 如“昨天股价大于前一天的”
	 * 
	 * @throws UnexpectedException
	 */
	private void connectRelativeDateWithOtherDate(ArrayList<SemanticNode> nodes) throws UnexpectedException {
		//ArrayList<SemanticNode> nodes = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 1; i < words.size(); i++) {
			if(words.get(i)!=null && words.get(i).isSkipOldDateParser()) continue;
			
			boolean needConnect = checkNodeIsRelativeDate(words.get(i));
			if (!needConnect) {
				continue;
			}
			DateNode relativeDate = (DateNode) words.get(i);
			// 只找前一个，不越过时间寻找
			DateNode hitDate = findLastDateNodeForRelativeDate(nodes,i);
			if (hitDate == null) {
				continue;
			}
			tryConnect2RelativeDate(relativeDate, hitDate);
		}
	}

	private void tryConnect2RelativeDate(DateNode relativeDate, DateNode hitDate)
			throws UnexpectedException {
		Unit relativeUnit = relativeDate.getUnitOfDate();
		Unit hitUnit = hitDate.getUnitOfDate();
		if (hitUnit != relativeUnit) {
			// 现只检查单位
			return;
		}
		relativeDate.setRelatedDate(hitDate);
	}

	private DateNode findLastDateNodeForRelativeDate(ArrayList<SemanticNode> nodes,int i) {
		//ArrayList<SemanticNode> nodes = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		int countClass = 0;
		int stepCount = 5;// 步数限制暂未定
		while (--i > -1) {
			// 只向左找
			// TODO:找的过程暂无其他规则，以后添加
			stepCount++;
			if (countClass > 1) {
				// 遇到超过两个指标，则不再向前找
				break;
			} else if (words.get(i).type == NodeType.CLASS) {
				countClass++;
				continue;
			} else if (words.get(i).type != NodeType.DATE) {
				continue;
			}

			DateNode curNode = (DateNode) words.get(i);
			if (curNode.isFake()) {
				// 伪时间，如"最近"无法作为标杆时间
				break;
			} else if (checkNodeIsRelativeDate(curNode)) {
				// 相对时间也无法作为标杆时间
				break;
			} else if (curNode.getDateinfo().isLength()) {
				// 数字型长度时间也无法作为标杆时间
				break;
			}
			return (DateNode) words.get(i);
		}
		return null;
	}

	/**
	 * 判断时间是否符合相对时间的要求 现只根据字符串判断
	 * 
	 * @param semanticNode
	 *            被检查的节点
	 * @return 若符合，则返回True
	 */
	private boolean checkNodeIsRelativeDate(SemanticNode semanticNode) {
		if (semanticNode.type != NodeType.DATE) {
			return false;
		}
		String text = semanticNode.getText();
		return DatePatterns.RELATIVE_DATE.matcher(text).matches();
	}

	/**
	 * 对如“三日涨30%”的问句中的时间进行调整
	 * 
	 * @param query
	 *            需调整的Query
	 * @throws NotSupportedException
	 * @throws UnexpectedException
	 */
	private void reparseSequenceDateOfUnitDay(ArrayList<SemanticNode> nodes, String backtestTime) throws NotSupportedException,
			UnexpectedException {
		//ArrayList<SemanticNode> nodes = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); i++) {
			if(words.get(i)!=null && words.get(i).isSkipOldDateParser()) continue;
			
			SemanticNode nodeI = words.get(i);
			if (nodeI.type != NodeType.DATE
					|| ((DateNode) nodeI).isSequence
					|| !DatePatterns.SEQUENCE_SP_DATE.matcher(nodeI.getText())
							.matches()) {
				// 若节点非DateNode，或者已经是连续类型的DateNode，或者无法与“SEQUENCE_SP_DAY”匹配
				// 则不作调整
				continue;
			}

			Matcher dateMatcher = DatePatterns.SEQUENCE_SP_DATE
					.matcher(nodeI.getText());
			dateMatcher.matches();
			int num = Integer.valueOf(dateMatcher.group(1));
			String unit = dateMatcher.group(2);
			// 所有的N日都转换为N日内
			if (num > Integer.MAX_VALUE || !unit.matches("^日$")) {
				continue;
			}
			DateNode dn = (DateNode) nodeI;
			changeDateBySequence(dn,backtestTime);
			String type = dn.getRangeType();
			if (!type.equals("><") && !type.equals("=")) {
				assert (false);
			}
			String tip = type.equals("=") ? String.format("时间“%s”现在默认识别为“%s”",
					dn.getText(), dn.getFrom()) : String
					.format("时间“%s”现在默认识别为“%s”至“%s”", dn.getText(), dn.getFrom(),
							dn.getTo());
			//query_.getLog().logMsg(ParseLog.LOG_TIP, tip);
			dn.isSequence = false;
		}
	}

	private void checkDate(ArrayList<SemanticNode> nodes) throws TBException, NotSupportedException {
		//ArrayList<SemanticNode> nodes = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); i++) {
			if(words.get(i)!=null && words.get(i).isSkipOldDateParser()) continue;
			
			if (words.get(i).type != NodeType.DATE) {
				continue;
			}
			DateNode curNode = (DateNode) words.get(i);
			if (curNode.getDateinfo() == null) {
				// “最近”以后会用FakeDate处理
				// boolean isRecent =
				// DatePatterns.RECENT.matcher(curNode.text).matches();
				//每天 每一天 这种会删掉，这里先让它不要报错
				if(DatePatterns.EVERY_UNIT.matcher(curNode.getText()).matches()) {
					continue;
				}
				throw new NotSupportedException(MsgDef.NOT_SUPPORTED_DATE_FMT,
						curNode.getText());
			}
			DateInfoNode from = curNode.getDateinfo().getFrom();
			DateInfoNode to = curNode.getDateinfo().getTo();
			if (!checkDateInfo(from) || !checkDateInfo(to)) {
				String err = String.format(MsgDef.WRONG_DATE_FMT, curNode.getText());
				throw new TBException(err);
			}
		}
	}

	private boolean checkDateInfo(DateInfoNode din) {
		assert (din != null);
		int year = din.getYear();
		int month = din.getMonth();
		int day = din.getDay();
		int dayOfMonth = DateUtil.getMonthDayCount(year, month);
		if (month >= 1 && month <= 12 && day >= 1 && day <= dayOfMonth) {
			return true;
		}
		return false;
	}

	/**
	 * 根据DateNode左右节点，判断DateNode.compare_类型 暂时不处理这种情况>=? <=? =
	 * 
	 * @param query
	 * @throws UnexpectedException
	 */
	private void compareWithLength(ArrayList<SemanticNode> nodes, String backtestTime) throws UnexpectedException {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); i++) {
			if(words.get(i)!=null && words.get(i).isSkipOldDateParser()) continue;
			
			if (words.get(i).type != NodeType.DATE) {
				continue;
			}
			DateNode curNode = (DateNode) words.get(i);
			String left = i > 0 ? words.get(i - 1).getText() : null;
			String right = i < words.size() - 1 ? words.get(i + 1).getText() : null;

			// 当时间节点右是均线指标时，不合并operator节点
			if (DatePatterns.TECH_PARAM_PATTERN.matcher(curNode.getText()).matches()) {
				if (Util.StringInTechLine(left) || Util.StringInTechLine(right))
					continue;
			}

			boolean leftIsGTSign = left != null && left.matches(">=?");
			boolean leftIsLTSign = left != null && left.matches("<=?");
			boolean leftIsEQSign = left != null && left.equals("=");
			// 暂时不处理这种情况>=? <=? =
			leftIsGTSign = false;
			leftIsLTSign = false;
			leftIsEQSign = false;
			boolean leftIsCompare = leftIsGTSign || leftIsLTSign
					|| leftIsEQSign;

			if (leftIsCompare) {
				SemanticNode leftLastNode = DateUtil
						.getLastOrNextNodeSkipBlankAndHiddenSign(i - 1, words,
								true);
				if (!checkForChangeNumDate(leftLastNode)) {
					return;
				}
			}
			boolean rightIsGTSign = right != null && right.matches("以上|之上");
			boolean rightIsLTSign = right != null
					&& right.matches("以下|之下|以内|之内");

			boolean hasCompareSing = leftIsGTSign || leftIsLTSign
					|| leftIsEQSign || rightIsGTSign || rightIsLTSign;
			boolean curIsNumTypeDate = DatePatterns.NUM_TYPE_DATE.matcher(
					curNode.getText()).matches();
			if (curIsNumTypeDate && hasCompareSing) {
				changeNumTypeDateByCompare(curNode,backtestTime);
			}
			if (curIsNumTypeDate && leftIsGTSign) {
				curNode.compare = CompareType.LONGER;
				curNode.oldNodes.add(0, words.get(i - 1));
				words.remove(i - 1);
				i--;
			} else if (curIsNumTypeDate && leftIsLTSign) {
				curNode.compare = CompareType.SHORTER;
				curNode.oldNodes.add(0, words.get(i - 1));
				words.remove(i - 1);
				i--;
			} else if (rightIsGTSign) {
				curNode.compare = CompareType.LONGER;
				curNode.oldNodes.add(words.get(i + 1));
				words.remove(i + 1);
			} else if (rightIsLTSign) {
				curNode.compare = CompareType.SHORTER;
				curNode.oldNodes.add(words.get(i + 1));
				words.remove(i + 1);
			} else {
				;// No Op
			}
		}
	}

	private boolean checkForChangeNumDate(SemanticNode leftLastNode) {
		if (leftLastNode == null || leftLastNode.type == NodeType.UNKNOWN) {
			// '上市超过5年'
			return true;
		} else if (leftLastNode.type != NodeType.CLASS) {
			return false;
		}
		ClassNodeFacade leftLastIndex = (ClassNodeFacade) leftLastNode;
		if (leftLastIndex.isDateIndex()) {
			return true;
		}
		return false;
	}

	/**
	 * 将形如“10年以上”的时间表达去歧义，这种硬指的是“大于10年”，而不是“大于2010年”
	 * 
	 * @param curNode
	 *            需调整的时间节点
	 * @throws UnexpectedException
	 */
	private void changeNumTypeDateByCompare(DateNode curNode, String backtestTime)
			throws UnexpectedException {
		// 必须为数字型时间，如“2个月”、“20年”
		assert (DatePatterns.NUM_TYPE_DATE.matcher(curNode.getText()).matches());
		if (curNode.getDateinfo().getDateUnit() != Unit.YEAR) {
			// 其他单位的数字型时间暂未遇到歧义
			return;
		}
		DateRange newDR = null;
		try {
			newDR = DateCompute.getDateInfoFromStr("近" + curNode.getText(), backtestTime);
		} catch (NotSupportedException e) {
			// 此种情况不会发生
			assert (false);
		}
		curNode.setDateinfo(newDR);

	}

	/*
	 * 2012.08.21 新增 以前以后之前之后的处理
	 */
	private void mergeConj(ArrayList<SemanticNode> nodes) {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); i++) {
			if(words.get(i)!=null && words.get(i).isSkipOldDateParser()){
				continue;
			}
			
			if (DatePatterns.DATE_CONJ_TYPE_HALF.matcher(words.get(i).getText()).matches()) {
				DateNode dn = new DateNode(words.get(i).getText());
				words.set(i, dn);
			} else if (i + 1 < words.size()
					&& DatePatterns.DATE_CONJ_TYPE_HALF_1.matcher(words.get(i).getText()).matches()
					&& DatePatterns.DATE_CONJ_TYPE_HALF_2.matcher(words.get(i + 1).getText()).matches()) {
				String text = words.get(i).getText() + words.get(i + 1).getText();
				if (DatePatterns.DATE_CONJ_TYPE_HALF.matcher(text).matches()) {
					DateNode dn = new DateNode(text);
					words.set(i, dn);
					words.remove(i + 1);
				}
			}
			
			//连续三年 和 三年连续
			else if ((words.get(i).getText().equals("连续") && i + 1 < words.size()
					&& DatePatterns.SEQUENCE_SP.matcher(words.get(i).getText() + words.get(i + 1).getText()).matches())
					||(i + 1 < words.size() && words.get(i+1).getText().equals("连续")
					&& DatePatterns.SEQUENCE_SP2.matcher(words.get(i).getText() + words.get(i + 1).getText()).matches())){
				String text = words.get(i).getText() + words.get(i + 1).getText();
				DateNode dn = new DateNode(text);
				words.set(i, dn);
				words.remove(i + 1);
			}
			//TODO:会改变jiedian
			
			//近期7个交易日，近期7天
			else if ((DatePatterns.NUM_TYPE_PAST_GROUP1.matcher(words.get(i).getText()).matches() && i + 1 < words.size()
					&& DatePatterns.NUM_TYPE_PAST.matcher(words.get(i).getText() + words.get(i + 1).getText()).matches())){
				String text = words.get(i).getText() + words.get(i + 1).getText();
				DateNode dn = new DateNode(text);
				words.set(i, dn);
				words.remove(i + 1);
			}
			
			//2015年9月7日15日
			else if(i + 1 < words.size()
					&& DatePatterns.YEAR_MONTH_DAY_WITH_SPLIT_SIGN_DUPLICATED.matcher(words.get(i).getText()+words.get(i+1).getText()).matches()){
				words.remove(i + 1);
			}
			
			//2015年9月15
			else if(i + 1 < words.size()
					&& DatePatterns.YEAR_MONTH_NODAY.matcher(words.get(i).getText()+words.get(i+1).getText()).matches()){
				String text = words.get(i).getText() + words.get(i + 1).getText() + "日";
				DateNode dn = new DateNode(text);
				words.set(i, dn);
				words.remove(i + 1);
			}
			
			//2015年每月
			else if(i + 1 < words.size()
					&& DatePatterns.YEAR_EVERY_MONTH.matcher(words.get(i).getText()+words.get(i+1).getText()).matches()){
				String text = words.get(i).getText() + words.get(i + 1).getText();
				DateNode dn = new DateNode(text);
				words.set(i, dn);
				words.remove(i + 1);
			}
			//BUG 5日线-》numnode(5),  FocusNode(日线)
			else if (words.get(i).isNumNode() && (i+1<nodes.size() && nodes.get(i+1).isFocusNode() && "日线".equals(nodes.get(i+1).getText())) ) {
				NumNode node = (NumNode) words.get(i);
				if(node.getNuminfo()==null || node.getNuminfo().getUnit()==null) {
					try{
						int day = Integer.parseInt(node.getText());
						DateNode dateNode = new DateNode();
						dateNode.setText( day+ "日");
						words.set(i, dateNode);
					}catch(Exception e) {
						
					}
				}
			}
			
			//TODO 这些hardcode需要重新设计??!!
			//时间轴需求: 把连续后接FocusNode的情况时，在连续后面添加3天
			else if (words.get(i).getText().equals("连续") ) {
				boolean hasNumOrDate = false;
				for(int j=i+1;j<words.size();j++) {
					SemanticNode nn = words.get(j);
					if(nn.isFocusNode() && nn.getText().equals("后")) break;
									
					if(nn.isNumNode() || nn.isDateNode()) {
						hasNumOrDate = true;
					}
				}
				
				for(int j=i-1;j>=0;j--) {
					SemanticNode nn = words.get(j);
					if(nn.isFocusNode() && nn.getText().equals("后")) break;
									
					if(nn.isNumNode() || nn.isDateNode()) {
						hasNumOrDate = true;
					}
				}
				
				if(hasNumOrDate==false) {
					DateNode node = new DateNode();
					node.setText( "3天");
					words.add(i+1, node);
					i++;
				}
			}
			//时间轴需求, 连续n阳线
			else if ( i-1 >0 && words.get(i-1).getText().equals("连续") && words.get(i).isNumNode() && i + 1 < words.size() && words.get(i+1)!=null && words.get(i+1).isFocusNode() ) {
				NumNode node = (NumNode)words.get(i);
				DateNode dateNode = new DateNode();
				dateNode.setText( node.getText() + "天");
				words.set(i, dateNode);
			}
			//时间轴需求, 横盘-->默认为‘横盘60日’  平台整理-->默认为‘平台整理15日’
			else if (words.get(i).getText().equals("横盘") || words.get(i).getText().equals("平台整理")) {
				
				boolean hasNumOrDate = false;
				for(int j=i+1;j<words.size();j++) {
					SemanticNode nn = words.get(j);
					if(nn.isFocusNode() && nn.getText().equals("后")) break;
									
					if(nn.isNumNode() || nn.isDateNode()) {
						hasNumOrDate = true;
					}
				}
				
				for(int j=i-1;j>=0;j--) {
					SemanticNode nn = words.get(j);
					if(nn.isFocusNode() && nn.getText().equals("后")) break;
									
					if(nn.isNumNode() || nn.isDateNode()) {
						hasNumOrDate = true;
					}
				}
				
				
				
				if(hasNumOrDate==false) {
					DateNode dateNode = new DateNode();
					if(words.get(i).getText().equals("横盘"))
						dateNode.setText("60天");
					else if(words.get(i).getText().equals("平台整理"))
						dateNode.setText("15天");
					words.add(i, dateNode);
					i++;
				}
				
			}
		}
		for (int i = 0; i < words.size(); i++) {
			if(words.get(i)!=null && words.get(i).isSkipOldDateParser()) continue;
			
			if (DatePatterns.DATE_CONJ_TYPE_LOGIC.matcher(words.get(i).getText()).matches()) {
				DateNode dn = new DateNode(words.get(i).getText());
				words.set(i, dn);
			} else if (i + 2 < words.size()
					&& DatePatterns.DATE_CONJ_TYPE_LOGIC_1.matcher(words.get(i).getText()).matches()
					&& DatePatterns.DATE_CONJ_TYPE_LOGIC_1.matcher(words.get(i + 1).getText()).matches()
					&& DatePatterns.DATE_CONJ_TYPE_LOGIC_2.matcher(words.get(i + 2).getText()).matches()) {
				String text = words.get(i).getText() + words.get(i + 1).getText() + words.get(i + 2).getText();
				if (DatePatterns.DATE_CONJ_TYPE_LOGIC.matcher(text).matches()) {
					DateNode dn = new DateNode(text);
					words.set(i, dn);
					words.remove(i + 1);
					words.remove(i + 1);
				}
			} else if (i + 2 < words.size()
					&& DatePatterns.DATE_CONJ_TYPE_LOGIC_1.matcher(
							words.get(i).getText()).matches()
					&& words.get(i + 1).getText().equals("个")
					&& DatePatterns.DATE_CONJ_TYPE_LOGIC_2.matcher(
							words.get(i + 2).getText()).matches()) {
				String text = words.get(i).getText() + words.get(i + 1).getText()
						+ words.get(i + 2).getText();
				if (DatePatterns.DATE_CONJ_TYPE_LOGIC.matcher(text).matches()) {
					DateNode dn = new DateNode(text);
					words.set(i, dn);
					words.remove(i + 1);
					words.remove(i + 1);
				}
			} else if (i + 1 < words.size()
					&& DatePatterns.DATE_CONJ_TYPE_LOGIC_1.matcher(
							words.get(i).getText()).matches()
					&& DatePatterns.DATE_CONJ_TYPE_LOGIC_2.matcher(
							words.get(i + 1).getText()).matches()) {
				String text = words.get(i).getText() + words.get(i + 1).getText();
				if (DatePatterns.DATE_CONJ_TYPE_LOGIC.matcher(text).matches()) {
					DateNode dn = new DateNode(text);
					words.set(i, dn);
					words.remove(i + 1);
				}
			}
		}
	}

	/**
	 * 合并Query中连续出现的DateNode
	 * 
	 * @param query
	 * @throws UnexpectedException
	 * @throws NotSupportedException
	 */
	private void addDate(ArrayList<SemanticNode> nodes, String backtestTime) throws NotSupportedException, UnexpectedException {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		for (int i = 0; i < words.size(); ++i) {
			if(words.get(i)!=null && words.get(i).isSkipOldDateParser()) continue;
			
			if (words.get(i).type != NodeType.DATE) {
				continue;
			}
			DateNode dn = (DateNode) words.get(i);
			for (int j = i + 1; j < words.size(); j++) {
				if (words.get(j).getText().equals(" "))
					continue;
				if (words.get(j).type != NodeType.DATE) {
					break;
				}
				// 如果遇到下一个为均线型指标时，则不合并
				if (j + 1 < words.size()
						&& Util.StringInTechLine(words.get(j + 1).getText())
						&& DatePatterns.TECH_PARAM_PATTERN.matcher(
								words.get(j).getText()).matches()) {
					break;
				}
				if (DatePatterns.TECH_PARAM_PATTERN.matcher(words.get(j).getText())
						.matches() && isTechLineClosing(j + 1, words)) {
					break;
				}
				// 根据单位判断两个DateNode是否可合并
				if (!isNextDateNeedMerage(dn, (DateNode) words.get(j), false, backtestTime)) {
					break;
				}
				DateNode nodeJ = (DateNode) words.get(j);
				dn.setText(dn.getText()+ words.get(j).getText() );
				dn.oldNodes.addAll(nodeJ.oldNodes);
				words.remove(j);
				j--;
			}
		}

	}

	/**
	 * case：14日25日56日120日均线多头排列；收盘价上穿30日、60日、120日三均线
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
						&& nNode.getRangeType() != null
						&& nNode.getRangeType().equals(OperDef.QP_EQ)) {
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

	/**
	 * 根据当前时间节点的maxUnit 和dateUnit来进行判断 如果前面节点的单位比后面节点的单位大 单位大小顺序：年 季度 月 星期 日
	 * 或者年上半年 季度上半季度 月上半月 周上半周 此时上半年、上半季度、上半月、上半周的时间单位依次为月、日、日、日 或者单位一致，且后面的数字 =
	 * 前面的数字 +1，可以合并 或者年当年 季度当季 月份当月 日当日
	 * 
	 * @param curNode
	 * @param rightNode
	 * @param existTechOP
	 * @return
	 * @throws NotSupportedException
	 * @throws UnexpectedException
	 */
	private boolean isNextDateNeedMerage(DateNode curNode, DateNode rightNode,
			boolean existTechOP, String backtestTime) {
		boolean needMerage = false;
		if (rightNode == null || rightNode != null
				&& rightNode.type != NodeType.DATE) {
			return needMerage;
		}
		try {
			DateRange curDateRange = DateCompute
					.getDateInfoFromStr(curNode.getText(), backtestTime);
			DateRange rightDateRange = DateCompute
					.getDateInfoFromStr(rightNode.getText(), backtestTime);
			if (curDateRange == null || rightDateRange == null) {
				return needMerage;
			}
			Unit curDateUnit = curDateRange.getDateUnit();
			Unit curMaxUnit = curDateRange.getMaxUnit();
			Unit rightDateUnit = rightDateRange.getDateUnit();
			Unit rightMaxUnit = rightDateRange.getMaxUnit();
			if (DateUtil.isBigerUnit(curDateUnit, rightDateUnit)
					&& !DatePatterns.THAT_REGION_NO48.matcher(rightNode.getText())
							.matches()) {
				needMerage = true;
			} else if (curDateUnit == rightMaxUnit) { // 以下逻辑需要修正，因为目前没有设定MaxUnit属性
				if (curDateUnit == Unit.YEAR) {
					if (rightDateRange.getFrom().getYear() == curDateRange
							.getFrom().getYear() + 1) {
						needMerage = true;
					} else if (!curNode.isLength() && !rightNode.isLength()) {
						needMerage = true;
					}
				} else if (curDateUnit == Unit.QUARTER) {
					if (rightDateRange.getFrom().getQuarter() == curDateRange
							.getFrom().getQuarter() + 1) {
						needMerage = true;
					} else if (!curNode.isLength() && !rightNode.isLength()) {
						needMerage = true;
					}
				} else if (curDateUnit == Unit.MONTH) {
					if (rightDateRange.getFrom().getMonth() == curDateRange
							.getFrom().getMonth() + 1) {
						needMerage = true;
					}
				} else if (curDateUnit == Unit.DAY) {
					if (rightDateRange.getFrom().getDay() == curDateRange
							.getFrom().getDay() + 1) {
						needMerage = true;
					}
				}
			} else if (curDateUnit == rightDateUnit
					&& curDateUnit != Unit.DAY
					&& curMaxUnit == rightMaxUnit
					&& curNode.compare == CompareType.EQUAL
					&& rightNode.compare == CompareType.EQUAL) {
				//右边时间节点完整，不合并
				if(DatePatterns.YEAR_MONTH_DAY_WITH_SPLIT_SIGN.matcher(rightNode.getText()).matches() || 
				   DatePatterns.YEAR_MONTH_DAY_AS_NUM.matcher(rightNode.getText()).matches())
					return needMerage;
				
				if (rightDateRange.getTo().isAfter(curDateRange.getTo())
						&& !DatePatterns.TODAY_NO19.matcher(rightNode.getText())
								.matches()
						&& !DatePatterns.RELATIVE_DAY_20
								.matcher(rightNode.getText()).matches()) {
					needMerage = true;
				} else if (DatePatterns.THAT_REGION_NO48
						.matcher(rightNode.getText()).matches()) {
					needMerage = true;
				} else if (DatePatterns.REPORT_NO41.matcher(rightNode.getText())
						.matches()) {
					needMerage = true;
				}
			}
		} catch (NotSupportedException e) {
			needMerage = false;
			return needMerage;
		}
		return needMerage;
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

	public enum DateTag {
		// 标记时间合并时，各节点对应的类型
		IRRELEVANT(0), HEAD(1), BETWEEN(2), END(3), DATE(4), TIME(5);

		private int tag;

		private DateTag(int tag) {
			this.tag = tag;
		}
	}

	private void changeNodeToDate(ArrayList<SemanticNode> nodes,int i) {
		DateNode dn = new DateNode(nodes.get(i).getText());
		nodes.remove(i);
		nodes.add(i, dn);
	}

	private boolean isConnectNumAndDate(ArrayList<SemanticNode> nodes,int i) {
		if (i - 1 < 0 || i + 1 >= nodes.size()) {// 没有前一个节点或者后一个节点
			return false;
		}
		if (nodes.get(i - 1).type == NodeType.NUM && nodes.get(i + 1).type == NodeType.DATE) {
			DateNode dn = (DateNode) nodes.get(i + 1);
			if (!dn.isSequence) {
				return true;
			}			
		}
		return false;
	}

	/**
	 * 合并时间与时间范围连词
	 * 
	 * @param query
	 */
	private ArrayList<SemanticNode> tagDate(ArrayList<SemanticNode> nodes, String backtestTime) {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		int i, j;
		ArrayList<DateTag> tags = new ArrayList<DateTag>(words.size());
		for (i = 0; i < words.size(); i++) {
			tags.add(DateTag.IRRELEVANT); // 0
		}

		// 判断是否是num + 连词 + 时间节点类型
		for (i = 0; i < words.size(); i++) {
			if(words.get(i)!=null && words.get(i).isSkipOldDateParser()) continue;
			
			if (DATE_BETWEEN_WORDS.contains(words.get(i).getText()) && isConnectNumAndDate(nodes,i)) {
				changeNodeToDate(nodes,i - 1);
			}
		}

		for (i = 0; i < words.size(); i++) {
			if(words.get(i)!=null && words.get(i).isSkipOldDateParser()) continue;
			
			
			// 如果词语后面为均线型技术指标，则不标记为日期
			/*
			 * if((i+1)<words.size() &&
			 * Util.StringInTechLine(words.get(i+1).text) &&
			 * DatePatterns.TECH_PARAM_PATTERN
			 * .matcher(words.get(i).text).matches()){ continue; }
			 */
			if (words.get(i).type == NodeType.DATE && isTechLineClosing(i, words)) {
				continue;
			}
			if (words.get(i).type == NodeType.DATE) {
				tags.set(i, DateTag.DATE); // tags==4,为date
			} else if (DATE_HEAD_WORDS.contains(words.get(i).getText())) {
				tags.set(i, DateTag.HEAD); // tags==1,为head
			} else if (DATE_BETWEEN_WORDS.contains(words.get(i).getText())) {
				tags.set(i, DateTag.BETWEEN); // tags==2,为between
			} else if (DATE_END_WORDS.contains(words.get(i).getText())) {
				tags.set(i, DateTag.END); // tags==3,为end
			} else if (words.get(i).type == NodeType.TIME) {
				tags.set(i, DateTag.TIME); // tags==5,为time
			}
			if (DATE_AMBI_WORDS.contains(words.get(i).getText())) {
				SemanticNode before = (i == 0) ? null : words.get(i - 1);
				SemanticNode after = (i == words.size() - 1) ? null : words.get(i + 1);
				if (after != null && after.type == NodeType.DATE) {
					if (before == null || before.type != NodeType.DATE) {
						tags.set(i, DateTag.HEAD);
					}
				} else if (before != null && before.type == NodeType.DATE) {
					if (after == null || after.type != NodeType.DATE) {
						tags.set(i, DateTag.END);
					}
				}
			}
		}
		ArrayList<SemanticNode> ref = new ArrayList<SemanticNode>();
		for (i = 0; i < words.size(); i++) {
			if(words.get(i)!=null && words.get(i).isSkipOldDateParser()){
				ref.add(words.get(i));
				continue;
			}
			
			String tagNums = "";
			String texts = "";
			int step = 0;
			//String mergeTagNums = "";
			String mergeTexts = words.get(i).getText();
			int mergeStep = 1;
			NodeType types = NodeType.UNKNOWN;
			DateNode rep = null;
			boolean hasHead = false;
			if (tags.get(i) == DateTag.IRRELEVANT || tags.get(i) == DateTag.BETWEEN || tags.get(i) == DateTag.END) {
				ref.add(words.get(i));
			}else if(tags.get(i) == DateTag.HEAD && DateAxisHandler.AXIS_SPLIT_PATTERN_WORD_ONLY.matcher(mergeTexts).matches()) { 
				//时间轴关键字(后,之后,以后),不要merge
				ref.add(words.get(i));
			}else {
				boolean endWithMid = false;
				boolean hasBlank = false;
				boolean hasConnect = false;
				for (j = i; j < words.size(); j++) {
					int tagNum = tags.get(j).tag;
					if (tags.get(j) != DateTag.IRRELEVANT) {
						endWithMid = false;
						if (tags.get(j) == DateTag.BETWEEN) {
							endWithMid = true;
							hasConnect = true;
						}
					}
					if (tags.get(j) != DateTag.IRRELEVANT && tags.get(j) != DateTag.HEAD || tags.get(j) == DateTag.HEAD && !hasHead) {
						if (tags.get(j) == DateTag.DATE && j > 0 && !isNeighbourDateNeedMerage(nodes,j, backtestTime)) {
							break;
						} else {
							step += 1;
							types = types != NodeType.DATE ? words.get(j).type : types;
							if (hasBlank) {
								// 用以处理如“2012年2月5日 2012年5月6日”中间无连接符无法识别的问题
								if (!hasConnect && tags.get(j) != DateTag.BETWEEN) {
									texts += "-";
									tagNums += DateTag.BETWEEN.tag;
								}
								hasBlank = false;
							}
							tagNums += tagNum;
							texts += words.get(j).getText();
							hasHead = tags.get(j) == DateTag.HEAD && !hasHead ? tags .get(j) == DateTag.HEAD && !hasHead : hasHead;
						}
					} else {
						String tmpText = words.get(j).getText().trim();
						// String tmpText = words.get(j).text.replaceAll("\\s",
						// "");
						if (tmpText.length() < 1) {
							step += 1;
							if (texts != null) {
								hasBlank = true;
							}
							continue;
						} else
							break;
					}
					// 如果非可以合并的前缀或者是已经匹配完整
					if (tagNums.equals("14") || tagNums.equals("43") || tagNums.equals("424") || tagNums.equals("1424")
							|| tagNums.equals("414") || tagNums.equals("434") || tagNums.equals("143")
							|| tagNums.equals("1143") || tagNums.equals("4243") || tagNums.equals("14243")
							//添加日期加时间的匹配类型
							|| "45245".equals(tagNums) || "145245".equals(tagNums)) {
						// 1143根据现有逻辑 暂不支持 eg: 截止到过去3个月以来
						//mergeTagNums = tagNums;
						mergeTexts = texts;
						mergeStep = step;
					}
				}
				texts = mergeTexts;
				step = mergeStep;
				if (types != NodeType.DATE || endWithMid) {
					int len = i + step;
					for (j = i; j < len; j++) {
						ref.add(words.get(j));
					}
					// 修改后，这一步不能这么做
					/*
					 * i = len - 1; if(len < words.size()){
					 * ref.add(words.get(len)); i = len; }
					 */
				} else {
					int len = i + step;
					rep = mergeDateTimeNode(texts, i, len, words);
					ref.add(rep);
					i = i + step - 1;
				}
			}
		}
		for (i = 0; i < ref.size() - 1; i++) {
			if (ref.get(i) == null || ref.get(i).getText().length() == 0) {
				ref.remove(i);
				i = i - 1;
			}
		}
		//nodes = ref;
		return ref;
	}

	private DateNode mergeDateTimeNode(String texts, int j, int len, List<SemanticNode> words) {
		DateNode rep = new DateNode(texts);
		DateRange firstDateNode = null;
		DateRange secondDateNode = null;
		TimeRange firstTimeNode = null;
		TimeRange secondTimeNode = null;
		for (; j < len; j++) {
			if (DatePatterns.BLANK.matcher(words.get(j).getText()).matches()) {
				// 空白节点不加入oldNodes中
				continue;
			}
			rep.oldNodes.add(words.get(j));
			//chenhao 将多个时间拼接成一个的时候已经解析出时间了,为什么还要搞那么多的YMD2YMD正则???
			if (words.get(j).type == NodeType.DATE) {
				if (firstDateNode == null) {
					firstDateNode = ((DateNode) words.get(j)).getDateinfo();
				} else if (secondDateNode == null) {
					secondDateNode = ((DateNode) words.get(j)).getDateinfo();
				}

			} else if (words.get(j).type == NodeType.TIME) {
				if (firstTimeNode == null) {
					firstTimeNode = ((TimeNode) words.get(j)).getTimeRange();
				} else if (secondTimeNode == null) {
					secondTimeNode = ((TimeNode) words.get(j)).getTimeRange();
				}
			}
		}
		if (firstDateNode != null) {
			if (secondDateNode != null) {
				rep.setDateinfo(firstDateNode.getFrom(), secondDateNode.getTo());
			} else {
				rep.setDateinfo(firstDateNode.getFrom(), firstDateNode.getTo());
			}
		}
		if (firstTimeNode != null) {
			TimeNode timeNode = new TimeNode("");
			TimeRange timeRange = null;
			if (secondTimeNode != null) {
				timeRange = new TimeRange(firstTimeNode.getFrom(), secondTimeNode.getTo());
			} else {
				timeRange = new TimeRange(firstTimeNode.getFrom(), firstTimeNode.getTo());
			}
			timeNode.setTimeRange(timeRange);
			rep.setTime(timeNode);
		}
		return rep;
	}

	private boolean isNeighbourDateNeedMerage(ArrayList<SemanticNode> nodes,int pos, String backtestTime) {
		//ArrayList<SemanticNode> words = query_.nodes();
		ArrayList<SemanticNode> words = nodes;
		boolean needMerage = true;
		if (words.get(pos).type == NodeType.DATE) {
			int posOfLastDate = -1;
			for (int i = pos - 1; i >= 0; i--) {
				boolean passBlank = false;
				if (words.get(i).getText().matches("\\s")) {
					if (passBlank == true) {
						break;
					}
					passBlank = true;
				} else if (words.get(i).type == NodeType.DATE) {
					posOfLastDate = i;
					break;
				} else {
					break;
				}
			}
			if (posOfLastDate != -1) {
				needMerage = isNextDateNeedMerage(
						(DateNode) words.get(posOfLastDate),
						(DateNode) words.get(pos), false, backtestTime);
			}
		}
		return needMerage;
	}

	/**
	 * 根据DateNode附近节点，判断DateNode.isSequence
	 * 
	 * @param query
	 * @throws NotSupportedException
	 * @throws UnexpectedException
	 */
	private void sequence(ArrayList<SemanticNode> nodes, String backtestTime) throws NotSupportedException, UnexpectedException {
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

	@SuppressWarnings("unused")
	private boolean tryTagDateAsOnlyDate2SequenceDate(
			ArrayList<SemanticNode> nodes, String backtestTime) throws NotSupportedException,
			UnexpectedException {
		ArrayList<Integer> datePos = getPositionByTypeOrText(nodes,
				NodeType.DATE);
		ArrayList<Integer> sequencePos = getPositionByTypeOrText(nodes, "连续")
				.size() > 0 ? getPositionByTypeOrText(nodes, "连续")
				: getPositionByTypeOrText(nodes, "持续");
		if (datePos.size() != 1 || sequencePos.size() != 1) {
			return false;
		}
		// 若一句话中只出现了一个时间和一个“连续”，则该连续必然修饰的是该时间
		int dateTag = datePos.get(0);
		int sequenceTag = sequencePos.get(0);
		DateNode dateN = (DateNode) nodes.get(dateTag);
		changeDateBySequence(dateN, backtestTime);
		if (sequenceTag < dateTag) {
			dateN.oldNodes.add(0, nodes.get(sequenceTag));
		} else {
			dateN.oldNodes.add(nodes.get(sequenceTag));
		}
		nodes.remove(sequenceTag);
		return true;
	}

	private int tryTagDate2SequenceDate(int i, ArrayList<SemanticNode> nodes,
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
		while (dir == Direction.LEFT ? --tag > -1 : ++tag < nodes.size()) {
			// change目前有18个词语，例如：上涨、下跌、涨、跌、跌幅、涨幅等，一般具有“连续/连”+(涨、跌、上涨、下跌、跌幅、涨幅)+时间
			// 特征
			if (nodes.get(tag).type == NodeType.CHANGE) {
				// 不允许跨过两次Change
				if (passChange) {
					break;
				}
				passChange = true;
			} else if (nodes.get(tag).type == NodeType.CLASS) {
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
			} else if (nodes.get(tag).type != NodeType.UNKNOWN) {
				break;
			}
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
				changeDateBySequence(dn, dir,backtestTime);
				dn.oldNodes.add(0, nodes.get(tag));
				nodes.remove(tag);
				i = dir == Direction.LEFT ? i - 1 : i;
				// tryTagDate2SequenceDate(i, nodes, Direction.RIGHT,
				// isSkipBreak);
				break;
			} else if (DatePatterns.SEQUENCE_DATE.matcher(text).matches()
					&& dir == Direction.RIGHT
					&& isLeftGERight(nodes.get(i), nodes.get(tag))) {
				changeDateBySequence(dn, dir,backtestTime);
				dn.setText(dn.getText()  + text);
				dn.oldNodes.add(nodes.get(tag));
				nodes.remove(tag);
				i = dir == Direction.LEFT ? i - 1 : i;
				break;
			}
		}
		return i;
	}

	private void changeDateBySequence(DateNode dateN, Direction dir, String backtestTime)
			throws NotSupportedException, UnexpectedException {
		dateN.isSequence = true;
		if(dir==Direction.RIGHT)
			dateN.setText(dateN.getText() + "连续");
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

	private void changeDateBySequence(DateNode dateN, String backtestTime)
			throws NotSupportedException, UnexpectedException {
		dateN.isSequence = true;
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
	 * 根据NodeType或String获取ArrayList<SemanticNode>中符合条件的节点位置
	 * 
	 * @param nodes
	 *            ArrayList<SemanticNode>
	 * @param obj
	 *            Object(NodeType or String)
	 * @return ArrayList<Integer> 符合条件的节点在nodes中的位置
	 */
	private ArrayList<Integer> getPositionByTypeOrText(
			ArrayList<SemanticNode> nodes, Object obj) {
		ArrayList<Integer> pos = new ArrayList<Integer>();
		if (obj.getClass() == NodeType.class) {
			for (int i = 0; i < nodes.size(); i++) {
				if (nodes.get(i).type == obj) {
					pos.add(i);
				}
			}
		} else if (obj.getClass() == String.class) {
			for (int i = 0; i < nodes.size(); i++) {
				if (nodes.get(i).getText().equals(obj)) {
					pos.add(i);
				}
			}
		}
		pos.trimToSize();
		return pos;
	}
}
