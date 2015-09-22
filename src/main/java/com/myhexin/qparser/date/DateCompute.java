package com.myhexin.qparser.date;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.ApplicationContext.ApplicationContextHelper;
import com.myhexin.qparser.date.parser.Parser;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.ReportType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.define.MsgDef;
import com.myhexin.qparser.except.DataConfException;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.node.DateNode;
import com.myhexin.qparser.node.FocusNode;
import com.myhexin.qparser.node.FocusNode.FocusItem;
import com.myhexin.qparser.node.SemanticNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.tech.TechMisc;

public class DateCompute
{
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory
			.getLogger(DateCompute.class.getName());
	private static ArrayList<Parser> Parsers = new ArrayList<Parser>();
	private static String path = "com.myhexin.qparser.date.parser.";
	// 按顺序加载,按顺序执行
	private static String[] ClassName = {
			// *********************************
			// 精确时间
			// *********************************
			"ParseTradeDate",// 尝试按交易日解析
			// 标准 年月日 时间
			"ParseDateWithYMD",
			"ParseDateWithYM",
			"ParseDateWithMD",
			"ParseDateWithOnlyYear",
			"ParseDateWithOnlyMonth",
			"ParseDateWithOnlyDay",

			"ParseOnlyQuarter",// 第?[1-4]季度
			"ParseYearQuarter",// XXXX年X季度
			// 标准日期     从()至()
			"ParseDateRangeYMD2YMD", "ParseDateRangeYMD2MD",
			"ParseDateRangeYMD2D", "ParseDateRangeYM2YM", "ParseDateRangeYM2M",
			"ParseDateRangeMD2MD", "ParseDateRangeMD2D",
			"ParseDateRangeY2Y",
			"ParseDateRangeM2M",
			"ParseDateRangeD2D",

			"ParseDateToday", // 今天
			"ParseDateTillNow",

			"ParsenextDate",// 次日

			// 其他相对时间 例子:上周
			"ParseDateRelativeDay", "ParseDateRelativeWeek",
			"ParseDateRelativeMonth", "ParseDateRelativeYear",
			"ParseDateRelativeQuarter",

			// 解析“周”相关时间 例子:
			"ParseDateWeekEnd", // 上周末
			"ParseDateWeekAll",

			"ParseNumTypeDateRangeIn",
			"ParseNumTypeDateRange", // 连续三个季度以来
			"ParseSequenceDate", // 处理 连续XXX 变为近XXX
			"ParseNumTypeFutureDateRange", // 未来3天
			"ParseNumTypeDate", // 3个季度以前/以后
			"ParseNumTypeDate2", // 之前三个季度 前3个季度
			"ParseNumTypeDate2NumTypeDate", // 1-3季度

			// 处理分钟时间
			"ParseDateWithMunites", // 设置minute单位 分钟的数字未做处理

			// *********************************
			// 泛化时间
			// *********************************
			"ParseNumTypeDateWithMarker", // 截止到()前连续X天
			"ParseTillDate", // 截止到()

			"ParseDateBeforeLength", // ()前连续XX
			"ParseDateAfterLength", // ()后连续XX
			
			"ParseDateBeforeLength2", // ()前1周
			"ParseDateAfterLength2", // ()后1周

			"ParseRegularDateRange",// 从()到()以来
			"ParseDateRangeAllKinds", // 自()以来
			"ParseBeforeAndAfterDate", // ()之前()之后                  ()之后()之前

			"ParseAfterDate", // ()之后
			"ParseBeforeDate", // ()之前
			"ParseBetweenDate", // ()以内

			"ParseThatRegionDate", // ()当天
			"ParseHalfRegionDate",// ()上半个季度
			"ParseReportDate",// ()(中报|年报|季报|第?[1-4]季报|半年报)
			// "ParseHalfYearDate", ()上/下半年 已废弃
			"ParsePeriodOfTenDays", // ()(上|中|下)旬
			
			"ParseFutureDate",//()即将  ()将要   

			"ParseSeqYearWithSomething",// 连续X年()
			"ParseSeqMonthWithSomething",// 连续X月()

			"ParseYearWithSomthing",// XXXX年()
			"ParseMonthWithSomthing",// XX月()
			// 特殊处理
			"ParseParticularDay", // ()XX日
			"ParseSequenceDate", // ()连续
			
			"ParseDateEnd" // ()底|末
	};
	static
	{
		for (String clazz : ClassName)
			try
			{
				// 得到所有的ClassName中每一个类实例
				Parsers.add((Parser) Class.forName(path + clazz.trim()).newInstance());
			} catch (Exception e)
			{
				logger_.error(e.getMessage());
				e.printStackTrace();
			}
	}

