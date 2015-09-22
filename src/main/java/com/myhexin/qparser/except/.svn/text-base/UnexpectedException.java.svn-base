package com.myhexin.qparser.except;

import com.myhexin.qparser.define.MsgDef;

public class UnexpectedException extends QPException {
    private static final long serialVersionUID = -1027664244244143142L;

    public UnexpectedException(String message) {
        super(message);
    }
    
    public UnexpectedException(String msg, Throwable cause) {
        this(msg);
        this.initCause(cause);
    }
    
    public UnexpectedException(String format, Object... args) {
        super(format, args);
    }
    
    public String getMessage() {
        return MsgDef.UNEXPECTED_STR;
    }
}
