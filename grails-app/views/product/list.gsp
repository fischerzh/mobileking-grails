
<%@ page import="ch.freebo.Product" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'product.label', default: 'Product')}" />
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
						
							<th class="header"><g:message code="product.loyaltyProgram.label" default="Loyalty Program" /></th>
						
							<g:sortableColumn property="imageLink" title="${message(code: 'product.imageLink.label', default: 'Image Link')}" />
						
							<g:sortableColumn property="ingredients" title="${message(code: 'product.ingredients.label', default: 'Ingredients')}" />
						
							<g:sortableColumn property="ean" title="${message(code: 'product.ean.label', default: 'Ean')}" />
						
							<g:sortableColumn property="name" title="${message(code: 'product.name.label', default: 'Name')}" />
						
							<th class="header"><g:message code="product.productCategory.label" default="Product Category" /></th>
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${productInstanceList}" var="productInstance">
						<tr>
						
							<td>${fieldValue(bean: productInstance, field: "loyaltyProgram")}</td>
						
							<td>${fieldValue(bean: productInstance, field: "imageLink")}</td>
						
							<td>${fieldValue(bean: productInstance, field: "ingredients")}</td>
						
							<td>${fieldValue(bean: productInstance, field: "ean")}</td>
						
							<td>${fieldValue(bean: productInstance, field: "name")}</td>
						
							<td>${fieldValue(bean: productInstance, field: "productCategory")}</td>
						
							<td class="link">
								<g:link action="show" id="${productInstance.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${productInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>
