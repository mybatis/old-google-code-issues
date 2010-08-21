<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Simple jsp page</title></head>
<body>
<stripes:form beanclass="org.sample.stripes.actions.ContactAction">
	<stripes:hidden name="contact.id"/>
	<table>
		<tr>
			<td>first name</td>
			<td><stripes:text name="contact.firstName" /></td>
		</tr>
		<tr>
			<td>last name</td>
			<td><stripes:text name="contact.lastName" /></td>
		</tr>
		<tr>
			<td>phone</td>
			<td><stripes:text name="contact.phone" /></td>
		</tr>
		<tr>
			<td>email</td>
			<td><stripes:text name="contact.email" /></td>
		</tr>
		<tr>
			<td colspan="2">
				<stripes:submit name="save" />
				<stripes:submit name="list" value="Cancel" />
				<stripes:submit name="delete" />
			</td>
		</tr>
	</table>
</stripes:form>
</body>
</html>
