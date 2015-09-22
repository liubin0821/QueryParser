package com.myhexin.qparser.ifind;

import com.myhexin.operate.InnerFieldType;
import com.myhexin.qparser.define.EnumDef.CompareType;
import com.myhexin.qparser.define.DataTypeDef; 
import com.myhexin.qparser.define.MiscDef;
import com.myhexin.qparser.define.OperDef;
import com.myhexin.qparser.define.UnitDef;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.onto.ClassNodeFacade;

public class IFindUtil {


    /**
     * 根据单位求数字的缩放量级
     * @param unit 单位
     * @param isSalaryUnit 是否是薪资类单位，若是“万元”也返回1（IFind数据要求）
     * @return 缩放量级
     */
    public static int getScaleFromUnit(String unit, boolean isSalaryUnit ) {
        int rtn = 1;
        if(unit == null) {
            rtn = 1;
        } else if(unit.equals(UnitDef.WAN_YUAN) ||
                unit.equals(UnitDef.WAN_GU)) {
            rtn = isSalaryUnit ? 1 : 10000;
        } else if(unit.equals(UnitDef.BAI_WAN_GU) ||
                unit.equals(UnitDef.BAI_WAN_YUAN)) {
            rtn = 1000000;
        } else if(unit.equals(UnitDef.YI_GU) ||
                unit.equals(UnitDef.YI_YUAN)) {
            rtn = 100000000;
        }
        return rtn;
    }
    

    /**
     * 将字符串值按指定类型转换成相应值
     * @param type 字符串代表的值类型
     * @param val 要转换的字符串
     * @return 转换后的值
     */
    public static Object parseVal(InnerFieldType type, String val) {
        switch(type) {
        case DOUBLE: return Double.parseDouble(val);
        case LONG:   return Long.parseLong(val);
        default:     return val;
        }
    }

    public static boolean isValPropText(String propText) {
        return propText.charAt(0) == MiscDef.COND_VALUE_PROP_MARK;
    }
    
    public static boolean isBoolPropText(String propText) {
        return propText!=null && propText.equals(MiscDef.BOOL_VAL_PROP);
    }
    
    /**
     * 给定的INST树节点内含的指标是否是字符串型的bool指标
     * 在IFind数据库里，这些指标其值是“是”和“否”字符串
     * @param tInst
     * @return
     * @throws UnexpectedException 
     */
    public static boolean isStrBoolIndex(ClassNodeFacade cn) throws UnexpectedException {
        return MiscDef.IS_DIVIDEND_CLASS.equals(cn.getText()) ||
            MiscDef.IS_IMPORTANT_INDEX_CLASS.equals(cn.getText());
    }
    
    
    
    public static boolean isSalaryPropText(String propText) {
        return propText.endsWith("薪酬") ||
            propText.endsWith("报酬总额");
    }
    
    /**
     * 将日期长度比较符转为IFind的比较符
     * @param dateCmp
     * @return
     * @throws UnexpectedException
     */
    public static String dateCmpToIFindCmp(CompareType dateCmp) throws UnexpectedException {
        switch(dateCmp) {
        case LONGER:
            return OperDef.GT;
        case SHORTER:
            return OperDef.LT;
        case EQUAL:
            return OperDef.EQ;
        default:
            throw new UnexpectedException("unknown date cmp: %s", dateCmp);
        }
    }
    
    
    /**
     * 将ifind客户端配置文件中使用的数据类型映射成本系统使用的数据类型
     * @param ifindType ifind配置文件中使用的数据类型
     * @return 本系统使用的数据类型
     * @throws UnexpectedException 待转换的类型未知
     */
    public static String mapIFindParamType(String ifindType)
    throws UnexpectedException {
        String rtn = null;
        if(ifindType.equals("dt_date")) { rtn = DataTypeDef.IFIND_DATE; }
        else if(ifindType.equals("dt_tradedate")) { rtn = DataTypeDef.IFIND_TRADE_DATE; }
        else if(ifindType.equals("dt_YQ")) { rtn = DataTypeDef.IFIND_YQ; }
        else if(ifindType.equals("dt_year")) { rtn = DataTypeDef.IFIND_YEAR; }
        else if(ifindType.equals("dt_string")) { rtn = DataTypeDef.IFIND_STR; }
        else if(ifindType.equals("dt_integer")) { rtn = DataTypeDef.IFIND_INT; }
        else if(ifindType.equals("quote_datatime")) { rtn = DataTypeDef.Tech_DATE_VAL; }
        else if(ifindType.equals("quote_formula")) { rtn = DataTypeDef.Tech_FORMULA_VAL; }
        else if(ifindType.equals("quote_param")) { rtn = DataTypeDef.Tech_PARAM_VAL; }
        else if(ifindType.equals("quote_sequence")) { rtn = DataTypeDef.Tech_SEQUENCE_VAL; }
        else if(ifindType.equals("quote_period")) { rtn = DataTypeDef.Tech_PERIOD_VAL; }
        else {
            throw new UnexpectedException("unknown ifind data type: [%s]",
                    ifindType);
        }
        return rtn;
    }
    
    public static boolean isDateParam(String paramType) {
        return paramType == DataTypeDef.IFIND_DATE ||
                paramType == DataTypeDef.IFIND_TRADE_DATE ||
                paramType == DataTypeDef.IFIND_YEAR ||
                paramType == DataTypeDef.IFIND_YQ;
    }
}
