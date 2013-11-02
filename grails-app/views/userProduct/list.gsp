
<%@ page import="ch.freebo.UserProduct" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'userProduct.label', default: 'Opt In/Out')}" />
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
						
							<g:sortableColumn property="optInDate" title="${message(code: 'userProduct.optInDate.label', default: 'Opt In Datum')}" />
						
							<g:sortableColumn property="optOutDate" title="${message(code: 'userProduct.optOutDate.label', default: 'Opt Out Datum')}" />
						
							<g:sortableColumn property="isActive" title="${message(code: 'userProduct.isActive.label', default: 'Produkt Aktiv')}" />
						
							<g:sortableColumn property="optIn" title="${message(code: 'userProduct.optIn.label', default: 'Opt In')}" />
						
							<th class="header"><g:message code="userProduct.product.label" default="Produkt" /></th>
						
							<th class="header"><g:message code="userProduct.user.label" default="User" /></th>
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${userProductInstanceList}" var="userProductInstance">
						<tr>
						
							<td><g:formatDate date="${userProductInstance.optInDate}" /></td>
						
							<td><g:formatDate date="${userProductInstance.optOutDate}" /></td>
						
							<td><g:formatBoolean boolean="${userProductInstance.isActive}" /></td>
						
							<td><g:formatBoolean boolean="${userProductInstance.optIn}" /></td>
						
							<td>${fieldValue(bean: userProductInstance, field: "product")}</td>
						
							<td>${fieldValue(bean: userProductInstance, field: "user")}</td>
						
							<td class="link">
								<g:link action="show" id="${userProductInstance.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${userProductInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>
