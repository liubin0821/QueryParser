package com.myhexin.qparser.except;


public class BacktestCondException extends QPException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7399682365713963054L;
	public final static String EMPTY_PARSE_RESULT ="解析结果为空";
	
	public BacktestCondException(String message) {
        super(message);
    }
    
    public BacktestCondException(String msg, Throwable cause) {
        this(msg);
        this.initCause(cause);
    }
    
    public BacktestCondException(String format, Object... args) {
        super(format, args);
    }
    
    private final static String MSG_PREFIX = "出错了: ";
    public String getMessage() {
        return MSG_PREFIX  + msg_;
    }
}
