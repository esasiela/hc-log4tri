package com.hedgecourt.log4tri.web.jsf;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hedgecourt.log4tri.core.LftConfig;

public class LftStartupServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 470760483039603687L;

    private Log log = LogFactory.getLog(LftStartupServlet.class);

    // configured by servlet init parameters "application.name" and "module.name"
    private static String APPLICATION_NAME = "";
    private static String MODULE_NAME = "";

    @Override
    public void destroy() {
        super.destroy();
        log.info(APPLICATION_NAME + " " + MODULE_NAME + " shutting down");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        APPLICATION_NAME = config.getInitParameter("application.name");
        MODULE_NAME = config.getInitParameter("module.name");

        log.info(APPLICATION_NAME + " " + MODULE_NAME + " starting up");

        LftConfig.load(APPLICATION_NAME, MODULE_NAME);

    }

}
