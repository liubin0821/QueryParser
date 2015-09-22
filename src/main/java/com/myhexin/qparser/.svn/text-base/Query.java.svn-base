package com.myhexin.qparser;

import java.util.ArrayList;
import java.util.HashMap;

import com.myhexin.qparser.node.SemanticNode;

public class Query {
	private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(Query.class.getName());
    public Query(String text){
        qid_ = makeQID_();
        this.text = text.trim().replaceAll("\\s+", " ");
        parseRlt_ = new ParseResult();
        parseLog_ = new ParseLog(this);
        //this.parseText = 
    }
    
    public Query(String text, String type){
        qid_ = makeQID_();
        this.text = text.trim().replaceAll("\\s+", " ");;
        setType(type);
        parseRlt_ = new ParseResult();
        parseLog_ = new ParseLog(this);
        // this.parseText = 
    }
    
    /**
     * 获取问句的解析结果
     * @return 问句解析结果
     * @see ParseResult
     */
    public ParseResult getParseResult() {
        return parseRlt_;
    }
    
    /**
     * 获取记录解析日志的对象
     * @return 日志对象
     * @see ParseLog
     */    
    public ParseLog getLog() {
        return parseLog_;
    }

    /**
     * 获取所有的日志信息
     * @param delimeter 各类信息之间的分隔符
     * @return 包括所有日志信息的字符串
     * @see ParseLog#toString(String)
     */
    public String getLog(String delimeter) {
        return parseLog_.toString(delimeter);
    }

    /**
     * 判断解析过程是否出现导致解析必须终止的错误，依据是{@link ParseLog#LOG_ERROR}
     * 类的日志是否被记录过。
     * @return 是否有致命错误
     */
    public boolean hasFatalError() {
        return parseLog_.getMsg(ParseLog.LOG_ERROR).length() > 0;
    }
    
    
    /**
     * 在本query解析失败或没有查询结果时，使用某些关键词进行新闻搜索
     * @return 用于搜索的关键词
     */
    public String getSearchKeyword() {
        return "";
    }
    
    /**
     * 获取问句的唯一标识。问句的标识是该问句被构造时系统的毫秒数
     * @return 问句唯一标识
     * @see System#currentTimeMillis()
     */
    public String getQID() {
        return qid_;
    }
    
    /**
     * 设置特定于问句的参数
     * @param name 参数名，取值必须是{@link Query#WEB_DATA}，{@link Query#DISABLE_CACHE}
     * 之一，否则将被忽略
     * @param value 参数值
     */
    public void setQueryParam(String name, String value) {
        if(name.equals(DISABLE_CACHE) || name.equals(WEB_DATA)
                || name.equals(QUERY_SRC)) {
            queryParam_.put(name, value);
        } else {
            logger_.warn("参数[{}]不识别，已被忽略", name);
        }
    }
    
    /**
     * 获取特定于问句的某个参数
     * @param name 参数名，取值必须是{@link Query#WEB_DATA}，{@link Query#DISABLE_CACHE}
     * 之一，否则将运行时抛出<code>NullPointerException</code>
     * @return 参数值
     */
    public String getQueryParam(String name) {
        return queryParam_.get(name);
    }
    
    /**
     * 对本问句的数据查询是否禁用cache
     * @return 是否禁用cache
     */
    public boolean isCacheDisabled() {
        String pCache = getQueryParam(Query.DISABLE_CACHE);
        return pCache != null && pCache.equals("true");
    }
    
    /**
     * 此问句是否来自信息栏
     * @return 是否来自信息栏
     */
    public boolean isFromInfoChannel() {
        return SRC_INFO.equals(getQueryParam(QUERY_SRC));
    }
    
    /**
     * 此问句是否来手机端
     * @return 是否来自手机端
     */
    public boolean isFromMobile(){
        return SRC_MOBILE.equals(getQueryParam(QUERY_SRC));
    }
    
    
    
