package com.myhexin.qparser.except;

public class PinyinException extends QPException{
    
    /**
     * 
     */
    private static final long serialVersionUID = 2619030475321844999L;
    
    public PinyinException( String message ){
        super(message);
        errorStr_ = message;
    }
    
    public String getMessage(){
       return String.format("拼音拼写错误");
    }
    
    //详细出错信息
    String errorStr_;
}
