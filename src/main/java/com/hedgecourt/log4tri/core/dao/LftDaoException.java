package com.hedgecourt.log4tri.core.dao;

import java.io.Serializable;

import com.hedgecourt.log4tri.core.LftException;

public class LftDaoException extends LftException implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1347800960479725847L;

    public LftDaoException() {
        super();
    }

    public LftDaoException(String message) {
        super(message);
    }

    public LftDaoException(Throwable cause) {
        super(cause);
    }

    public LftDaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public LftDaoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
