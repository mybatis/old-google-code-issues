package org.sample.mybatis.mappers;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.ibatis.session.SqlSessionManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sample.beans.Contact;
import org.sample.guice.WebModule;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ContactMapperTest extends MapperTestBase {

	ContactMapper contactMapper;
	SqlSessionManager ssm;

	@Before
	public void beforeContactMapperTest() {
		// todo - refactor to super class
		Injector injector = Guice.createInjector(new WebModule());
		ssm = injector.getInstance(SqlSessionManager.class);
		contactMapper = injector.getInstance(ContactMapper.class);
		Assert.assertNotNull(contactMapper);
	}

	@Test
	public void crudOperations() {
		ssm.startManagedSession();
		try {
			
			Contact expected = new Contact();
			expected.setFirstName("larry");
			expected.setLastName("meadors");
			expected.setPhone("123-456-7890");
			expected.setEmail("blah@blah.com");

			// get the contact count before the test
			int startSize = contactMapper.selectAll().size();

			// create
			assertNull(expected.getId());
			contactMapper.insert(expected);
			assertNotNull(expected.getId());

			// read
			Contact actual = contactMapper.select(expected.getId());
			assertNotNull(actual);
			assertEquals(expected.getFirstName(), actual.getFirstName());
			assertEquals(startSize + 1, contactMapper.selectAll().size());

			// update
			expected.setFirstName("Larry!");
			contactMapper.update(expected);
			actual = contactMapper.select(expected.getId());
			assertNotNull(actual);
			assertEquals(expected.getFirstName(), actual.getFirstName());
			assertEquals(startSize + 1, contactMapper.selectAll().size());

			//delete
			contactMapper.delete(expected.getId());
			assertEquals(startSize, contactMapper.selectAll().size());

		} finally {
			ssm.rollback();
			ssm.close();
		}
	}

}
