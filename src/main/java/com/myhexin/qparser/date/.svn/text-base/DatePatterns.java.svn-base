package com.myhexin.qparser.date;

import java.util.regex.Pattern;

public class DatePatterns {
	//单位
	static final String YEAR_UNIT = "年|年度";
	static final String MONTH_UNIT = "月|月份";
	static final String DAY_UNIT = "日|号";
	static final String QUARTER_UNIT = "季度?";
	
	static final String YEAR_NUM = "198\\d|199\\d|200\\d|201\\d";
	static final String SHORT_YEAR_NUM = "9\\d|0\\d|1\\d";
	static final String MONTH_NUM = "0?[1-9]|1[0-2]";
	static final String DAY_NUM = "0?[1-9]|[12]\\d|3[01]";
	static final String QUARTER_NUM = "[1-4]|一|二|三|四";
	
	//分隔符
	static final String SPLIT = "\\-|\\.|/|、|—";
	
    //***************************************************************************
    //确切时间
    //***************************************************************************	
    // XXXX年   XXXX
    public static Pattern ONLY_YEAR = Pattern.compile("^("+YEAR_NUM+")(?:"+YEAR_UNIT+"|)$");
    
    //XX月
    public static Pattern ONLY_MONTH = Pattern.compile("^("+MONTH_NUM+")(?:"+MONTH_UNIT+")$");
    
    //XX日
    public static Pattern ONLY_DAY = Pattern.compile("^("+DAY_NUM+")(?:"+DAY_UNIT+")$");
    
    //XX分钟
    public static Pattern MUNITE = Pattern.compile("\\d+分钟?");
    
    
    //X季度  第X季度
    public static Pattern ONLY_QUARTER = Pattern.compile("^第?("+QUARTER_NUM+")(?:"+QUARTER_UNIT+")$");
	
    //XXXX年每月
    public static Pattern YEAR_EVERY_MONTH = Pattern.compile("^("+YEAR_NUM+")(?:"+YEAR_UNIT+"|)"+"每月$");
    
	//XXXX年XX月XX日YY日    XXXX-XX-XX-YY  XXXXXX月XX日YY日
    public static Pattern YEAR_MONTH_DAY_WITH_SPLIT_SIGN_DUPLICATED = Pattern.
    		compile("^("+YEAR_NUM+")?("+YEAR_UNIT+"|"+SPLIT+")?"+"("+MONTH_NUM+")?("+MONTH_UNIT+"|"+SPLIT+")?"+"("+DAY_NUM+")("+DAY_UNIT+")"+"("+DAY_NUM+")("+DAY_UNIT+")$");
	
	//XXXX年XX月XX日    XXXX-XX-XX  XXXXXX月XX日
    public static Pattern YEAR_MONTH_DAY_WITH_SPLIT_SIGN = Pattern.
    		compile("^("+YEAR_NUM+")(?:"+YEAR_UNIT+"|"+SPLIT+")?"+"("+MONTH_NUM+")(?:"+MONTH_UNIT+"|"+SPLIT+")?"+"("+DAY_NUM+")(?:"+DAY_UNIT+")?$");
    
    //XXXXXXXX   XXXXXXXX日
    public static Pattern YEAR_MONTH_DAY_AS_NUM = 
    		Pattern.compile("^("+YEAR_NUM+")("+MONTH_NUM+")("+DAY_NUM+")(?:"+DAY_UNIT+")?$");
    
    //XXXX年XX月  XXXX-XX月
    public static Pattern YEAR_MONTH = Pattern
            .compile("^("+YEAR_NUM+")(?:"+YEAR_UNIT+"|"+SPLIT+")("+MONTH_NUM+")(?:"+MONTH_UNIT+")$");
    
    //XX年XX月  XX-XX月
    public static Pattern YEAR_MONTH_2 = Pattern
            .compile("^("+SHORT_YEAR_NUM+")(?:"+YEAR_UNIT+"|"+SPLIT+")("+MONTH_NUM+")(?:"+MONTH_UNIT+")$");
    
