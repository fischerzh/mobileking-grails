
<%@ page import="ch.freebo.LoyaltyCard" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'loyaltyCard.label', default: 'LoyaltyCard')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="row-fluid">
			
			<div class="span3">
				<div class="well">
					<ul class="nav nav-list">
						<li class="nav-header">${entityName}</li>
						<li class="active">
							<g:link class="list" action="list">
								<i class="icon-list icon-white"></i>
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
					<h1><g:message code="default.list.label" args="[entityName]" /></h1>
				</div>

				<g:if test="${flash.message}">
				<bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
				</g:if>
				
				<table class="table table-striped">
					<thead>
						<tr>
						
							<g:sortableColumn property="username" title="${message(code: 'loyaltyCard.username.label', default: 'Username')}" />
						
							<g:sortableColumn property="password" title="${message(code: 'loyaltyCard.password.label', default: 'Password')}" />
						
							<g:sortableColumn property="link" title="${message(code: 'loyaltyCard.link.label', default: 'Link')}" />
						
							<g:sortableColumn property="name" title="${message(code: 'loyaltyCard.name.label', default: 'Name')}" />
						
							<g:sortableColumn property="number" title="${message(code: 'loyaltyCard.number.label', default: 'Number')}" />
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${loyaltyCardInstanceList}" var="loyaltyCardInstance">
						<tr>
						
							<td>${fieldValue(bean: loyaltyCardInstance, field: "username")}</td>
						
							<td>${fieldValue(bean: loyaltyCardInstance, field: "password")}</td>
						
							<td>${fieldValue(bean: loyaltyCardInstance, field: "link")}</td>
						
							<td>${fieldValue(bean: loyaltyCardInstance, field: "name")}</td>
						
							<td>${fieldValue(bean: loyaltyCardInstance, field: "number")}</td>
						
							<td class="link">
								<g:link action="show" id="${loyaltyCardInstance.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${loyaltyCardInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>
