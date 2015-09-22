package com.myhexin.qparser.except;

import com.myhexin.qparser.define.EnumDef.NodeType;


public class BadDictException extends QPException {
    
    private static final long serialVersionUID = 2241870944858891440L;
    
    public BadDictException(String message,NodeType nodeType,String nodeText) {
        super(message);
        nodeType_ = nodeType;
        nodeText_ = nodeText;
    }

    public String getMessage() {
        return String.format("Bad dict info of %s Node [%s]: %s"
                , nodeType_,nodeText_, msg_);
    }

    private NodeType nodeType_;
    private String nodeText_;
    
}
