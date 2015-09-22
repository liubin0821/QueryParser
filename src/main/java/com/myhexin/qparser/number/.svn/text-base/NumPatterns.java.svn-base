package com.myhexin.qparser.number;

import java.util.regex.Pattern;

public class NumPatterns {
    // 单位
    static String DISPENSABLE_UNITS = "(%|手|倍|股|档|元|块|块钱|港元|港币|港圆|美元|美金|点|个|种|次|只|份|支|部|部分|家|户|盘|人|岁|位|)";
    static String INDISPENSABLE_UNITS = "(%|手|倍|股|档|元|块|块钱|港元|港币|港圆|美元|美金|点|个|种|次|只|份|支|部|部分|家|户|盘|人|岁|位)";
    // 基本数字
    static Pattern LONG = Pattern.compile("^\\d+?$");
    static Pattern DOUBLE = Pattern.compile("^\\d+?\\.?\\d*?$");

    // 数字单位
    static Pattern NUM_UNITS_RIGHT = Pattern
            .compile("^%|手|股|档|倍|元|块|块钱|港元|港币|港圆|美元|美金|毛|角|分|点|个|种|次|只|份|支|部|部分|家|户|盘|人|岁|位$");
    static Pattern NUM_AND_UNITS_WITHOUT_CHINESE = Pattern
            .compile("^(.*?)(%|手|股|档|倍|元|块|块钱|港元|港币|港圆|美元|美金|毛|角|分|点|个|种|次|只|份|支|部|部分|家|户|盘|人|岁|位)$");
    static Pattern NUM_UNITS_LEFT = Pattern.compile("^百分之$");
    static Pattern NUM_UNITS_RIGHT_TEN_PER = Pattern.compile("^成$");

    static Pattern NUM_WITH_UNIT = Pattern
            .compile("^(.*?)(%|手|股|档|倍|元|块|块钱|港元|港币|港圆|美元|美金|毛|角|分|点|个|种|次|只|份|支|部|部分|家|户|盘|人|岁|位)$");
    static Pattern  THOUSAND_NUM_WITH_UNIT = Pattern
            .compile("^(.*?)([万亿](?:%|手|股|档|倍|元|块|块钱|港元|港币|港圆|美元|美金|毛|角|分|点|个|种|次|只|份|支|部|部分|家|户|盘|人|岁|位))$");
    static Pattern NUM_WITH_MONEY_UNIT = Pattern.compile("^(.*?)(元|圆|块|块钱|毛|角|分)$");
    static Pattern UNIT_FOR_MONEY_1 = Pattern.compile("^元|圆|块|块钱$");
    static Pattern UNIT_FOR_MONEY_2 = Pattern.compile("^毛|角$");
    static Pattern UNIT_FOR_MONEY_3 = Pattern.compile("^分$");
    static Pattern CHINESE_THOUSAND =  Pattern.compile("([千万亿]+)");
    
    /**明确为分时点的单位*/
    static Pattern TIME_HOUR_UNITS_AFTER_NUM = Pattern.compile("^(点钟|小时)$");
    static Pattern TIME_MIN_UNIT_AFTER_NUM = Pattern.compile("^(分|分钟)$");
    static Pattern TIME_SEC_UNIT_AFTER_NUM = Pattern.compile("^(秒|秒钟)$");

    /**分时单位*/
    static Pattern HMS_UNIT = Pattern.compile("^(分钟线|小时线|个?小时|分钟|分)$");
    /**分时数字单位*/
    public static final Pattern NUM_WITH_HMS_UNIT = Pattern.compile("^(\\d+)(分钟线|小时线|个?小时|分钟|分)$");
    


    // 日期单位
    static Pattern DATE_UNITS = Pattern
            .compile("^小时|分钟|天|日|号|个?交易日|周|个?星期|个?礼拜|个?月|月份|季|个?季度|季报|年|个?年度|年前$");
    static Pattern DATE_UNITS_AFTER_NUM = Pattern.compile("^日|号|日内$");
    static Pattern UNITS_AFTER_DECIMALS = Pattern.compile("^日|号$");//小数后面的日期单位 如 5.27日

    // 以下为需从数字中分离的，年月日均有的时间
    static Pattern DATE_NEED_SEPARATE_1 = Pattern
            .compile("^(199\\d|200\\d|201\\d)[\\-\\./、—](0?[1-9]|1[0-2])[\\-\\./、—](0?[1-9]|[12]\\d|3[0-1])$");
    static Pattern DATE_NEED_SEPARATE_2 = Pattern
            .compile("^(199\\d|200\\d|201\\d)(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[0-1])$");
    static Pattern DATE_NEED_SEPARATE_3 = Pattern
            .compile("^([109]\\d)[\\-\\./、—](0?[1-9]|1[0-2])[\\-\\./、—](0?[1-9]|[12]\\d|3[0-1])$");
    // 跟星期相关
    static Pattern DATE_WEEK_SIGN_1 = Pattern.compile("^本周|上周|下周$");
    static Pattern DATE_WEEK_SIGN_2 = Pattern.compile("^[1-6]|末|日$");

