package org.sample.stripes.test;

import net.sourceforge.stripes.config.ConfigurableComponent;
import net.sourceforge.stripes.config.RuntimeConfiguration;
import net.sourceforge.stripes.exception.StripesRuntimeException;
import net.sourceforge.stripes.localization.LocalePicker;

import java.util.HashMap;
import java.util.Map;

public class MockConfiguration extends RuntimeConfiguration {

	/** Mock Provisions **/
	protected Map<Class<? extends ConfigurableComponent>, ConfigurableComponent> substitutes
			= new HashMap<Class<? extends ConfigurableComponent>, ConfigurableComponent>();

	public void addMockComponent(Class<? extends ConfigurableComponent> componentType, ConfigurableComponent compoment) {
		substitutes.put(componentType, compoment);
	}

	public void clearMockComponent() {
		substitutes.clear();
	}

	/** Overriden Runtimes **/
	@Override
	public LocalePicker getLocalePicker() {
		LocalePicker localePicker = (LocalePicker)substitutes.get(LocalePicker.class);
		if( localePicker== null) {
			localePicker = super.getLocalePicker();
		}
		return localePicker;
	}

	@Override
	protected <T extends ConfigurableComponent> T initializeComponent(Class<T> componentType, String propertyName) {
		ConfigurableComponent component = substitutes.get(componentType);
		if(component == null) {
			component = super.initializeComponent(componentType, propertyName);
		} else {
			try {
                component.init(this);
            }
            catch (Exception e) {
                throw new StripesRuntimeException("Could not instantiate configured "
                        + componentType.getSimpleName() + "]. Please check "
                        + "the configuration parameters specified in your web.xml.", e);

            }
		}
		return (T)component;
	}
}