    //XX月-XX日  XX月XX日
    public static Pattern MONTH_DAY = Pattern
            .compile("^("+MONTH_NUM+")(?:"+MONTH_UNIT+"|"+SPLIT+")("+DAY_NUM+")(?:"+DAY_UNIT+")?$");
    
    //XXXX年X季度
    public static Pattern YEAR_QUARTER = Pattern
            .compile("^("+YEAR_NUM+")(?:"+YEAR_UNIT+"|"+")("+QUARTER_NUM+")(?:"+QUARTER_UNIT+")$");
    
	public static Pattern YEAR_MONTH_NODAY = Pattern
			.compile("^(198\\d|199\\d|200\\d|201\\d)年(0?[1-9]|1[0-2])月(0?[1-9]|[12]\\d|3[01])$");
    
    // 连接符也做间隔符的pattern放在前面解析
    //XXXX年XX月XX日至XXXX年XX月XX日
    public static Pattern WHOLE_2_WHOLE_NO9 = Pattern
            .compile("^([从自]?)" +
            		"("+YEAR_NUM+")(?:"+YEAR_UNIT+"|"+SPLIT+")?" +
            		"("+MONTH_NUM+")(?:"+MONTH_UNIT+"|"+SPLIT+")?" +
            		"("+DAY_NUM+")(?:"+DAY_UNIT+")?" +
            		"[\\s]*[与至到和后~\\-、—]" +
            		"("+YEAR_NUM+")(?:"+YEAR_UNIT+"|"+SPLIT+")?" +
            		"("+MONTH_NUM+")(?:"+MONTH_UNIT+"|"+SPLIT+")?" +
            		"("+DAY_NUM+")(?:"+DAY_UNIT+")?" +
            		"(为止|以来|之间|之前|前|)$");
    
    //XXXX年XX月XX日至XX月XX日
    public static Pattern YMD_2MD_NO10 = Pattern
            .compile("^([从自]?)" +
            		"("+YEAR_NUM+")(?:"+YEAR_UNIT+"|"+SPLIT+")?" +
            		"("+MONTH_NUM+")(?:"+MONTH_UNIT+"|"+SPLIT+")?" +
            		"("+DAY_NUM+")(?:"+DAY_UNIT+")?" +
            		"[\\s]*[与至到和后~\\-、—]" +
            		"("+MONTH_NUM+")(?:"+MONTH_UNIT+"|"+SPLIT+")?" +
            		"("+DAY_NUM+")(?:"+DAY_UNIT+")?" +
            		"(为止|以来|之间|之前|前|)$");
    
    //XXXX年XX月XX日至XX日
    public static Pattern YMD_2_D_NO11 = Pattern
            .compile("^([从自]?)" +
            		"("+YEAR_NUM+")(?:"+YEAR_UNIT+"|"+SPLIT+")?" +
            		"("+MONTH_NUM+")(?:"+MONTH_UNIT+"|"+SPLIT+")?" +
            		"("+DAY_NUM+")(?:"+DAY_UNIT+")?" +
            		"[\\s]*[与至到和后~\\-、—]" +
            		"("+DAY_NUM+")(?:"+DAY_UNIT+")?" +
            		"(为止|以来|之间|之前|前|)$");
    
    //XXXX年XX月至XXXX年XX月
    public static Pattern YM_2_YM_NO12 = Pattern
            .compile("^([从自]?)" +
            		"("+YEAR_NUM+")(?:"+YEAR_UNIT+"|"+SPLIT+")?" +
            		"("+MONTH_NUM+")(?:"+MONTH_UNIT+")?" +
            		"[\\s]*[与至到和后~\\-、—]" +
            		"("+YEAR_NUM+")(?:"+YEAR_UNIT+"|"+SPLIT+")?" +
            		"("+MONTH_NUM+")(?:"+MONTH_UNIT+")?" +
            		"(为止|以来|之间|之前|前|)$");
    
