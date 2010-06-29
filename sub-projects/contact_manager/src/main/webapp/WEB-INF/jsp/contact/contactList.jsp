<%--@elvariable id="actionBean" type="org.sample.stripes.actions.ContactAction"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Simple jsp page</title></head>
<body>
<c:if test="${empty actionBean.contactList}">
	No contacts found, <stripes:link beanclass="org.sample.stripes.actions.ContactAction" event="create">click here to add one.</stripes:link>
</c:if>
<c:if test="${!empty actionBean.contactList}">
	<table>
		<tr>
			<td>id</td>
			<td>first name</td>
			<td>last name</td>
			<td>phone</td>
			<td>email</td>
		</tr>
		<c:forEach items="${actionBean.contactList}" var="contact">
			<tr>
				<td>
					<stripes:link beanclass="org.sample.stripes.actions.ContactAction" event="edit">
						<stripes:param name="id" value="${contact.id}"/>
						edit
					</stripes:link>
				</td>
				<td>${contact.firstName}</td>
				<td>${contact.lastName}</td>
				<td>${contact.phone}</td>
				<td>${contact.email}</td>
			</tr>
		</c:forEach>
	</table>
</c:if>
</body>
</html>
