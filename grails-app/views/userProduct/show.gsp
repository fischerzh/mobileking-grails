
<%@ page import="ch.freebo.UserProduct" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'userProduct.label', default: 'UserProduct')}" />
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
				
					<g:if test="${userProductInstance?.optInDate}">
						<dt><g:message code="userProduct.optInDate.label" default="Opt In Date" /></dt>
						
							<dd><g:formatDate date="${userProductInstance?.optInDate}" /></dd>
						
					</g:if>
				
					<g:if test="${userProductInstance?.optOutDate}">
						<dt><g:message code="userProduct.optOutDate.label" default="Opt Out Date" /></dt>
						
							<dd><g:formatDate date="${userProductInstance?.optOutDate}" /></dd>
						
					</g:if>
				
					<g:if test="${userProductInstance?.isActive}">
						<dt><g:message code="userProduct.isActive.label" default="Is Active" /></dt>
						
							<dd><g:formatBoolean boolean="${userProductInstance?.isActive}" /></dd>
						
					</g:if>
				
					<g:if test="${userProductInstance?.optIn}">
						<dt><g:message code="userProduct.optIn.label" default="Opt In" /></dt>
						
							<dd><g:formatBoolean boolean="${userProductInstance?.optIn}" /></dd>
						
					</g:if>
				
					<g:if test="${userProductInstance?.product}">
						<dt><g:message code="userProduct.product.label" default="Product" /></dt>
						
							<dd><g:link controller="product" action="show" id="${userProductInstance?.product?.id}">${userProductInstance?.product?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${userProductInstance?.user}">
						<dt><g:message code="userProduct.user.label" default="User" /></dt>
						
							<dd><g:link controller="user" action="show" id="${userProductInstance?.user?.id}">${userProductInstance?.user?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
				</dl>

				<g:form>
					<g:hiddenField name="id" value="${userProductInstance?.id}" />
					<div class="form-actions">
						<g:link class="btn" action="edit" id="${userProductInstance?.id}">
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
