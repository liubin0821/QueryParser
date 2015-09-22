package com.myhexin.qparser.node;

import java.util.HashMap;

import com.myhexin.qparser.Query;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.BadDictException;

public final class SpecialNode extends SemanticNode{
	@Override
	protected SemanticNode copy() {
		SpecialNode rtn = new SpecialNode(super.text);
		rtn.msg_=msg_;
		super.copy(rtn);
		return rtn;
	}
    public SpecialNode(String text) {
        super(text);
        type = NodeType.SPECIAL;        
    }
    public void parseNode(HashMap<String, String> k2v, Query.Type qtype) 
        throws BadDictException {
        msg_ = k2v.get("msg");
        if (msg_ == null){
            throw new BadDictException("Special 词典提示信息缺失", NodeType.SPECIAL, text);
        }
    }
    
    public String getMsg() { return msg_; }
    
    private String msg_ = null;
}
