package com.myhexin.qparser.tech;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myhexin.qparser.Param;
import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.TechOpType;
import com.myhexin.qparser.define.EnumDef.TechPeriodType;
import com.myhexin.qparser.define.MiscDef;
import com.myhexin.qparser.except.*;
import com.myhexin.qparser.ifind.IFindParam;
import com.myhexin.qparser.ifind.IndexInfo;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.node.TechOpNode;
import com.myhexin.qparser.node.TechPeriodNode;
import com.myhexin.qparser.onto.ClassNodeFacade;
import com.myhexin.qparser.util.Util;

public class TechUtil {
    public static final String PERIOD_DAY_ID = "16384";
    public static final String PERIOD_WEEK_ID = "20481";
    public static final String PERIOD_MONTH_ID = "24577";
    public static final String PERIOD_YEAR_ID = "28673";
    private static final String PERIOD_MIN_1_ID = "12289";
    private static final String PERIOD_MIN_5_ID = "12293";
    private static final String PERIOD_MIN_15_ID = "12303";
    private static final String PERIOD_MIN_30_ID = "12318";
    private static final String PERIOD_MIN_60_ID = "12348";
    public static final String PERIOD_DEFAULT_ID = PERIOD_DAY_ID;
    
    public static final String TECH_PRE_STR = "行情";
    public static final String DEFAULT_DATE_RANGE = "-1-0";
    
    public static final String PERIOD_DEF_STR = String.format("%s(%s)", PERIOD_DAY_ID, DEFAULT_DATE_RANGE);
    
    public static final String TECH_VALUE_NODE_TITLE = "_浮点型数值";
    public static final String TECH_FORMULA_SEQUENCE_TITLE = "公式序列";
    
    /********* 行情公式翻译相关数据，公式的信息最终会转移到配置文件中 **********/
    
    /** 对应特别公式的行情指标：例如“多头排列”，“bias短线超跌”等 */
    public static final HashMap<String,String> SPECIAL_FORMULAS = new HashMap<String,String>();
    public static final HashMap<String, String> SPECIAL_PUBNAME = new HashMap<String,String>();
    
    /** TechOp的公式信息 */
    public static final HashMap<String,TechOpInfo> TECH_OP_INFOS = new HashMap<String,TechOpInfo>();
    
    /** 指标默认对应的techOp */
    public static final HashMap<String,String> CLASS_DEF_TECHOP = new HashMap<String,String>();
    
    public static final String DEFAULT_REQUEST_PARAM_STRING = 
        "method=quote&datetime=16384(-1-0)" +
        "&fuquan=Q" +
        "&append=Y" +
        "&sortby=10" +
        "&sorttype=select" +
        "&datatype=10" +
        "&codelist=17();33()" +
        "&sortappend=Y&formula=period:16384;ID:7615;NAME:默认名字;source:";

   
    //返回默认的请求参数映射表,时间还要特殊处理
    public static HashMap<String,String> getRequstParamsMap(){
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("method", "quote");
        params.put("datetime", "16384");
        params.put("sortby", "10");
        params.put("sorttype", "select");
        params.put("datatype", "10");
        params.put("codelist", "17();33()");
        params.put("sortappend", "Y");
        return params;
    }
    
    //返回默认的公式参数映射表
    public static HashMap<String,String> getFormulaParamsMap(){
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("period", "16384");
        params.put("ID", "7615");
        params.put("NAME", "默认名字");
        return params;
    }

    private static String SPLIT_FLAG = "\\$\\$";
    
