<%@include file="../common/IncludeTop.jsp"%>

<bean:define id="order" name="orderBean" property="order" />
<bean:define id="itemList" name="orderBean" property="order.lineItems" />

<table align="left" bgcolor="#008800" border="0" cellspacing="2" cellpadding="2">
<tr><td bgcolor="#FFFF88">
<html:link page="/shop/index.shtml"><b><font color="BLACK" size="2">&lt;&lt; Main Menu</font></b></html:link>
</td></tr>
</table>

<p>

<table width="60%" align="center" border="0" cellpadding="3" cellspacing="1" bgcolor="#FFFF88">
<tr bgcolor="#FFFF88"><td align="center" colspan="2">
  <font size="4"><b>Order #<bean:write name="order" property="orderId" /></b></font>
  <br /><font size="3"><b><bean:write name="order" property="orderDate" format="yyyy/MM/dd hh:mm:ss" /></b></font>
</td></tr>
<tr bgcolor="#FFFF88"><td colspan="2">
<font color="GREEN" size="4"><b>Payment Details</b></font>
</td></tr>
<tr bgcolor="#FFFF88"><td>
Card Type:</td><td>
<bean:write name="order" property="cardType" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Card Number:</td><td><bean:write name="order" property="creditCard" /> <font color="red" size="2">* Fake number!</font>
</td></tr>
<tr bgcolor="#FFFF88"><td>
Expiry Date (MM/YYYY):</td><td><bean:write name="order" property="expiryDate" />
</td></tr>
<tr bgcolor="#FFFF88"><td colspan="2">
<font color="GREEN" size="4"><b>Billing Address</b></font>
</td></tr>
<tr bgcolor="#FFFF88"><td>
First name:</td><td><bean:write name="order" property="billToFirstName" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Last name:</td><td><bean:write name="order" property="billToLastName" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Address 1:</td><td><bean:write name="order" property="billAddress1" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Address 2:</td><td><bean:write name="order" property="billAddress2" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
City: </td><td><bean:write name="order" property="billCity" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
State:</td><td><bean:write name="order" property="billState" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Zip:</td><td><bean:write name="order" property="billZip" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Country: </td><td><bean:write name="order" property="billCountry" />
</td></tr>
<tr bgcolor="#FFFF88"><td colspan="2">
<font color="GREEN" size="4"><b>Shipping Address</b></font>
</td></tr><tr bgcolor="#FFFF88"><td>
First name:</td><td><bean:write name="order" property="shipToFirstName" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Last name:</td><td><bean:write name="order" property="shipToLastName" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Address 1:</td><td><bean:write name="order" property="shipAddress1" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Address 2:</td><td><bean:write name="order" property="shipAddress2" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
City: </td><td><bean:write name="order" property="shipCity" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
State:</td><td><bean:write name="order" property="shipState" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Zip:</td><td><bean:write name="order" property="shipZip" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Country: </td><td><bean:write name="order" property="shipCountry" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Courier: </td><td><bean:write name="order" property="courier" />
</td></tr>
<tr bgcolor="#FFFF88"><td colspan="2">
  <b><font color="GREEN" size="4">Status:</font> <bean:write name="order" property="status" /></b>
</td></tr>
<tr bgcolor="#FFFF88"><td colspan="2">
<table width="100%" align="center" bgcolor="#008800" border="0" cellspacing="2" cellpadding="3">
  <tr bgcolor="#CCCCCC">
  <td><b>Item ID</b></td>
  <td><b>Description</b></td>
  <td><b>Quantity</b></td>
  <td><b>Price</b></td>
  <td><b>Total Cost</b></td>
  </tr>
<logic:iterate id="item" name="itemList">
  <tr bgcolor="#FFFF88">
  <td><b><html:link paramId="itemId" paramName="item" paramProperty="itemId" page="/shop/viewItem.shtml"><font color="BLACK"><bean:write name="item" property="itemId" /></font></html:link></b></td>
  <td>
  <logic:present name="item" property="item">
     <bean:write name="item" property="item.attribute1" />
     <bean:write name="item" property="item.attribute2" />
     <bean:write name="item" property="item.attribute3" />
     <bean:write name="item" property="item.attribute4" />
     <bean:write name="item" property="item.attribute5" />
     <bean:write name="item" property="item.product.name" />
  </logic:present>
  <logic:notPresent name="item" property="item">
  <i>{description unavailable}</i>
  </logic:notPresent>
  </td>

  <td><bean:write name="item" property="quantity" /></td>
  <td align="right"><bean:write name="item" property="unitPrice" format="$#,##0.00" /></td>
  <td align="right"><bean:write name="item" property="total" format="$#,##0.00" /></td>
  </tr>
</logic:iterate>
  <tr bgcolor="#FFFF88">
  <td colspan="5" align="right"><b>Total: <bean:write name="order" property="totalPrice" format="$#,##0.00" /></b></td>
  </tr>
</table>
</td></tr>



</table>


<%@include file="../common/IncludeBottom.jsp"%></p></p>


