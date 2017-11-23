package eu.pmav.listeners;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class InitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // Setup logger level.
//        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
//        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
//        rootLogger.setLevel(Level.INFO);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
