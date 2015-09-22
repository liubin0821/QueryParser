package com.myhexin.qparser.define;

import com.myhexin.operate.ExprOpType;
import com.myhexin.qparser.define.EnumDef.OperatorType;
import com.myhexin.qparser.except.UnexpectedException;

public class OperDef {
    //======= Web端显示使用的操作符 ============
    public static final String Web_AND = "且";
    public static final String Web_OR = "或";
    public static final String Web_GT =">";
    public static final String Web_LT ="<";
    public static final String Web_GE =">=";
    public static final String Web_LE ="<=";
    public static final String Web_EQ ="=";
    public static final String Web_NE ="!=";
    public static final String Web_CONTAIN ="包含";
    public static final String Web_NOT_CONTAIN ="不包含";
    public static final String Web_GROUPBY ="groupby";
    public static final String Web_SORT ="sort";
    public static final String Web_AVG ="avg";
    public static final String Web_ADD ="+";
    public static final String Web_SUB ="-";
    public static final String Web_MUL ="*";
    public static final String Web_DIV ="/";
    public static final String Web_RATE ="-/";
    public static final String WEB_CMP_LOGIC_AND ="and";
    public static final String WEB_CMP_LOGIC_OR ="or";
    
  //======= Web端显示使用的汉语操作符 ============
    public static final String Web_GT_CH ="大于";
    public static final String Web_LT_CH ="小于";
    public static final String Web_GE_CH="大于等于";
    public static final String Web_LE_CH ="小于等于";
    public static final String Web_EQ_CH ="等于";
    public static final String Web_NE_CH ="不等于";
    
    //======= 解析系统内部使用的操作符 ============
    public static final String QP_GT = ">" ;
    public static final String QP_LT = "<";
    public static final String QP_GE = ">=";
    public static final String QP_LE = "<=";
    public static final String QP_EQ = "=";
    public static final String QP_NE = "!=";
    public static final String QP_IN = "><";
    public static final String QP_NI = "<>";
    
    //======= IFind客户端使用的操作符 ============
    public static final String IFIND_CLT_GT = Web_GT ;
    public static final String IFIND_CLT_LT = Web_LT;
    public static final String IFIND_CLT_GE = Web_GE;
    public static final String IFIND_CLT_LE = Web_LE;
    public static final String IFIND_CLT_EQ = Web_EQ;
    public static final String IFIND_CLT_UE = Web_NE;
    
    public enum ExprTokenType  { 
        MAX, //最大 max
        MIN, //最小 min
        AVG, //求平均 avg
        SORT,//排序 sort
        AND, //且，与，and，&&， &
        OR,//或 ，or，||，|
        NOT, //非，not，~
        ID, //标识符<ID, 符号表入口>
        NUM, //常数<NUM, 符号表入口>
        LPS, //左小括号(
        RPS,// 右小括号(
        PLUS,//加，+
        SUBT,//减，-
        MULT, //乘，*
        DIVI, //除，/
        RATE, // a RATE b means  (a-b)/|b|
        EQUAL,//相等，=, ==
        NQUEAL,//不相等 !=
        GRE,//大于，>
        EGRE,//大于等于, >=
        LESS,//小于，<
        ELESS,//小于等于，<=
        EOS,//终结符， @
        COUNT;
    }

    
    /** 以下为Condition中使用的比较符 */
    //compare operators
    public static final String GT = "(";
    public static final String LT = "&lt;";
    public static final String GE = "&gt;=";
    public static final String LE = "&lt;=";
    public static final String EQ = "=";
    public static final String NE = "!=";
    public static final String GT_LT = "()";
    public static final String GT_LE = "(]";
    public static final String GE_LT = "[)";
    public static final String GE_LE = "[]";
    public static final String LT_GT = ")(";
    public static final String CONTAIN = "包含";
    public static final String NOT_CONTAIN = "不包含";
    
    public static boolean isCmpareOper(String operStr){
        return operStr == GT || operStr == LT || operStr == GE
            || operStr == LE || operStr == EQ || operStr == NE
            || operStr == GT_LE || operStr == GT_LE || operStr == GE_LT
            || operStr == GE_LE || operStr == LT_GT 
            || operStr == CONTAIN || operStr == NOT_CONTAIN;
    }
    
    // logic operators
    public static final String AND = "and";
    public static final String OR = "or";
    
    public static boolean isLogicOper(String cmpStr){
        return cmpStr == AND || cmpStr == OR;
    }
    
    // arithmetic operators
    public static final String ADD = "+";
    public static final String SUB = "-";
    public static final String DIV = "/";
    public static final String MUL = "*";
    public static final String RATE = "-%";
    public static final String GROUPBY = "groupby";
    
