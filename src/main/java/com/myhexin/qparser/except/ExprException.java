package com.myhexin.qparser.except;


public class ExprException extends QPException {

    /**
     * 
     */
    private static final long serialVersionUID = 7413934194762665338L;

    public ExprException( String message, String stateInfo ){
        super(message);
        exceptPart_ = message;
        stateInfo_ = stateInfo;
    }
    
    public String getMessage(){
       return String.format("请检查:%s  重点检查:%s ", exceptPart_, stateInfo_); 
    }
   
    private String exceptPart_;
    private String stateInfo_;
    
}