    //XXXX年XX月至XX月
    public static Pattern YM_2_M_NO13 = Pattern
            .compile("^([从自]?)" +
            		"("+YEAR_NUM+")(?:"+YEAR_UNIT+"|"+SPLIT+")?" +
            		"("+MONTH_NUM+")(?:"+MONTH_UNIT+")?" +
            		"[\\s]*[与至到和后~\\-、—]" +
            		"("+MONTH_NUM+")(?:"+MONTH_UNIT+")?" +
            		"(为止|以来|之间|之前|前|)$");// Above
    
    //XX月XX日至XX月XX日
    public static Pattern MD_2_MD_NO14 = Pattern
            .compile("^(?:从|自|自从|)" +
            		"("+MONTH_NUM+")(?:"+MONTH_UNIT+"|"+SPLIT+")?" +
            		"("+DAY_NUM+")(?:"+DAY_UNIT+")?" +
            		"[\\s]*[与至到和后~\\-、—]" +
            		"("+MONTH_NUM+")(?:"+MONTH_UNIT+"|"+SPLIT+")?" +
            		"("+DAY_NUM+")(?:"+DAY_UNIT+")?" +
            		"(?:为止|以来|之间|)$");
    
  //XX月XX日至XX日
    public static Pattern MD_2_D_NO15 = Pattern
            .compile("^(?:从|自|自从|)" +
            		"("+MONTH_NUM+")(?:"+MONTH_UNIT+"|"+SPLIT+")?" +
            		"("+DAY_NUM+")(?:"+DAY_UNIT+")?" +
            		"(?:与|至|到|和后~|\\-|、|—)" +
            		"("+DAY_NUM+")(?:"+DAY_UNIT+")?" +
            		"(?:为止|以来|之间|)$");// Above

    //XXXX年至XXXX年
    public static Pattern ONLY_YEAR_2_YEAR_NO16 = Pattern
            .compile("^([从自]?)" +
            		"("+YEAR_NUM+")(?:"+YEAR_UNIT+")?" +
            		"[至到和后~\\-、—\\.]" +
            		"("+YEAR_NUM+")(?:"+YEAR_UNIT+")?" +
            		"(为止|以来|之间|之前|前|)$");
    
    //XX月至XX月
    public static Pattern ONLY_MONTH_2_MONTH_NO17 = Pattern
            .compile("^([从自]?)" +
            		"("+MONTH_NUM+")(?:"+MONTH_UNIT+")?" +
            		"[至到和后~\\-、—\\.]" +
            		"("+MONTH_NUM+")(?:"+MONTH_UNIT+")?" +
            		"(为止|以来|之间|之前|前|)$");
    
    //XX日至XX日
    public static Pattern ONLY_DAY_2_DAY_NO18 = Pattern
            .compile("^([从自]?)" +
            		"("+DAY_NUM+")(?:"+DAY_UNIT+")?" +
            		"[至到和后~\\-、—\\.]" +
            		"("+DAY_NUM+")(?:"+DAY_UNIT+")?" +
            		"(为止|以来|之间|之前|前|)$");// Above
    
    
    // “今天，昨天，前天， 大前天”是可能计算TradeDay的pattern
    public static Pattern TODAY_NO19 = Pattern.compile("^今[天日]?|当日|当天|现在|当前$");// ?
    public static Pattern TO_TODAY = Pattern.compile("^[至到]今[天日]?|当日|当天|现在|当前$");
    public static Pattern RELATIVE_DAY_20 = Pattern.compile("^(昨|前|上|大前|明|后|大后)[天日]$");
    public static Pattern NEXT_DATE = Pattern.compile("^次(日)$");  
    //static Pattern UNIT_LESS_THAN_DAY = Pattern.compile("^上午|下午|([上下]午)?(0?[1-9]|1\\d|2[0-4])点?$");
 
    public static Pattern TILL_NOW = Pattern.compile("^至今$");
    
