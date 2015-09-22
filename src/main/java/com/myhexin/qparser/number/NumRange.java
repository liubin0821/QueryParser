package com.myhexin.qparser.number;//from to 格式 --||

import com.myhexin.qparser.define.OperDef;

public class NumRange implements Cloneable{
    private String UNIT = null;
    private double from = NumRange.MIN_;
    private double to = NumRange.MAX_;
    private boolean includeFrom = false;
    private boolean includeTo= false;
    public static double MAX_ = Double.valueOf("1000000000000000");
    public static double MIN_ = Double.valueOf("-1000000000000000");
    
    public NumRange(){}
    
    public boolean equals(Object obj) {
    	if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof NumRange))
			return false;
		final NumRange compare = (NumRange) obj;
    	
        if (this.from != compare.getDoubleFrom() || this.to != compare.getDoubleTo()
                || this.includeFrom != compare.includeFrom || this.includeTo != compare.includeTo)
            return false;
        if ((this.UNIT == null && compare.UNIT != null)
        		|| (this.UNIT!=null && !this.UNIT.equals(compare.UNIT)))
            return false;
        return true;
    }
    
    public void setNumRange(String from,String to){
        
        Double fromref = Double.valueOf(from);
        Double toref = Double.valueOf(to);
        if(fromref<toref){
            this.from=fromref;
            this.to=toref;
        }
        else{
            this.from=toref;
            this.to=fromref;
        }
    }
    
    public void setNumRange(Double from, Double to) {
        from = from == null ? NumRange.MIN_ : from;
        to = to == null ? NumRange.MAX_ : to;
        if (from < to) {
            this.from = from;
            this.to = to;
        } else {
            this.from = to;
            this.to = from;
        }
    }
    public String getUnit() {
        return UNIT;
    }
    public void setUnit(String unit) {
        this.UNIT = unit==null||unit.isEmpty()?null:unit;
    }    
    
    public long getLongFrom(){
        long fromref=new Double(from).longValue();
        return fromref;
    }
    
    public double getDoubleFrom(){
        return from;
    }
    
    public void setFrom(String from) {
        Double fromref = Double.valueOf(from);
        if(fromref<to){
            this.from=fromref;
        }
        else{
            this.from=to;
            this.to=fromref;            
        }
    }
    public void setFrom(double from) {
        if(from<to){
            this.from=from;
        }
        else{
            this.from=to;
            this.to=from;            
        }
    }
    public long getLongTo(){
        long toref=new Double(to).longValue();
        return toref;
    }    
    public double getDoubleTo(){
        return to;
    }
    public void setTo(String to) {
        Double toref = Double.valueOf(to);
        if(toref>from){
            this.to=toref;
        }
        else{
            this.to=from;
            this.from=toref;
        }
    }
    
    public void setTo(double to) {
        if(to>from){
            this.to=to;
        }
        else{
            this.to=from;
            this.from=to;
        }
    }
    
    public String toNumString() {
        String info = null;
        String rangeType = getRangeType();
        if (rangeType.equals(OperDef.QP_EQ)) {
            String num = String.valueOf(from);
            info = String.format("Num:%s\tUnit:%s", num, UNIT);
        } else if (NumUtil.isGreaterType(rangeType)) {
            String num = String.valueOf(from);
            String compare = rangeType.equals(OperDef.QP_GT) ? ">" : ">=";
            info = String.format("Num:%s\tUnit:%s", compare + num, UNIT);
        } else if (NumUtil.isLessType(rangeType)) {
            String num = String.valueOf(to);
            String compare = rangeType.equals(OperDef.QP_LT) ? "<" : "<=";
            info = String.format("Num:%s\tUnit:%s", compare + num, UNIT);
        } else if (rangeType.equals(OperDef.QP_IN)) {
            String num1 = String.valueOf(from);
            String num2 = String.valueOf(to);
            String compare1 = includeFrom ? "<=" : "<";
            String compare2 = includeTo ? "<=" : "<";
            info = String.format("Num:%s num %s\tUnit:%s", num1 + compare1,
                    compare2 + num2, UNIT);
        }else{
            assert(false);
        }
        return info;
    }
    
    public NumRange clone(){
    	try {
			return (NumRange)(super.clone());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return this;
		}
    }
    
    public String toString(){
        return String.format("%.2f--%.2f", from,to);
    }
    
    public String getRangeType() {
        if(from == to) {
            return OperDef.QP_EQ;
        } else if(from == MIN_) {
            return includeTo?OperDef.QP_LE:OperDef.QP_LT;
        } else if(to == MAX_) {
            return includeFrom?OperDef.QP_GE:OperDef.QP_GT;
        } else {
            return OperDef.QP_IN;
        }
    }

    /**
     * 判断两个数字范围有没有交集
     * @param compare
     * @return
     */
    public boolean hasIntersection(NumRange compare) {
        if (compare == null) {
            return false;
        }
        boolean hasIntersection = compare.getDoubleFrom() > this.getDoubleTo();
        hasIntersection |= this.getDoubleFrom() > compare.getDoubleTo();
        hasIntersection |= compare.getDoubleFrom() == this.getDoubleTo()
                && compare.includeFrom && this.includeTo;
        hasIntersection |= this.getDoubleFrom() == compare.getDoubleTo()
                && compare.includeTo && this.includeFrom;
        hasIntersection |= this.equals(compare);
        
        return hasIntersection;
    }
    
    /**
     * 判断一个数字是否在此数值范围之内
     * @param number
     * @return
     */
    public boolean contains(Double number) {
        boolean contains = (number > this.from || number == this.from
                && this.includeFrom)
                && (number < this.to || number == this.to && this.includeTo);
        return contains;
    }
    
    /**
     * 判断另一个数值范围的两个端点是否均在此数值范围内
     * @param compare
     * @return
     */
    public boolean containsTermipointOf(NumRange compare) {
        String rangeType = compare.getRangeType();
        Double fromTermipoint = NumUtil.isLessType(rangeType) ? compare
                .getDoubleTo() : compare.getDoubleFrom();
        Double toTermipoint = NumUtil.isGreaterType(rangeType) ? compare
                .getDoubleFrom() : compare.getDoubleTo();
        return this.contains(fromTermipoint) && this.contains(toTermipoint);
    }
    
    public void setIncludeFrom(boolean includeFrom) {
        this.includeFrom = includeFrom;
    }
    
    public boolean isIncludeFrom() {
        return includeFrom;
    }
    
    public void setIncludeTo(boolean includeTo) {
        this.includeTo = includeTo;
    }

    public boolean isIncludeTo() {
        return includeTo;
    }

    public void setBothInclude(boolean include) {
        setIncludeFrom(include);
        setIncludeTo(include);
    }
    
    public boolean isBothInclude() {
        return includeFrom && includeTo;
    }
    
    public boolean isLongTypeRange() {
        boolean isLong = from == Math.floor(new Double(from));
        isLong &= to == Math.floor(new Double(to));
        return isLong;
    }

    public boolean isDoubleTypeRange() {
        boolean isLong = from == Math.floor(new Double(from));
        isLong &= to == Math.floor(new Double(to));
        return !isLong;
    }
    
    public void transposeInclude() {
        boolean includeTmp = this.includeFrom;
        this.includeFrom = this.includeTo;
        this.includeTo = includeTmp;
    }
}
