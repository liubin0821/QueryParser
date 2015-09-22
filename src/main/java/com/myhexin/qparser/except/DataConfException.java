package com.myhexin.qparser.except;

public class DataConfException extends QPException {
    private static final long serialVersionUID = 886283098193132484L;
    
    public DataConfException(String fileName, int lineNo, String msg) {
        super(String.format("In file [%s] line %d: %s", fileName,
                lineNo, msg));
    }
    
    public DataConfException(String fileName, int lineNo,
            String format, Object... args) {
        this(fileName, lineNo, String.format(format, args));
    }
    
    public DataConfException(String fileName, String msg, Throwable cause) {
        this(fileName, -1, msg);
        this.initCause(cause);
    }
}
