<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://iambookmaster.com/iambookmaster.tld" prefix="iambookmaster" %>
		
<iambookmaster:userIsLogged>
<iambookmaster:exists name="book" scope="page">
		<iambookmaster:bookTypeAvailabe type="editor" name="book">
			<div>
			<iambookmaster:loadModelLink name="book" type="editor">
				<iambookmaster:message value="jspLoadLinkEditor"/>
			</iambookmaster:loadModelLink>
			</div>
		</iambookmaster:bookTypeAvailabe>
		
		<div>
			<iambookmaster:loadModelLink name="book" type="project" target="_blank">
				<iambookmaster:message value="jspLoadLinkProject"/>
			</iambookmaster:loadModelLink>
		</div>
		
		<iambookmaster:bookTypeAvailabe type="html" name="book">
			<div>
				<iambookmaster:loadModelLink name="book" type="html" target="_blank">
					<iambookmaster:message value="jspLoadLinkHTML"/>
				</iambookmaster:loadModelLink>
			</div>
		</iambookmaster:bookTypeAvailabe>
		
		<iambookmaster:bookTypeAvailabe type="text" name="book">
			<div>
				<iambookmaster:loadModelLink name="book" type="text" target="_blank">
					<iambookmaster:message value="jspLoadLinkText"/>
				</iambookmaster:loadModelLink>
			</div>
			
			<div>
				<iambookmaster:loadModelLink name="book" type="urq">
					<iambookmaster:message value="jspLoadLinkURQ"/>
				</iambookmaster:loadModelLink>
			</div>
			
			<div>
				<iambookmaster:loadModelLink name="book" type="urqShort">
					<iambookmaster:message value="jspLoadLinkURQShort"/>
				</iambookmaster:loadModelLink>
			</div>

			<div>
				<iambookmaster:loadModelLink name="book" type="qsp">
					<iambookmaster:message value="jspLoadLinkQSP"/>
				</iambookmaster:loadModelLink>
			</div>
		</iambookmaster:bookTypeAvailabe>

</iambookmaster:exists>
</iambookmaster:userIsLogged>