    // 以下为需从数字中分离的，只有年份的时间
    static Pattern DATE_YEAR_1 = Pattern.compile("^200\\d|201\\d|199\\d$");
    static Pattern DATE_YEAR_2 = Pattern.compile("^0\\d$");
    
    //以下为需从数字中分离的，范围型的时间
    static Pattern DATE_NEED_SEPARATE_RANGE_1 = Pattern
            .compile("^(199\\d|200\\d|201\\d)(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[0-1])"
                    + "[\\-、到至和]"
                    + "(199\\d|200\\d|201\\d)(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[0-1])$");
    static Pattern DATE_NEED_SEPARATE_RANGE_2 = Pattern
            .compile("^(199\\d|200\\d|201\\d)[\\-\\./、—](0?[1-9]|1[0-2])[\\-\\./、—](0?[1-9]|[12]\\d|3[0-1])[\\-、到至](199\\d|200\\d|201\\d)[\\-\\./、—](0?[1-9]|1[0-2])[\\-\\./、—](0?[1-9]|[12]\\d|3[0-1])$");
    static Pattern DATE_NEED_SEPARATE_RANGE_3 = Pattern
            .compile("^(199\\d|200\\d|201\\d)[\\-、和到至](199\\d|200\\d|201\\d)$");

    //需在数字解析处特殊处理的日期
    static Pattern ONLY_YEAR = Pattern.compile("^198\\d|199\\d|200\\d|201\\d$");
    static Pattern ONLY_MONTH = Pattern.compile("^0?[1-9]|1[0-2]$");
    static Pattern ONLY_MONTH_WITH_UNIT = Pattern.compile("^(0?[1-9]|1[0-2])月$");
    static Pattern ONLY_DAY = Pattern.compile("^0?[1-9]|[12]\\d|3[01]$");

    // 中文数字
    static Pattern CHINESE_NUM = Pattern
            .compile("^[\\d零一两二三四五六七八九十百千万亿仟佰拾玖捌柒陆伍肆叁贰壹]+?$");
    static Pattern DOUBLE_WITH_CHINESE_NUM = Pattern
            .compile("(\\d+?\\.?\\d*?)([零一两二三四五六七八九十百千万亿仟佰拾玖捌柒陆伍肆叁贰壹]+?)$");
    static Pattern CHINESE_DATE_WITH_UNIT = Pattern
            .compile("^([\\d零一两二三四五六七八九十百千万亿仟佰拾玖捌柒陆伍肆叁贰壹]+?)(天|日|号|个?交易日|周|个?星期|个?礼拜|个?月|月份|季|个?季度|季报|年|个?年度|年前)$");
    static Pattern CHINESE_NUM_WITH_UNIT = Pattern
            .compile("^([\\d零一两二三四五六七八九十百千万亿仟佰拾玖捌柒陆伍肆叁贰壹]+?)(%|手|股|档|倍|元|块|块钱|港元|港币|港圆|美元|美金|点|毛|角|分|个|种|次|只|份|支|部|部分|盘|家|户|人|岁|位)$");
    static Pattern CHINESE_NUM_TYPE_SORT = Pattern
            .compile("^(前|后|第)([一两二三四五六七八九十百千万亿仟佰拾玖捌柒陆伍肆叁贰壹]+?)$");

    //连接符 为了匹配“2010/01/10”这种时间形式，“/”还必须做整合时的连接符
    static Pattern COMBINATOR = Pattern
            .compile("^(、|－|~|/|\\-\\-|\\-|\\—\\—|\\—|\\.)$");

    //比较符
    static Pattern COMPARISON_LEFT = Pattern.compile("^<|>|=|<=|>=|小于|大于|小于等于|大于等于|$");
    static Pattern COMPARISON_RIGHT = Pattern.compile("^上|以上|以下|以内|内|之上|之下$");

