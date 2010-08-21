<%@include file="../common/IncludeTop.jsp"%>

<bean:define id="cart" name="cartBean" property="cart" />

<table border="0" width="100%" cellspacing="0" cellpadding="0">
<tr><td valign="top" width="20%" align="left">
<table align="left" bgcolor="#008800" border="0" cellspacing="2" cellpadding="2">
<tr><td bgcolor="#FFFF88">
<html:link page="/shop/index.shtml"><b><font color="BLACK" size="2">&lt;&lt; Main Menu</font></b></html:link>
</td></tr>
</table>
</td><td valign="top" align="center">
<h2 align="center">Shopping Cart</h2>
<html:form action="/shop/updateCartQuantities.shtml" method="post" >
<table align="center" bgcolor="#008800" border="0" cellspacing="2" cellpadding="5">
  <tr bgcolor="#cccccc">
  <td><b>Item ID</b></td>  <td><b>Product ID</b></td>  <td><b>Description</b></td> <td><b>In Stock?</b></td> <td><b>Quantity</b></td>  <td><b>List Price</b></td> <td><b>Total Cost</b></td>  <td>&nbsp;</td>
  </tr>

<logic:equal name="cart" property="numberOfItems" value="0">
<tr bgcolor="#FFFF88"><td colspan="8"><b>Your cart is empty.</b></td></tr>
</logic:equal>

<logic:iterate id="cartItem" name="cart" property="cartItems">
  <tr bgcolor="#FFFF88">
  <td><b>
  
 <html:link paramId="itemId" paramName="cartItem" paramProperty="item.itemId" page="/shop/viewItem.shtml">
 <bean:write name="cartItem" property="item.itemId" /></html:link></b></td>
  <td><bean:write name="cartItem" property="item.productId" /></td>
  <td>
     <bean:write name="cartItem" property="item.attribute1" />
     <bean:write name="cartItem" property="item.attribute2" />
     <bean:write name="cartItem" property="item.attribute3" />
     <bean:write name="cartItem" property="item.attribute4" />
     <bean:write name="cartItem" property="item.attribute5" />
     <bean:write name="cartItem" property="item.product.name" />
   </td>
  <td align="center"><bean:write name="cartItem" property="inStock" /></td>
  <td align="center">
  <input type="text" size="3" name="<bean:write name="cartItem" property="item.itemId"/>" value="<bean:write name="cartItem" property="quantity"/>" />
  </td>
  <td align="right"><bean:write name="cartItem" property="item.listPrice" format="$#,##0.00" /></td>
  <td align="right"><bean:write name="cartItem" property="total" format="$#,##0.00" /></td>
  <td><html:link paramId="workingItemId" paramName="cartItem" paramProperty="item.itemId" page="/shop/removeItemFromCart.shtml">
  <img border="0" src="../images/button_remove.gif" /></html:link></td>
  </tr>
</logic:iterate>
<tr bgcolor="#FFFF88">
<td colspan="7" align="right">
<b>Sub Total: <bean:write name="cart" property="subTotal" format="$#,##0.00" /></b><br />
<input type="image" border="0" src="../images/button_update_cart.gif" name="update" />

</td>
<td>&nbsp;</td>
</tr>
</table>
<center>
  <logic:equal name="cart" property="cartItemList.previousPageAvailable" value="true" >
    <a href="switchCartPage.shtml?pageDirection=previous"><font color="green"><B>&lt;&lt; Prev</B></font></a>
  </logic:equal>
  <logic:equal name="cart" property="cartItemList.nextPageAvailable" value="true" >
    <a href="switchCartPage.shtml?pageDirection=previous"><font color="green"><B>Next &gt;&gt;</B></font></a>
  </logic:equal>
</center>
</html:form>

<logic:notEqual name="cart" property="numberOfItems" value="0">
<br /><center><html:link page="/shop/checkout.shtml"><img border="0" src="../images/button_checkout.gif" /></html:link></center>
</logic:notEqual>

</td>


<td valign="top" width="20%" align="right">
<logic:present name="accountBean" scope="session">
<logic:equal name="accountBean" property="authenticated" value="true">
    <logic:equal name="accountBean" property="account.listOption" value="true">
      <%@include file="IncludeMyList.jsp" %>
    </logic:equal>
</logic:equal>
</logic:present>
</td>

</tr>
</table>

<%@include file="../common/IncludeBanner.jsp"%>
<%@include file="../common/IncludeBottom.jsp"%></p>


