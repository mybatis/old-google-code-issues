<%@include file="../common/IncludeTop.jsp"%>

<bean:define id="cart" name="cartBean" property="cart" />

<table border="0" width="100%" cellspacing="0" cellpadding="0">
<tr><td valign="top" width="20%" align="left">
<table align="left" bgcolor="#008800" border="0" cellspacing="2" cellpadding="2">
<tr><td bgcolor="#FFFF88">
<html:link page="/shop/viewCart.shtml"><b><font color="BLACK" size="2">&lt;&lt; Shopping Cart</font></b></html:link>
</td></tr>
</table>
</td>

<td valign="top" align="center">
<h2 align="center">Checkout Summary</h2>

<table align="center" bgcolor="#008800" border="0" cellspacing="2" cellpadding="5">

  <tr bgcolor="#cccccc">
  <td><b>Item ID</b></td>  <td><b>Product ID</b></td>  <td><b>Description</b></td> <td><b>In Stock?</b></td> <td><b>Quantity</b></td>  <td><b>List Price</b></td> <td><b>Total Cost</b></td>
  </tr>

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
  <bean:write name="cartItem" property="quantity" />
  </td>
  <td align="right"><bean:write name="cartItem" property="item.listPrice" format="$#,##0.00" /></td>
  <td align="right"><bean:write name="cartItem" property="total" format="$#,##0.00" /></td>
  </tr>
</logic:iterate>
<tr bgcolor="#FFFF88">
<td colspan="7" align="right">
<b>Sub Total: <bean:write name="cart" property="subTotal" format="$#,##0.00" /></b><br />

</td>
</tr>
</table>
<center>
  <logic:notEqual name="cart" property="cartItemList.firstPage" value="true" >
    <a href="switchCartPage.shtml?pageDirection=previous"><img src="../images/button_prev.gif" border="0"></a>
  </logic:notEqual>
  <logic:notEqual name="cart" property="cartItemList.lastPage" value="true" >
    <a href="switchCartPage.shtml?pageDirection=next"><img src="../images/button_next.gif" border="0"></a>
  </logic:notEqual>
</center>
<br>
<center><html:link page="/shop/newOrderForm.shtml"><img border="0" src="../images/button_continue.gif" /></html:link></center>
</td>


<td valign="top" width="20%" align="right">
&nbsp;
</td>

</tr>
</table>

<%@include file="../common/IncludeBottom.jsp"%></p>




