
<%@ page import="ch.freebo.Shopping" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'shopping.label', default: 'Shopping')}" />
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
				
					<g:if test="${shoppingInstance?.productShoppings}">
						<dt><g:message code="shopping.productShoppings.label" default="Product Shoppings" /></dt>
						
							<g:each in="${shoppingInstance.productShoppings}" var="p">
							<dd><g:link controller="productShoppings" action="show" id="${p.id}">${p?.encodeAsHTML()}</g:link></dd>
							</g:each>
						
					</g:if>
				
					<g:if test="${shoppingInstance?.date}">
						<dt><g:message code="shopping.date.label" default="Date" /></dt>
						
							<dd><g:formatDate date="${shoppingInstance?.date}" /></dd>
						
					</g:if>
				
					<g:if test="${shoppingInstance?.retailer}">
						<dt><g:message code="shopping.retailer.label" default="Retailer" /></dt>
						
							<dd><g:link controller="retailer" action="show" id="${shoppingInstance?.retailer?.id}">${shoppingInstance?.retailer?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${shoppingInstance?.user}">
						<dt><g:message code="shopping.user.label" default="User" /></dt>
						
							<dd><g:link controller="user" action="show" id="${shoppingInstance?.user?.id}">${shoppingInstance?.user?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
				</dl>

				<g:form>
					<g:hiddenField name="id" value="${shoppingInstance?.id}" />
					<div class="form-actions">
						<g:link class="btn" action="edit" id="${shoppingInstance?.id}">
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
