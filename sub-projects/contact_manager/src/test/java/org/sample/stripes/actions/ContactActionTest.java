package org.sample.stripes.actions;

import org.junit.Test;
import org.sample.beans.Contact;
import org.sample.mybatis.mappers.ContactMapper;
import org.sample.stripes.test.RoundTripTestBase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

public class ContactActionTest extends RoundTripTestBase<ContactAction> {

	public ContactMapper contactMapper;

	@Test
	public void list() throws Exception {
		trip.execute();
		assertEquals(ContactAction.LIST, trip.getDestination());
		assertNotNull(action.getContactList());
	}

	@Test
	public void create() throws Exception {
		trip.execute("create");
		assertNotNull(action.getContact());
	}

	@Test
	public void edit() throws Exception {
		trip.setParameter("id", "1");
		trip.execute("edit");
		verify(contactMapper).select(1);
	}

	@Test
	public void saveInsert() throws Exception {

		trip.execute("save");

		verify(contactMapper).insert(any(Contact.class));

	}

	@Test
	public void saveUpdate() throws Exception {

		trip.setParameter("contact.id", "1");

		trip.execute("save");

		verify(contactMapper).update(any(Contact.class));

	}

}
