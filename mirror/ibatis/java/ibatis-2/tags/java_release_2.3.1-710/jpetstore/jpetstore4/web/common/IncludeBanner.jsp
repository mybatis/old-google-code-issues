<br>
<logic:present name="accountBean" scope="session">
<logic:equal name="accountBean" property="authenticated" value="true">
    <logic:equal name="accountBean" property="account.bannerOption" value="true">
      <table align="center" background="../images/bkg-topbar.gif" cellpadding="5" width="100%">
      <tr><td>
      <center>
          <bean:write filter="false" name="accountBean" property="account.bannerName"/>
          &nbsp;
      </center>
      </td></tr>
      </table>
    </logic:equal>
</logic:equal>
</logic:present>
