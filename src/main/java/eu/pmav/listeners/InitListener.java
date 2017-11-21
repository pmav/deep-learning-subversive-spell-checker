package eu.pmav.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class InitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("Running at startup.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
