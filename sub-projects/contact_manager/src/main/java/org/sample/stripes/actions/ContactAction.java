package org.sample.stripes.actions;

import com.google.inject.Inject;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import org.sample.mybatis.mappers.ContactMapper;
import org.sample.stripes.extensions.BaseAction;

public class ContactAction extends BaseAction {

	public static final String LIST = "/WEB-INF/jsp/contact/contactList.jsp";
	public static final String EDIT = "/WEB-INF/jsp/contact/contactEdit.jsp";

	ContactMapper contactMapper;

	@Inject
	public ContactAction(ContactMapper contactMapper) {
		this.contactMapper = contactMapper;
	}

	@DefaultHandler
	public Resolution list() {
		return new ForwardResolution(LIST);
	}

	public Resolution create() {
		return new ForwardResolution(EDIT);
	}

	public Resolution edit() {
		return new ForwardResolution(EDIT);
	}

	public Resolution save() {
		return new RedirectResolution(getClass());
	}

	public Resolution delete() {
		return new RedirectResolution(getClass());
	}

}