    // 数值范围 在数字范围中，“/”不再做连接符
    static Pattern NUM_WITH_COMPARE = Pattern.compile("^(!=|>=|>|<=|<|[大小等]于|[大小]于等于)(.+?)$");
    static Pattern NUM_REGULAR = Pattern
            .compile("^(\\-?\\d+?\\.?\\d*?)(分|手|倍|股|档|点|元|港元|港币|港圆|美元|美金|个|种|次|只|份|支|部|部分|家|户|盘|%|人|岁|位|)$");
    static Pattern NUM_WITH_OP = Pattern
            .compile("^([1-9]\\d*?)([^\\d]*?)/([1-9]\\d*?)([^\\d]*?)$");
    static Pattern NUM_RANGE = Pattern
    .compile("^[从自]?(.+?)(?:、|,|~|至|到|和|与|\\—\\—|\\—|\\-\\-|\\-|－)(.+?)$");
   //中文数字
    
	// 两个时间之间的连词
	static Pattern NUM_CONNECT_NUM_MARK = Pattern
			.compile("~|至|到|和|与|\\—\\—|\\—|\\-\\-|\\-|－");
    
    static Pattern NUM_REGULAR_CHINESE_WITH_DOUBLE_HEAD = Pattern
    .compile("^(\\-?\\d+?\\.?\\d*?)([零一两二三四五六七八九十百千万亿仟佰拾玖捌柒陆伍肆叁贰壹]+)(手|倍|股|档|点|元|个|种|次|只|份|支|部|部分|家|户|盘|%|人|岁|位|)$");
    static Pattern NUM_REGULAR_CHINESE = Pattern
    .compile("^([\\d零一两二三四五六七八九十百千万亿仟佰拾玖捌柒陆伍肆叁贰壹]+?)(手|倍|股|档|点|元|港元|港币|港圆|美元|美金|个|种|次|只|份|支|部|部分|家|户|盘|%|人|岁|位|)$");
    static Pattern NUM_RANGE_CHINESE = Pattern.compile("^[从自]?"
            + "(\\-?\\d+\\.?\\d*?)([零一两二三四五六七八九十百千万亿仟佰拾玖捌柒陆伍肆叁贰壹]*?)"
            + "(手|倍|股|档|元|块|块钱|港元|港币|港圆|美元|美金|点|个|种|次|只|份|支|部|部分|家|户|盘|%|人|岁|位|)"
            + "(?:、|,|~|至|到|和|与|\\—\\—|\\—|\\-\\-|\\-|－)"
            + "(\\-?\\d+\\.?\\d*?)([零一两二三四五六七八九十百千万亿仟佰拾玖捌柒陆伍肆叁贰壹]+)"
            + "(手|倍|股|档|元|块|块钱|港元|港币|港圆|美元|美金|点|个|种|次|只|份|支|部|部分|家|户|盘|%|人|岁|位|)$");
    //金钱的表示
    static Pattern NUM_MONEY = Pattern.compile("^(\\d+?)[元圆块](\\d+?)[毛角](\\d+?)分$");// 4元3角1分
    static Pattern NUM_MONEY_1 = Pattern.compile("(\\d+?)[元圆块](\\d+?[毛角]|\\d*?)$");// 4块3毛
    static Pattern NUM_MONEY_2 = Pattern.compile("(\\d+?)[毛角](\\d+?分|\\d*?)");

    // 两个数字可按AND合并的中间字符
    static Pattern NUM_AND_SIGN = Pattern.compile("^[,，、;；\\s]$");
    // 连续出现需去重
    static Pattern BLANK = Pattern.compile("^\\s$");
    static Pattern SINGLE_HORIZONTAL = Pattern.compile("^\\-|－$");
    // 浮动标志词
    static Pattern MOVABLE_LEFT = Pattern.compile("^大约|大概|大致$");
    static Pattern MOVABLE_RIGHT = Pattern.compile("^左右|上下$");
    static Pattern MOVABLE_UP_RIGHT = Pattern.compile("^多$");
    static Pattern MOVABLE_DOWN_LEFT = Pattern.compile("^近$");
    //数字加比较次
    static Pattern NUM_AROUND = Pattern.compile("^(.*)(左右|上下)$");
    static Pattern NUM_MORE = Pattern.compile("^(.*)多$");
    // 数字附近的冗余词
    static Pattern SHOULD_REMOVE_LEFT = Pattern.compile("^在$");
    static Pattern SHOULD_REMOVE_RIGHT = Pattern.compile("^之间$");
    // 数字间的特殊操作词
    static Pattern OP_DIV_BETWEEN_NUM_RIGHT_ON_LEFT= Pattern.compile("^分之$");
    //分数表示
    static Pattern OP_FRACTION_BETWEEN_NUM_RIGHT_ON_LEFT= Pattern.compile("^/$");
    
    static Pattern NEGATIVE_SIGN= Pattern.compile("^-|负$");
}
