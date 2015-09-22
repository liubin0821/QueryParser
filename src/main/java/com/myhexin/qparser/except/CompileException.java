package com.myhexin.qparser.except;

public class CompileException extends QPException {

    private static final long serialVersionUID = -7264326980045857635L;

    public CompileException(String message) {
        super(message);
    }
    
    public CompileException(String format, Object... args) {
        super(format, args);
    }
    
    public String getMessage() {
        return msg_;
    }
}
