package com.myhexin.qparser.time.info;

import com.myhexin.qparser.define.EnumDef.BaseTimeUnit;
import com.myhexin.qparser.define.OperDef;
import com.myhexin.qparser.time.tool.TimeUtil;

/**
 * 时间范围，默认从00:00:00至23:59:59
 */
public class TimeRange implements Cloneable{
    
    private TimePoint from = TimeUtil.getMin();
    private TimePoint to = TimeUtil.getMax();
    
    /**
     * 标记是否为长度时间，如“5分钟”
     */
    private boolean isLength = false;
    private BaseTimeUnit unit = null;
    private int length = -1;
    private boolean canNotAdjust = false;
    
    public TimeRange(TimePoint from,TimePoint to){
        this.from = from;
        this.to = to;
    }

    public TimePoint getFrom() {
        return from;
    }

    public TimePoint getTo() {
        return to;
    }
    
    public boolean isLegal() {
		return from != null && to != null && from.isLegal() && to.isLegal();
		//&& (from.isEarlier(to) || from.equals(to));
    }
    
    public String getRangeType(){
        if(from.isMin()&&to.isMax()){
            return OperDef.QP_IN;
        }else if(from.equals(to)) {
            return OperDef.QP_EQ;
        } else if(from.isMin()) {
            return OperDef.QP_LT;
        } else if(to.isMax()) {
            return OperDef.QP_GT;
        } else {
            return OperDef.QP_IN;
        }   
    }
    
    public String toString() { 
        String rtn = String.format("%s--%s", from.toString(),to.toString());
        return rtn;
    }
    public TimeRange clone() {
        try {
            TimeRange rtn = (TimeRange) super.clone();
            return rtn;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public void setIsLength() {
        this.isLength = true;
    }

    public boolean isLength() {
        return isLength;
    }

    public void setUnit(BaseTimeUnit unit) {
        this.unit = unit;
    }

    public BaseTimeUnit getUnit() {
        return unit;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public void setCanNotAdjust() {
        this.canNotAdjust = true;
    }

    public boolean isCanNotAdjust() {
        return canNotAdjust;
    }

    public boolean isInTrade() {
        return from.isInTrade()&&to.isInTrade();
    }

    public void reSetRange(TimePoint repFrom, TimePoint repTo) {
        this.from = repFrom;
        this.to = repTo;
    }
}
