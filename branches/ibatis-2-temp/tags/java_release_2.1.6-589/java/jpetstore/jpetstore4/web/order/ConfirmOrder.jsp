<%@include file="../common/IncludeTop.jsp"%>


<table align="left" bgcolor="#008800" border="0" cellspacing="2" cellpadding="2">
<tr><td bgcolor="#FFFF88">
<html:link page="/shop/index.shtml"><b><font color="BLACK" size="2">&lt;&lt; Main Menu</font></b></html:link>
</td></tr>
</table>

<p>
<center>
<b>Please confirm the information below and then press continue...</b>
</center>
<p>
<table width="60%" align="center" border="0" cellpadding="3" cellspacing="1" bgcolor="#FFFF88">
<tr bgcolor="#FFFF88"><td align="center" colspan="2">
  <font size="4"><b>Order</b></font>
  <br /><font size="3"><b><bean:write name="orderBean" property="order.orderDate" format="yyyy/MM/dd hh:mm:ss" /></b></font>
</td></tr>

<%-- Don't display payment details
<tr bgcolor="#FFFF88"><td colspan="2">
<font color="GREEN" size="4"><b>Payment Details</b></font>
</td></tr>
<tr bgcolor="#FFFF88"><td>
Card Type:</td><td>
<bean:write name="orderBean" property="order.cardType" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Card Number:</td><td><bean:write name="orderBean" property="order.creditCard" /> <font color="red" size="2">* Fake number!</font>
</td></tr>
<tr bgcolor="#FFFF88"><td>
Expiry Date (MM/YYYY):</td><td><bean:write name="orderBean" property="order.expiryDate" />
</td></tr>
--%>

<tr bgcolor="#FFFF88"><td colspan="2">
<font color="GREEN" size="4"><b>Billing Address</b></font>
</td></tr>
<tr bgcolor="#FFFF88"><td>
First name:</td><td><bean:write name="orderBean" property="order.billToFirstName" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Last name:</td><td><bean:write name="orderBean" property="order.billToLastName" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Address 1:</td><td><bean:write name="orderBean" property="order.billAddress1" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Address 2:</td><td><bean:write name="orderBean" property="order.billAddress2" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
City: </td><td><bean:write name="orderBean" property="order.billCity" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
State:</td><td><bean:write name="orderBean" property="order.billState" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Zip:</td><td><bean:write name="orderBean" property="order.billZip" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Country: </td><td><bean:write name="orderBean" property="order.billCountry" />
</td></tr>
<tr bgcolor="#FFFF88"><td colspan="2">
<font color="GREEN" size="4"><b>Shipping Address</b></font>
</td></tr><tr bgcolor="#FFFF88"><td>
First name:</td><td><bean:write name="orderBean" property="order.shipToFirstName" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Last name:</td><td><bean:write name="orderBean" property="order.shipToLastName" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Address 1:</td><td><bean:write name="orderBean" property="order.shipAddress1" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Address 2:</td><td><bean:write name="orderBean" property="order.shipAddress2" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
City: </td><td><bean:write name="orderBean" property="order.shipCity" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
State:</td><td><bean:write name="orderBean" property="order.shipState" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Zip:</td><td><bean:write name="orderBean" property="order.shipZip" />
</td></tr>
<tr bgcolor="#FFFF88"><td>
Country: </td><td><bean:write name="orderBean" property="order.shipCountry" />
</td></tr>

</table>
<p>
<center><html:link page="/shop/newOrder.shtml?confirmed=true"><img border="0" src="../images/button_continue.gif" /></html:link></center>


<%@include file="../common/IncludeBottom.jsp"%></p></p></p></p>




