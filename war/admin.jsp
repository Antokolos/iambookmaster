<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://iambookmaster.com/iambookmaster.tld" prefix="iambookmaster" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title><iambookmaster:message value="jspMainTitle"/></title>
  <link rel="stylesheet" type="text/css" href="creator.css"/>
</head>
<body>

<iambookmaster:userIsNotAdmin>
This page is for administrator only
</iambookmaster:userIsNotAdmin>

<iambookmaster:userIsAdmin>

<table border="1">
<tr><th>Users</th><th>Books</th></tr>
<tr><td>
<%-- Users selection --%>
<form action="/admin.jsp" method="post">
<table>
<tr>
<td>Name</td>
<td><input type="text" name="userMask" value="<iambookmaster:exists name="userMask"><iambookmaster:write name="userMask"/></iambookmaster:exists>"></td>
</tr>
<tr>
<td>E-mail</td>
<td><input type="text" name="userEmail" value="<iambookmaster:exists name="userEmail"><iambookmaster:write name="userEmail"/></iambookmaster:exists>"></td>
</tr>
<tr>
<td colspan="2"><input type="submit" name="userSubmit" value="Search"></td>
</tr>
</table>
</form>
</td><td>
<%-- Books selection --%>
<form action="/admin.jsp" method="post">
<table>
<tr>
<td>Name</td>
<td><input type="text" name="bookMask" value="<iambookmaster:exists name="bookMask"><iambookmaster:write name="bookMask"/></iambookmaster:exists>"></td>
</tr>
<tr>
<td>Owner's E-mail </td>
<td><input type="text" name="bookUserEmail" value="<iambookmaster:exists name="bookUserEmail"><iambookmaster:write name="bookUserEmail"/></iambookmaster:exists>"></td>
</tr>
<tr>
<td>External ID</td>
<td><input type="text" name="bookExternalId" value="<iambookmaster:exists name="bookExternalId"><iambookmaster:write name="bookExternalId"/></iambookmaster:exists>"></td>
</tr>
<tr>
<td colspan="2"><input type="submit" name="booksSubmit" value="Search"></td>
</tr>
</table>
</form>
</td></tr>
</table>
<%-- show selected users --%>
<iambookmaster:exists name="userSubmit">
	<iambookmaster:exists name="userMask">
	<iambookmaster:notEmpty name="userMask">
		<iambookmaster:usersCriteria name="criteria" property="name" valueName="userMask"/>
	</iambookmaster:notEmpty>
	</iambookmaster:exists>	
	<iambookmaster:exists name="userEmail">
	<iambookmaster:notEmpty name="userEmail">
		<iambookmaster:usersCriteria name="criteria" property="email" valueName="userEmail"/>
	</iambookmaster:notEmpty>
	</iambookmaster:exists>	
	<iambookmaster:exists name="criteria">
		<iambookmaster:selectUsers name="users" criteria="criteria"/>
	</iambookmaster:exists>	
	<iambookmaster:notExists name="criteria">
		<iambookmaster:selectUsers name="users"/>
	</iambookmaster:notExists>	
	<iambookmaster:empty name="users">
	<p>User not found</p>
	</iambookmaster:empty>
	<iambookmaster:notEmpty name="users">
	<p>User Search results</p>
	<table border="1">
	<iambookmaster:iterate name="users" item="user">
	<tr>
	<td>
		<iambookmaster:locked name="user">
			<img alt="Locked user" src="images/RedFlag.gif">
		</iambookmaster:locked>
		<iambookmaster:notLocked name="user">
			&nbsp;
		</iambookmaster:notLocked>
	</td><td>
		<a href="/admin.jsp?viewUser=<iambookmaster:userInfo name="user" item="id"/>">
			<iambookmaster:write name="user" property="nick"/>
		</a>
	</td><td>
		<a href="mailto:<iambookmaster:write name="user" property="email"/>">
			<iambookmaster:write name="user" property="email"/>
		</a>
	</td></tr>
	</iambookmaster:iterate>
	</table>
	</iambookmaster:notEmpty>
</iambookmaster:exists>

<%-- show selected books --%>
<iambookmaster:exists name="booksSubmit">
	<iambookmaster:exists name="bookMask">
	<iambookmaster:notEmpty name="bookMask">
		<iambookmaster:usersCriteria name="criteria" property="name" valueName="bookMask"/>
	</iambookmaster:notEmpty>
	</iambookmaster:exists>
		
	<iambookmaster:exists name="criteria">
		<iambookmaster:selectBooks name="books" criteria="criteria"/>
	</iambookmaster:exists>	
	<iambookmaster:notExists name="criteria">
		<iambookmaster:selectBooks name="books"/>
	</iambookmaster:notExists>	
	<iambookmaster:empty name="books">
	<p>Books not found</p>
	</iambookmaster:empty>
	<iambookmaster:notEmpty name="books">
	<p>Books Search Result</p>
	<table border="1">
	<tr><th>&nbsp;</th><th>Name</th><th>Owner</th></tr>
	<iambookmaster:iterate name="books" item="book">
	<tr>
	<td>
		<iambookmaster:locked name="book">
			<img alt="Locked book" src="images/RedFlag.gif"/>
		</iambookmaster:locked>
		<iambookmaster:notLocked name="book">
			&nbsp;
		</iambookmaster:notLocked>
	</td>
	<td>
		<a href="/admin.jsp?viewBook=<iambookmaster:bookInfo name="book" item="id"/>">
			<iambookmaster:write name="book" property="name"/>
		</a>
	</td><td>
		<a href="/admin.jsp?viewUser=<iambookmaster:bookInfo name="book" item="ownerId"/>">
			<iambookmaster:bookInfo name="book" item="ownerEmail"/>
		</a>
	</td>
	</tr>
	</iambookmaster:iterate>
	</table>
	</iambookmaster:notEmpty>
