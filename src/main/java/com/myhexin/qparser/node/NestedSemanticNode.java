package com.myhexin.qparser.node;

import java.util.HashMap;
import com.myhexin.qparser.Query.Type;
import com.myhexin.qparser.define.EnumDef.NodeType;
import com.myhexin.qparser.except.QPException;

public final class NestedSemanticNode extends SemanticNode {
    public NestedSemanticNode() {}
    @Override
    protected SemanticNode copy() {
    	NestedSemanticNode rtn = new NestedSemanticNode();
			super.copy(rtn);
			return rtn;
	}
    public NestedSemanticNode(String text) {
        super(text);
        type = NodeType.NESTED;
    }

	@Override
	public void parseNode(HashMap<String, String> k2v, Type qtype)
			throws QPException {
		
	}
}
