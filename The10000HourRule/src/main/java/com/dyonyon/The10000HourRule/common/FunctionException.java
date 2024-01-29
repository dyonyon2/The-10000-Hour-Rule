package com.dyonyon.The10000HourRule.common;

public class FunctionException extends Throwable{
    public FunctionException() {
    }

    public FunctionException(String message) {
        super(message);
    }

    public FunctionException(String message, Throwable cause) {
        super(message, cause);
    }

    public FunctionException(Throwable cause) {
        super(cause);
    }

    public FunctionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
