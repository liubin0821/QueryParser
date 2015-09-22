package com.myhexin.qparser.node;

import java.util.HashMap;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.*;
import com.myhexin.qparser.except.BadDictException;
import com.myhexin.qparser.except.NotSupportedException;
import com.myhexin.qparser.except.UnexpectedException;
import com.myhexin.qparser.number.NumParser;
import com.myhexin.qparser.number.NumRange;


public final class OperatorNode extends SemanticNode {
	@Override
	protected SemanticNode copy() {
		OperatorNode rtn = new OperatorNode(super.text);
		rtn.growthType=growthType;
		rtn.isBetween=isBetween;
		rtn.isFromChange=isFromChange;
		rtn.onOneProp=onOneProp;
		rtn.operatorType=operatorType;
		rtn.standard=standard;


		super.copy(rtn);
		return rtn;
	}
	
    public OperatorNode(String text) {
        super(text);
        type = NodeType.OPERATOR;  
        try {
			this.standard = NumParser.getNumNodeFromStr(">0");
		} catch (NotSupportedException e) {
			//解析 >0 时不应该抛异常
			throw new InternalError();
		} catch (UnexpectedException e) {
		    throw new InternalError();
        }
    }
    public OperatorNode(String text, OperatorType operatorType, boolean isBetween ,
            boolean onOneProp,  NumNode standard) {
        super(text);
        type = NodeType.OPERATOR;  
        this.operatorType = operatorType;
        this.isBetween = isBetween;
        this.onOneProp = onOneProp;
        this.standard = standard;
    }
    
    public static OperatorType getOperatorType(String str){
        OperatorType ot = null;
        if (str.startsWith("add")) {
            ot = OperatorType.ADD;
        } else if (str.startsWith("sub")) {
            ot = OperatorType.SUBTRACT;
        } else if (str.startsWith("mul")) {
            ot = OperatorType.MULTIPLY;
        } else if (str.startsWith("div")) {
            ot = OperatorType.DIVIDE;
        }
        return ot;
    }
    
    @SuppressWarnings("unused")
    public void parseNode(HashMap<String, String> k2v, Query.Type qtype) throws BadDictException {
        String operaTypeStr = k2v.get("OperatorType");
        String isBetweenStr = k2v.get("isBetween");
        String standardStr = k2v.get("standard");
        String onOnePropStr = k2v.get("onOne");
        boolean isRightOpType = operaTypeStr.startsWith("add")
                || operaTypeStr.startsWith("sub")
                || operaTypeStr.startsWith("mul")
                || operaTypeStr.startsWith("div")
                || operaTypeStr.startsWith("rate");
        if (operaTypeStr == null) {
            String err = "Operator 在词典里无OperatorType";
            throw new BadDictException(err, NodeType.OPERATOR, text);
        } else if (isBetweenStr == null) {
            String err = "Operator 在词典里无isBetween";
            throw new BadDictException(err, NodeType.OPERATOR, text);
        } else if (onOnePropStr == null) {
            String err = "Operator 在词典里无onOne";
            throw new BadDictException(err, NodeType.OPERATOR, text);
        }else if (!isRightOpType) {
            String err = "Operator 在词典里的OperatorType信息错误";
            throw new BadDictException(err, NodeType.OPERATOR, text);
        }else if (!isBetweenStr.equals("true") && !isBetweenStr.equals("false")) {
            String err = "Operator 在词典里的isBetween信息错误";
            throw new BadDictException(err, NodeType.OPERATOR, text);
        }else if (!onOnePropStr.equals("true") && !onOnePropStr.equals("false")) {
            String err = "Operator 在词典里的onOne信息错误";
            throw new BadDictException(err, NodeType.OPERATOR, text);
        }
        OperatorType operaT = null;
        if (operaTypeStr.startsWith("add")) {
            operaT = OperatorType.ADD;
        } else if (operaTypeStr.startsWith("sub")) {
            operaT = OperatorType.SUBTRACT;
        } else if (operaTypeStr.startsWith("mul")) {
            operaT = OperatorType.MULTIPLY;
        } else if (operaTypeStr.startsWith("div")) {
            operaT = OperatorType.DIVIDE;
        } else if (operaTypeStr.startsWith("rate")) {
            operaT = OperatorType.RATE;
        }
        boolean ib = isBetweenStr.equals("true");
        boolean op = onOnePropStr.equals("true");
        NumNode nn = null;
        NumRange rn = null;
        if (standardStr != null) {
            nn = new NumNode(standardStr);
            rn =null;
            try {
                rn = NumParser.getNumRangeFromStr(standardStr);
            } catch (NotSupportedException e) {
                throw new BadDictException(e.getMessage(), NodeType.OPERATOR, text);
            }
            nn.setNuminfo(rn);
        }
        isBetween = ib;
        operatorType = operaT;
        onOneProp = op;
        standard = nn;
    }
    
    public String toString(){
    	String str = super.toString();
    	str += "  OperatorType:"+operatorType;
    	return str;
    }
    
    public void setGrowthType(GrowthType growthType) {
        this.growthType = growthType;
    }
    public GrowthType getGrowthType() {
        return growthType;
    }

    public boolean onOneProp =false;
    public OperatorType operatorType = null;
    public boolean isBetween = true;
    public NumNode standard = null;
    public boolean isFromChange = false;
    private GrowthType growthType= null;
}
