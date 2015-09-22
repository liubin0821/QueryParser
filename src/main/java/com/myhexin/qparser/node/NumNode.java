package com.myhexin.qparser.node;

import java.util.HashMap;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.Unit;
import com.myhexin.qparser.define.EnumConvert;
import com.myhexin.qparser.define.OperDef;
import com.myhexin.qparser.except.BadDictException;
import com.myhexin.qparser.number.NumRange;

public class NumNode extends SemanticNode implements Cloneable{
	@Override
	protected NumNode copy() {
		NumNode rtn = new NumNode(super.text);
		rtn.isBetween=isBetween;
		rtn.moveType=moveType;
		rtn.needMove=needMove;
		rtn.numinfo=numinfo;
		rtn.oldStr_=oldStr_;

		super.copy(rtn);
		return rtn;
	}
    public NumNode(String text) {
        super(text);
        type = NodeType.NUM;
        oldStr_= text;
    }
    
    public NumNode(){}
    
    public NumNode(String text, double from, double to){
        super(text);
        type = NodeType.NUM;
        oldStr_= text;
        this.numinfo = new NumRange();
        this.numinfo.setNumRange(from, to);
    }
    
    @Override
    public String getPubText() {
        return oldStr_ == null ? text : oldStr_;
    }
    
    public void setNuminfo(NumRange numinfo) {
        this.numinfo = numinfo;
    }
    public NumRange getNuminfo() {
        return numinfo;
    }
    
    public Unit getUnit() {
        String unitStr=null;
        if(numinfo==null){
            unitStr =null;
        }else{
            unitStr = numinfo.getUnit();
        }
        if (unitStr == null) {
            return Unit.UNKNOWN;
        } else {
            return EnumConvert.getUnitFromStr(unitStr);
        }
    }
    public double getFrom(){
        if(numinfo==null){
            return NumRange.MIN_;
        }
        Double from = numinfo.getDoubleFrom();
        return from;
    }
    
    
    public double getTo(){
        if(numinfo==null){
            return NumRange.MAX_;
        }
        Double to = numinfo.getDoubleTo();
        return to;
    }
    
    public long getLongFrom(){
        if(numinfo==null){
            return (new Double(NumRange.MIN_)).longValue() ;
        }
        long from = numinfo.getLongFrom();
        return from;
    }
    
    
    public long getLongTo(){
        if(numinfo==null){
            return (new Double(NumRange.MAX_)).longValue() ;
        }
        long to = numinfo.getLongTo();
        return to;
    }
    
    public String getRangeType() {
        if(numinfo==null){
            return null;
        }
        String rangeType = numinfo.getRangeType();
        if(isBetween){
            return rangeType;
        }
        if(rangeType.equals(OperDef.QP_EQ)){
            rangeType = OperDef.QP_NE;
        }else if(rangeType.equals(OperDef.QP_IN)){
            rangeType = OperDef.QP_NI;
        }else {
            ;//No Op 
        }
        return rangeType;
    }
    
    public boolean equals(NumNode nn){
        if(this==nn){
            return true;
        }
        if(this.numinfo==null||nn.numinfo==null){
            if(this.text.equals(nn.text)){
                return true;
            }else{
                return false;
            }
        }else{
            return this.numinfo.equals(nn.getNuminfo());
        }
    }
    
    public String toString(){
    	String str = super.toString();
    	str += "  NumUnit:"+getUnit();
    	if (numinfo!=null)
    		str += "  Num:" + String.format("(%s)", numinfo.toNumString());
    	return str;
    }
    
    @Override
    public void parseNode(HashMap<String, String> k2v, Query.Type qtype) throws BadDictException {
    }
    
    public enum MoveType{
        UP,DOWN,BOTH
    }
    
    /*public NumNode clone(){
    	NumNode rtn = (NumNode) super.clone();
    	if(numinfo != null){
    		rtn.numinfo = numinfo.clone();
    	}
		return rtn;
    }*/

    public boolean isFake() {
        return this instanceof FakeNumNode;
    }
    
    /**
     * 判断数字是否为整型
     * @return
     */
    public boolean isLongNum() {
        return numinfo!=null && numinfo.isLongTypeRange();
    }
    
    /**
     * 判断数字是否为浮点型
     * @return
     */
    public boolean isDoubleNum() {
        return numinfo!=null && numinfo.isDoubleTypeRange();
    }
    
    public boolean isBetween = true;
    public String oldStr_ = null;
    public String getOldStr_() {
		return oldStr_;
	}

	public void setOldStr_(String oldStr_) {
		this.oldStr_ = oldStr_;
	}

	private NumRange numinfo=null;
    public boolean needMove = false;
    public MoveType moveType = MoveType.BOTH;
    
    /*
     * 比较各属性值是否相等
     * text			内容
     * isCombined	是否被合并
     * isBound		是否被绑定
     * 
     */
    public boolean equals(Object obj) {
    	if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof NumNode))
			return false;
		final NumNode nn = (NumNode) obj;
		
		if (!this.text.equals(nn.text))
			return false;
		if (this.isCombined != nn.isCombined)
			return false;
		if (this.isBoundToIndex() != nn.isBoundToIndex())
			return false;
		if (this.isBetween != nn.isBetween)
			return false;
		if ((this.numinfo == null && nn.numinfo != null)
				|| (this.numinfo!=null && !this.numinfo.equals(nn.numinfo)))
			return false;
		return true;
    }
}