    /**
     * 获取解析错误信息，是<code>getLog().getMsg(ParseLog.LOG_ERROR)</code>的方便包装
     * @return 解析错误信息
     */
    public String getErrorMsg() {
        return parseLog_.getMsg(ParseLog.LOG_ERROR);
    }

    
    /**
     * 生成一个问句唯一编号即QID，是16位数字。算法如下：<br>
     * 1) {@link System#currentTimeMillis}取当前毫秒时间，在相当长时间内，是一个13位整数，
     * 用{@link #QID_MAGIC_}减去此时间并转换成字符串<br>
     * 2) {@link System#nanoTime}取当前纳秒时间（至少有4位数），转换成字符串
     * 3) 将毫秒的后12位与纳秒的后3位拼接起来，最前面再补0，即获得16位的QID。
     * @return 问句编号QID
     */
    private static String makeQID_() {
        String reverseMs = String.valueOf(QID_MAGIC_ - System.currentTimeMillis());
        String ns = String.valueOf(System.nanoTime());
        StringBuilder sb = new StringBuilder();
        sb.append('0');
        sb.append(reverseMs, reverseMs.length()-12, reverseMs.length());
        sb.append(ns, ns.length()-4, ns.length());
        return sb.toString();
    }

    public void setType(Query.Type type) {
    	if (type != null)
    		this.type_ = type;
    	else
    		this.type_ = Type.ALL;
    }
    
	public void setType(String type) {
		if (type == null) {
			type_ = Type.ALL;
			return;
		}

		type = type.toUpperCase();
		try {
			type_ = Type.valueOf(type);
		} catch (Exception e) {
			logger_.error("unknown type: ", type_);
			type_ = Type.ALL;
		}
		// 主搜索的域search因为涉及所有领域，QUERY暂时当作all处理
		if (type_ == Type.SEARCH)
			type_ = Type.ALL;
	}

    public Query.Type getType() {
        return type_;
    }

    /*public void setQueryNode(QueryNodes node) {
        this.node = node;
    }*/

    public QueryNodes getQueryNode() {
        return null;
    }

    public String getDomain(){
    	return this.domain;
    }
    public void setDomain(String domain) {
    	this.domain = domain;
    }
    
    public ArrayList<SemanticNode> getNodes() {
        return null;
    }
    
    
    /** 问句唯一编号 */
    private String qid_;
    /** 用户提供的问句内容字串 */
    public String text;
    
    /** 返回给{@link QueryParser#parse}的调用者的 最终解析结果 */
    private ParseResult parseRlt_ = null;
    /** 存储解析过程中产生的所有日志信息 */
    private ParseLog parseLog_;
    /** 特定于问句的参数。问句类型*/
    private Type type_ = Type.ALL;
    
    //处理跨领域时指定领域
    private String domain;

    /** 问句相关的参数 到值的映射*/
    private HashMap<String, String> queryParam_ = new HashMap<String, String>();
    /** 特定于问句的参数。是否禁用cache，默认为false，即使用 */
    public static final String DISABLE_CACHE = "disable_cache";
    /** 特定于问句的参数。来自用户界面的信息 */
    public static final String WEB_DATA = "web_data";
    /** 特定于问句的参数。问句来源 */
    public static final String QUERY_SRC = "query_src";
    /** {@link QUERY_SRC}的值之一，表明问句来自“信息频道” */
    public static final String SRC_INFO = "info";
    public static final String SRC_MOBILE = "mobile";
    
    public static enum Param {
        DISABLE_CACHE,
        WEB_DATA,
        QUERY_SRC,
        QUERY_TYPE
    }
    
    public static enum Type{
    	ALL, 
        STOCK,
        HKSTOCK,
        FUND,
        HGHY,
        SEARCH // 主搜索的域search因为涉及所有领域，QUERY暂时当作all处理
    	/*
    	ALL,
        STOCK,
        HKSTOCK,
        FUND,
        FINA_PROD, 
    	TRUST, 
    	SEARCH,

    	// 以下为新增领域，板块领域现默认按照行业领域进行解析
    	INDUSTRY, // 行业
    	CONCEPT, // 概念
    	REGION, // 地域
    	SECTOR, // 板块
    	PERSON, // 人物
    	BOND, // 债券
    	REPORT, //研报
    	FUND_MANAGER, //基金经理
    	*/
    }
    
    private static long QID_MAGIC_ = 100000000000000L;
}
