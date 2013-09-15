
<%@ page import="ch.freebo.LoyaltyProgram" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'loyaltyProgram.label', default: 'LoyaltyProgram')}" />
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
						
							<g:sortableColumn property="linkToLoyaltyProvider" title="${message(code: 'loyaltyProgram.linkToLoyaltyProvider.label', default: 'Link To Loyalty Provider')}" />
						
							<g:sortableColumn property="status" title="${message(code: 'loyaltyProgram.status.label', default: 'Status')}" />
						
							<g:sortableColumn property="ranking" title="${message(code: 'loyaltyProgram.ranking.label', default: 'Ranking')}" />
						
							<th class="header"><g:message code="loyaltyProgram.users.label" default="Users" /></th>
						
							<g:sortableColumn property="name" title="${message(code: 'loyaltyProgram.name.label', default: 'Name')}" />
						
							<th class="header"><g:message code="loyaltyProgram.product.label" default="Product" /></th>
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${loyaltyProgramInstanceList}" var="loyaltyProgramInstance">
						<tr>
						
							<td>${fieldValue(bean: loyaltyProgramInstance, field: "linkToLoyaltyProvider")}</td>
						
							<td><g:formatBoolean boolean="${loyaltyProgramInstance.status}" /></td>
						
							<td><g:formatBoolean boolean="${loyaltyProgramInstance.ranking}" /></td>
						
							<td>${fieldValue(bean: loyaltyProgramInstance, field: "users")}</td>
						
							<td>${fieldValue(bean: loyaltyProgramInstance, field: "name")}</td>
						
							<td>${fieldValue(bean: loyaltyProgramInstance, field: "product")}</td>
						
							<td class="link">
								<g:link action="show" id="${loyaltyProgramInstance.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${loyaltyProgramInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>
