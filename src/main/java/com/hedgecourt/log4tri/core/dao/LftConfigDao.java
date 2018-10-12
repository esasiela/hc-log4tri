package com.hedgecourt.log4tri.core.dao;

import java.util.Properties;

public interface LftConfigDao extends LftDao {
    public Properties read() throws LftDaoException;

    public boolean create(String key, String value) throws LftDaoException;

    public boolean delete(String key) throws LftDaoException;

    public boolean update(String key, String value) throws LftDaoException;
}
