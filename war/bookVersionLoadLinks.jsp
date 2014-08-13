<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://iambookmaster.com/iambookmaster.tld" prefix="iambookmaster" %>
		
<iambookmaster:userIsLogged>
<iambookmaster:exists name="version" scope="page">
		<iambookmaster:bookTypeAvailabe type="editor" name="version">
			<div>
			<iambookmaster:loadModelLink name="version" type="editor">Load</iambookmaster:loadModelLink>
			</div>
		</iambookmaster:bookTypeAvailabe>
		
		<div>
			<iambookmaster:loadModelLink name="version" type="project" target="_blank">Project</iambookmaster:loadModelLink>
		</div>
		
		<iambookmaster:bookTypeAvailabe type="html" name="version">
			<div>
				<iambookmaster:loadModelLink name="version" type="html" target="_blank">HTML</iambookmaster:loadModelLink>
			</div>
		</iambookmaster:bookTypeAvailabe>

		<iambookmaster:bookTypeAvailabe type="text" name="version">
			<div>
				<iambookmaster:loadModelLink name="version" type="text" target="_blank">
					Text
				</iambookmaster:loadModelLink>
			</div>

			<div>
				<iambookmaster:loadModelLink name="version" type="urq">
					<iambookmaster:message value="jspLoadLinkURQ"/>
				</iambookmaster:loadModelLink>
			</div>

			<div>
				<iambookmaster:loadModelLink name="version" type="urqShort">
					<iambookmaster:message value="jspLoadLinkURQShort"/>
				</iambookmaster:loadModelLink>
			</div>

			<div>
				<iambookmaster:loadModelLink name="version" type="qsp">
					<iambookmaster:message value="jspLoadLinkQSP"/>
				</iambookmaster:loadModelLink>
			</div>
		</iambookmaster:bookTypeAvailabe>
</iambookmaster:exists>
</iambookmaster:userIsLogged>