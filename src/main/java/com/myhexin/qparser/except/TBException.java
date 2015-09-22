package com.myhexin.qparser.except;

public class TBException extends QPException {

    private static final long serialVersionUID = -668543194200975060L;

    public TBException(String message) {
        super(message);
    }

    public TBException(String format, Object... args) {
        super(format, args);
    }

    public String getMessage() {
        return msg_;
    }
}
