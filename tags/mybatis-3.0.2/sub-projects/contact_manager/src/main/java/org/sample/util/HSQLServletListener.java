package org.sample.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Web application lifecycle listener.
 * @author nmaves
 */
public class HSQLServletListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		DatabaseInitializer.init();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		try {
			Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:contacts", "SA", "");

			PreparedStatement s = c.prepareStatement("SHUTDOWN");

			s.execute();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
