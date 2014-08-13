<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://iambookmaster.com/iambookmaster.tld" prefix="iambookmaster" %>
		
<iambookmaster:userIsLogged>
<iambookmaster:exists name="book" scope="page">

<iambookmaster:form action="/webpublish.do" method="POST" name="publichBook">
	<iambookmaster:message value="jspMainRePublich"/><br/>
	 <input type="hidden" name="operation" value="create"/>
	 <input type="hidden" name="b" value="<iambookmaster:bookInfo name="book" item="id"/>"/>
	 <input type="submit"" name="publish" value="<iambookmaster:message value="jspMainPublichButton"/>"/>
</iambookmaster:form>

</iambookmaster:exists>
</iambookmaster:userIsLogged>