    public static void loadInfo(ArrayList<String> fileStrings) 
    throws DataConfException, UnexpectedException, NotSupportedException{
       int lineNum = 1;
       for(String line : fileStrings){
           if(line.startsWith("#") || line.trim().length() == 0){
               lineNum ++;
               continue;
           } else if (line.startsWith("class_special:")){
               line = line.substring("class_special:".length());
               String[] params = line.split(SPLIT_FLAG);
               if(params.length != 3){
                   throw new DataConfException(Param.TECH_INFO_FILE,lineNum,
                           "class_special 未包含3个必要参数\nline--"+line);
               }
               String idxName = params[0].substring("name=".length());
               String pubname = params[1].substring("pubname=".length());
               String formula = params[2].substring("formula=".length());
               SPECIAL_FORMULAS.put(idxName, formula);
               SPECIAL_PUBNAME.put(idxName, pubname);
           }else if (line.startsWith("techop_info:")){
               line = line.substring("techop_info:".length());
               String[] params = line.split(SPLIT_FLAG);
               if(params.length != 2 && params.length != 3){
                   throw new DataConfException(Param.TECH_INFO_FILE,lineNum,
                           "techop_info 未包2/3个必要参数\nline--"+line);
               }
               String name = params[0].substring("name=".length());
               String formula = params[1].substring("formula=".length());
               String defIndexes = params.length == 3 ? params[2].substring("defindex=".length()) : null;
               TechOpInfo newTechOpInfo = new TechOpInfo(formula, defIndexes);
               TECH_OP_INFOS.put(name, newTechOpInfo);
           } else if (line.startsWith("techop_idxmap:")){
               line = line.substring("techop_idxmap:".length());
               String[] params = line.split(SPLIT_FLAG);
               if(params.length != 4){
                   throw new DataConfException(Param.TECH_INFO_FILE,lineNum,
                           "techop_idxmap 未包含4个必要参数\nline--"+line);
               }
               String op = params[0].substring("op=".length());
               TechOpInfo techOp = TECH_OP_INFOS.get(op);
               if(techOp == null){
                   throw new DataConfException(Param.TECH_INFO_FILE,lineNum,
                           "techop_idxmap 的techop_info 不存在--op="+op);
               }               
               String idx = params[1].substring("idx=".length());
               String paramMap = params[2].substring("map=".length());
               String idxFormula = params[3].substring("formula=".length());
               techOp.addIndexParam(idx, paramMap);
               techOp.addIndexFormula(idx, idxFormula);               
           } else if (line.startsWith("class_defop:")) {
               line = line.substring("class_defop:".length());
               String[] params = line.split(SPLIT_FLAG);
               if(params.length != 2){
                   throw new DataConfException(Param.TECH_INFO_FILE,lineNum,
                           "class_defop 未包含2个必要参数\nline--"+line);
               }
               String idx = params[0].split("=")[1];
               String defOp = params[1].split("=")[1];
               CLASS_DEF_TECHOP.put(idx, defOp);               
           } else {
               throw new DataConfException(Param.TECH_INFO_FILE,lineNum,
               "该行的前缀不符合要求，无法识别配置\nline--"+line);
           }
           lineNum ++;
       }        
    }
    

    /**
     * 总是返回较小周期的概念
     * @param left
     * @param right
     * @return
     * @throws UnexpectedException 
     */
    public static int comparePeriod(TechPeriodType left, TechPeriodType right) throws UnexpectedException{
        if(left == null){
            return -1;
        } else if(left == right){ 
            return 0; 
        } else if (isMinPeriod(left)){            
            return 1;
        } else if (left == TechPeriodType.DAY){ 
            return -1; 
        } else if(left == TechPeriodType.WEEK){
            return right == TechPeriodType.DAY ? 1 : -1;
        } else if(left.equals(PERIOD_MONTH_ID)){
            return right == TechPeriodType.YEAR ? -1 : 1;
        } else if(left  == TechPeriodType.YEAR){
            return 1;
        } else {
            throw new UnexpectedException("UnKonw TechTime [%s]",left);
        }
    }

    /** 获取公式请求的周期id */
    public static String getPeriodIdByType(TechPeriodType periodType) throws UnexpectedException {
        switch(periodType){
            case DAY: return  PERIOD_DAY_ID;
            case WEEK: return  PERIOD_WEEK_ID;
            case MONTH: return  PERIOD_MONTH_ID;
            case YEAR: return  PERIOD_YEAR_ID;
            case MIN_1: return  PERIOD_MIN_1_ID;
            case MIN_5: return  PERIOD_MIN_5_ID;
            case MIN_15: return  PERIOD_MIN_15_ID;
            case MIN_30: return  PERIOD_MIN_30_ID;
            case MIN_60: return  PERIOD_MIN_60_ID;
            default : throw new UnexpectedException("error PeriodType [%s]", periodType);
        }
    }
    
