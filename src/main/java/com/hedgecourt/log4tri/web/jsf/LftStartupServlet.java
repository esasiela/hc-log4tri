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

    private static final String APPLICATION_NAME = "log4tri";
    private static final String MODULE_NAME = "webapp";

    @Override
    public void destroy() {
        super.destroy();
        log.info(APPLICATION_NAME + " " + MODULE_NAME + " shutting down");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log.info(APPLICATION_NAME + " " + MODULE_NAME + " starting up");

        LftConfig.load(APPLICATION_NAME, MODULE_NAME);

    }

}
