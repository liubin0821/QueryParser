package com.myhexin.qparser.date;

import com.myhexin.qparser.define.EnumConvert;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.node.NumNode;
import com.myhexin.qparser.number.NumRange;

public class DateFrequencyInfo {
	
	private DateFrequencyInfo(){}

    public DateFrequencyInfo(Unit setUnit, NumRange setLength, boolean isSequence) {
        setUnit(setUnit);
        setLength(setLength);
        setSequence(isSequence);
    }

    public boolean isUseless() {
        return false;
    }
    
    public void setLength(NumRange length) {
        this.lengthRange = length;
    }

    public NumRange getLength() {
        return lengthRange;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setSequence(boolean isSequence) {
        this.isSequence = isSequence;
    }

    public boolean isSequence() {
        return isSequence;
    }

    public String toString() {
        return String.format("length:%s;unit:%s;isSequence:%s", lengthRange, unit,
                isSequence);
    }
    /**
     * 将FrequencyInfo 转换成NumNode。即使单位为时间单位，也转成数字。
     * @return
     */
    public NumNode toNumNode() {
        String unitStr = EnumConvert.getStrFromUnit(unit);
        String text = String.format("%s%s", lengthRange, unitStr);
        NumNode numNode = new NumNode(text);
        numNode.setNuminfo(lengthRange);
        return numNode;
    }
    
    private NumRange lengthRange = null;
    private Unit unit = null;
    private boolean isSequence = false;
    
    /*
     * 比较各属性值是否相等
     * text			内容
     * isCombined	是否被合并
     * isBound		是否被绑定
     * props		属性是否相同
     */
    public boolean equals(Object obj) {
    	if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DateFrequencyInfo))
			return false;
		final DateFrequencyInfo dfi = (DateFrequencyInfo) obj;
		
		if (!this.lengthRange.equals(dfi.lengthRange))
			return false;
		if (this.unit != dfi.unit)
			return false;
		if (this.isSequence != dfi.isSequence)
			return false;
		return true;
    }
}
