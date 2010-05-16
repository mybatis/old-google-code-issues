<%@include file="../common/IncludeTop.jsp"%>

<html:form method="post" action="/shop/editAccount.shtml">

<html:hidden name="accountBean" property="validation" value="edit" />
<html:hidden name="accountBean" property="username" />

<table cellpadding="10" cellspacing="0" align="center" border="1" bgcolor="#dddddd"><tr><td>


<font color="darkgreen"><h3>User Information</h3></font>
<table border="0" cellpadding="3" cellspacing="1" bgcolor="#FFFF88">
<tr bgcolor="#FFFF88"><td>
User ID:</td><td><bean:write name="accountBean" property="username" />
</td></tr><tr bgcolor="#FFFF88"><td>
New password:</td><td><html:password name="accountBean" property="password" />
</td></tr><tr bgcolor="#FFFF88"><td>
Repeat password:</td><td> <html:password name="accountBean" property="repeatedPassword" />
</td></tr>
</table>

<%@include file="IncludeAccountFields.jsp"%>

</td></tr></table>

<br /><center>
<input border="0" type="image" src="../images/button_submit.gif" name="submit" value="Save Account Information" />
</center>

</html:form>
<p>
<center><b><html:link page="/shop/listOrders.shtml">My Orders</html:link></b></center>

<%@include file="../common/IncludeBottom.jsp"%></p>

