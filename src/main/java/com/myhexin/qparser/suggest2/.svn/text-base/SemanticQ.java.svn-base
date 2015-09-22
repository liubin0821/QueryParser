package com.myhexin.qparser.suggest2;

import java.util.ArrayList;

public class SemanticQ {
    //当前系统的换行符
    private static final String LS = System.getProperty("line.separator");
    public String rawQueryStr_;//最原始的用户的问句
    public ArrayList<String> singleWordSegList_;//单字分词列表
    public ArrayList<String> fineGritTemplate_;//细粒度模板，完全是分词结果
    public ArrayList<String> midGritTemplate_;//中等力度模板，分词结果和句式混合
    public ArrayList<String> coarseGrainedTemplate_;//粗粒度模板，完全有句式组成
    public ArrayList<String> indexList_;//指标列表
    /**
     * 存放每一个datanode和每一个numnode的信息
     * 日期的格式:date_unit|年_index|0_isSequence|true_mayTrade|false_fromto|2012020208|2012030412_expandfromto|2012020707|2012040814
     * 数字的格式:num_unit|户_index|1_isBetween|false_needMove|false_moveType|both_range|-0.2212|123132
     */
    public ArrayList<String> dataAndNumInfoList_;
    
    /**包含股票名称或代码的时候，存放股票名称或代码*/
    public ArrayList<String> stockInfo_;
    public boolean qpusable_;//该问句解析是否有结果
    public boolean hasAnswer_;//该问句查询是否有答案
    public int appearCount_;//问句被问到的次数
    public boolean onlyStock_;//是否问句中仅包含股票名或股票代码
    public boolean containStock_;//是否问句中包含股票名或股票代码
    public String treeLog_;//用来存放生成的treeLog 
    
    public SemanticQ(){
        rawQueryStr_ = "";
        singleWordSegList_ = new ArrayList<String>();
        fineGritTemplate_ = new ArrayList<String>();
        midGritTemplate_ = new ArrayList<String>();
        coarseGrainedTemplate_ = new ArrayList<String>();
        indexList_ = new ArrayList<String>();
        stockInfo_ = new ArrayList<String>();
        dataAndNumInfoList_ = new ArrayList<String>();
        
        qpusable_ = true;
        hasAnswer_ = true;
        appearCount_ = 0;
        onlyStock_ = false;
        containStock_ = false;
    }
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("rawQueryStr:[");sb.append(rawQueryStr_);sb.append("]").append(LS);
        sb.append("singleWordSegList:").append(LS);
        for( String str : singleWordSegList_ ){
            sb.append( "[" );sb.append(str);sb.append("]"+LS);
        }
        sb.append("fineGritTemplate:").append(LS);
        for( String str : fineGritTemplate_ ){
            sb.append( "[" );sb.append(str);sb.append("]"+LS);
        }
        sb.append("midGritTemplate:").append(LS);
        for( String str : midGritTemplate_ ){
            sb.append( "[" );sb.append(str);sb.append("]"+LS);
        }
        sb.append("coarseGrainedTemplate:").append(LS);
        for( String str : coarseGrainedTemplate_ ){
            sb.append( "[" );sb.append(str);sb.append("]"+LS);
        }
        sb.append("indexList:").append(LS);
        for( String str : indexList_ ){
            sb.append( "[" );sb.append(str);sb.append("]"+LS);
        }
        sb.append("dataAndNumInfoList_");
        for( String str : dataAndNumInfoList_ ){
            sb.append( "[" );sb.append(str);sb.append("]"+LS);
        }
        sb.append("usable:[");sb.append(qpusable_ == true? "true" : "false");sb.append("]").append(LS);
        sb.append("hasAnswer:[");sb.append(hasAnswer_ == true? "true" : "false");sb.append("]").append(LS);
        sb.append("appearCount:[");sb.append( Integer.toString(appearCount_) );sb.append("]").append(LS);
        sb.append("onlyStock:[");sb.append(onlyStock_ == true? "true" : "false");sb.append("]").append(LS);
        sb.append("containStock:[");sb.append(containStock_ == true? "true" : "false");sb.append("]").append(LS);
        sb.append("stockInfo:");sb.append(LS);
        for( String str : stockInfo_ ){
            sb.append("[");sb.append(str);sb.append("]"+LS);
        }
        sb.append("treeLog:\n");sb.append(treeLog_);sb.append(LS);
        return sb.toString();
    }
}
