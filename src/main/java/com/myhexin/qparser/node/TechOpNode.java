package com.myhexin.qparser.node;

import java.util.*;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.define.EnumDef.OperatorType;
import com.myhexin.qparser.define.EnumDef.TechOpType;
import com.myhexin.qparser.except.BadDictException;
import com.myhexin.qparser.except.QPException;

public class TechOpNode extends SemanticNode {
	@Override
	protected SemanticNode copy() {
		TechOpNode rtn = new TechOpNode();
		rtn.innerOp=innerOp;
		rtn.isFromOperator=isFromOperator;
		rtn.leftSize=leftSize;
		rtn.maxBindNum=maxBindNum;
		rtn.operType=operType;
		rtn.periodNode=periodNode;




		super.copy(rtn);
		return rtn;
	}
	private TechOpNode(){}
    public TechOpNode(String text) {
        super(text);
        type = NodeType.TECHOPERATOR;
    }
    
    private TechOpNode(int maxBindNum, OperatorNode opNode) {
        this(opNode.text);
        this.maxBindNum = maxBindNum;
        type = NodeType.TECHOPERATOR;
        innerOp = opNode;
    }

    @Override
    public void parseNode(HashMap<String, String> k2v, Query.Type qtype) throws QPException {
        String maxBindNumStr = k2v.get("maxBindNum");
        String opType = k2v.get("techopType");
        if(maxBindNumStr == null || opType == null){
            String error = "TechOperator 在词典无 max_bindnum 或 oper_type";
            throw new BadDictException(error, NodeType.TECHOPERATOR, text);            
        }
        setOperType(getOperTypeByStr(opType));
        maxBindNum = Integer.parseInt(maxBindNumStr);
    }
    
    /** 从词典读取TechOp类型 
     * @throws BadDictException */
    public TechOpType getOperTypeByStr(String opType) throws BadDictException {
        if ("common".equals(opType)){
            return TechOpType.COMMON;
        } else if ("for_line".equals(opType)){
            return TechOpType.FOR_LINE;
        } else if ("for_idx".equals(opType)){
            return TechOpType.FOR_IDX;
        } else {
            String error = String.format("词典techopType[%s]不支持",opType);
            throw new BadDictException(error, NodeType.TECHOPERATOR, text); 
        }
    }

    public static TechOpNode getTechFromOp(OperatorNode opNode){
        TechOpNode techOp = new TechOpNode(2,opNode);
        techOp.setOperType(TechOpType.COMMON);
        return techOp;
    }     
    
    public boolean isFromOperator(){
        return innerOp != null;
    }
    
    public static boolean canBeATechOp(OperatorNode opNode) {
        OperatorType opType = opNode.operatorType;
        switch(opType){
            case SUBTRACT: 
                return true;
            default:
                return false;
        }
    }
    
    public String getUserOperText(){
        if(innerOp == null || !"sub".equals(text)){
            return text;
        }else{
            return innerOp.standard.getRangeType();
        }
    }
    
    public void setPeriod(TechPeriodNode period){
        periodNode = period;
    }
    
    public TechPeriodNode getPeriod(){
        return periodNode;
    }
    
    public void setOperType(TechOpType operType) {
        this.operType = operType;
    }

    public TechOpType getOperType() {
        return operType;
    }

    public int maxBindNum;
    public boolean isFromOperator = false;
    public OperatorNode innerOp ;
    public int leftSize;
    private TechPeriodNode periodNode = null;
    private TechOpType operType;
    
}
