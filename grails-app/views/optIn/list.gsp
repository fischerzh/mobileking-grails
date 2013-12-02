
<%@ page import="ch.freebo.OptIn" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'optIn.label', default: 'OptIn')}" />
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
						
							<g:sortableColumn property="optInDate" title="${message(code: 'optIn.optInDate.label', default: 'Opt In Date')}" />
						
							<g:sortableColumn property="optOutDate" title="${message(code: 'optIn.optOutDate.label', default: 'Opt Out Date')}" />
						
							<g:sortableColumn property="isActive" title="${message(code: 'optIn.isActive.label', default: 'Is Active')}" />
						
							<g:sortableColumn property="lastUpdated" title="${message(code: 'optIn.lastUpdated.label', default: 'Last Updated')}" />
						
							<g:sortableColumn property="optIn" title="${message(code: 'optIn.optIn.label', default: 'Opt In')}" />
						
							<th class="header"><g:message code="optIn.product.label" default="Product" /></th>
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${optInInstanceList}" var="optInInstance">
						<tr>
						
							<td><g:formatDate date="${optInInstance.optInDate}" /></td>
						
							<td><g:formatDate date="${optInInstance.optOutDate}" /></td>
						
							<td><g:formatBoolean boolean="${optInInstance.isActive}" /></td>
						
							<td><g:formatDate date="${optInInstance.lastUpdated}" /></td>
						
							<td><g:formatBoolean boolean="${optInInstance.optIn}" /></td>
						
							<td>${fieldValue(bean: optInInstance, field: "product")}</td>
						
							<td class="link">
								<g:link action="show" id="${optInInstance.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${optInInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>
