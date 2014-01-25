
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
	
	<div class="span3">
				<div class="well">
					<ul class="nav nav-list">
						<li class="nav-header">${entityName}</li>
						<li  class="active">
							<g:link class="list" action="list">
								<i class="icon-list"></i>
								<g:message code="default.label" default="Message Panel" />
							</g:link>
						</li>
						<li>
							<g:link class="create" action="create">
								<i class="icon-list"></i>
								<g:message code="default.label" default="Shopping Simulator" />
							</g:link>
						</li>
					</ul>
				</div>
			</div>

		<div class="span9">

			<div class="page-header">
				<h1>
					<g:message code="default.label" default="Message Panel" />
				</h1>
			</div>
			
				<g:if test="${flash.message}">
				<bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
				</g:if>
				

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
			
			<g:select name="controlPanelUser" from="${controlPanelInstanceList.username}" 
					  noSelection="${['null':'Select One...']}" />

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
