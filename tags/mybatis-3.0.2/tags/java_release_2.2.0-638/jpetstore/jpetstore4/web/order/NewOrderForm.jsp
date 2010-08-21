<%@include file="../common/IncludeTop.jsp"%>


<html:form action="/shop/newOrder.shtml" styleId="orderBean" method="post" >

<TABLE bgcolor="#008800" border=0 cellpadding=3 cellspacing=1 bgcolor=#FFFF88>
<TR bgcolor=#FFFF88><TD colspan=2>
<FONT color=GREEN size=4><B>Payment Details</B></FONT>
</TD></TR><TR bgcolor=#FFFF88><TD>
Card Type:</TD><TD>
<html:select name="orderBean" property="order.cardType">
  <html:options name="orderBean" property="creditCardTypes" />
</html:select>
</TD></TR>
<TR bgcolor=#FFFF88><TD>
Card Number:</TD><TD><html:text name="orderBean" property="order.creditCard" /> <font color=red size=2>* Use a fake number!</font>
</TD></TR>
<TR bgcolor=#FFFF88><TD>
Expiry Date (MM/YYYY):</TD><TD><html:text name="orderBean" property="order.expiryDate" />
</TD></TR>
<TR bgcolor=#FFFF88><TD colspan=2>
<FONT color=GREEN size=4><B>Billing Address</B></FONT>
</TD></TR>

<TR bgcolor=#FFFF88><TD>
First name:</TD><TD><html:text name="orderBean" property="order.billToFirstName" />
</TD></TR>
<TR bgcolor=#FFFF88><TD>
Last name:</TD><TD><html:text name="orderBean" property="order.billToLastName" />
</TD></TR>
<TR bgcolor=#FFFF88><TD>
Address 1:</TD><TD><html:text size="40" name="orderBean" property="order.billAddress1" />
</TD></TR>
<TR bgcolor=#FFFF88><TD>
Address 2:</TD><TD><html:text size="40" name="orderBean" property="order.billAddress2" />
</TD></TR>
<TR bgcolor=#FFFF88><TD>
City: </TD><TD><html:text name="orderBean" property="order.billCity" />
</TD></TR>
<TR bgcolor=#FFFF88><TD>
State:</TD><TD><html:text size="4" name="orderBean" property="order.billState" />
</TD></TR>
<TR bgcolor=#FFFF88><TD>
Zip:</TD><TD><html:text size="10" name="orderBean" property="order.billZip" />
</TD></TR>
<TR bgcolor=#FFFF88><TD>
Country: </TD><TD><html:text size="15" name="orderBean" property="order.billCountry" />
</TD></TR>

<TR bgcolor=#FFFF88><TD colspan=2>
<html:checkbox name="orderBean" property="shippingAddressRequired" /> Ship to different address...
</TD></TR>

</TABLE>
<P>
<input type="image" src="../images/button_submit.gif">

</html:form>

<%@include file="../common/IncludeBottom.jsp"%>