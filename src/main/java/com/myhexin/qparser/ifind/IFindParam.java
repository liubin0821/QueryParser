package com.myhexin.qparser.ifind;

import com.myhexin.qparser.date.DateCompute;
import com.myhexin.qparser.date.DateRange;
import com.myhexin.qparser.date.DateUtil;
import com.myhexin.qparser.define.MiscDef;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;

/**
 * IFind指标的参数信息 
 * 对应ifind_index.xml中的param节点
 */
public class IFindParam {
    /** ifind 参数内部名称 */
    public String name = null;
    /** 参数名称 */
    public String title = null;
    /** 默认取值 */
    public String defaultValue = null;
    /** 参数默认取值的列表 */
    public String listName = null;
    /** 外部指标指定类型--对应参数中的ifind_type节点，最终转换为内部类型*/
    public String origType = null;
    /** 转换为的内部值的类型 */
    public String valType = null;
    
    public static final IFindParam UNIT_PARAM = new IFindParam();
    static {
        UNIT_PARAM.title = UNIT_PARAM.name = "单位设置";
    }
    
    public IFindParam(){}
    
    public IFindParam(String name, String value, String title,
            String oldType,String listName) throws UnexpectedException {
        this.name = name;
        this.defaultValue = value;
        this.title = title;
        this.origType = oldType;
        this.valType = IFindUtil.mapIFindParamType(oldType);
        this.listName = listName;
    }

    /**
     * 根据给定值获得系统指令所需的值。首先给定值如果是<code>null</code>，则使用
     * {@link #defaultValue}。目前仅支持如下情况：<ul>
     * <li>参数列表为空，且参数是“交易日期”且默认值是“最新”，返回{@link DateUtil#getLatestTradeDate()}</li>
     * <li>参数列表为空，且不满足上述条件， 返回传入的值</li>
     * <li>给定值不是参数列表的值时，返回传入的值</li>
     * <li>
     * <li>如果列表是{@link MiscDef#START_DATE_PARAM_LIST_NAME}或者{@link
     * MiscDef#END_DATE_PARAM_LIST_NAME}且给定“最新”时，返回最近一个交易日 </li>
     * <li>如果列表是{@link MiscDef#START_DATE_PARAM_LIST_NAME}，且给定是
     * “截止日前一交易日”时，返回另一参数给定的相对日期的前一个交易日</li>
     * <li>以上情况均不满足时，抛出异常</li>
     * </ul>
     * @param val
     * @param relativeVal
     * @return
     * @throws UnexpectedException
     */
    public String getValForCommand(String val, String relativeVal)
    throws UnexpectedException {
        if(val == null) { val = this.defaultValue; }
        
        if(listName == null) {
            if(title.equals(MiscDef.TRADE_DATE_PROP) &&
                    "最新".equals(val)) {
                return DateUtil.getLatestTradeDate().toString("");
            }
            return val;
        }
        
        IFindParamList ipl = IFindParamList.getList(listName);
        String valTitle = ipl.getTitle(val);
        if(valTitle == null) return val;
        
        if((listName.equals(MiscDef.START_DATE_PARAM_LIST_NAME) ||
                listName.equals(MiscDef.END_DATE_PARAM_LIST_NAME)) &&
                valTitle.equals("最新")) {
            return DateUtil.getLatestTradeDate().toString("");
        } else if(listName.equals(MiscDef.START_DATE_PARAM_LIST_NAME) &&
                val.equals("1")){
            if(relativeVal == null) {
                throw new UnexpectedException("need relative date");
            }
            try {
                DateRange dr = DateCompute.getDateInfoFromStr(relativeVal,null);
                String aDayEarlier = DateUtil.rollTradeDate(dr.getFrom(),-1).toString("");
                return aDayEarlier;
            } catch (NotSupportedException e) {
                throw new UnexpectedException("failed to parse [%s]: %s",
                        relativeVal, e.getMessage());
            }
        } else {
            throw new UnexpectedException("listName=[%s], value=[%s], relativeVal=[%s]",
                    listName, val, relativeVal);
        }
    }
    
    public String toString() {
        return String.format("title:%s;defVal:%s;oldType:%s", title,
                defaultValue, origType);
    }
}