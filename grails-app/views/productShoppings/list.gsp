
<%@ page import="ch.freebo.ProductShoppings" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'productShoppings.label', default: 'Einkaufs Details')}" />
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
						
							<g:sortableColumn property="price" title="${message(code: 'productShoppings.price.label', default: 'Preis')}" />
						
							<th class="header"><g:message code="productShoppings.product.label" default="Produkt" /></th>
						
							<g:sortableColumn property="qty" title="${message(code: 'productShoppings.qty.label', default: 'Anzahl')}" />
						
							<th class="header"><g:message code="productShoppings.shopping.label" default="Einkauf" /></th>
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${productShoppingsInstanceList}" var="productShoppingsInstance">
						<tr>
						
							<td>${fieldValue(bean: productShoppingsInstance, field: "price")}</td>
						
							<td>${fieldValue(bean: productShoppingsInstance, field: "product")}</td>
						
							<td>${fieldValue(bean: productShoppingsInstance, field: "qty")}</td>
						
							<td>${fieldValue(bean: productShoppingsInstance, field: "shopping")}</td>
						
							<td class="link">
								<g:link action="show" id="${productShoppingsInstance.id}" class="btn btn-small">Details &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${productShoppingsInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>
