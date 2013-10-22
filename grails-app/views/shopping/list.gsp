
<%@ page import="ch.freebo.Shopping" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'shopping.label', default: 'Shopping')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="row-fluid">
			
			<div class="span3">
				<div class="well">
					<ul class="nav nav-list">
						<li class="nav-header">EINKÄUFE</li>
						<li class="active">
							<g:link class="list" action="list">
								<i class="icon-list icon-white"></i>
								<g:message code="default.label" default="Einkäufe" />
							</g:link>
						</li>
						<li>
							<g:link class="create" action="create">
								<i class="icon-plus"></i>
								<g:message code="default.label" default="Einkauf anlegen" />
							</g:link>
						</li>
					</ul>
				</div>
			</div>

			<div class="span9">
				
				<div class="page-header">
					<h1><g:message code="default.label" default="Einkäufe" /></h1>
				</div>

				<g:if test="${flash.message}">
				<bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
				</g:if>
				
				<table class="table table-striped">
					<thead>
						<tr>
						
							<g:sortableColumn property="date" title="${message(code: 'shopping.date.label', default: 'Date')}" />
						
							<th class="header"><g:message code="shopping.location.label" default="Location" /></th>
						
							<th class="header"><g:message code="shopping.retailer.label" default="Retailer" /></th>
						
							<th class="header"><g:message code="shopping.user.label" default="User" /></th>
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${shoppingInstanceList}" var="shoppingInstance">
						<tr>
						
							<td><g:formatDate date="${shoppingInstance.date}" /></td>
						
							<td>${fieldValue(bean: shoppingInstance, field: "location")}</td>
						
							<td>${fieldValue(bean: shoppingInstance, field: "retailer")}</td>
						
							<td>${fieldValue(bean: shoppingInstance, field: "user")}</td>
						
							<td class="link">
								<g:link action="show" id="${shoppingInstance.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${shoppingInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>
