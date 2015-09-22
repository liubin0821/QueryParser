package com.myhexin.qparser.node;

import java.util.HashMap;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.LogicType;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.BadDictException;



public final class LogicNode extends SemanticNode{
	@Override
	protected SemanticNode copy() {
		LogicNode rtn = new LogicNode();
	    	rtn.logicType=logicType;
			super.copy(rtn);
			return rtn;
	}
	private LogicNode(){}
    public LogicNode(String text) {
        super(text);
        type = NodeType.LOGIC;        
    }
    @Override
    public void parseNode(HashMap<String, String> k2v, Query.Type qtype) throws BadDictException {
        String msg = "Logic 词典信息错误";
        String logicTypeStr = k2v.get("logicType");
        LogicType lt = LogicNode.getLogicType(logicTypeStr);
        if (lt == null) {
            throw new BadDictException(msg, NodeType.LOGIC, text);
        }
        logicType = lt;
    }
    
    public static LogicType getLogicType(String str){
        LogicType lt = null;
        if (str.startsWith("and")) {
            lt = LogicType.AND;
        } else if (str.startsWith("or")) {
            lt = LogicType.OR;
        }
        return lt;
    }
    
    public LogicType logicType = null;
   
    /*@Override
    public LogicNode clone() {          	
        return (LogicNode) super.clone();
    }*/
}
