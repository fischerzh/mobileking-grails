
<%@ page import="ch.freebo.LogMessages" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'logMessages.label', default: 'LogMessages')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
	
			<ul class="nav nav-pills">
  <li >
   <g:link class="create" controller="userProduct" action="list">User Favorites (Opt-In/Out)</g:link>
  </li>
  <li class="active"><a href="#">User Log Infos</a></li>
  <li><a href="#">User Achievements (Badges / Rank)</a></li>
</ul>
		<div class="row-fluid">
			
			<div class="span3">
				<div class="well">
					<ul class="nav nav-list">
						<li class="nav-header">${entityName}</li>
						<li class="active">
							<g:link class="list" action="list">
								<i class="icon-list icon-white"></i>
								<g:message code="default.label" default="Log Messages" />
							</g:link>
						</li>
						<li>
							<g:link class="create" action="create">
								<i class="icon-list icon-white"></i>
								<g:message code="default.label" default="Login Information" />
							</g:link>
						</li>
					</ul>
				</div>
			</div>

			<div class="span9">
				
				<div class="page-header">
					<h1><g:message code="default.list.label" args="[entityName]" /></h1>
				</div>

				<g:if test="${flash.message}">
				<bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
				</g:if>
				
				<table class="table table-striped">
					<thead>
						<tr>
						
							<g:sortableColumn property="user" title="${message(code: 'logMessages.messageId.label', default: 'User')}" />
						
							<g:sortableColumn property="createDate" title="${message(code: 'logMessages.createDate.label', default: 'Create Date')}" />
						
							<g:sortableColumn property="action" title="${message(code: 'logMessages.action.label', default: 'Action')}" />
						
							<g:sortableColumn property="message" title="${message(code: 'logMessages.message.label', default: 'Message')}" />
						
							<g:sortableColumn property="logDate" title="${message(code: 'logMessages.logDate.label', default: 'Log Date')}" />
						
							<g:sortableColumn property="location" title="${message(code: 'logMessages.location.label', default: 'Location')}" />
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${userLogList}" var="user">
					<g:each in="${user.logMessages}" var="logMessage">
<%--					${userLogList.logMessages}--%>
						<tr>
<%--						${fieldValue(bean: logMessage, field: "messageId")}--%>
							<td>${fieldValue(bean: logMessage, field: "messageId")}</td>
						
							<td>${fieldValue(bean: logMessage, field: "createDate")}</td>
						
							<td>${fieldValue(bean: logMessage, field: "action")}</td>
						
							<td>${fieldValue(bean: logMessage, field: "message")}</td>
						
							<td><g:formatDate date="${logMessage.logDate}" /></td>
						
							<td>${fieldValue(bean: logMessage, field: "location")}</td>
						
							<td class="link">
								<g:link action="show" id="${logMessage.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
											</g:each>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${logMessagesInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>
