package com.myhexin.qparser.define;

import com.myhexin.qparser.date.DateInfoNode;

/**
 * 定义一些杂七杂八的常量
 */
public class MiscDef {
    public static final String STR_AND = "_&_";
    public static final String STR_OR = ",";
    public static final String RANGE_PARAM_MAKR = "#TO#";
    public static final String INDEX_NOT_AVAIL = "index_not_avail";
    
    public static final String WEB_CMP_VAL_AND = "and";
    public static final String WEB_CMP_VAL_OR = "or";
    
    public static final String FUND_NAME_PROP = "_基金简称";
    public static final String FUND_CODE_PROP = "_基金代码";
    public static final String FUND_NAME_CLASS = "基金简称";
    public static final String FUND_CODE_CLASS = "基金代码";
    public static final String FUND_NAME_ID = "00001";
    public static final String FUND_CODE_ID = "00003";
    public static final String STK_NAME_PROP = "_股票简称";
    public static final String STK_CODE_PROP = "_股票代码";
    public static final String STK_NAME_CLASS = "股票简称";
    public static final String STK_CODE_CLASS = "股票代码";
    public static final String STK_NAME_ID = "00420";
    public static final String STK_CODE_ID = "00421";
    public static final String STK_CONCEPT_CLASS = "所属概念";
    public static final String STK_CONCEPT_PROP = "_所属概念";
    public static final String STK_HANGYE_CLASS = "所属申万行业";
    public static final String NUM_VAL_PROP = "_数值";
    public static final String BOOLEAN_VAL_PROP = "_浮点型数值";
    public static final String DATE_VAL_PROP = "_日期";
    public static final String BOOL_VAL_PROP = "_是否";
    public static final String BOOL_YES_VAL = "+是";
    public static final String BOOL_NO_VAL = "+否";
    public static final String DATE_RANGE_PROP = "+区间";
    public static final String FAREN_PROP = "_法人代表";
    public static final String RPOVINCE_PROP = "_省份";
    public static final String CITY_PROP = "_城市";
    public static final String REPORT_PROP = "报告期";
    public static final String TRADE_DATE_PROP = "交易日期";
    public static final String START_DATE_PROP = "起始交易日期";
    public static final String END_DATE_PROP = "截止交易日期";
    public static final String START_DATE_PARAM_LIST_NAME = "P00011";
    public static final String END_DATE_PARAM_LIST_NAME = "P00009";
    public static final String START_DATE_PARAM_NAME = "FD1";
    public static final String END_DATE_PARAM_NAME = "FD2";
    public static final String ON_LIST_CLASS = "上榜次数";
    public static final String UNLOCK_CLASS = "解禁股数";
    public static final String ATTENTION_CLASS = "关注度";
    public static final String AH_PRICE_CLASS = "ah价格对比";
    public static final String TOTAL_STOCK_ISSUE_CLASS="总股本";
    public static final String IS_IMPORTANT_INDEX_CLASS = "是否属于重要指数成分";
    public static final String IS_DIVIDEND_CLASS = "是否分红";
    public static final String QTYPE_FUND = "fund";
    public static final String QTYPE_STOCK = "stock";
    public static final String PROD_ONTO_URI = "base:has_production";
    public static final String CONCEPT_ONTO_URI = "base:has_stockconcept";
    public static final String GID_KEY_CODE = "1";
    public static final String GID_KEY_NAME = "2";
    public static final String STK_MARK_FOR_SMART_OPER = "_STK_%s_";
    public static final String WEB_QUERY_PTN_STK_DOCTOR = "doctor_%s";
    public static final String WEB_QUERY_PTN_CONCEPT = "concept_%s";
    public static final String MERGED_TRADE_DATE_RANGE = "%s-%s";
    public static final String MERGED_TRADE_DATE_RANGE2 = "%s";
    
    public static final String RULE_LATEST = "_最新";
    public static final String LOGIC_CONN_SET = 
    	"和|与|跟|同";
    public static final String UNKNOWN_OPER_SET = 
    	"比例|比值|比|之比|相差|之商|比率|比重|占比|之差|差|总和|总计|和|之和|之积|积";
    public static final String NEGATIVE_WORD_SET =
        "不包含|不包括|不含有|不等于|不属于|不存在|" +
        "不含|不等|不属|不是|不为|没有|剔除|去除|除去|除了|除掉|刨除|除|无|非|不";
    public static final String TRIGGER_WORD_SET =
    	"=|等于|包含|包括|含有|属于|存在|是|为|含|有|" + NEGATIVE_WORD_SET;
    public static final String SRV_XML_HEADER =
        "<?xml version=\"1.0\" encoding=\"gbk\"?>\n<request><items>\n";
    public static final String SRV_XML_TAIL =
        "\t</items>\n\t<thscodes></thscodes>\n</request>\n";
    public static final String SPARQL_PREFIX =
        "prefix rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n" +
        "prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
        "prefix xsd:<http://www.w3.org/2001/XMLSchema#>\n" +
        "prefix owl:<http://www.w3.org/2002/07/owl#>\n" +
        "prefix pf:<http://jena.hpl.hp.com/ARQ/property#>\n" +
        "prefix base:<http://www.10jqka.com.cn/ontologies#>\n" +
        "prefix People:<http://www.10jqka.com.cn/ontologies/People#>\n" +
        "prefix sec:<http://www.10jqka.com.cn/ontologies/sec#>\n";

