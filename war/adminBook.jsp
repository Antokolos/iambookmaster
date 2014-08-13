<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://iambookmaster.com/iambookmaster.tld" prefix="iambookmaster" %>
<iambookmaster:userIsNotAdmin>
This page is for administrator only
</iambookmaster:userIsNotAdmin>

<iambookmaster:userIsAdmin>
<iambookmaster:exists name="book">
<p>
<iambookmaster:locked name="book">
	<img alt="Locked book" src="images/RedFlag.gif"/>This book is locked
	<a href="/admin.jsp?unlockBook=<iambookmaster:bookInfo name="book" item="id"/>">Unlock</a>
</iambookmaster:locked>
<iambookmaster:notLocked name="book">
	<a href="/admin.jsp?lockBook=<iambookmaster:bookInfo name="book" item="id"/>">Lock this book</a>
</iambookmaster:notLocked>
<a onclick="return confirm('Remove this book?')" href="/admin.jsp?removeBook=<iambookmaster:bookInfo name="book" item="id"/>">Remove this book</a>
</p>
<table border="1">
<tr>
<td>Name</td>
<td>
	<iambookmaster:equalsTrue name="book" property="published">
		<img alt="This book is published" src="images/printer.png"/>
	</iambookmaster:equalsTrue>
	<iambookmaster:equalsFalse name="book" property="published">
		<img alt="This book is not published" src="images/media-floppy.png"/>
	</iambookmaster:equalsFalse>
	<iambookmaster:write name="book" property="name"/>
</td>
</tr>
<tr><td>Owner</td>
<td>
<a href="/admin.jsp?viewUser=<iambookmaster:bookInfo name="book" item="ownerId"/>">
	<iambookmaster:bookInfo name="book" item="ownerName"/>
</a>
&nbsp;
<a href="mailto:<iambookmaster:bookInfo name="book" item="ownerEmail"/>">
	<iambookmaster:bookInfo name="book" item="ownerEmail"/>
</a>
</td></tr>

<tr>
<td>Authors</td>
<td><iambookmaster:write name="book" property="authors"/></td>
</tr>

<tr>
<td>External ID</td>
<td><iambookmaster:write name="book" property="externalId"/></td>
</tr>

<tr><td>
	<%@ include file="bookLoadLinks.jsp" %>
</td><td>
	<iambookmaster:write name="book" property="description"/>
</td></tr>

</table>

<%@ include file="bookVersions.jsp" %>

</iambookmaster:exists>
</iambookmaster:userIsAdmin>