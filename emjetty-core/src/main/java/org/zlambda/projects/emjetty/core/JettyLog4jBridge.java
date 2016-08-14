package org.zlambda.projects.emjetty.core;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.eclipse.jetty.util.log.AbstractLogger;
import org.eclipse.jetty.util.log.Logger;

public class JettyLog4jBridge extends AbstractLogger {
    private final org.apache.logging.log4j.Logger logger;

    public JettyLog4jBridge(org.apache.logging.log4j.Logger logger) {
        this.logger = logger;
    }

    public JettyLog4jBridge(Class<?> clazz) {
        logger = LogManager.getLogger(clazz);
    }

    public JettyLog4jBridge(String name) {
        logger = LogManager.getLogger(name);
    }

    @Override
    protected Logger newLogger(String fullname) {
        return new JettyLog4jBridge(fullname);
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public void warn(String msg, Object... args) {
        logger.warn(msg, args);
    }

    @Override
    public void warn(Throwable thrown) {
        logger.warn(thrown);
    }

    @Override
    public void warn(String msg, Throwable thrown) {
        logger.warn(msg, thrown);
    }

    @Override
    public void info(String msg, Object... args) {
        logger.info(msg, args);
    }

    @Override
    public void info(Throwable thrown) {
        logger.info(thrown);
    }

    @Override
    public void info(String msg, Throwable thrown) {
        logger.info(msg, thrown);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void setDebugEnabled(boolean enabled) {
        warn("Method setDebugEnabled is not implemented");
    }

    @Override
    public void debug(String msg, Object... args) {
        logger.debug(msg, args);
    }

    @Override
    public void debug(Throwable thrown) {
        logger.debug(thrown);
    }

    @Override
    public void debug(String msg, Throwable thrown) {
        logger.debug(msg, thrown);
    }

    @Override
    public void ignore(Throwable ignored) {
        logger.catching(Level.TRACE, ignored);
    }
}
