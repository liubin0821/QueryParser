package com.myhexin.qparser.define;

public class DataTypeDef {
    public static final String IFIND_DATE = "dt_date";
    public static final String IFIND_YQ = "dt_yq";
    public static final String IFIND_STR = "dt_str";
    public static final String IFIND_TRADE_DATE = "dt_trade_date";
    public static final String IFIND_YEAR = "dt_year";
    public static final String IFIND_INT = "dt_int";

    
    /** 技术指标的特别参数 */
    public static final String Tech_DATE_VAL = "quote_datatime";
    public static final String Tech_FORMULA_VAL = "quote_formula";
    public static final String Tech_PARAM_VAL = "quote_param";
    public static final String Tech_SEQUENCE_VAL = "quote_sequence";
    public static final String Tech_PERIOD_VAL = "quote_period";
    
    public static boolean isIFindDateType(String fieldName ) {
    	if(IFIND_DATE.equals(fieldName) 
    			|| IFIND_YQ.equals(fieldName) 
    			|| IFIND_STR.equals(fieldName) 
    			|| IFIND_TRADE_DATE.equals(fieldName)
    			|| IFIND_YEAR.equals(fieldName)
    			|| IFIND_INT.equals(fieldName) ) {
    		return true;
    	}else{
    		return false;
    	}
    	
    }
}