    public static String TECH_DAY_USER_SAY = "日线周期";
    public static String TECH_WEEK_USER_SAY = "周线周期";
    public static String TECH_MONTH_USER_SAY = "月线周期";
    public static String TECH_QUARTER_USER_SAY = "季线周期";
    public static String TECH_YEAR_USER_SAY = "年线周期";
    
    public static String TECH_MIN_1_USER_SAY = "1分钟线";
    public static String TECH_MIN_5_USER_SAY = "5分钟线";
    public static String TECH_MIN_15_USER_SAY = "15分钟线";
    public static String TECH_MIN_30_USER_SAY = "30分钟线";
    public static String TECH_MIN_60_USER_SAY = "60分钟线";
    
    /** 获取分钟线的用户说法 */
    public static String getPeriodUserSayByType(TechPeriodType periodType) throws UnexpectedException {
        switch(periodType){
            case DAY: return  TECH_DAY_USER_SAY;
            case WEEK: return  TECH_WEEK_USER_SAY;
            case MONTH: return  TECH_MONTH_USER_SAY;
            case YEAR: return  TECH_YEAR_USER_SAY;
            case MIN_1: return  TECH_MIN_1_USER_SAY;
            case MIN_5: return  TECH_MIN_5_USER_SAY;
            case MIN_15: return  TECH_MIN_15_USER_SAY;
            case MIN_30: return  TECH_MIN_30_USER_SAY;
            case MIN_60: return  TECH_MIN_60_USER_SAY;
            default : throw new UnexpectedException("error PeriodType [%s]", periodType);
        }
    }
    
    
    public static TechPeriodType getPeriodTypeByUserSay(String word) throws UnexpectedException{
        if(TECH_DAY_USER_SAY.equals(word)){
            return TechPeriodType.DAY;
        } else if(TECH_WEEK_USER_SAY.equals(word)){
            return TechPeriodType.WEEK;
        }  else if(TECH_MONTH_USER_SAY.equals(word)){
            return TechPeriodType.MONTH;
        }  else if(TECH_YEAR_USER_SAY.equals(word)){
            return TechPeriodType.YEAR;
        }  else if(TECH_MIN_1_USER_SAY.equals(word)){
            return TechPeriodType.MIN_1;
        }  else if(TECH_MIN_5_USER_SAY.equals(word)){
            return TechPeriodType.MIN_5;
        }  else if(TECH_MIN_15_USER_SAY.equals(word)){
            return TechPeriodType.MIN_15;
        }  else if(TECH_MIN_30_USER_SAY.equals(word)){
            return TechPeriodType.MIN_30;
        }  else if(TECH_MIN_60_USER_SAY.equals(word)){
            return TechPeriodType.MIN_60;
        }  else if(TECH_QUARTER_USER_SAY.equals(word)){
            return TechPeriodType.QUARTER;
        }  else {
            throw new UnexpectedException("error userSay--TechPeriod[%s]",word);
        } 
    }
    /** 检测是否周期为分钟线类型 */
    public static boolean isMinPeriod(TechPeriodType type){
        switch(type){
            case MIN_1:
            case MIN_5:
            case MIN_15:
            case MIN_30:
            case MIN_60: return true;
            default: return false;
        }
    }
    
    public static boolean isMinuteWord(String word){
        return Util.WordIn(word, MiscDef.MIN_WORDS);
    }
    
    public static boolean isMinMidWord(String word){
        return Util.WordIn(word, MiscDef.MIN_MID_WORDS);
    }
    
    public static boolean isKLineWord(String word){
        return Util.WordIn(word, MiscDef.K_LINE_WORDS);
    }
    
    public static boolean isHourWord(String word){
        return Util.WordIn(word, MiscDef.HOUR_WORDS);
    }
    