    static String RELATIVE_WORD="上|下|本|这|上上|下下";
    // 对年度、月份和季度的相对描述
    public static Pattern RELATIVE_YEAR_NO25 = Pattern.compile("^("+RELATIVE_WORD+")个?年度$");
    public static Pattern RELATIVE_MONTH_NO26 = Pattern.compile("^("+RELATIVE_WORD+")个?月$");
    public static Pattern RELATIVE_QUARTER_NO27 = Pattern.compile("^("+RELATIVE_WORD+")个?季度$");
    
    // 对周的描述************************************************************
    public static Pattern RELATIVE_WEEK_NO28 = Pattern.compile("^("+RELATIVE_WORD+")个?(周|礼拜|星期)$");
    public static Pattern WEEK_END_NO29 = Pattern.compile("^([上下本这]?)个?(周末|双休日)$");// Above
    public static Pattern WEEK_ALL_NO30 = Pattern.compile("^([上下本这]?)个?(周|礼拜|星期)([1-7一二三四五六日天])$");
    
    
    // 数字型日期
    // 2013.08.23 时间修改：使之更加规范化 NUM_TYPE_RANGE_NO31、
    public static Pattern NUM_TYPE_RANGE_NO = Pattern
            .compile("^(最近连续|过去|连续|以往|过往|前|上|近|持续|最近|)(\\d{1,3}|半)(日|天|周|个?星期|个?礼拜|季|个?季度|个?月|年|个?年度)(内|以内)$");
    
    public static Pattern NUM_TYPE_RANGE_NO31 = Pattern
            .compile("^(最近连续|过去|以往|过往|前|上|近|持续|最近|)(\\d{1,3}|半)(日|天|周|个?星期|个?礼拜|季|个?季度|个?月|年|个?年度)(以来|来|)$");// Above
	public static Pattern SEQUENCE_SP = Pattern
			.compile("^(?:连续)(\\d{1,2}|[一二三四五六七八九十]{1,2})(?:个)?(年|季度|季|月|周|日|天|交易日)$");
	public static Pattern SEQUENCE_SP2 = Pattern
			.compile("^(\\d{1,2}|[一二三四五六七八九十]{1,2})(?:个)?(年|季度|季|月|周|日|天|交易日)(?:连续)$");
	
	public static Pattern SEQUENCE_SP3 = Pattern
			.compile("^(?:从|自|自从|)?" +"("+YEAR_NUM+")?("+YEAR_UNIT+"|"+SPLIT+")?" +
            		"("+MONTH_NUM+")?("+MONTH_UNIT+"|"+SPLIT+")?" +
            		"("+DAY_NUM+")("+DAY_UNIT+")" +
            		"[\\s]*[与至到和后~\\-、—]" +
            		"("+YEAR_NUM+")?("+YEAR_UNIT+"|"+SPLIT+")?" +
            		"("+MONTH_NUM+")?("+MONTH_UNIT+"|"+SPLIT+")?" +
            		"("+DAY_NUM+")("+DAY_UNIT+")" +
            		"(?:以来|之间|)?(?:的)?(连续|每天)$");
	
	public static Pattern SEQUENCE_SP4 = Pattern
			.compile("^(?:从|自|自从|)?" + "("+YEAR_NUM+")?("+YEAR_UNIT+"|"+SPLIT+")?" +
            		"("+MONTH_NUM+")?("+MONTH_UNIT+"|"+SPLIT+")?" +
            		"("+DAY_NUM+")("+DAY_UNIT+")?" +
            		"[\\s]*[与至到和后~\\-、—]" +
            		"("+YEAR_NUM+")?("+YEAR_UNIT+"|"+SPLIT+")?" +
            		"("+MONTH_NUM+")?("+MONTH_UNIT+"|"+SPLIT+")?" +
            		"("+DAY_NUM+")("+DAY_UNIT+")?" +
            		"(?:以来|之间|)?(?:的)?(.*?)(\\d{1,3}[日|天])(?:连续|持续|每天)?$");
	
	//近期7天
	public static Pattern NUM_TYPE_PAST = Pattern
			.compile("^(?:近期|近|最近)(?:\\d{1,2}|[一二三四五六七八九十]{1,2})(?:个)?(?:年|季度|季|月|周|日|天|交易日)$");
	
