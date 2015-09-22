package com.myhexin.qparser;

import java.util.ArrayList;
import java.util.HashMap;

import com.myhexin.qparser.interaction.CmpValInteract;
import com.myhexin.qparser.util.Pair;
import com.myhexin.qparser.util.StrStrPair;
import com.myhexin.qparser.util.Util;

/**
 * 记录问句解析过程中产生的各种信息，这些信息包含调试信息，关于我们如何
 * 理解问句的信息，解析出错信息，对用户的建议信息等
 */
public class ParseLog {
    private Query query_ = null;
    private static final String ITEM_DELIM = " ";
    private static final String LOG_QUERY = "[QUERY]";
    private static final String LOG_SKIP = "[SKIP]";
    private static final String LOG_TRANS = "[TRANS]";
    //private static final String LOG_REL_QUERY = "[RELATED]";
    public static final String LOG_TREE = "[TREE]";
    public static final String LOG_XML_CLT = "[XML-CLT]";
    public static final String LOG_XML_SRV = "[XML-SRV]";
    public static final String LOG_JSON_WEB = "[JSON-WEB]";
    public static final String LOG_WARN = "[WARN]";
    public static final String LOG_ERROR = "[ERROR]";
    /** 显示给用户的提示信息 */
    public static final String LOG_TIP = "[TIP]";
    /** 经过各种转换后最终用于解析的问句 */
    public static final String TIP_PARSE_TEXT = "[PARSE-TEXT]";
    /** 处理本问句所使用到的pattern */
    public static final String LOG_PATTERN = "[PATTERN]";
    public static final String LOG_REGEX = "[REGEX]";
    public static final String LOG_CMP_VALS = "[CMP-VALS]";
    
    private HashMap<String, StringBuilder> strInfo_ =
        new HashMap<String, StringBuilder>();
    //skip wods还是应该保留，其用来存储及时认识也要忽略的词
    private ArrayList<String> skippedWords_ =
        new ArrayList<String>();
    //unknownWords用来 存储无论从系统中任何地方都未曾出现过的词
    private ArrayList<String> unknownWords_ =
        new ArrayList<String>();
    private ArrayList<StrStrPair> transedWords_ =
        new ArrayList<StrStrPair>();
    private ArrayList<Pair<String, ArrayList<String>>> alterWords_ =
        new ArrayList<Pair<String, ArrayList<String>>>();
    private ArrayList<CmpValInteract> cmpValInter_ =
        new ArrayList<CmpValInteract>();
    
    public ParseLog(Query query) {
        query_ = query;
        strInfo_.put(LOG_TREE, new StringBuilder());
        strInfo_.put(LOG_XML_CLT, new StringBuilder());
        strInfo_.put(LOG_XML_SRV, new StringBuilder());
        strInfo_.put(LOG_WARN, new StringBuilder());
        strInfo_.put(LOG_ERROR, new StringBuilder());
        strInfo_.put(LOG_TIP, new StringBuilder());
        strInfo_.put(TIP_PARSE_TEXT, new StringBuilder());
        strInfo_.put(LOG_JSON_WEB, new StringBuilder());
        strInfo_.put(LOG_PATTERN, new StringBuilder());
        strInfo_.put(LOG_REGEX, new StringBuilder());
    }
    
    /**
     * 获取以字符串记录的日志
     * @param logType 日志类型，取值必须是{@link ParseLog}所定义的静态
     * {@link String}常量，否则将运行时抛出{@link NullPointerException}
     * @return 对应类型的日志，若无则返回<code>String("")</code>
     * @exception NullPointerException
     */
    public String getMsg(String logType) {
        return strInfo_.get(logType).toString();
    }
    
    /**
     * 记录字符串型的日志
     * @param logType 日志类型，取值必须是{@link ParseLog}所定义
     * 的静态String常量，否则将运行时抛出{@link NullPointerException}
     * @param msg 日志内容
     * @exception NullPointerException
     */
    public void logMsg(String logType, String msg) {
        StringBuilder sb = strInfo_.get(logType);
        if(sb.length() > 0) { sb.append(ITEM_DELIM); }
        strInfo_.get(logType).append(msg);
    }
    
    public void clearMsg(String logType) {
        strInfo_.get(logType).setLength(0);
    }
    
    /**
     * 记录字符串型的日志
     * @param logType 日志类型，取值必须是{@link ParseLog}所定义
     * 的静态String常量，否则将运行时抛出{@link NullPointerException}
     * @param fmt 格式化字符串
     * @param args 日志内容
     * @exception NullPointerException
     * @see String#format(String format, Object... args)
     */
    public void logMsg(String logType, String fmt, Object... args) {
        logMsg(logType, String.format(fmt, args));
    }
    
    /**
     * 清空某种日志的内控
     * @param logType 日志类型，取值必须是{@link ParseLog}所定义
     */
    public void releaseMsg(String logType){
        strInfo_.put(logType, new StringBuilder());
    }
    
    /**
     * 记录解析时发生的同义词转换
     * @param from 转换前的词
     * @param to 转换后的词
     */
    public void logTransWord(String from, String to) {
        transedWords_.add(new StrStrPair(from, to));
    }
    
