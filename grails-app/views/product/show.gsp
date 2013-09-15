
<%@ page import="ch.freebo.Product" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'product.label', default: 'Product')}" />
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
				
					<g:if test="${productInstance?.loyaltyProgram}">
						<dt><g:message code="product.loyaltyProgram.label" default="Loyalty Program" /></dt>
						
							<dd><g:link controller="loyaltyProgram" action="show" id="${productInstance?.loyaltyProgram?.id}">${productInstance?.loyaltyProgram?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${productInstance?.imageLink}">
						<dt><g:message code="product.imageLink.label" default="Image Link" /></dt>
						
							<dd><g:fieldValue bean="${productInstance}" field="imageLink"/></dd>
						
					</g:if>
				
					<g:if test="${productInstance?.shoppings}">
						<dt><g:message code="product.shoppings.label" default="Shoppings" /></dt>
						
							<g:each in="${productInstance.shoppings}" var="s">
							<dd><g:link controller="shopping" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></dd>
							</g:each>
						
					</g:if>
				
					<g:if test="${productInstance?.ingredients}">
						<dt><g:message code="product.ingredients.label" default="Ingredients" /></dt>
						
							<dd><g:fieldValue bean="${productInstance}" field="ingredients"/></dd>
						
					</g:if>
				
					<g:if test="${productInstance?.ean}">
						<dt><g:message code="product.ean.label" default="Ean" /></dt>
						
							<dd><g:fieldValue bean="${productInstance}" field="ean"/></dd>
						
					</g:if>
				
					<g:if test="${productInstance?.name}">
						<dt><g:message code="product.name.label" default="Name" /></dt>
						
							<dd><g:fieldValue bean="${productInstance}" field="name"/></dd>
						
					</g:if>
				
					<g:if test="${productInstance?.productCategory}">
						<dt><g:message code="product.productCategory.label" default="Product Category" /></dt>
						
							<dd><g:link controller="productCategory" action="show" id="${productInstance?.productCategory?.id}">${productInstance?.productCategory?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
				</dl>

				<g:form>
					<g:hiddenField name="id" value="${productInstance?.id}" />
					<div class="form-actions">
						<g:link class="btn" action="edit" id="${productInstance?.id}">
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