    /** 一个问句所能生成的最大指标数（包括同一指标的不同参数形式） */
    public static final int MAX_COND_COUNT = 25;
    /** 往前排除非交易日时，最大前推天数 */
    public static final int MAX_NON_TRADE_DATE = 30;
    /** 问句的最长长度，超过此长度将不予处理 */
    public static final int MAX_QUERY_LENGTH = 150;
    /** 来自“信息栏”的问句的最长长度，超过此长度将不予处理 */
    public static final int MAX_INFO_QUERY_LENGTH = 8;
    
    public static DateInfoNode DEFAULT_FROM = new DateInfoNode(1980,1,1);
    public static DateInfoNode DEFAULT_TO  = new DateInfoNode(2050,12,31);
    
    public static final int SORT_DEF_K = 50;
    
    public static final char COND_VALUE_PROP_MARK = '_';
    public static final char COND_RANGE_PROP_MARK = '+';
    public static final char COND_N_DAY_PROP_MARK = '%';
    public static final char COND_YEAR_QUARTER_PROP_MARK = '!';
    public static final char COND_YEAR_UNTIL_PROP_MARK = '$';
    public static final char COND_N_YEAR_BEFORE_PROP_MARK = '#';
    public static final char COND_N_YEAR_PROP_MARK = '*';
    
    public static final String OPENING = "开盘价";
    public static final String CLOSING = "收盘价";
    public static final String NEW_PRICE = "最新价";
    public static final String NEW_PRICE_UNIT = "元";
    public static final String NEW_CHANGE = "最新涨跌幅";
    public static final String NEW_CHANGE_UNIT = "%";
    public static final String DDE = "dde大单净额";
    public static final String LATEST_DDE = "最新dde大单净额";
    public static final String ZHULI_ZC_E = "主力增仓额";
    public static final String LATEST_ZHULI_ZC_E = "最新主力增仓额";
    public static final String ZHULI_JLX = "主力净流向";
    public static final String LATEST_ZHULI_JLX = "最新主力净流向";
    public static final String DDE_UNIT = "百万元";
    public static final String VOL = "成交额";
    public static final String VOL_UNIT = "百万元";
    public static final String PE = "市盈率(pe)";
    public static final String PE_UNIT = "倍";
    public static final String NEW_TURNOVER_RATE = "最新换手率";
    public static final String NEW_TURNOVER_RATE_UNIT = "%";
    public static final String NET_PROFITS = "净利润";
    public static final String NET_PROFITS_UNIT = "百万元";    
    public static final String CHENG_JIAO_LIANG = "成交量";
    /**
     * 以下指标默认使用环比增长。
     */
    public static final String PROPORTION_OF_BLOCKHOLDER = "大股东持股比例";
    public static final String SHAREHOLDERS_NUMBER = "股东户数";
    public static final String THE_NUMBER_OF_HOLDINGS_PER_HOUSEHOLD = "户均持股数量";
    public static final String THE_PROPORTION_OF_HOLDINGS_PER_HOUSEHOLD = "户均持股比例";
    public static final String INSTITUTIONAL_OWNERSHIP = "机构持股数量";
    public static final String THE_NUMBER_OF_FUND_HOLDINGS = "基金持股数量";
    public static final String THE_PROPORTION_OF_THE_FUNDS_HOLDINGS ="基金持股比例";
    public static final String THE_BROKERAGE_FINANCIAL_PRODUCTS_THE_NUMBER_OF_SHARES = "券商理财产品持股数量";
    public static final String QFII_NUMBER_OF_HOLDINGS = "qfii持股数量";
    public static final String QFII_PROPORTION_OF_HOLDINGS="qfii持股比例";
    public static final String THE_NUMBER_OF_INSURANCE_COMPANY_HOLDINGS = "保险公司持股数量";
    public static final String THE_NUMBER_OF_SOCIAL_SECURITY_FUND_HOLDINGS = "社保基金持股数量";
    public static final String THE_PROPORTION_OF_SOCIAL_SECURITY_FUND_HOLDINGS = "社保基金持股比例";
    public static final String TRUST_COMPANY_NUMBER_OF_HOLDINGS = "信托公司持股数量";
    
    
    public static final String MAX_USER_SAY = "最大"; 
    public static final String MIN_USER_SAY = "最小"; 
    public static final String TOPK_DECEND_USER_SAY = "排名前";
    public static final String TOPK_DECEND_USER_SAY_RATE = "排名前%s%%";
    public static final String TOPK_NOT_DECEND_USER_SAY = "从小到大排名前"; 
    public static final String TOPK_NOT_DECEND_USER_SAY_RATE = "从小到大排名前%s%%"; 
    public static final String NOT_TOPK_MAXNUM = "第%s";
    public static final String NOT_TOPK_MAXNUM_RATE = "第%s%";
    public static final String NOT_TOPK_MINNUM_RATE = "从小到大第%s%%";
    public static final String NOT_TOPK_MINNUM = "从小到大第%s";
	public static final String LEFT_TAG_STR = "{{";
	public static final String RIGHT_TAG_STR = "}}";
	public static final String BIG_FIRST = "从大到小排名";
	public static final String SMALL_FIRST = "从小到大排名";

