package org.sample.guice;

import com.google.inject.Injector;
import com.silvermindsoftware.stripes.integration.guice.DefaultGuiceInjectorFactory;
import com.silvermindsoftware.stripes.integration.guice.GuiceInjectorFactory;

import javax.servlet.ServletContext;

public class InjectorFactory extends DefaultGuiceInjectorFactory implements GuiceInjectorFactory {
	@Override
	public Injector getInjector(ServletContext servletContext) {
		Injector injector = super.getInjector(servletContext);
		GuiceInjector.getInstance().init(injector);
		return injector;
	}

}
