<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://iambookmaster.com/iambookmaster.tld" prefix="iambookmaster" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title><iambookmaster:message value="jspMainTitle"/></title>
  <link rel="stylesheet" type="text/css" href="creator.css"/>
</head>
<body>
<iambookmaster:rememberCallback/>

<iambookmaster:userIsNotLogged>
<p>
<iambookmaster:message value="jspMainHaveTo"/>
<a href="<iambookmaster:logingURL/>"><iambookmaster:message value="jspMainSignIn"/></a>
<iambookmaster:message value="jspMainToWorkWithEditor"/>
</p>
<p>
<iambookmaster:message value="jspExternalLogin"/>
<br/>
<a href="<iambookmaster:logingURL/>" target="_blank"><iambookmaster:message value="jspExternalLoginLink"/></a>
</p>
</iambookmaster:userIsNotLogged>

<iambookmaster:userIsLogged>
<iambookmaster:updateUserBasicAccountInfo/>

<iambookmaster:currentUser name="user"/>
<p>
<iambookmaster:message value="jspMainToWelcome" arg0Name="user" arg0Property="nick"/>
&nbsp; 
<iambookmaster:userIsAdmin>
<a href="admin.jsp"><iambookmaster:message value="jspMainAdminLink"/></a>&nbsp;
</iambookmaster:userIsAdmin>
<a href="<iambookmaster:logoutURL/>"><iambookmaster:message value="jspMainLogOut"/></a>
</p>
<iambookmaster:locked name="user">
	<p><img alt="Locked account" src="images/RedFlag.gif">
	<iambookmaster:message value="jspMainAccountLocked"/>
	</p>
</iambookmaster:locked>

<iambookmaster:bookTypeNotAvailabe type="editor" name="">
<p>
<iambookmaster:message value="jspMainEditorNotDetected"/>
</p>
</iambookmaster:bookTypeNotAvailabe>

<iambookmaster:notExists name="selectedBook">
	<iambookmaster:booksCriteria name="criteria" property="user" valueName="user"/>
	<iambookmaster:selectBooks name="books" criteria="criteria"/>
	<iambookmaster:empty name="books">
		<p><iambookmaster:message value="jspMainNoGameBooks"/></p>
	</iambookmaster:empty>
	<iambookmaster:notEmpty name="books">
		<p><iambookmaster:message value="jspMainYourBooks"/></p>
		<table>
		<iambookmaster:iterate name="books" item="book">
		<tr>
		<td style="border-top:1px solid black">
			<iambookmaster:locked name="book">
				<div>
				<img alt="Locked book" src="images/RedFlag.gif">
				<iambookmaster:message value="jspMainBookIsLocked"/>
				</div>
			</iambookmaster:locked>
			<iambookmaster:equalsTrue name="book" property="published">
				<img title="<iambookmaster:message value="jspMainBookIsPublished"/>"
				 src="images/printer.png"/>
			</iambookmaster:equalsTrue>
			<iambookmaster:equalsFalse name="book" property="published">
				<img title="<iambookmaster:message value="jspMainBookIsNotPublished"/>"
				src="images/media-floppy.png"/>
			</iambookmaster:equalsFalse>
			<a href="main.jsp?selectedBook=<iambookmaster:bookInfo name="book" item="id"/>" title="Open book">
				<iambookmaster:write name="book" property="name"/>
			</a>
		<div style="border: 1px solid black;">
			<iambookmaster:write name="book" property="description"/></div>
		</td>
		<td style="border-top:1px solid black">
			<%@ include file="bookLoadLinks.jsp" %>
		</td>
		</tr>
		</iambookmaster:iterate>
		</table>
	</iambookmaster:notEmpty>
</iambookmaster:notExists>
<iambookmaster:exists name="selectedBook">
	<iambookmaster:booksCriteria name="criteria" property="user" valueName="user"/>
	<iambookmaster:booksCriteria name="criteria" property="id" valueName="selectedBook"/>
	<iambookmaster:selectBooks name="books" criteria="criteria"/>
	<p><a href="main.jsp" title="Return to all books">Return</a></p>
	<iambookmaster:empty name="books">
		<p><iambookmaster:message value="jspMainNoGameBooks"/></p>
	</iambookmaster:empty>
	<iambookmaster:notEmpty name="books">
		<iambookmaster:iterate name="books" item="book">
		<iambookmaster:locked name="book">
			<div>
			<img alt="Locked book" src="images/RedFlag.gif">
			<iambookmaster:message value="jspMainBookIsLocked"/>
			</div>
		</iambookmaster:locked>
		<div>
			<iambookmaster:equalsTrue name="book" property="published">
				<img title="<iambookmaster:message value="jspMainBookIsPublished"/>"
				 src="images/printer.png"/>
			</iambookmaster:equalsTrue>
			<iambookmaster:equalsFalse name="book" property="published">
				<img title="<iambookmaster:message value="jspMainBookIsNotPublished"/>"
				src="images/media-floppy.png"/>
			</iambookmaster:equalsFalse>
			<iambookmaster:write name="book" property="name"/>
		</div>
		<table>
			<tr><td>
				<div style="border: 1px solid black;">
					<iambookmaster:write name="book" property="description"/>
				</div>
			</td><td>
				<%@ include file="bookLoadLinks.jsp" %>
			</td></tr>
		</table>
		<div>
			<%@ include file="bookPublish.jsp" %>
		</div>
		<div>
			<%@ include file="bookVersions.jsp" %>
		</div>
		</iambookmaster:iterate>
	
	</iambookmaster:notEmpty>
</iambookmaster:exists>

</iambookmaster:userIsLogged>

</body>
</html>