	public static Pattern NUM_TYPE_PAST_GROUP1 = Pattern
			.compile("^(?:近期|近|最近)$");
	
	public static Pattern NUM_TYPE_PAST_GROUP2 = Pattern
			.compile("^(?:\\d{1,2}|[一二三四五六七八九十]{1,2})(?:个)?(?:年|季度|季|月|周|日|天|交易日)$");
	
	//7天每天
	public static Pattern SEQUENCE_SP5 = Pattern
			.compile("^(?:\\d{1,2}|[一二三四五六七八九十]{1,2})(?:个)?(?:年|季度|季|月|周|日|天|交易日)(?:的)?(?:[\\d{1,3}|每|每一][年|季度|季|月|周|日|天|交易日])$");
	
	//每天
	public static Pattern EVERY_UNIT = Pattern
			.compile("^(?:[每|每一][年|天|])$");
	
	//后面7天
	public static Pattern DAY_OFFSET = Pattern
			.compile("^(后|后面|前)(\\d{1,3}[日|天])$");
	
	//星期几
	public static Pattern WEEK_DAY = Pattern
			.compile("^(1|2|3|4|5|6|7|一|二|三|四|五|六|日|天)$");
	
	//5天内即将
    public static Pattern SEQUENCE_SP6 = Pattern
            .compile("^(\\d{1,3})(?:内)?(年|天|日|个?月|季|个?季度|周)(?:即将|将要|将会)$");
	
	//未来3天
    public static Pattern NUM_TYPE_FUTURE_NO32 = Pattern
            .compile("^未来(\\d{1,3})(年|天|日|个?月|季|个?季度|周)$");
    //3个季度以前/以后
    public static Pattern NUM_TYPE_NOT_RANGE_NO33 = Pattern
            .compile("^(\\d{1,3}|半)(天|年|个月|星期|周|礼拜|日|季|个季度)(前|之前|后|以前|之后|以后)$");
    //前三个季度
    public static Pattern NUM_TYPE_NOT_RANGE_NO33_1 = Pattern
            .compile("^(前|之前|后|以前|之后|以后)(\\d{1,3}|半)(天|年|个月|星期|周|礼拜|日|季|个?季度)$"); //后3天
    //1-3季度
    public static Pattern NUM_TYPE_2_NUM_TYPE_NO34 = Pattern
            .compile("^(\\d{1,3})[至到~\\-、—](\\d{1,3})(天|周|礼拜|星期|个月|个季度|年)(内|以内|以来|来|)$");// 未来？过去？
    //截止到()前连续X天
    public static Pattern NUM_TYPE_RANGE_WITH_MARKER_NO35 = Pattern
            .compile("(截止到|截止)(.+?)(过去|连续|以往|过往|前|之前|持续|累计)"
                    + "(\\d+?)(年|月|个月|日|天|季度|个季度|星期|礼拜|周)(以来|)$");
    //()连续
    public static Pattern SEQUENCE_IN_END = Pattern
            .compile("(.+?)连续$");
    
    //()底|末
    public static Pattern DATE_END = Pattern.compile("(.+?)[底末]$");
    
    //()单日
    public static Pattern DATE_ONE_DAY = Pattern.compile("(.+?)单日$");
    