    public static boolean isArithmeticOper(String cmpStr){
        return cmpStr == ADD || cmpStr == SUB || cmpStr == DIV
            || cmpStr == MUL || cmpStr == RATE || cmpStr == GROUPBY;
    }
    
    
    public static final String SORT = "sort";
    public static final String AVG = "avg";

    
    /**
     * 将{@link Condition}中定义的<code>operator</code>映射为Web显示时
     * 相应的<code>operator</code>
     * @param ifindOp ifind中使用的操作符
     * @return web端所支持的操作符
     * @throws UnexpectedException 
     * @see #mapToIFindOp(String)
     */
    public static String ifindOp2WebOp(String ifindOp) throws UnexpectedException {
        String webOp = null;
        if(ifindOp == null) {
            webOp = null;
        } else if(ifindOp == EQ) {
            webOp = String.format(" %s ", OperDef.Web_EQ);
        } else if(ifindOp == NE) {
            webOp = String.format(" %s ", OperDef.Web_NE);
        } else if(ifindOp == LE) {
            webOp = String.format(" %s ", OperDef.Web_LE);
        } else if(ifindOp == LT) {
            webOp = String.format(" %s ", OperDef.Web_LT);
        } else if(ifindOp == GE) {
            webOp = String.format(" %s ", OperDef.Web_GE);
        } else if(ifindOp == GT) {
            webOp = String.format(" %s ", OperDef.Web_GT);
        } else if(ifindOp == SORT) {
            webOp = OperDef.Web_SORT;
        } else if(ifindOp == AVG) {
            webOp = OperDef.Web_AVG;
        } else if( ifindOp == GROUPBY){
            webOp = OperDef.Web_GROUPBY;
        } else if(ifindOp == CONTAIN) {
            webOp = OperDef.Web_CONTAIN;
        } else if(ifindOp == NOT_CONTAIN) {
            webOp = OperDef.Web_NOT_CONTAIN;
        } else if (ifindOp == DIV) {
            webOp = String.format(" %s ", OperDef.Web_DIV);
        } else if (ifindOp == MUL) {
            webOp = String.format(" %s ", OperDef.Web_MUL);
        } else if (ifindOp == ADD) {
            webOp = String.format(" %s ", OperDef.Web_ADD);
        } else if (ifindOp == SUB) {
            webOp = String.format(" %s ", OperDef.Web_SUB);
        } else if (ifindOp == RATE) {
            webOp = String.format(" %s ", OperDef.Web_RATE);
        } else {
            throw new UnexpectedException("no web op for ifind op: %s", ifindOp);
        }
        
        return webOp;
    }
    
    /**
     * 将本类中中定义的<code>operator</code>映射为{@link Condition}中
     * 相应的<code>operator</code>
     * @param webOp web端所支持的操作符
     * @return ifind中使用的操作符
     * @throws UnexpectedException 
     * @see #mapFromIFindOp(String)
     */
    public static String webOp2IFindOp(String webOp) throws UnexpectedException {
        if(webOp .equals(OperDef.Web_EQ)) {
           return EQ;
        } else if(webOp .equals(OperDef.Web_LE)) {
           return LE;
        } else if(webOp .equals(OperDef.Web_LT)) {
           return LT;
        } else if(webOp .equals(OperDef.Web_GE)) {
           return GE;
        } else if(webOp .equals(OperDef.Web_GT)) { 
           return GT;
        }  else if(webOp .equals(OperDef.Web_SORT)) { 
           return SORT;
        } else if(webOp.equals(OperDef.Web_AVG)){
           return AVG;
        } else if(webOp.equals( OperDef.Web_GROUPBY) ){
           return GROUPBY;
        }else if(webOp .equals(OperDef.Web_CONTAIN)) {
           return CONTAIN;
        } else if(webOp .equals(OperDef.Web_NOT_CONTAIN)) {
           return NOT_CONTAIN;
        } else {
            throw new UnexpectedException("no ifind op for web op: %s", webOp);
        }
    }
    
    /**
     * 将ifind比较符转换成DataQuery模块使用的比较符
     * @param ifindOp ifind比较符
     * @return DQ使用的比较符
     * @throws UnexpectedException
     */
    public static ExprOpType ifindOp2DqOp(String ifindOp)
    throws UnexpectedException {
        if (ifindOp == CONTAIN) {
            return ExprOpType.STR_CONTAIN;
        } else if (ifindOp == NOT_CONTAIN) {
            return ExprOpType.STR_NOT_CONTAIN;
        } else if (ifindOp == EQ) {
            return ExprOpType.EQ;
        } else if (ifindOp == NE) {
            return ExprOpType.NOT_EQ;
        } else if (ifindOp == GE) {
            return ExprOpType.GE;
        } else if (ifindOp == GT) {
            return ExprOpType.GT;
        } else if (ifindOp == LE) {
            return ExprOpType.LE;
        } else if (ifindOp == LT) {
            return ExprOpType.LT;
        } else if (ifindOp == DIV) {
            return ExprOpType.DIV;
        } else if (ifindOp == MUL) {
            return ExprOpType.MUL;
        } else if (ifindOp == ADD) {
            return ExprOpType.ADD;
        } else if (ifindOp == SUB) {
            return ExprOpType.SUB;
        } else if (ifindOp == RATE) {
            return ExprOpType.RATE;
        } else {
            throw new UnexpectedException("no dq op for ifind op: %s", ifindOp);
        }
    }
    
    public static String qpOp2IFindOp(OperatorType qpOper) throws UnexpectedException {
        if (qpOper == OperatorType.RATE) {
            return RATE;
        } else if (qpOper == OperatorType.ADD) {
            return ADD;
        } else if (qpOper == OperatorType.SUBTRACT) {
            return SUB;
        } else if (qpOper == OperatorType.MULTIPLY) {
            return MUL;
        } else if (qpOper == OperatorType.DIVIDE) {
            return DIV;
        } else {
            throw new UnexpectedException("no ifind op for qp op: %s", qpOper);
        }
    }

}
