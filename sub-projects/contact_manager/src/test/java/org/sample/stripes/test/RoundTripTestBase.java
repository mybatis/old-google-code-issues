package org.sample.stripes.test;

import net.sourceforge.stripes.action.ActionBean;
import org.apache.log4j.Logger;
import org.junit.Before;
import static org.mockito.Mockito.mock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

public class RoundTripTestBase<ActionClass> extends StripesTestBase {
	private static final Logger log = Logger.getLogger(RoundTripTestBase.class);

	protected Class<ActionClass> actionClassType;
	protected ActionClass action;

	@SuppressWarnings({"unchecked"})
	public RoundTripTestBase() {
		actionClassType = ((Class<ActionClass>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
	}

	@SuppressWarnings({"unchecked"})
	@Before
	public void beforeRoundTripTestBase() throws InvocationTargetException, IllegalAccessException, InstantiationException {

		// mock all the fields that are not annotated with @NoMock - they are constructor params
		Field[] fields = this.getClass().getDeclaredFields();
		Map<Class, Object> fieldMap = new HashMap<Class, Object>();
		for (Field f : fields) {
			boolean mock = true;
			for (Annotation anno : f.getAnnotations()) {
				if (anno.annotationType().equals(NoMock.class)) {
					mock = false;
					break;
				}
			}
			if(mock){
				Class<?> type = f.getType();
				log.debug("Creating mock for " + type + " to map.");
				try{
					Object value = mock(type);
					fieldMap.put(type, value);
					f.set(this, value);
				}catch(RuntimeException e){
					log.error("Could not mock " + type + " for field '" + f.getName() + "'");
				}
			}
		}

		// get the constructor for the action we are testing (assumption - one constructor)
		Constructor constructor = actionClassType.getConstructors()[0];

		// build the array for the constructor parameters
		Class[] constructorTypes = constructor.getParameterTypes();
		Object[] constructorParms = new Object[constructorTypes.length];
		for (int i = 0; i < constructorParms.length; i++) {
			constructorParms[i] = fieldMap.get(constructorTypes[i]);
		}

		// create the action
		action = (ActionClass) constructor.newInstance(constructorParms);

		prepareMockRoundtrip((ActionBean) action);
	}

}

