package com.myhexin.qparser.time.parse;

import java.util.regex.Pattern;

public class TimePatterns {
    
    //以下为现支持的时间形式
    //确定时间点
    public static final Pattern HMS = Pattern
            .compile("^(上午|下午|)([01]?\\d|2[0-3])(?:\\:|点)([0-5]?\\d)(?:\\:|分)([0-5]?\\d)秒?$");
    
    public static final Pattern HM_1 = Pattern
    .compile("^(上午|下午|)([01]?\\d|2[0-3])(?:\\:|点)([0-5]?\\d)(分钟|分|)$");
    public static final Pattern HM_2 = Pattern
    .compile("^(上午|下午|)([01]?\\d|2[0-3])点半$");

    public static final Pattern ONLY_HOUR_WITH_AM_OR_PM = Pattern
    .compile("^(上午|下午|)([01]?\\d|2[0-3])(点钟|点)$");
    //长度型时间
    public static final Pattern LENGTH_TYPE_RANGE_1 = Pattern
    .compile("^(?:最近|过去|连续|以往|过往|前|之前|近|)(\\d{1,3}|半)(个?小时|分钟|分)(?:内|以内|以来|)$");
    public static final Pattern LENGTH_TYPE_RANGE_2 = Pattern
    .compile("^(\\d{1,3}|半)(个?小时|分钟|分)(以前|之前|前|以后|之后|后)$");
    public static final Pattern LENGTH_TYPE_RANGE_3 = Pattern
    .compile("^(上午|下午|)(开盘|收盘|尾盘)(以前|之前|前|以后|之后|后)(\\d{1,3}|半)(个?小时|分钟|分)(内|以内|以来|)$");
    public static final Pattern LENGTH_TYPE_RANGE_4 = Pattern
    .compile("^(上午|下午|)(开盘|收盘|尾盘)(\\d{1,3}|半)(个?小时|分钟|分)(以前|之前|前|以后|之后|后)$");
    
    public static final Pattern LENGTH_TYPE_RANGE_5 = Pattern
    .compile("^(上午|下午)(前|后)(\\d{1,3}|半)(个?小时|分钟|分)(内|以内|以来|)$");
    
    public static final Pattern LENGTH_TYPE_RANGE_6 = Pattern
    .compile("^(上午|下午|)(开盘|收盘|尾盘)(\\d{1,3}|半)(个?小时|分钟|分)(内|以内|以来|)$");
    
    public static final Pattern AM_OR_PM_OPEN_OR_CLOSE = Pattern
    .compile("^(上午|下午|)(开盘|收盘|尾盘|)$");
    
    // 泛化的日期范围
    public static final Pattern FROM_ONE_TIME_TO_ANTHER_TIME = Pattern
            .compile("^(?:从|自|自从|)(.+)(?:与|至|到|~|\\-|、|—|和)(.+?)(?:为止|以来|)$");
    public static final Pattern BEFORE_AND_AFTER = Pattern
    .compile("^(.+?)([以之]?前|[以之]?后)(.+?)([以之]?前|[以之]?后)$");
    
    public static final Pattern IN_ONE_TIME_AFTER = Pattern
            .compile("^(.+?)(?:后|以后|之后)$");
    public static final Pattern IN_ONE_TIME_BEFORE = Pattern
            .compile("^(.+?)(?:前|以前|之前)$");
    
    
    //以下为辅助pattern，不做解析时间范围用
    public static final Pattern AM_OR_PM = Pattern
    .compile("^(上|下)午$");
    public static final Pattern OPEN_OR_CLOSE = Pattern
    .compile("^(开|收)盘$");
    public static final Pattern STATES = Pattern
    .compile("^(盘中|盘前|尾盘)$");
    
    public static final Pattern HOUR_NUM = Pattern
    .compile("^([01]?\\d|2[0-3])$");
    public static final Pattern MIN_SEC_NUM = Pattern
    .compile("^[0-5]?\\d$");
    
    public static final Pattern CAN_BE_TECH_MIN = Pattern
    .compile("^(60|30|15|5|1)(分钟|分)$");
    public static final Pattern ONLY_MIN_WITH_UNIT = Pattern
    .compile("^([0-5]?\\d)(分钟?)$");
    
    public static final Pattern LENGTH_HOUR = Pattern
    .compile("^(\\d+|半)(个?小时)$");
    
    public static final Pattern LENGTH_MIN = Pattern
    .compile("^(\\d+)(分钟?)$");
    
    public static final Pattern LENGTH_SEC = Pattern
    	    .compile("^(\\d+)(秒钟?)$");
    
    public static final Pattern ONLY_HOUR = Pattern
    .compile("^([01]?\\d|2[0-3])(点钟|点)$");
    
	public static final Pattern ONLY_MIN = Pattern.compile("^([0-5]?\\d)(分|)$");
    
    public static final Pattern LENGTH_TYPE_RANGE_NEED_BEDECK = Pattern
    .compile("^(以前|之前|前|以后|之后|后)(\\d{1,3}|半)(个?小时|分钟|分)(内|以内|以来|)$");
    
}
