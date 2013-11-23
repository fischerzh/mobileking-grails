
<%@ page import="ch.freebo.LogMessages" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'logMessages.label', default: 'LogMessages')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="row-fluid">
			
			<div class="span3">
				<div class="well">
					<ul class="nav nav-list">
						<li class="nav-header">${entityName}</li>
						<li>
							<g:link class="list" action="list">
								<i class="icon-list"></i>
								<g:message code="default.list.label" args="[entityName]" />
							</g:link>
						</li>
						<li>
							<g:link class="create" action="create">
								<i class="icon-plus"></i>
								<g:message code="default.create.label" args="[entityName]" />
							</g:link>
						</li>
					</ul>
				</div>
			</div>
			
			<div class="span9">

				<div class="page-header">
					<h1><g:message code="default.show.label" args="[entityName]" /></h1>
				</div>

				<g:if test="${flash.message}">
				<bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
				</g:if>

				<dl>
				
					<g:if test="${logMessagesInstance?.messageId}">
						<dt><g:message code="logMessages.messageId.label" default="Message Id" /></dt>
						
							<dd><g:fieldValue bean="${logMessagesInstance}" field="messageId"/></dd>
						
					</g:if>
				
					<g:if test="${logMessagesInstance?.createDate}">
						<dt><g:message code="logMessages.createDate.label" default="Create Date" /></dt>
						
							<dd><g:fieldValue bean="${logMessagesInstance}" field="createDate"/></dd>
						
					</g:if>
				
					<g:if test="${logMessagesInstance?.action}">
						<dt><g:message code="logMessages.action.label" default="Action" /></dt>
						
							<dd><g:fieldValue bean="${logMessagesInstance}" field="action"/></dd>
						
					</g:if>
				
					<g:if test="${logMessagesInstance?.message}">
						<dt><g:message code="logMessages.message.label" default="Message" /></dt>
						
							<dd><g:fieldValue bean="${logMessagesInstance}" field="message"/></dd>
						
					</g:if>
				
					<g:if test="${logMessagesInstance?.logDate}">
						<dt><g:message code="logMessages.logDate.label" default="Log Date" /></dt>
						
							<dd><g:formatDate date="${logMessagesInstance?.logDate}" /></dd>
						
					</g:if>
				
					<g:if test="${logMessagesInstance?.location}">
						<dt><g:message code="logMessages.location.label" default="Location" /></dt>
						
							<dd><g:fieldValue bean="${logMessagesInstance}" field="location"/></dd>
						
					</g:if>
				
				</dl>

				<g:form>
					<g:hiddenField name="id" value="${logMessagesInstance?.id}" />
					<div class="form-actions">
						<g:link class="btn" action="edit" id="${logMessagesInstance?.id}">
							<i class="icon-pencil"></i>
							<g:message code="default.button.edit.label" default="Edit" />
						</g:link>
						<button class="btn btn-danger" type="submit" name="_action_delete">
							<i class="icon-trash icon-white"></i>
							<g:message code="default.button.delete.label" default="Delete" />
						</button>
					</div>
				</g:form>

			</div>

		</div>
	</body>
</html>