	/**
	 * 根据中文时间解析出日期范围
	 * 
	 * @param DateString
	 *            表述日期的字符串
	 * @return 解析出的时间范围
	 * @throws NotSupportedException
	 * @throws UnexpectedException
	 */
	public static DateRange getDateInfoFromStr(String DateString, String backtestTime)
			throws NotSupportedException
	{

		if (DateString == null || DateString.length() == 0)
		{
			logger_.error("getDateInfoFromStr根据中文时间解析出日期范围参数DateString为空");
			throw new NotSupportedException(MsgDef.NOT_SUPPORTED_DATE_FMT,
					DateString);
		}
		// 将“今年”、“明年”等替换为数字形式，如“2012年”、“2013年”
		DateString = DateUtil.replaceYearWord(DateString);
		
		if((DatePatterns.YEAR_EVERY_MONTH).matcher(DateString).matches()){//XX年每月与XX年效果相同
			DateString = DateString.substring(0,DateString.indexOf("每月"));
		}
		DateRange range = null;
		try
		{
			// 调用时间函数列表中函数进行解析
			for (Parser parser : Parsers)
			{
				range = parser.doParse(DateString,backtestTime);
				if (range != null)
					return range;
			}
		} catch (Exception e)
		{
			logger_.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 将年份补全，如“09年”，补全为“2009年”
	 * 
	 * @param dateString
	 * @return
	 */
	private static String completeDateString(String dateString)
	{
		if (DatePatterns.PRE_COMPUTE_BY_COMPLETE_YEAR.matcher(dateString)
				.matches())
		{
			if (Param.DEBUG_DATEINFO)
			{
				System.out
						.println("形如“09年，pattern：PRE_COMPUTE_BY_COMPLETE_YEAR”");
			}

			Matcher dateMatcher = DatePatterns.PRE_COMPUTE_BY_COMPLETE_YEAR
					.matcher(dateString);
			dateMatcher.matches();
			String yearStr = dateMatcher.group(1);
			String elseStr = dateMatcher.group(2);
			String addStr = yearStr.startsWith("9") ? "19" : "20";
			dateString = String.format("%s%s年%s", addStr, yearStr, elseStr);
		}
		else if (DatePatterns.PRE_COMPUTE_BY_COMPLETE_YEAR_RANGE.matcher(
				dateString).matches())
		{
			if (Param.DEBUG_DATEINFO)
			{
				System.out
						.println("形如“09-10年，PRE_COMPUTE_BY_COMPLETE_YEAR_RANGE”");
			}
			Matcher dateMatcher = DatePatterns.PRE_COMPUTE_BY_COMPLETE_YEAR_RANGE
					.matcher(dateString);
			dateMatcher.matches();
			String yearStr1 = dateMatcher.group(1);
			String yearStr2 = dateMatcher.group(2);
			String elseStr = dateMatcher.group(3);
			yearStr1 = yearStr1.startsWith("9") ? "19" + yearStr1 : "20"
					+ yearStr1;
			yearStr2 = yearStr2.startsWith("9") ? "19" + yearStr2 : "20"
					+ yearStr2;
			dateString = String
					.format("%s年至%s年%s", yearStr1, yearStr2, elseStr);
		}
		// 原则上不应该把天替换成日，因为2天与2日表达的意思应该是不一样的
		/*
		 * if (dateString.matches(DatePatterns.REGEX_N_TIAN)) { dateString =
		 * dateString.replaceAll("天", "日"); }
		 */
		return dateString;
	}

	public static void getDateInfo(ArrayList<SemanticNode> nodes, String backtestTime) throws NotSupportedException,
			UnexpectedException
	{
		//ArrayList<SemanticNode> words = query.nodes();
		ArrayList<SemanticNode> words = nodes;
		int i;
		for (i = 0; i < words.size(); i++)
		{
			
			if(words.get(i)!=null && words.get(i).isSkipOldDateParser()) continue;
			
			if (words.get(i).type != NodeType.DATE)
			{
				continue;
			}
			DateNode curNode = (DateNode) words.get(i);
			if (DatePatterns.SEQUENCE_SP.matcher(curNode.getText()).matches())
			{
				DateRange dr = getDateInfoFromStr(curNode.getText(), backtestTime);
				if (dr != null)
				{
					curNode.setDateinfo(dr);
					curNode.isSequence = true;
					continue;
				}
			}
			String str = curNode.getText();
			String left = i > 0 ? words.get(i - 1).getText() : null;
			if (str.startsWith("0") || str.startsWith("9") || left == null
					|| !left.equals("连续"))
			{
				// TODO:添加逻辑，10年以前这中默认为10年前
				// 若时间形如“09年”或“91年”，则可确定是年份残缺
				// 若时间以“2”开头，且左侧有连续，则不作补全。这部分在连续信息添加后还会再处理
				str = completeDateString(str);
				if (str.matches(TechMisc.REGEX_N_DAY_NAME))
				{
					curNode.setText(str);
				}
			}
			DateRange range = getDateInfoFromStr(str,backtestTime);
			
			String head = i > 0 ? words.get(i - 1).getText() : null;
//			if( head != null && ( "大于".equals(head)  || "小于".equals(head) || "大于等于".equals(head) ||  
//					"小于等于".equals(head)  ||  "等于".equals(head)  ||  "不等于".equals(head) ) && range != null ) {
//				String fromDay = range.getFrom().toString();
//				String toDay = range.getTo().toString();
//				if( StringUtils.isNotBlank( fromDay ) && fromDay.equals( toDay ) ) { //如果时间范围是同一天，则时间属性改为yyyyMMDD格式
//					String formatFromDay = StringUtils.replace( fromDay, "-", StringUtils.EMPTY );
//					curNode.setText( formatFromDay );
//				}
//			}
			//			if (range == null)
			//			{
			//				// 解析不了，则将其转化为UnknownNode而不是抛出异常
			//				UnknownNode uNode = new UnknownNode(str);
			//				words.set(i, uNode);
			//				continue;
			//			}

			if (head != null && head.equals("早于") && range != null)
			{
				DateInfoNode from = range.getFrom();
				range.setDateRange(null, from);
				curNode.setText(head + curNode.getText() );
				words.remove(i - 1);
				i--;
			}
			else if (head != null && head.equals("晚于") && range != null)
			{
				DateInfoNode to = range.getTo();
				range.setDateRange(to, null);
				curNode.setText( head + curNode.getText());
				words.remove(i - 1);
				i--;
			}
			if (range != null) {
				curNode.setDateinfo(range);
			}
		}
	}

	public static void main(String[] args) throws UnexpectedException,
			DataConfException
	{
		ApplicationContextHelper.loadApplicationContext();
		try
		{
			//DateUtil.loadTradeDate(Util.readTxtFile("./data/tradedates.txt"));
			String time = /* "2012年至2013年2季" */
			"最近2年";
			DateRange range;
			Param.DEBUG_DATEINFO = true;
			String backtestTime = null;
			range = getDateInfoFromStr(time,backtestTime);

			if (range != null)
			{
				System.out.println(range.toDateString());
				System.out.println(range.getLength());
			}
			else
			{
				System.out.println("无法解析");
			}
		} catch (NotSupportedException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	private static void adjustDateNodesToTradeDate(ArrayList<SemanticNode> nodes){
		for(SemanticNode node:nodes){//调整DateNode时间为交易日
			if(node!=null && node.isSkipOldDateParser()) continue;
			
			if(node.isDateNode()){
				DateNode dateNode = (DateNode) node;
				//判断时间节点是否需要调整时间
				if(!isNeedAdjustDateToTradeDate(dateNode)) continue;
				
				DateRange dateRange = dateNode.getDateinfo();
				if(dateRange == null) continue;
				DateInfoNode to = dateRange.getTo();
				int length = dateRange.getLength();
				try {
					DateInfoNode temp = to;	//调整to
					/*int tryCount = 0;
					while(!DateUtil.isTradeDate(temp.toString().replace("-", "")))
					{
						temp = DateUtil.getNewDate(temp, "daytag", -1);
						//防止死循环
						if(tryCount++>30) {
							break;
						}
					}*/
					temp = DateUtil.getNewTradeDateByDay(temp, DateUtil.CHANGE_BY_DAY, 0);
					
					dateRange.setTo(temp);
					int	isNotTradeDate = length-1;	//调整from
					try{
						temp = DateUtil.rollTradeDate(temp,-1*isNotTradeDate);
						if(temp==null) {
							throw new UnexpectedException("[RollTradeDate_ERROR]temp==null");
						}
					}catch(UnexpectedException ex){
						logger_.info("交易日期信息不足");
						temp = new DateInfoNode(1990, 12, 19);
					}
					dateRange.setFrom(temp);
					dateNode.setDateinfo(dateRange);
				} catch (UnexpectedException e) {
					logger_.error(e.getMessage());
				}
			}
		}
	}
	
	/**
	 * 调整时间为交易日,需满足:1.问句中含有"常用行情指标";2.时间有必要调整为交易日
	 * @param nodes
	 * */
	public static void adjustHqDateToTradeDate(ArrayList<SemanticNode> nodes){
		if(!isHqIndex(nodes)) return;
		adjustDateNodesToTradeDate(nodes);
	}
	
	/**把DateNode的时间推算到交易时间*/
	public static DateNode adjustToTradeDate(DateNode dateNode){
		ArrayList<SemanticNode> nodes =new ArrayList<SemanticNode>();
    	nodes.add(dateNode);
    	adjustDateNodesToTradeDate(nodes);
    	if(nodes!=null && nodes.size()>0 && nodes.get(0).isDateNode()){
			dateNode = (DateNode) nodes.get(0);
			return dateNode;
		}
    	return null;
	}
	
	private static List<String> regexList = new ArrayList<String>();
	private static List<Pattern> patternList = new ArrayList<Pattern>();
	static {
		regexList.add("^(最近连续|过去|连续|以往|过往|前|上|近|持续|最近|)(\\d{1,3}|半)(日|天)(内|以内|以来|来|)$");
		regexList.add("^今[天日]|当日|当天|现在|当前$");
		regexList.add("^(昨|前|上|大前)[天日]$");
		
		//2015-09-08 liuxiaofeng 以下这些都是明确指定的时间,不需要调整,调整会导致错误
		//比如: 20060630 单季度.主营证券跌价损失, 是QUARTER类型指标, 20060630非交易日,转换后就错误了
		//regexList.add("^("+DatePatterns.YEAR_NUM+")(?:"+DatePatterns.YEAR_UNIT+"|"+DatePatterns.SPLIT+")?"+"("+DatePatterns.MONTH_NUM+")(?:"+DatePatterns.MONTH_UNIT+"|"+DatePatterns.SPLIT+")?"+"("+DatePatterns.DAY_NUM+")(?:"+DatePatterns.DAY_UNIT+")?$");
		//regexList.add("^("+DatePatterns.SHORT_YEAR_NUM+")(?:"+DatePatterns.YEAR_UNIT+"|"+DatePatterns.SPLIT+")?"+"("+DatePatterns.MONTH_NUM+")(?:"+DatePatterns.MONTH_UNIT+"|"+DatePatterns.SPLIT+")?"+"("+DatePatterns.DAY_NUM+")(?:"+DatePatterns.DAY_UNIT+")?$");
		//regexList.add("^("+DatePatterns.YEAR_NUM+")("+DatePatterns.MONTH_NUM+")("+DatePatterns.DAY_NUM+")(?:"+DatePatterns.DAY_UNIT+")?$");
		//regexList.add("^("+DatePatterns.MONTH_NUM+")(?:"+DatePatterns.MONTH_UNIT+"|"+DatePatterns.SPLIT+")("+DatePatterns.DAY_NUM+")(?:"+DatePatterns.DAY_UNIT+")?$");
		
		for(String regex:regexList){
			//System.out.println(regex);
			Pattern pattern = Pattern.compile(regex);
			patternList.add(pattern);
		}
	}
	
	
	
	/**判断Date节点是否需要调整为交易日*/
	private static boolean isNeedAdjustDateToTradeDate(DateNode dateNode){
		DateRange dateRange = dateNode.getDateinfo();
		if(dateRange == null) return false;
		Unit unit = dateRange.getDateUnit();
		if(unit != null && unit != Unit.DAY){//只有时间单位是"天"的需要调整时间
			return false;
		}
		String text = dateNode.getText();
		if(text==null){
			logger_.error("DateNode text is null");
			return false;
		}
		boolean res = false;
		for(Pattern regex:patternList){
			res = regex.matcher(text).find(); //isMatchToAdjustToTradeDate(regex, text);
			if(res){
				return true;
			}
		}
		return false;
	}
	
	/*private static boolean isMatchToAdjustToTradeDate(String regex,String text){
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		return matcher.find();
	}*/
	
	private static List<String> indexAdjust=new ArrayList<String>();
	static{
	indexAdjust.add("abs_常用行情指标");
	indexAdjust.add("abs_技术面指标");
	}
	
	/**判断问句中是否含有"常用行情指标"或"技术面指标"*/
	private static boolean isHqIndex(ArrayList<SemanticNode> nodes){
		boolean hasFocusNode = false;
		for(SemanticNode node:nodes){
			if(node.isFocusNode()){
				hasFocusNode = true;
				FocusNode focusNode = (FocusNode)node;
				List<FocusItem> focusItems = focusNode.focusList;
				if(focusItems==null) continue;
				for(FocusItem focusItem : focusItems){
					ClassNodeFacade classNodeFacade = focusItem.getIndex();
					if(classNodeFacade == null) continue;
					if (classNodeFacade.getReportType() == ReportType.TRADE_DAILY) {
						return true;
					}
					Set<String> categorysLevelAll=classNodeFacade.getCategorysAll();
					if(categorysLevelAll==null) continue;
					
					for(String index:indexAdjust){
						if(categorysLevelAll.contains(index))
							return true;
					}
				}
			}
		}
		return hasFocusNode;
	}
}


