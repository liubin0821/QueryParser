package com.myhexin.qparser.define;

import java.lang.reflect.Field;

import com.myhexin.qparser.except.UnexpectedException;

public class EnumDef {

	public enum UrlReqType{
		CK_CRF,
		TKN_LTP,
        TKN_DYNAMIC_LTP,
        UNKNOWN,
        //B->A句式转换
        SIMI_SOLR,SIMI_SFE,
        SIMI_NOTRANS_TOKENIZE,
        SIM_HASTRANS_TOKENIZE,
    }
	
    public enum LogicType {
		AND, OR, TIMESEQUENCE
    }
    
    public enum OntoPropType {
        DP,//词典类型属性
        IP,//指标属性
        NP
    }

    public enum ChangeType {
        INCREASE, DECREASE,NOCREASE
    }
    public enum RefType{
        COPY,RELATIVE,REVERSAL,NULL
    }
    public enum PropType {
		STR, NUM, DATE, TIME, INDEX, GEO, INST, UNKNOWN, BOOL, DOUBLE, LONG, TECH_PERIOD, DATE_LIST, CONSIST_PERIOD
    }

    public enum Direction {
        LEFT, RIGHT, BOTH
    }

    public enum OperatorType {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, RATE
    }
    public enum GrowthType {
        YEAR_ON_YEAR,CHAIN
    }
    
    public enum RangeabilityType{
        GRADUAL,STRONGLY
    }
    public enum SortValueType {
        NUM, DATE
    }

    public enum SpecialType {
        STOCK, SORT
    }

    public enum QuestType {
        HOW_MUCH, WHEN, WHO, WHERE, WHICH, WHAT,UNKNOWN
    }

    public enum ChangeDateType{
        //预测，累计，复合，预告
        PREDICT,ACCUMULATE,COMPOUND,HERALD
    }
    
    public enum Unit {
		YUAN, HKD, USD, GE,ZHONG, CI, BEI, UNKNOWN, PERCENT, JIA, GU,DANG, HU, ZHI, SHOU, SUI, WEI,  
		DAY, MONTH, YEAR, WEEK, QUARTER, HALF_YEAR, MUNITE,DIAN, TIAO, GEN;
    }

    public enum DataSource {
        DB, ONTO, IFIND, QUOTE, HBASE, REDIS, UNKNOWN;
        
        public static DataSource fromStr(String name) {
            if(name.equals(DB.name())) {
                return DB;
            } else if(name.equals(ONTO.name())) {
                return ONTO;
            } else if(name.equals(IFIND.name())) {
                return IFIND;
            } else if(name.equals(QUOTE.name())) {
                return QUOTE;
            } else if(name.equals(HBASE.name())) {
                return HBASE;
            } else if(name.equals(REDIS.name())) {
                return REDIS;
            } else {
                return UNKNOWN;
            }
        }
    }

    public enum CompareType {
        LONGER, SHORTER, EQUAL
    }
    
    public enum CombineType{
    	PRE,POST,BOTH
    }
    
    public enum NodeType { 
    	ENV,//环境信息, 为一个map 2014.12.18 新添加
    	SPECIAL,// 基本废弃
        CHANGE,
        LOGIC,// 逻辑词
        DATE,// 日期节点
        TIME,// 时间节点
        NUM,// 数字节点
        OPERATOR,// 操作符
        SORT,// 排序
        AVG,// 求平均
        GEO,// 基本废弃
        QWORD,// 疑问词
        INST,// 指标实例
        PROP,// 指标属性
        CLASS,// 指标类
        PREDICT,// 基本废弃
        STR_VAL,// 字符型属性的值
        TRIGGER,//触发词
        OTHER,// 基本废弃
        BOOL,
        NEGATIVE,// 否定词，如：非，没，不，没有
        TECHOPERATOR,// 技术操作
        /** 改变数值节点，用于Pattern替换时*/
        CHANGE_NUM_NODE,
        /** 复制节点，用于Pattern替换时*/
        COPY_NODE,
        /** 改变defClass节点,用于Pattern替换时*/
        CHANG_DEF,
        COUNT,
        TECH_PERIOD,
        BOUNDARY,
        FOCUS,
        /**表示别名*/
        ALIAS,
        UNKNOWN,
        NESTED,
        COMBINED,  //被合并为其他节点了
        
		TECH_IDX_GROUP, CONSIST_PERIOD//持续周期
    }

    public enum StdOnto {
        LABEL, TYPE, SBJ, OBJ, PREDICT
    }
    
    public enum DateCycleType {
        DAY,WEEK,MONTH,QUARTER,YEAR
    }
    public enum IndexValType {
        NUM,DATE,STR
    }
    
