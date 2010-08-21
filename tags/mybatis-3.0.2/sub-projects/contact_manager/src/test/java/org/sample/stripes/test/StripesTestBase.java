package org.sample.stripes.test;

import com.silvermindsoftware.stripes.integration.guice.GuiceContextListener;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.controller.DispatcherServlet;
import net.sourceforge.stripes.controller.StripesFilter;
import net.sourceforge.stripes.mock.MockHttpSession;
import net.sourceforge.stripes.mock.MockRoundtrip;
import net.sourceforge.stripes.mock.MockServletContext;
import org.junit.After;
import org.junit.BeforeClass;
import org.sample.guice.InjectorFactory;
import org.sample.guice.WebModule;
import org.sample.stripes.test.MockActionResolver;
import org.sample.stripes.test.MockConfiguration;

import javax.servlet.ServletContextEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Class description
 */
public class StripesTestBase {

	protected static GuiceContextListener contextListener;
	protected static MockServletContext mockServletContext;
	protected MockHttpSession session;
	protected MockRoundtrip trip;

	@BeforeClass
	public static void setup() {

		// when running multiple classes we need to make sure they do not reinitialize the shared code.
		if (mockServletContext == null) {
			mockServletContext = new MockServletContext("web");

			mockServletContext.addInitParameter("Guice.Modules", WebModule.class.getName());
			mockServletContext.addInitParameter("GuiceInjectorFactory.Class", InjectorFactory.class.getName());

			ServletContextEvent servletContextEvent = new ServletContextEvent(mockServletContext);

			contextListener = new GuiceContextListener();

			contextListener.contextInitialized(servletContextEvent);

			Map<String, String> params = new HashMap<String, String>();

			params.put("Configuration.Class", MockConfiguration.class.getName());
			params.put("ActionResolver.Packages", "org.sample.stripes.actions");
			params.put("ActionBeanContext.Class", ActionBeanContext.class.getName());
			params.put("PopulationStrategy.Class", net.sourceforge.stripes.tag.BeanFirstPopulationStrategy.class.getName());
			params.put("ActionResolver.Class", MockActionResolver.class.getName());
			mockServletContext.addFilter(StripesFilter.class, "Stripes Filter", params);

			mockServletContext.setServlet(DispatcherServlet.class, "StripesDispatcher", null);
		}
	}

	@After
	public void cleanup() {
		getActionResolver().cleanup();
	}

	/* Helper Methods */
	protected void prepareMockRoundtrip(ActionBean actionBean) {
		session = new MockHttpSession(mockServletContext);

		getActionResolver().addMock(actionBean);

		trip = new MockRoundtrip(mockServletContext, actionBean.getClass(), session);
	}

	public static MockConfiguration getConfiguration() {
		return (MockConfiguration) StripesFilter.getConfiguration();
	}

	public static MockActionResolver getActionResolver() {
		return (MockActionResolver) getConfiguration().getActionResolver();
	}

}
