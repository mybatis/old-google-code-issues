package org.sample.stripes.actions;

import com.google.inject.Inject;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import org.sample.beans.Contact;
import org.sample.mybatis.mappers.ContactMapper;
import org.sample.stripes.extensions.BaseAction;

import java.util.List;

public class ContactAction extends BaseAction {

	public static final String LIST = "/WEB-INF/jsp/contact/contactList.jsp";
	public static final String EDIT = "/WEB-INF/jsp/contact/contactEdit.jsp";

	ContactMapper contactMapper;

	List<Contact> contactList;
	Integer id;
	Contact contact = new Contact();

	@Inject
	public ContactAction(ContactMapper contactMapper) {
		this.contactMapper = contactMapper;
	}

	@DefaultHandler
	public Resolution list() {
		contactList = contactMapper.selectAll();
		return new ForwardResolution(LIST);
	}

	public Resolution create() {
		contact = new Contact();
		return new ForwardResolution(EDIT);
	}

	public Resolution edit() {
		contact = contactMapper.select(id);
		return new ForwardResolution(EDIT);
	}

	public Resolution save() {
		if(nullOrZero(contact.getId())){
			contactMapper.insert(contact);
		}else{
			contactMapper.update(contact);
		}
		return new RedirectResolution(getClass());
	}

	public Resolution delete() {
		contactMapper.delete(contact.getId());
		return new RedirectResolution(getClass());
	}

	public List<Contact> getContactList() {
		return contactList;
	}

	public void setContactList(List<Contact> contactList) {
		this.contactList = contactList;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}
}
