<%@ page import="java.io.PrintWriter"%>
<%@include file="../common/IncludeTop.jsp"%>

<logic:notPresent name="BeanActionException">
  <logic:notPresent name="message">
    <H3>Something happened...</H3>
    <B>But no further information was provided.</B>
  </logic:notPresent>
</logic:notPresent>
<P/>
<logic:present name="BeanActionException">
  <H3>Error!</H3>
  <B><font color="red"><bean:write name="BeanActionException" property="class.name"/></font></B>
  <P/>
  <bean:write name="BeanActionException" property="message"/>
</logic:present>
<P/>
<logic:present name="BeanActionException">
  <h4>Stack</h4>
  <i><pre>
<%
  Exception e = (Exception)request.getAttribute("BeanActionException");
  e.printStackTrace(new PrintWriter(out));
%>
  </pre></i>
</logic:present>

<%@include file="../common/IncludeBottom.jsp"%>