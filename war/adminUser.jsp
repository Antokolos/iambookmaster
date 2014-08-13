<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://iambookmaster.com/iambookmaster.tld" prefix="iambookmaster" %>
<iambookmaster:userIsNotAdmin>
This page is for administrator only
</iambookmaster:userIsNotAdmin>

<iambookmaster:userIsAdmin>
<iambookmaster:exists name="user">
<p>
<iambookmaster:locked name="user">
	<img alt="Locked user" src="images/RedFlag.gif">This user is locked
	<a href="/admin.jsp?unlockUser=<iambookmaster:userInfo name="user" item="id"/>">Unlock</a>
</iambookmaster:locked>
<iambookmaster:notLocked name="user">
	<a href="/admin.jsp?lockUser=<iambookmaster:userInfo name="user" item="id"/>">Lock this user</a>
</iambookmaster:notLocked>
<a onclick="return confirm('Remove this user?')" href="/admin.jsp?removeUser=<iambookmaster:userInfo name="user" item="id"/>">Remove this user</a>
</p>
<table border="1">
<tr><th colspan="2">User's info</th></tr>
<tr>
<td>Name</td>
<td><iambookmaster:write name="user" property="nick"/></td>
</tr>
<tr><td>E-mail</td>
<td>
	<a href="mailto:<iambookmaster:write name="user" property="email"/>">
		<iambookmaster:write name="user" property="email"/>
	</a>
</td></tr>
</table>

<iambookmaster:booksCriteria name="bookCriteria" property="user" valueName="user"/>
<iambookmaster:selectBooks name="books" criteria="bookCriteria"/>

<iambookmaster:empty name="books">
	<p>This user does not have books</p>
</iambookmaster:empty>
<iambookmaster:notEmpty name="books">
	<table border="1">
	<tr><th>&nbsp;</th><th>User's books:</th></tr>
	<iambookmaster:iterate name="books" item="book">
	<tr>
	<td>
		<iambookmaster:locked name="book">
			<img title="This book is locked" alt="Locked book" src="images/RedFlag.gif"/>
		</iambookmaster:locked>
		<iambookmaster:notLocked name="book">
			&nbsp;
		</iambookmaster:notLocked>
	</td>
	<td>
		<a href="/admin.jsp?viewBook=<iambookmaster:bookInfo name="book" item="id"/>">
			<iambookmaster:write name="book" property="name"/>
		</a>
	</td>
	</tr>
	</iambookmaster:iterate>
	</table>
</iambookmaster:notEmpty>

</iambookmaster:exists>
</iambookmaster:userIsAdmin>