
<%@ page import="ch.freebo.ControlPanel"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="bootstrap">
<g:set var="entityName"
	value="${message(code: 'controlPanel.label', default: 'ControlPanel')}" />
<title><g:message code="default.list.label" args="[entityName]" /></title>
</head>
<body>
	<div class="row-fluid">

		<div class="span9">

			<div class="page-header">
				<h1>
					<g:message code="default.label" default="Control Panel" />
				</h1>
			</div>

			<g:form method="post" controller="controlPanel" action="callGCMService" id="${controlPanelInstanceList?.id}">

				<%--        <g:select name="controlPanelInstanceList.username" from="${controlPanelInstanceList}" />--%>
<table class="table table-striped">
				<thead>
					<tr>
						<th class="header"><g:message code="shopping.location.label"
								default="User" /></th>

						<th class="header"><g:message code="shopping.location.label"
								default="Registrierte Geräte" /></th>

						<th class="header"><g:message code="shopping.location.label"
								default="Registrations ID" /></th>


					</tr>
				</thead>


				<tbody>
					<g:each in="${controlPanelInstanceList}" var="controlPanelInstance">
						<tr>

							<td>
								${fieldValue(bean: controlPanelInstance, field: "username")}
							</td>

							<td>
								${fieldValue(bean: controlPanelInstance, field: "devices")}
							</td>

							<td>
								${controlPanelInstance.devices.deviceId}
							</td>

						</tr>
					</g:each>
				</tbody>
			</table>

				<g:textArea name="inputMessage" value="Message an registrierte Geräte!" />
				
				<input type="submit" name="submit" />
			</g:form>


			<div class="pagination">
				<bootstrap:paginate total="${controlPanelInstanceTotal}" />
			</div>
		</div>

	</div>
</body>
</html>
