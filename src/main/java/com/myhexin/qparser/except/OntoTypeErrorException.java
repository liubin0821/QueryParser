package com.myhexin.qparser.except;

public class OntoTypeErrorException extends UnexpectedException {
    private static final long serialVersionUID = -1684112128693017941L;

    public OntoTypeErrorException(String message) {
        super(message);
    }
    
    public String getMessage() {
        return "对不起，未能正确处理您的问句 - 系统发生未知错误";
    }
}