    // 关于交易日的Pattern
    public static Pattern TRADE_DATE_1 = Pattern.compile("^(上|下|这|上上)个?交易日$");
    public static Pattern TRADE_DATE_2 = Pattern.compile("^未来(\\d{1,5})个?交易日$");
    public static Pattern TRADE_DATE_3 = Pattern
            .compile("^(\\d{1,5})个?交易日(内|以内|以来|)$");
    public static Pattern TRADE_DATE_4 = Pattern
            .compile("^(\\d{1,5})[至到~\\-、—](\\d{1,5})个?交易日(内|以内|以来)$");
    public static Pattern TRADE_DATE_5 = Pattern
            .compile("^(最近连续|过去|连续|以往|过往|上|前|之前|近|持续|最近)(\\d{1,5})个?交易日(以来|)$");
    public static Pattern TRADE_DATE_6 = Pattern
            .compile("^(\\d{1,5})个?交易日(前|之前|后|以前|之后|以后)$");   
    public static Pattern TRADE_DATE_WITH_MARKER_7 = Pattern
    	    .compile("(?:截止到|截止)(.+?)(过去|连续|以往|过往|前|之前|持续|累计)"
    	            + "(\\d+?)个?交易日(以来|)$");
    
    
    
    
    //***************************************************************************
    //带泛化时间
    //***************************************************************************
    //截止到()    
    public static Pattern TILL_NO45 = Pattern.compile("(截止到|截止)(.+?)$");
    //()前连续XX
	public static Pattern IN_ONE_DAY_AFTER_LENGTH = Pattern
			.compile("^(.+?)(?:后|以后|以来|之后)(连续.+?)$");
	//()后连续XX
    public static Pattern IN_ONE_DAY_BEFORE_LENGTH = Pattern
			.compile("^(.+?)(?:前|以前|之前)(连续.+?)$");
    
    //()前1周
	public static Pattern IN_ONE_DAY_AFTER_LENGTH2 = Pattern
			.compile("^(.+?)(?:后|以后|以来|之后)(\\d+?(?:天|周|月|季度))$");
	//()后1周
    public static Pattern IN_ONE_DAY_BEFORE_LENGTH2 = Pattern
			.compile("^(.+?)(?:前|以前|之前)(\\d+?(?:天|周|月|季度))$");
    //时间合并时使用
	public static Pattern DATE_CONJ_LENGTH2 = Pattern
			.compile("^(?:前|以前|之前|后|以后|以来|之后)(\\d+?(?:天|周|月|季度))$");

    
    
	//从()到()以来
    public static Pattern FROM_ONE_DAY_TO_ANTHER_DAY_NO36 = Pattern
            .compile("^(?:从|自|自从|)(.+)(?:与|至|到|~|\\-|、|—|和)(.+?)(?:为止|以来|)$");
    //自()以来
    public static Pattern FOR_ALL_KIND_FROM_TO_NO37 = Pattern
            .compile("^(?:从|自|自从)(.+?)(?:为止|以来|)$");
    //()之前()之后   ()之后()之前
    public static Pattern BEFORE_AND_AFTER_NO38 = Pattern
    .compile("^(.+?)([以之]?前|[以之]?后)(.+?)([以之]?前|[以之]?后)$");   
    
    //()之后
    public static Pattern IN_ONE_DAY_AFTER_NO42 = Pattern
            .compile("^(.+?)(?:后|以后|以来|之后)$");
    //()之前
    public static Pattern IN_ONE_DAY_BEFORE_NO43 = Pattern
            .compile("^(.+?)(?:前|以前|之前)$");
    
    public static Pattern RANGE_DATE = Pattern
            .compile("(1?\\d)(天|年|月|星期|周|礼拜|日|季|季度)");
    
    //()以内
    public static Pattern IN_A_DATE_RANGE_NO44 = Pattern.compile("^(.+?)(?:内|以内)$");
    

  //()当天	
    public static Pattern THAT_REGION_NO48 = Pattern.compile("^(.*?)当(日|天|月|季|季度|年)$");
  //()上半个季度
    public static Pattern HALF_A_REGION_NO47 = Pattern.compile("^(.*?)(上|下|前|后)半(年|个?年度|个?季度|个?月|周|个?星期|个?礼拜)$");
    //()(中报|年报|季报|第?[1-4]季报|半年报)
    public static Pattern REPORT_NO41 = Pattern.compile("^(.*?)(中报|年报|季报|第?[1-4]季报|半年报)$");
    
    // 解析部分需放到范围型pattern解析之后
    //()上/下半年    已废弃
    public static Pattern HALF_A_YEAR_NO38 = Pattern.compile("^(.*?)(上|下)半年$");  //方法废弃
    //()(上|中|下)旬
    public static Pattern A_PERIOD_OF_TEN_DAYS_NO39 = Pattern
            .compile("^(.*?)(上|中|下)旬$");   	
    
