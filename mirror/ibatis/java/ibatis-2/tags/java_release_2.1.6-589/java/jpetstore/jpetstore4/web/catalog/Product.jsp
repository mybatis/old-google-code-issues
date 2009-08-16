<%@include file="../common/IncludeTop.jsp"%>

<bean:define id="product" name="catalogBean" property="product" />
<bean:define id="itemList" name="catalogBean" property="itemList" />

<table align="left" bgcolor="#008800" border="0" cellspacing="2" cellpadding="2">
<tr><td bgcolor="#FFFF88">
<html:link paramId="categoryId" paramName="product" paramProperty="categoryId" page="/shop/viewCategory.shtml"><b><font color="BLACK" size="2">&lt;&lt; <bean:write name="product" property="categoryId" /></font></b></html:link>
</td></tr>
</table>

<p>

<center>
  <b><font size="4"><bean:write name="product" property="name" /></font></b>
</center>

<table align="center" bgcolor="#008800" border="0" cellspacing="2" cellpadding="3">
  <tr bgcolor="#CCCCCC">  <td><b>Item ID</b></td>  <td><b>Product ID</b></td>  <td><b>Description</b></td>  <td><b>List Price</b></td>  <td>&nbsp;</td>  </tr>
<logic:iterate id="item" name="itemList" >
  <tr bgcolor="#FFFF88">
  <td><b>

 <html:link paramId="itemId" paramName="item" paramProperty="itemId" page="/shop/viewItem.shtml">
 <bean:write name="item" property="itemId" /></html:link></b></td>
  <td><bean:write name="item" property="productId" /></td>
  <td>
     <bean:write name="item" property="attribute1" />
     <bean:write name="item" property="attribute2" />
     <bean:write name="item" property="attribute3" />
     <bean:write name="item" property="attribute4" />
     <bean:write name="item" property="attribute5" />
     <bean:write name="product" property="name" />
   </td>
  <td><bean:write name="item" property="listPrice" format="$#,##0.00" /></td>
  <td><html:link paramId="workingItemId" paramName="item" paramProperty="itemId" page="/shop/addItemToCart.shtml">
  <img border="0" src="../images/button_add_to_cart.gif" /></html:link></td>
  </tr>
</logic:iterate>
  <tr><td bgcolor="#FFFFFF" colspan="2">
  <logic:notEqual name="itemList" property="firstPage" value="true" >
    <a href="switchItemListPage.shtml?pageDirection=previous"><img src="../images/button_prev.gif" border="0"></a>
  </logic:notEqual>
  <logic:notEqual name="itemList" property="lastPage" value="true" >
    <a href="switchItemListPage.shtml?pageDirection=next"><img src="../images/button_next.gif" border="0"></a>
  </logic:notEqual>
  </td></tr>
</table>

<%@include file="../common/IncludeBottom.jsp"%></p></p>




