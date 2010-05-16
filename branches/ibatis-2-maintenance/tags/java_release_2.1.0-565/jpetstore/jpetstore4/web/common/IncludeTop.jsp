<%@page contentType="text/html"%>
<%@ taglib uri="struts-logic" prefix="logic" %>
<%@ taglib uri="struts-bean" prefix="bean" %>
<%@ taglib uri="struts-html" prefix="html" %>

<html><head><title>JPetStore Demo</title>
<meta content="text/html; charset=windows-1252" http-equiv="Content-Type" />
<META HTTP-EQUIV="Cache-Control" CONTENT="max-age=0">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta http-equiv="expires" content="0">
<META HTTP-EQUIV="Expires" CONTENT="Tue, 01 Jan 1980 1:00:00 GMT">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
</head>

<body bgcolor="white">

<table background="../images/bkg-topbar.gif" border="0" cellspacing="0" cellpadding="5" width="100%">
  <tbody>
  <tr>
    <td><html:link page="/shop/index.shtml"><img border="0" src="../images/logo-topbar.gif" /></html:link></td>
    <td align="right"><html:link page="/shop/viewCart.shtml"><img border="0" name="img_cart" src="../images/cart.gif" /></html:link>
      <img border="0" src="../images/separator.gif" />

<logic:notPresent name="accountBean" scope="session">
  <html:link page="/shop/signonForm.shtml">
  <img border="0" name="img_signin" src="../images/sign-in.gif" /></html:link>
</logic:notPresent>
<logic:present name="accountBean" scope="session">
  <logic:notEqual name="accountBean" property="authenticated" value="true" scope="session">
        <html:link page="/shop/signonForm.shtml">
        <img border="0" name="img_signin" src="../images/sign-in.gif" /></html:link>
  </logic:notEqual>
</logic:present>

<logic:present name="accountBean" scope="session">
  <logic:equal name="accountBean" property="authenticated" value="true" scope="session">
      <html:link page="/shop/signoff.shtml">
      <img border="0" name="img_signout" src="../images/sign-out.gif" /></html:link>
      <img border="0" src="../images/separator.gif" />
      <html:link page="/shop/editAccountForm.shtml">
      <img border="0" name="img_myaccount" src="../images/my_account.gif" /></html:link>
  </logic:equal>
</logic:present>

      <img border="0" src="../images/separator.gif" /><a href="../help.html"><img border="0" name="img_help" src="../images/help.gif" /></a>
    </td>
    <td align="left" valign="bottom">
      <html:form method="post" action="/shop/searchProducts.shtml">
        <input name="keyword" size="14" />&nbsp;<input border="0" src="../images/search.gif" type="image" />
      </html:form>
    </td>
  </tr>
  </tbody>
</table>

<%@include file="../common/IncludeQuickHeader.jsp"%>

<!-- Support for non-traditional but simple message -->
<logic:present name="message">
  <b><font color="BLUE"><bean:write name="message" /></font></b>
</logic:present>

<!-- Support for non-traditional but simpler use of errors... -->
<logic:present name="errors">
  <logic:iterate id="error" name="errors">
    <B><FONT color=RED>
      <BR><bean:write name="error" />
    </FONT></B>
  </logic:iterate>
</logic:present>