    public static Pattern FURURE_DATE = Pattern
            .compile("^(.*?)(即将|将要|将|未来)$");  
    
    //连续X年()   
    public static Pattern SEQ_YEAR_WITH_SOMETHING = Pattern.compile("^(连续\\d+?年)([^年]+)$"); 
    //连续X月()
    public static Pattern SEQ_MONTH_WITH_SOMETHING = Pattern.compile("^(连续\\d+?月)([^年月季]+)$"); 
    
    
    //XXXX年()
    public static Pattern YEAR_WITH_SOMTHING = Pattern.compile("^("+YEAR_NUM+")(?:"+YEAR_UNIT+")([^年]+)$");
    
    //XX月()
    public static Pattern MONTH_WITH_SOMTHING = Pattern.compile("^("+MONTH_NUM+")(?:"+MONTH_UNIT+")([^年月季]+)$");
    
    //()XX日
    public static Pattern NOT_REGULAR_DAY_NO46 = Pattern
            .compile("^(.+?)([12]\\d|3[0-1]|[1-9])[日号]$");
    //***************************************************************************
    //解析使用不使用
    //***************************************************************************
    // 公式中时间参数对应的Pattern，例如：5日均线
    //XX月 XX周
    public static Pattern REGEX_WEEK_MONTH = Pattern.compile("(\\d+)(周|月)");
    public static Pattern TECH_PARAM_PATTERN = Pattern
            .compile("^([1-9]\\d*)(分钟|小时|日|天|周|月)");
    // 空格
    public static Pattern BLANK = Pattern.compile("^\\s$");
    public static Pattern ADD_MARKER = Pattern.compile("^\\{\\{|\\}\\}$");
    
    // 对年份部分残缺的时间进行修复
    public static Pattern PRE_COMPUTE_BY_COMPLETE_YEAR_RANGE = Pattern
    .compile("^(9\\d|0\\d|1\\d)(?:年|年度|)(?:与|至|到|~|\\-|、|—|和)(9\\d|0\\d|1\\d)(?:年|年度)(.*?)$");
    public static Pattern PRE_COMPUTE_BY_COMPLETE_YEAR = Pattern
            .compile("^(0\\d|1\\d|9\\d)(?:年|年度)(.*?)$");
    //public static final String REGEX_N_TIAN = "(\\d+)天";
       

    public static Pattern NUM_TYPE_DATE = Pattern.compile("^\\d{1,3}(日|天|个月|周|个季度|年)$");
    public static Pattern RECENT = Pattern.compile("最近");

    public static Pattern SEQUENCE_SP_DATE = Pattern.compile("^([1-9]\\d?)(日|年|月|季度?)$");
    public static Pattern SEQUENCE_SP_YEAR = Pattern.compile("^([1-9]\\d?)年$");
    public static Pattern  RELATIVE_DATE =  Pattern
    .compile("^(上|前|之前)(\\d{1,5})(年|个?月|日|天|季|个?季度|个?星期|个?礼拜|周|个?交易日)(内|以内|以来|)$");
    public static Pattern SP_KEY_WORDS = Pattern.compile("^有|出现$");
    
