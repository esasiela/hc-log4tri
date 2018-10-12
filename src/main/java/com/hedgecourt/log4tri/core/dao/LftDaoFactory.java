package com.hedgecourt.log4tri.core.dao;

import java.io.IOException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NoInitialContextException;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.hedgecourt.log4tri.core.LftConfig;
import com.hedgecourt.log4tri.core.dao.impl.LftConfigDaoImpl;

public class LftDaoFactory {

    private static Log log = LogFactory.getLog(LftDaoFactory.class);
    private static DataSource dataSource = null;
    private static Boolean loadedFlag = new Boolean(false);

    public static final String CONTEXT_SYSTEM_PROPERTY = LftConfig.PROPERTY_PREFIX + ".config";

    private synchronized static void initialize() {
        log.trace("exec");
        if (loadedFlag.booleanValue()) {
            log.warn("loaded flag is already set, someone else beat me to it");
            return;
        }
        loadedFlag = new Boolean(true);

        log.debug("loaded flag is not set, continuing with initialization");
        initializeImplClasses();
        initializeDataSource();
    }

    private synchronized static void initializeImplClasses() {
        log.trace("exec");
        // TODO load all expected impl classes, allowing fail fast rather than waiting for user to hit a certain use case
    }

    private synchronized static void initializeDataSource() {
        log.trace("exec");

        // store a reference to either a container-supplied datasource or create
        // one yourself
        try {
            log.debug("looking in JNDI context for data source");

            Context envContext = new InitialContext();
            Context initContext = (Context) envContext.lookup("java:/comp/env");

            dataSource = (DataSource) initContext.lookup(LftConfig.DB_CONTEXT_RESOURCE);

            if (dataSource != null) {
                log.info("initialized dataSource from JNDI");
            }

        } catch (NoInitialContextException E) {
            log.debug("no initial context, are you sandalone app? checking sys prop '" + CONTEXT_SYSTEM_PROPERTY + ".context'...");

            String contextFilename = System.getProperty(CONTEXT_SYSTEM_PROPERTY);

            if (StringUtils.isBlank(contextFilename)) {
                log.fatal("no context file specified in system property '" + CONTEXT_SYSTEM_PROPERTY + "'.");
            } else {
                log.info("found '" + CONTEXT_SYSTEM_PROPERTY + "'=[" + contextFilename + "]");

                try {
                    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                    Document doc = null;

                    doc = docBuilder.parse(contextFilename);

                    // now look for a resource node in the context.
                    NodeList resourceNodes = doc.getElementsByTagName("Resource");
                    for (int nodeIdx = 0; nodeIdx < resourceNodes.getLength(); nodeIdx++) {

                        Node resourceNode = resourceNodes.item(nodeIdx);

                        if (resourceNode.getNodeType() == Node.ELEMENT_NODE) {
                            log.debug("have a resource node");

                            Element resourceElement = (Element) resourceNode;

                            log.debug("name is [" + resourceElement.getAttribute("name") + "]");

                            if (StringUtils.equals(resourceElement.getAttribute("name"), LftConfig.DB_CONTEXT_RESOURCE)) {
                                log.debug("located resource node for [" + LftConfig.DB_CONTEXT_RESOURCE + "]");

                                BasicDataSource bds = new BasicDataSource();
                                bds.setDriverClassName(resourceElement.getAttribute("driverClassName"));
                                bds.setUrl(resourceElement.getAttribute("url"));
                                bds.setUsername(resourceElement.getAttribute("username"));
                                bds.setPassword(resourceElement.getAttribute("password"));

                                dataSource = bds;
                            }

                        }

                    }

                } catch (ParserConfigurationException Ex) {
                    log.error("failed configuring the context XML parser: " + Ex.getMessage());
                } catch (SAXException Es) {
                    log.error("failed parsing context XML file: " + Es.getMessage());
                } catch (IOException Ei) {
                    log.error("io error parsing context XML file: " + Ei.getMessage());
                }
            }

        } catch (Exception E) {
            log.fatal("failed establishing data source", E);
        }
    }

    public static DataSource getDataSource() {
        if (!loadedFlag.booleanValue()) {
            log.debug("loaded flag is false, initializing");
            initialize();
        }
        return dataSource;
    }

    public static LftConfigDao getLftConfigDao() {
        // TODO pull the impl class from the init list rather than hardcode here
        return new LftConfigDaoImpl();
    }
}
