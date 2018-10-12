package com.hedgecourt.log4tri.core.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hedgecourt.log4tri.core.LftConfig;
import com.hedgecourt.log4tri.core.dao.LftConfigDao;
import com.hedgecourt.log4tri.core.dao.LftDaoException;
import com.hedgecourt.log4tri.core.dao.LftDaoFactory;

public class LftConfigDaoImpl extends LftBaseDaoImpl implements LftConfigDao {

    private Log log = LogFactory.getLog(LftConfigDaoImpl.class);

    public static final String ENVIRONMENT_PROPERTY = LftConfig.PROPERTY_PREFIX + ".environment";

    public LftConfigDaoImpl() {
        super();
    }

    @Override
    public Properties read() throws LftDaoException {
        // TODO add an argument to read() that allows the module to be specified
        log.trace("exec");
        Properties p = new Properties();

        DataSource ds = null;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            ds = LftDaoFactory.getDataSource();

            if (ds == null) {
                log.fatal("null datasource, cannot load config, application behavior is certain to be funky.");
                throw new LftDaoException("null datasource, cannot load config");
            }
            conn = ds.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT name,val FROM configuration ORDER BY name ASC");
            while (rs.next()) {
                String key = rs.getString(1);
                p.put(rs.getString(1), rs.getString(2));
                log.trace("loaded key/val [" + key + "] [" + p.getProperty(key) + "]");
            }

        } catch (Exception E) {
            if (E instanceof LftDaoException) {
                throw (LftDaoException) E;
            } else {
                throw new LftDaoException("failed reading configuration from database", E);
            }
        } finally {
            try {
                rs.close();
            } catch (Exception E) {
            }
            try {
                stmt.close();
            } catch (Exception E) {
            }
            try {
                conn.close();
            } catch (Exception E) {
            }
        }

        // TODO remove stupid environment name hack that completely depends on dev being windows and production being anything else
        // for reals, shouldnt the env name be stored in the confg database? seems like the simplest usecase for config
        if (StringUtils.equals("Windows 10", System.getProperty("os.name"))) {
            p.setProperty(ENVIRONMENT_PROPERTY, "dev");

        } else {
            p.setProperty(ENVIRONMENT_PROPERTY, "prd");
        }

        return p;
    }

    @Override
    public boolean create(String key, String value) throws LftDaoException {
        log.trace("exec");

        DataSource ds = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        int retval = -1;
        try {
            ds = LftDaoFactory.getDataSource();
            conn = ds.getConnection();
            stmt = conn.prepareStatement("INSERT INTO configuration (name,val) VALUES(?,?)");

            stmt.setString(1, key);
            stmt.setString(2, value);

            retval = stmt.executeUpdate();
            log.trace("executeUpdate returned [" + retval + "]");

        } catch (SQLIntegrityConstraintViolationException E) {
            log.debug("sql integrity constraint violation, must be a duplicate row");
            retval = 0;
        } catch (Exception E) {
            throw new LftDaoException("failed inserting new configuration row", E);
        } finally {
            try {
                stmt.close();
            } catch (Exception E) {
            }
            try {
                conn.close();
            } catch (Exception E) {
            }
        }

        return retval == 1;
    }

    @Override
    public boolean delete(String key) throws LftDaoException {
        log.trace("exec");

        DataSource ds = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        int retval = -1;
        try {
            ds = LftDaoFactory.getDataSource();
            conn = ds.getConnection();
            stmt = conn.prepareStatement("DELETE FROM configuration WHERE name = ?");

            stmt.setString(1, key);

            retval = stmt.executeUpdate();
            log.trace("executeUpdate returned [" + retval + "]");

        } catch (Exception E) {
            throw new LftDaoException("failed deleting configuration row", E);
        } finally {
            try {
                stmt.close();
            } catch (Exception E) {
            }
            try {
                conn.close();
            } catch (Exception E) {
            }
        }

        // retVal==1 means 1 row was deleted, anything else is a false/error
        return retval == 1;
    }

    @Override
    public boolean update(String key, String value) throws LftDaoException {
        this.delete(key);
        return this.create(key, value);
    }
}
