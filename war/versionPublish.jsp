<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://iambookmaster.com/iambookmaster.tld" prefix="iambookmaster" %>
		
<iambookmaster:userIsLogged>
<iambookmaster:exists name="version" scope="page">

<form action="/webpublish.do" method="post">
	 <input type="hidden" name="operation" value="create"/>
	 <input type="hidden" name="v" value="<iambookmaster:write name="version" property="id"/>"/>
	 <input type="submit"" name="publish" value="<iambookmaster:message value="jspMainPublichButton"/>"/>
</form>

</iambookmaster:exists>
</iambookmaster:userIsLogged>