package com.myhexin.qparser.except;

import java.io.PrintWriter;
import java.io.StringWriter;

public class QPException extends Exception{
    
    private static final long serialVersionUID = 4859068976770453711L;

    public QPException(String message){
        msg_ = message;
    }
    
    public QPException(String message, Throwable cause) {
        this(message);
        this.initCause(cause);
    }
    
    public QPException(String format, Object... args) {
        msg_ = String.format(format, args);
    }
    
    public String getMessage(){
        return msg_;
    }
    
    public String getLogMsg() {
        StackTraceElement[] trace = this.getStackTrace();
        if(trace.length == 0) return msg_;
        
        StackTraceElement ste = trace[0];
        StringWriter s = new StringWriter();
        PrintWriter pw = new PrintWriter(s);
        pw.print(String.format("Exception in %s:%d - %s", ste.getFileName(),
                ste.getLineNumber(), msg_));
        pw.println();
        this.printStackTrace(pw);
        Throwable cause = this.getCause();
        if(cause != null) {
            pw.println(" Cause by:");
            cause.printStackTrace(pw);
        }
        return s.getBuffer().toString();
    }
    
    protected String msg_ =null;
    
}
