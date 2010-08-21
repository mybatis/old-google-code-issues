<%@include file="../common/IncludeTop.jsp"%>

<center>
  <font size="4"><b>My Orders</b></font>
</center>
<BR>
<table align="center" bgcolor="#008800" border="0" cellspacing="2" cellpadding="3">
  <tr bgcolor="#CCCCCC">  <td><b>Order ID</b></td>  <td><b>Date</b></td>  <td><b>Total Price</b></td>  </tr>

<logic:iterate id="order" name="orderBean" property="orderList">
  <tr bgcolor="#FFFF88">
  <td><b><html:link paramId="orderId" paramName="order" paramProperty="orderId" page="/shop/viewOrder.shtml"><font color="BLACK"><bean:write name="order" property="orderId" /></font></html:link></b></td>
  <td><bean:write name="order" property="orderDate" format="yyyy/MM/dd hh:mm:ss" /></td>
  <td><bean:write name="order" property="totalPrice" format="$#,##0.00" /></td>
  </tr>
</logic:iterate>
</table>
<BR>
<center>
  <logic:notEqual name="orderBean" property="orderList.firstPage" value="true" >
    <a href="switchOrderPage.shtml?pageDirection=previous"><img src="../images/button_prev.gif" border="0"></a>
  </logic:notEqual>
  <logic:notEqual name="orderBean" property="orderList.lastPage" value="true" >
    <a href="switchOrderPage.shtml?pageDirection=next"><img src="../images/button_next.gif" border="0"></a>
  </logic:notEqual>
</center>

<%@include file="../common/IncludeBottom.jsp"%></p>