    public enum SpecialWordType{
        TB_COMMON_SKIP,
        TB_COMMON_STOP,
        FUZZY_SKIP,
        FUZZY_STOP,
        TRIGGER_SKIP,
        TRIGGER_STOP,
        IGNORE_TRANS,
        IGNORE_SKIP,
        INTERVAL_INDEX,
        FORECAST_INDEX,
        NOT_COPY_PARAMS_INDEX,
        PHRASE_PUNC, //短语标点符，如,，、空格等
        SENT_PUNC,   //句子标点符，如；。
        WHITE_SPACE, //空白字符
        PATTERN_WORD,
        RULE_WORD,
        HIDDEN_LEFT_TAG,
        HIDDEN_RIGHT_TAG,
        UNKNOWN
    }
    
    public enum IndexType{
        UNKNOW,QUOTE,PAYMENT,PREDICT,NEW_STOCK, 
        ONTO_FLATTENED, ONTO_PERSON,TECH_LINES,TECH_SPECIAL,TECH_A_LINE,
        BLOCK_TRADE, TECH_SUB_LINE,
    }
    
    /**
     * 指定伪数字节点类型
     */
    public enum FakeNumType{
        MORE,LESS,FLAT,MORE_MINUS,LESS_MINUS,FLAT_MINUS
    }
    /**
     * 指定伪时间节点类型
     */
    public enum FakeDateType{
        BEFORE,FUTURE,BOTH
    }
    
    /**
     * 指定时间范围类型
     */
    public enum DateType{
        BEFORE,FUTURE,BOTH
    }
    
    public enum ReportType {
        YEAR, HALF_YEAR, QUARTER, MONTH, FUTURE_QUARTER, DAILY, TRADE_DAILY, NATURAL_DAILY, FUTURE_DAILY, NOW
        ,FUTURE_YEAR,TIME
    }
    
    public enum ChangeableIndexType{
        QUOTE,PREDICT
    }
    
    public enum WebAction{
        HIGH_LIGHT
    }
    
    /**
     * 节点的隐含属性
     */
    public enum HiddenType{
       TRANS_HIDDEN,REGEX_HIDDEN,CHANGEN_HIDDEN,PATTERN_HIDDEN,HIDDEN,NON_HIDDEN,FAKE_TIME_HIDDEN
    }
    
    public enum   TradePointType {
        AM_OPEN,AM_CLOSE,PM_OPEN,PM_CLOSE,
    }
    
    /**
     * 技术指标支持的周期
     */
    public enum TechPeriodType{
        DAY, WEEK, MONTH, QUARTER, YEAR, MIN, MIN_1, MIN_5, MIN_15, MIN_30, MIN_60, HOUR, UNKNOWN
    }

  
    
    
    public enum ChunkType {
        TECH, UNKNOWN
    }
    
	public enum DateCombineType {
		IGNORE_BEFORE, AS_ONE_DATE, DO_NOTHING, AS_ONE_DATE_ADD_SEQ//中间加上连续
	}
	
    public enum DescNodeType {
    	NUM, TEXT, LOGIC
    }
    
    public enum BaseTimeUnit {
        HOUR, MIN, SEC,
    }
    
    public enum DateUnit {
        TRADE_DAY, DAY, WEEK, MONTH, YEAR
        // QUARTER,HALF_YEAR
    }
    
    public enum ModifyNumType {
        PLUS, MINUS
    }   
    
    /** 技术形态的位置信息 */
    public enum TechOpPos {
        LEFT, RIGHT, MID, COMMON
    }

    /**
     * 技术形态所能绑定的指标数目
     */
    public enum TechMaxBindNum {
        UNARY, BINARY, MULTI
    }

    /**
     * 技术指标形态类型信息
     */
    public enum TechOpType{
        COMMON, FOR_IDX, FOR_LINE
    }
    
    /** 技术形态子线类型 
     * @deprecated*/
    public enum TechOpIdxesType {
        LINES, ONLY_IDX, IDX_NUMLINE
    }
    
    public enum  MatchType{
    	UNIT_EQUAL,
    }
    
    /**
     * 通用枚举valueOf
     * @param <T>
     * @param enumType
     * @param name
     * @return
     * @throws UnexpectedException
     */
    public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name)
            throws UnexpectedException {
        String logMsg = null;
        try {
            T result = Enum.valueOf(enumType, name);
            return result;
        } catch (Exception e) {
            String enumsStr = EnumDef.createEnumStr(enumType);
            logMsg = String.format("type定义有误[%s] 候选项[%s]", name, enumsStr);
            throw new UnexpectedException(logMsg);
        }
    }
    
    /**
     * 将所有的枚举类型的可能性以字符串的形式列出
     * 
     * @param enumClass
     * @return
     */
    public static String createEnumStr(Class enumClass) {
        try {
            Field[] array = enumClass.getFields();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; i++) {
                sb.append(array[i].getName());
                if (i + 1 < array.length) {
                    sb.append("|");
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}