	public static final String HBASE_TRADE_DATE = "hbase_trade_date";
	public static final String BLOCK_TRADE = "大宗交易";
	public static final String BLOCK_TRADE_DATE_NAME = "交易日期";
	public static final String BLOCK_TRADE_DATE_GID = "2.1";
    public static final String HBASE_NEW_STKCODE_INNERNAME = "nsStockName";
    public static final String BLOCK_HBASE_COUNT_GID = "2.2";
    
    public static final String COUNT_STR_APPEND = "_次数";

    /** web显示结果的操作 */
    public static final String  WEBSTYLE_HIGH_LIGHT = "HIGH_LIGHT";
    
    /** 技术指标相关词汇*/
    public static final String[] MIN_WORDS = {"分钟", "分"};
    public static final String[] K_LINE_WORDS = {"k线","线","均线","刻线"};
    public static final String[] MIN_MID_WORDS = {"周期"};     
    public static final String TECH_ANALY_PERIOD = "分析周期";
    public static final String MA_TECH_INST_NAME = "均线";
    public static final String MA_PARAM_N_DAY_NAME = "n日";
    public static final String NUM_TO_PERIOD_REGEX = "(\\d+)分";
    public static final String[] HOUR_WORDS = {"小时","小时线"};
    public static final String NUM_60 = "60";
    public static final String MINUTE_WORD = "分钟";
    public static final String[] DAY_WORDS = {"日", "日均线", "日线"};
    public static final String[] WEEK_WORDS = {"周", "周均线", "周线"};
    public static final String[] MONTH_WORDS = {"月", "月均线", "月线"};
    public static final String[] YEAR_WORDS = {"年", "年均线", "年线"};
    
    
    /**
     * zd add on 2012-11-09: 处理"多头排列，空头排列"要用到的字串
     */
    public static final String[] MULTI_PARALLEL_LINES = {"多头排列", "空头排列"};
    
    /**
     * 表头要显示的参数列表
     */
    public static final String PARAM_DATE = "日期";
    public static final String PARAM_FQ = "复权";
    public static final String PARAM_SEASON = "季度";
    public static final String PARAM_YEAR = "年度";
    public static final String PARAM_REPORT_PERIOD = "报告期";
    public static final String PARAM_RECENT_N_DAYS = "近n天内";
    public static final String PARAM_N_DAY_HIGH_PRICE = "创n天以来新";
    
    //分时上下午开盘时间
    /** 分时上午开盘 */
    public static final String AM_OPEN = "9:30:00";
    /** 分时上午收盘 */
    public static final String AM_CLOSE = "11:30:00";
    /** 分时下午开盘 */
    public static final String PM_OPEN = "13:00:00";
    /** 分时下午收盘 */
    public static final String PM_CLOSE = "15:00:00";
    /** 尾盘 */
    public static final String STATE_WP = "15:00:00";
   
    /**
     * 要特殊过滤的参数值
     */
   public static final String NEWEST = "最新";
   
   /**计算小标签的格式*/
   public static final String CALC_FLAG_FORMAT = "[%d]";
   
   
   public static final int MINPERID_FROM_YEAR = 1990;
   public static final String MINPERID_FROM = "0925";
   public static final String MINPERID_TO = "1500";
   /** 分时行情关键字 */
   public static final String TIME_KEY = "_分时";
   /** 区间分时行情关键字 */
   public static final String TIME_INTERVAL_KEY = "_区间分时";
   
   /** 分时行情指标过期时间 */
   public static final String TIME_INDEX_OVER_TIME = "上个交易日";
   public static final String TIME_INDEX_DEF_TIME = "这个交易日";
    
}
