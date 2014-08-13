<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://iambookmaster.com/iambookmaster.tld" prefix="iambookmaster" %>
		
<iambookmaster:userIsLogged>
<iambookmaster:exists name="book" scope="page">

<iambookmaster:selectBookVersions name="versions" book="book"/>
<iambookmaster:empty name="versions">
<p>This book does not have versions</p>
</iambookmaster:empty>
<iambookmaster:notEmpty name="versions">
<table border="1">
<caption>Versions of the book</caption>
<tr>
<th>Date</th><th>Version</th><th>&nbsp;</th>
<th><iambookmaster:message value="jspMainRePublich"/></th>
</tr>
<iambookmaster:iterate name="versions" item="version">
	<tr>
	<td>
		<iambookmaster:equalsTrue name="version" property="published">
			<img alt="This book is published" src="images/printer.png"/>
		</iambookmaster:equalsTrue>
		<iambookmaster:equalsFalse name="version" property="published">
			<img alt="This book is not published" src="images/media-floppy.png"/>
		</iambookmaster:equalsFalse>
		<iambookmaster:write name="version" property="date" format="dd-MMM-yyyy hh:mm:ss"/>
	</td>
	<td>
		<b><iambookmaster:write name="version" property="versions"/></b>
	</td>
	<td>
		<%@ include file="bookVersionLoadLinks.jsp" %>
	</td>
	<td>
		<%@ include file="versionPublish.jsp" %>
	</td>
	</tr>
</iambookmaster:iterate>
</table>
</iambookmaster:notEmpty>

</iambookmaster:exists>
</iambookmaster:userIsLogged>