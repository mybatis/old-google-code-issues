package org.sample.guice;

import org.junit.Test;

public class EnvironmentSettingsTest {

	@Test(expected = RuntimeException.class)
	public void shouldBlowChunks() {
		new EnvironmentSettings("something.missing.properties");
	}

}
