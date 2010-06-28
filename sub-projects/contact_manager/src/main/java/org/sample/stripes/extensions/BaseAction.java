package org.sample.stripes.extensions;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;

public class BaseAction implements ActionBean {

	private static final Logger log = Logger.getLogger(BaseAction.class);

	protected String title = "org.sample";

	ActionBeanContext context;
	protected SqlSessionManager sqlSessionManager;

	public ActionBeanContext getContext() {
		return context;
	}

	public void setContext(ActionBeanContext context) {
		this.context = context;
	}

	public String getTitle() {
		return title;
	}

}
