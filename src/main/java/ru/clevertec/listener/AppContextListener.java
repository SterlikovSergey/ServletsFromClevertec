package ru.clevertec.listener;
import ru.clevertec.repository.Repository;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Repository repository = new Repository();
        sce.getServletContext().setAttribute("repository", repository);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup code if needed
    }
}