    public static boolean isDayWord(String word) {
        return Util.WordIn(word, MiscDef.DAY_WORDS);
    }
    
    public static boolean isWeekWord(String word) {
        return Util.WordIn(word, MiscDef.WEEK_WORDS);
    }
    
    public static boolean isMonthWord(String word) {
        return Util.WordIn(word, MiscDef.MONTH_WORDS);
    }
    
    public static boolean isYearWord(String word) {
        return Util.WordIn(word, MiscDef.YEAR_WORDS);
    }

    public static String getSuitableStringForTechPeriod(String word){
        Pattern p = Pattern.compile(MiscDef.NUM_TO_PERIOD_REGEX);
        Matcher m = p.matcher(word);
        if(m.find()){
            return m.group(1);
        }
        return null;
    }
    
    public static TechPeriodNode getTechPeriodNodeByNodes(NumNode numNode, String text) 
    throws NotSupportedException, UnexpectedException {
        assert(isMinuteWord(text));
        TechPeriodNode period = new TechPeriodNode(numNode.getText()+text,getMinPeriodTypeByNum(numNode));
        period.setNumNode(numNode);
        return period;
    }

    /** 根据分钟数值，生成分钟线分析周期 */
    public static TechPeriodType getMinPeriodTypeByNum(NumNode numNode) throws NotSupportedException {
        double num = numNode.getFrom();
        if(num == 1 ){
            return TechPeriodType.MIN_1;
        } else if(num == 5 ){
            return TechPeriodType.MIN_5;
        } else if(num == 15 ){
            return TechPeriodType.MIN_15;
        } else if(num == 30 ){
            return TechPeriodType.MIN_30;
        } else if(num == 60 ){
            return TechPeriodType.MIN_60;
        } else {
            return TechPeriodType.MIN_1;
        }
    }
    
    public static boolean checkPeriod(TechPeriodNode period){
        TechPeriodType type = period.getPeriodType();
        double num = period.getNumNode().getFrom();
        if(type == TechPeriodType.MIN_1 &&  num == 1 ) { return true; } 
        if(type == TechPeriodType.MIN_5 &&  num == 5 ) { return true; } 
        if(type == TechPeriodType.MIN_15 &&  num == 15 ) { return true; } 
        if(type == TechPeriodType.MIN_30 &&  num == 30 ) { return true; } 
        if(type == TechPeriodType.MIN_60 &&  num == 60 ) { return true; }
        return false;
    }
    
    
    /** 根据配置文件默认值生成分析周期Type 
     * @throws UnexpectedException */
    public static TechPeriodType getPeriodTypeByStr(String word) throws UnexpectedException{
        if("DAY".equals(word)){
            return TechPeriodType.DAY;
        } else if("WEEK".equals(word)){
            return TechPeriodType.WEEK;
        } else if("MONTH".equals(word)){
            return TechPeriodType.MONTH;
        } else if("YEAR".equals(word)){
            return TechPeriodType.YEAR;
        } else if("MIN_1".equals(word)){
            return TechPeriodType.MIN_1;
        } else if("MIN_5".equals(word)){
            return TechPeriodType.MIN_5;
        } else if("MIN_15".equals(word)){
            return TechPeriodType.MIN_15;
        } else if("MIN_30".equals(word)){
            return TechPeriodType.MIN_30;
        } else if("MIN_60".equals(word)){
            return TechPeriodType.MIN_60;
        } else {
            throw new UnexpectedException("word[%s] is not PeriodStr", word);
        }
    }
    
    public static String getPeriodIdStrByStr(String word) throws UnexpectedException{
        TechPeriodType type = getPeriodTypeByStr(word);
        return getPeriodIdByType(type);
    }
    
    public static TechPeriodType getIndexDefaultPeriod(IndexInfo indexInfo) throws UnexpectedException{
        return getPeriodTypeByStr(
                indexInfo.getParamByTitle(MiscDef.TECH_ANALY_PERIOD).defaultValue);
    }



}
