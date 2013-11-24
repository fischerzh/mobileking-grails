
<%@ page import="ch.freebo.ProductShoppings" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'productShoppings.label', default: 'ProductShoppings')}" />
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
				
					<g:if test="${productShoppingsInstance?.price}">
						<dt><g:message code="productShoppings.price.label" default="Price" /></dt>
						
							<dd><g:fieldValue bean="${productShoppingsInstance}" field="price"/></dd>
						
					</g:if>
				
					<g:if test="${productShoppingsInstance?.product}">
						<dt><g:message code="productShoppings.product.label" default="Product" /></dt>
						
							<dd><g:link controller="product" action="show" id="${productShoppingsInstance?.product?.id}">${productShoppingsInstance?.product?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${productShoppingsInstance?.qty}">
						<dt><g:message code="productShoppings.qty.label" default="Qty" /></dt>
						
							<dd><g:fieldValue bean="${productShoppingsInstance}" field="qty"/></dd>
						
					</g:if>
				
				</dl>

				<g:form>
					<g:hiddenField name="id" value="${productShoppingsInstance?.id}" />
					<div class="form-actions">
						<g:link class="btn" action="edit" id="${productShoppingsInstance?.id}">
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
