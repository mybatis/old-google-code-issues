package org.sample.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class GuiceInjector {

	static GuiceInjector guiceInjector;
	Injector injector;

	public static GuiceInjector getInstance() {
		if (guiceInjector == null) {
			guiceInjector = new GuiceInjector();
		}
		return guiceInjector;
	}

	public static void init(Module... module) {
		getInstance().init(Guice.createInjector(module));
	}

	public static Injector get() {
		return getInstance().getInjector();
	}

	public Injector getInjector() {
		return this.injector;
	}

	public void init(Injector injector) {
		if (this.injector == null) {
			this.injector = injector;
		}
	}

	public void release() {
		this.injector = null;
	}

}
