
<bean:define id="myList" name="accountBean" property="myList" />

<logic:present name="myList" >
<p>&nbsp;</p>
<table align="right" bgcolor="#008800" border="0" cellspacing="2" cellpadding="3">
  <tr bgcolor="#CCCCCC"><td>
<font size="4"><b>Pet Favorites</b></font>
<font size="2"><i><br />Shop for more of your <br />favorite pets here.</i></font>
  </td></tr>
  <tr bgcolor="#FFFF88">
  <td>
  <logic:iterate id="product" name="myList"  >
    <html:link paramId="productId" paramName="product" paramProperty="productId" page="/shop/viewProduct.shtml">
        <bean:write name="product" property="name" /></html:link>
    <br />
    <font size="2">(<bean:write name="product" property="productId" />)</font>
    <br />
  </logic:iterate>
  </td>
  </tr>
  <tr>
  <td bgcolor="#FFFFFF">
  <logic:notEqual name="myList" property="firstPage" value="true" >
    <a href="switchMyListPage.shtml?pageDirection=previous&account.listOption=<bean:write name="accountBean" property="account.listOption"/>&account.bannerOption=<bean:write name="accountBean" property="account.bannerOption"/>"><img src="../images/button_prev.gif" border="0"></a>
  </logic:notEqual>
  <logic:notEqual name="myList" property="lastPage" value="true" >
    <a href="switchMyListPage.shtml?pageDirection=next&account.listOption=<bean:write name="accountBean" property="account.listOption"/>&account.bannerOption=<bean:write name="accountBean" property="account.bannerOption"/>"><img src="../images/button_next.gif" border="0"></a>
  </logic:notEqual>
  </td>
  </tr>

</table>
</logic:present>




