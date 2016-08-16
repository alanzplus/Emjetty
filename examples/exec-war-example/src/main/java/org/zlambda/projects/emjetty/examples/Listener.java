package org.zlambda.projects.emjetty.examples;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class Listener implements ServletContextListener {
    private static final Logger LOGGER = LogManager.getLogger(Listener.class);
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("init");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("destroy");
    }
}
