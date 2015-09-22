package com.myhexin.qparser.suggest2;

import java.util.ArrayList;

import com.myhexin.qparser.define.MiscDef;

public class SuggestInfo {
    //当前系统的换行符
    private static final String LS = System.getProperty("line.separator");
    public static final String STOCK_INFO_SPL = MiscDef.STR_OR;
    /**此次SuggestInfo传递的目的
    *"q2s_alt":问句解析出错的情况，需要推荐问句服务给出备选问句推荐
    *"q2s_rel":问句解析正确的情况，需要推荐问句服务给出相关问句推荐
    *"q2s_qprlt":问句解析提供给推荐问句QueryParser的结果
    *"s2q_needqprlt":推荐问句服务请求问句解析服务提供QueryParser的结果
    *"empty":空的SuggestInfo包
    */
    public String suggestInfoType_;
    /**最原始的用户的问句*/
    public String userQueryStr_;
    /**需要注意的是smticNodeTextList_ 和 smticNodeType 是一一对应的，即相同位置上具有相
    *QueryParser中Query中的SematicNode的text属性
    */
    public ArrayList<String> smticNodeTextList_;
    /**QueryParser中Query中的SemanticNode的类型*/
    public ArrayList<String> smticNodeTypeList_;
    /**是否解析正确
     * "succcess" : QueryParser解析正确
     * "failed" : QueryParser解析失败
     */
    public String qpOK_;
    /**是否仅包含股票
     * 是"true"
     * 否"false"
     * */
    public String isStock_;
    /**是否包含股票，包括股票名，股票代码
     * 是"true"
     * 否"false"
     * */
    public String containStock_;
    /**股票代码或股票名称*/
    public ArrayList<String> stockInfo_;
    /**时间和数字的相关信息*/
    public ArrayList<String> dateAndNumInfo_;
    /**用来记录该问句所生成的tree*/
    public String treeLog_;
    
    public SuggestInfo(){
        suggestInfoType_ = "empty";
        userQueryStr_ = "";
        smticNodeTextList_ = new ArrayList<String>();
        smticNodeTypeList_ = new ArrayList<String>();
        qpOK_ = "";
        isStock_ = "false";
        stockInfo_ = new ArrayList<String>();
        dateAndNumInfo_ = new ArrayList<String>();
    }
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append( "suggestInfoType:[" ); sb.append(suggestInfoType_); sb.append("]").append(LS);
        sb.append( "userQueryStr:[" ); sb.append(userQueryStr_); sb.append("]").append(LS);
        sb.append( "smticNodeTextList:" );sb.append(LS);
        for( String str : smticNodeTextList_ ){
            sb.append( "[" );sb.append(str);sb.append("]"+LS);
        }
        sb.append( "smticNodeTypeList:" );sb.append(LS);
        for( String str : smticNodeTypeList_ ){
            sb.append("[");sb.append(str);sb.append("]").append(LS);
        }
        sb.append( "isStock:[" ); sb.append(isStock_); sb.append("]").append(LS);
        sb.append( "containStock:[" ); sb.append(containStock_); sb.append("]").append(LS);
        sb.append( "stockInfo:[" ); sb.append(LS);
        for( String str : stockInfo_ ){
            sb.append("[");sb.append(str);sb.append("]").append(LS);
        }
        return sb.toString();
    }
}

