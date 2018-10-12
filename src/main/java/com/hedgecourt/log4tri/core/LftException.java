package com.hedgecourt.log4tri.core;

import java.io.Serializable;

public class LftException extends Exception implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6505870804453008362L;

    public LftException() {
        super();
    }

    public LftException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public LftException(Throwable cause) {
        super(cause);
    }

    public LftException(String message, Throwable cause) {
        super(message, cause);
    }

    public LftException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
