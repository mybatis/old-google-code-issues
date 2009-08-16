<%@include file="../common/IncludeTop.jsp"%>

<html:form action="/shop/signon" method="POST">

<table align="center" border="0">
<tr>
<td colspan="2">Please enter your username and password.
<br />&nbsp;</td>
</tr>
<tr>
<td>Username:</td>
<td><input type="text" name="username" value="j2ee" /></td>
</tr>
<tr>
<td>Password:</td>
<td><input type="password" name="password" value="j2ee" /></td>
</tr>
<tr>
<td>&nbsp;</td>
<td><input type="image" border="0" src="../images/button_submit.gif" /></td>
</tr>
</table>
</html:form>
<center>
<html:link page="/shop/newAccountForm.shtml">
<img border="0" src="../images/button_register_now.gif" />
</html:link>
</center>


<%@include file="../common/IncludeBottom.jsp"%>