</iambookmaster:exists>

<%-- show one selected book --%>
<iambookmaster:exists name="viewBook">
	<iambookmaster:booksCriteria name="criteria" property="id" valueName="viewBook"/>
	<iambookmaster:selectBooks name="books" criteria="criteria"/>
	<iambookmaster:empty name="books">
		<p>Book with this ID not found</p>
	</iambookmaster:empty>
	<iambookmaster:notEmpty name="books">
		<iambookmaster:iterate name="books" item="book">
			<%@ include file="adminBook.jsp" %>
		</iambookmaster:iterate>
	</iambookmaster:notEmpty>
</iambookmaster:exists>

<%-- show one user --%>
<iambookmaster:exists name="viewUser">
	<iambookmaster:usersCriteria name="criteria" property="id" valueName="viewUser"/>
	<iambookmaster:selectUsers name="users" criteria="criteria"/>
	<iambookmaster:empty name="users">
		<p>User with this ID not found</p>
	</iambookmaster:empty>
	<iambookmaster:notEmpty name="users">
		<iambookmaster:iterate name="users" item="user">
			<%@ include file="adminUser.jsp" %>
		</iambookmaster:iterate>
	</iambookmaster:notEmpty>
	
</iambookmaster:exists>

<%-- lock user --%>
<iambookmaster:exists name="lockUser">
	<iambookmaster:usersCriteria name="criteria" property="id" valueName="lockUser"/>
	<iambookmaster:selectUsers name="users" criteria="criteria"/>
	<iambookmaster:empty name="users">
		<p>User with this ID not found</p>
	</iambookmaster:empty>
	<iambookmaster:notEmpty name="users">
		<iambookmaster:iterate name="users" item="user">
			<iambookmaster:lock name="user" lock="true"/>
			<%@ include file="adminUser.jsp" %>
		</iambookmaster:iterate>
	</iambookmaster:notEmpty>
</iambookmaster:exists>


<%-- unlock user --%>
<iambookmaster:exists name="unlockUser">
	<iambookmaster:usersCriteria name="criteria" property="id" valueName="unlockUser"/>
	<iambookmaster:selectUsers name="users" criteria="criteria"/>
	<iambookmaster:empty name="users">
		<p>User with this ID not found</p>
	</iambookmaster:empty>
	<iambookmaster:notEmpty name="users">
		<iambookmaster:iterate name="users" item="user">
			<iambookmaster:lock name="user" lock="false"/>
			<%@ include file="adminUser.jsp" %>
		</iambookmaster:iterate>
	</iambookmaster:notEmpty>
</iambookmaster:exists>


<%-- lock selected book --%>
<iambookmaster:exists name="unlockBook">
	<iambookmaster:booksCriteria name="criteria" property="id" valueName="unlockBook"/>
	<iambookmaster:selectBooks name="books" criteria="criteria"/>
	<iambookmaster:empty name="books">
		<p>Book with this ID not found</p>
	</iambookmaster:empty>
	<iambookmaster:notEmpty name="books">
		<iambookmaster:iterate name="books" item="book">
			<iambookmaster:lock name="book" lock="false"/>
			<%@ include file="adminBook.jsp" %>
		</iambookmaster:iterate>
	</iambookmaster:notEmpty>
</iambookmaster:exists>


<%-- lock selected book --%>
<iambookmaster:exists name="lockBook">
	<iambookmaster:booksCriteria name="criteria" property="id" valueName="lockBook"/>
	<iambookmaster:selectBooks name="books" criteria="criteria"/>
	<iambookmaster:empty name="books">
		<p>Book with this ID not found</p>
	</iambookmaster:empty>
	<iambookmaster:notEmpty name="books">
		<iambookmaster:iterate name="books" item="book">
			<iambookmaster:lock name="book" lock="true"/>
			<%@ include file="adminBook.jsp" %>
		</iambookmaster:iterate>
	</iambookmaster:notEmpty>
</iambookmaster:exists>

<%-- remove selected book --%>
<iambookmaster:exists name="removeBook">
	<iambookmaster:booksCriteria name="criteria" property="id" valueName="removeBook"/>
	<iambookmaster:selectBooks name="books" criteria="criteria"/>
	<iambookmaster:empty name="books">
		<p>Book with this ID not found</p>
	</iambookmaster:empty>
	<iambookmaster:notEmpty name="books">
		<iambookmaster:iterate name="books" item="book">
			<iambookmaster:remove name="book"/>
			<p>Book was removed</p>
		</iambookmaster:iterate>
	</iambookmaster:notEmpty>
</iambookmaster:exists>

<%-- remove selected user --%>
<iambookmaster:exists name="removeUser">
	<iambookmaster:usersCriteria name="criteria" property="id" valueName="removeUser"/>
	<iambookmaster:selectUsers name="users" criteria="criteria"/>
	<iambookmaster:empty name="users">
		<p>User with this ID not found</p>
	</iambookmaster:empty>
	<iambookmaster:notEmpty name="users">
		<iambookmaster:iterate name="users" item="user">
			<iambookmaster:remove name="user"/>
			<p>User was removed</p>
		</iambookmaster:iterate>
	</iambookmaster:notEmpty>
</iambookmaster:exists>

</iambookmaster:userIsAdmin>

</body>
</html>