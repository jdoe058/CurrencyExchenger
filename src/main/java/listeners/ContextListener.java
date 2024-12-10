package listeners;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import utilities.ConnectionManager;

@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ConnectionManager.closePool();
        ServletContextListener.super.contextDestroyed(sce);
    }
}