    /**
     * 记录问句中所不认识的词
     * @param word 不认识的词
     * 应该仅在合并所有unknown的时候调用，防止重复添加
     */
    public void logUnknownWord(String word) {
        unknownWords_.add(word);
    }
    
    /**
     * 记录问句中已经认识但认为可以忽略的词，去掉此类词不影响句子意思
     * @param word 可忽略的词
     */
    public void logSkipWord(String word) {
        skippedWords_.add(word);
    }
    
    /**
     * 记录问句中与某指标同类型的一组指标，当用户查询此指标时，很可能也
     * 关心同组内其他指标。
     * @param word 问句中用户所问的指标
     * @param alters 与<code>word</code>同类型的指标
     */
    public void logAlterWord(String word, ArrayList<String> alters) {
        alterWords_.add(
                new Pair<String, ArrayList<String>>(word, alters));
    }
    
    
    /**
     * 记录问句中比较值的交互信息。
     * @param cmpVal 单个比较值信息。
     */
    public void logCmpVals(CmpValInteract cmpVal) {
        cmpValInter_.add(cmpVal);
    }
    
    /**
     * 获取问句中所有比较值的交互信息。
     * @return  问句中所有比较值的交互信息。
     */
    public ArrayList<CmpValInteract> getCmpVals() {
        return cmpValInter_;
    }
    
    /**
     * 获取所有(转换前,转换后)组成的词列表
     * @return (转换前,转换后)组成的词列表
     */
    public ArrayList<StrStrPair> getTransWords(){
        return transedWords_;
    }
    
    /**
     * 获取所有被忽略的词列表
     * @return 所有被忽略的词
     */
    public ArrayList<String> getSkipWords(){
        return skippedWords_;
    }

    /**
     * 获取所有不认识的词列表
     * @return 所有不认识的词
     */
    public ArrayList<String> getUnknownWord(){
        return unknownWords_;
    }
    
    /**
     * 获取所有(指标,同类型指标)组成的二元对列表
     * @return 指标与其同类型指标列表
     */
    public ArrayList<Pair<String, ArrayList<String>>> getAlterWords(){
        return alterWords_;
    }
    
    public String toString() {
        return toString("\n");
    }
    
    public String toString(String delimeter) {
        StringBuilder sb = new StringBuilder();
        sb.append("qid=").append(query_.getQID());
        sb.append(delimeter);
        sb.append(LOG_QUERY).append('=').append(query_.text);
        sb.append(delimeter);
        if(skippedWords_.size() > 0){
            sb.append(LOG_SKIP).append('=').append(
                    Util.joinStr(skippedWords_, ITEM_DELIM));
            sb.append(delimeter);
        }
        if(transedWords_.size() > 0){
            sb.append(LOG_TRANS).append('=');
            for(StrStrPair trans : transedWords_) {
                sb.append(trans.first).append('≌')
                    .append(trans.second).append(ITEM_DELIM);
            }
            sb.append(delimeter);
        }
        if(cmpValInter_.size() > 0){
             sb.append(LOG_CMP_VALS).append('=');
             for(CmpValInteract cmpVal : cmpValInter_){
                 sb.append(cmpVal.toString()).append("\n");
             }
         }

        if(strInfo_.get(LOG_WARN).length() > 0){
            sb.append(LOG_WARN).append('=').append(strInfo_.get(LOG_WARN));
            sb.append(delimeter);
        }
        if(strInfo_.get(LOG_ERROR).length() > 0){
            sb.append(LOG_ERROR).append('=').append(strInfo_.get(LOG_ERROR));
            sb.append(delimeter);
        }
        if(strInfo_.get(LOG_TIP).length() > 0){
            sb.append(LOG_TIP).append('=').append(strInfo_.get(LOG_TIP));
            sb.append(delimeter);
        }
        sb.append(LOG_TREE).append("=\n").append(strInfo_.get(LOG_TREE));
        sb.append(delimeter);
        if(strInfo_.get(LOG_XML_CLT).length() > 0) {
            sb.append(LOG_XML_CLT).append("=\n").append(strInfo_.get(LOG_XML_CLT));
            sb.append(delimeter);
        }
        if(strInfo_.get(LOG_XML_SRV).length() > 0) {
            sb.append(LOG_XML_SRV).append("=\n").append(strInfo_.get(LOG_XML_SRV));
            sb.append(delimeter);
        }
        if(strInfo_.get(LOG_JSON_WEB).length() > 0) {
            sb.append(LOG_JSON_WEB).append("=\n").append(strInfo_.get(LOG_JSON_WEB));
            sb.append(delimeter);
        }
        if(strInfo_.get(LOG_PATTERN).length() > 0) {
            sb.append(LOG_PATTERN).append("=\n").append(strInfo_.get(LOG_PATTERN));
            sb.append(delimeter);
        }
        if(strInfo_.get(LOG_REGEX).length() > 0) {
            sb.append(LOG_REGEX).append("=\n").append(strInfo_.get(LOG_REGEX));
            sb.append(delimeter);
        }
        return sb.toString();
    }

    public CmpValInteract getCmpValsByIndexName(String oldText) {
        if (cmpValInter_ == null) {
            return null;
        }
        for (CmpValInteract cmp : cmpValInter_) {
            if (cmp.indexName.equals(oldText)) {
                return cmp;
            }
        }
        return null;
    }

}
