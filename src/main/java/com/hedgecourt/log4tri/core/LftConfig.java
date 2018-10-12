package com.hedgecourt.log4tri.core;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hedgecourt.log4tri.core.dao.LftConfigDao;
import com.hedgecourt.log4tri.core.dao.LftDaoFactory;

public class LftConfig {
    private static Log log = LogFactory.getLog(LftConfig.class);

    private static Boolean loadedFlag = new Boolean(false);
    private static boolean successfulLoad = false;

    private static Properties properties = new Properties();

    public static final String PROPERTY_PREFIX = "log4tri";
    public static final String SILENT_SYSTEM_PROPERTY = PROPERTY_PREFIX + ".config.silentLoad";
    // TODO move this to init parameter, which in turn would either be a servlet attribute or system property
    public static final String DB_CONTEXT_RESOURCE = "jdbc/log4tri";

    // populated by the call to init()
    public static String APPLICATION_NAME = "unpop";
    public static String MODULE_NAME = "unpop";

    public static void main(String[] args) {
        load("test", "batch");
    }

    public synchronized static void reload() {
        log.info("reloading configuration");

        loadedFlag = new Boolean(false);
        load();
    }

    public synchronized static void load(String applicationName, String moduleName) {
        APPLICATION_NAME = applicationName;
        MODULE_NAME = moduleName;
        load();
    }

    protected synchronized static void load() {
        if (loadedFlag.booleanValue() == true) {
            return;
        }
        log.trace("configuration not loaded yet, getting exclusive lock...");

        // we made it here, unlikely any other thread with a different method is
        // competing with us, but for the sake of safety
        synchronized (loadedFlag) {
            // check the flag again, never can be too safe with these things
            if (loadedFlag.booleanValue() == true) {
                log.warn("after obtaining exclusive lock, loaded flag is true. bet you a hamburger this line NEVER runs.");
                return;
            }

            // ok, now we're certain we have the sync to ourselves AND the props
            // are not loaded yet.
            log.trace("have exclusive lock, loading configuration.");

            try {

                // load the properties from the DAO.
                LftConfigDao cd = LftDaoFactory.getLftConfigDao();
                properties = cd.read();

                setSuccessfulLoad(true);

                log.info("configuration loaded");

                if (!StringUtils.equals(System.getProperty(SILENT_SYSTEM_PROPERTY, "false"), "true")) {
                    for (String key : getProperties().stringPropertyNames()) {
                        log.info("key=[" + key + "] val=[" + (StringUtils.contains(key, "password") ? "********" : getProperty(key)) + "]");
                    }
                }
            } catch (Exception E) {

                log.fatal("failed loading configuration.", E);

                // do this to prevent NPE's all over the place which might bury the real error in messy logs
                properties = new Properties();
            }

            // before we release the lock, set the flag to true so no one else
            // tries this.
            loadedFlag = new Boolean(true);
        }

    }

    public static Properties getProperties() {
        return properties;
    }

    public static String getProperty(String key) {
        return getProperties().getProperty(key);
    }

    public static boolean isSuccessfulLoad() {
        return successfulLoad;
    }

    private static void setSuccessfulLoad(boolean isSuccess) {
        successfulLoad = isSuccess;
    }

}
