package org.sample.guice;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nmaves
 */
public class WebModuleTest {

	@Test
	public void instance() {

		try {
			WebModule module = new WebModule();
		} catch (Exception e) {
			e.printStackTrace();
			fail("should not have thrown an exception");
		}


	}

}