    public static Pattern SEQUENCE_TAG = Pattern.compile("^(连续|持续|每(?:年|季度|季|月|日|天))$");
    public static Pattern SEQUENCE_DATE = Pattern.compile("^逐|每个?(年|季度|季|月|日|天)$");
    public static Pattern SEQUENCE_BREAK_TAG = Pattern.compile("^(创|后|\\.)$"); //时间轴需求, 不能跨后
    public static Pattern SEQUENCE_MAY_BREAK_TAG = Pattern.compile("^(,|;|\0|\t)$");

    
    // 已加入词典
    /*
    // 连词
    public static Pattern CONJ_TYPE_1 = Pattern.compile("^([以之])$");
    public static Pattern CONJ_TYPE_2 = Pattern.compile("^([前后])$");
    public static Pattern CONJ_TYPE = Pattern.compile("^([以之]前|[以之]后)$");
    
    // (周|礼拜|星期)([1-7一二三四五六日天])
    public static Pattern DATE_CONJ_TYPE_WEEK_1 = Pattern.compile("^(周|礼拜?|星期)$");
    public static Pattern DATE_CONJ_TYPE_WEEK_2 = Pattern.compile("^([1-7一二三四五六日天])|(拜日)$");
    public static Pattern DATE_CONJ_TYPE_WEEK = Pattern.compile("^((周|礼拜|星期)([1-7一二三四五六日天]))|周末$");
    
    // 个(交易日|星期|礼拜|月|季度|年度)
    public static Pattern DATE_CONJ_TYPE_UNIT_1 = Pattern.compile("^个$");
    public static Pattern DATE_CONJ_TYPE_UNIT_2 = Pattern.compile("^(交易日|星期|礼拜|月|季度|年度)$");
    public static Pattern DATE_CONJ_TYPE_UNIT = Pattern.compile("^个(交易日|星期|礼拜|月|季度|年度)$");
    */
    // 半|每(周|个?星期|个?礼拜|个?月|个?季度|个?年度)
    public static Pattern DATE_CONJ_TYPE_HALF_1 = Pattern.compile("^半|每$");
    public static Pattern DATE_CONJ_TYPE_HALF_2 = Pattern.compile("^(天|日|个?交易日|周|个?星期|个?礼拜|个?月|个?季度|年|个?年度)$");
    public static Pattern DATE_CONJ_TYPE_HALF = Pattern.compile("^(半|每)(天|日|个?交易日|周|个?星期|个?礼拜|个?月|个?季度|年|个?年度)$");
    
    // (上|下|本|这)
    public static Pattern DATE_CONJ_TYPE_LOGIC_1 = Pattern.compile("^(上|下|本|这|上|下|这个)$");
    public static Pattern DATE_CONJ_TYPE_LOGIC_2 = Pattern.compile("^(上|下|)(个?交易日|周|个?星期|个?礼拜|年|个?月|日|个?季度|个?年度|个?((周|礼拜|星期)([1-7一二三四五六日天]))|周末)$");
    public static Pattern DATE_CONJ_TYPE_LOGIC = Pattern.compile("^(上|下|本|这|上上|下下)(个?交易日|周|个?星期|个?礼拜|年|个?月|日|个?季度|个?年度|个?((周|礼拜|星期)([1-7一二三四五六日天])|周末))$");
    public static Pattern SINGLE_DATE = Pattern.compile("^单(季度|月|日)$"); //目标函数不在使用
    
    //n日多重均线
	public static Pattern MulTI_NDay = Pattern.compile("((?:\\d{1,3}日[,|，|\\.|\\s|、]?))+");
	public static Pattern ONE_NDay = Pattern.compile("^(?:\\d{1,3}日?)$");
	public static Pattern DRAFT_NDay = Pattern.compile("^(,|，|\\.|\\s|、)*?(\\d{1,3})$");
	
    //2014年,2015年
	public static Pattern MulTI_YEAR = Pattern.compile("((?:[198\\d|199\\d|200\\d|201\\d][年|年度]?[ ,|，|\\.|\\s|、]?))+");
	//2015年
	public static Pattern ONE_YEAR = Pattern.compile("^(?:(?:198\\d|199\\d|200\\d|201\\d)(?:年|年度)?)$");
	
	//交易日期和n日重叠
	public static Pattern DUPLICATE_DAY = Pattern.compile("^(198\\d|199\\d|200\\d|201\\d)年(0?[1-9]|1[0-2])月(0?[1-9]|[12]\\d|3[01])日((?:指数)?\\d{1,3}日.*)$");
	
	//n日或者年份
	public static Pattern NDAY_YEAR = Pattern.compile("(?:\\d{1,3}[日|天])|(?:198\\d|199\\d|200\\d|201\\d)[年|年份]");
}
