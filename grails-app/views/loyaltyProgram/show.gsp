
<%@ page import="ch.freebo.LoyaltyProgram" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'loyaltyProgram.label', default: 'LoyaltyProgram')}" />
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
				
					<g:if test="${loyaltyProgramInstance?.loyaltyProgramLevels}">
						<dt><g:message code="loyaltyProgram.loyaltyProgramLevels.label" default="Loyalty Program Levels" /></dt>
						
							<g:each in="${loyaltyProgramInstance.loyaltyProgramLevels}" var="l">
							<dd><g:link controller="loyaltyProgramLevels" action="show" id="${l.id}">${l?.encodeAsHTML()}</g:link></dd>
							</g:each>
						
					</g:if>
				
					<g:if test="${loyaltyProgramInstance?.linkToLoyaltyProvider}">
						<dt><g:message code="loyaltyProgram.linkToLoyaltyProvider.label" default="Link To Loyalty Provider" /></dt>
						
							<dd><g:fieldValue bean="${loyaltyProgramInstance}" field="linkToLoyaltyProvider"/></dd>
						
					</g:if>
				
					<g:if test="${loyaltyProgramInstance?.status}">
						<dt><g:message code="loyaltyProgram.status.label" default="Status" /></dt>
						
							<dd><g:formatBoolean boolean="${loyaltyProgramInstance?.status}" /></dd>
						
					</g:if>
				
					<g:if test="${loyaltyProgramInstance?.ranking}">
						<dt><g:message code="loyaltyProgram.ranking.label" default="Ranking" /></dt>
						
							<dd><g:formatBoolean boolean="${loyaltyProgramInstance?.ranking}" /></dd>
						
					</g:if>
				
					<g:if test="${loyaltyProgramInstance?.users}">
						<dt><g:message code="loyaltyProgram.users.label" default="Users" /></dt>
						
							<dd><g:link controller="user" action="show" id="${loyaltyProgramInstance?.users?.id}">${loyaltyProgramInstance?.users?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${loyaltyProgramInstance?.name}">
						<dt><g:message code="loyaltyProgram.name.label" default="Name" /></dt>
						
							<dd><g:fieldValue bean="${loyaltyProgramInstance}" field="name"/></dd>
						
					</g:if>
				
					<g:if test="${loyaltyProgramInstance?.product}">
						<dt><g:message code="loyaltyProgram.product.label" default="Product" /></dt>
						
							<dd><g:link controller="product" action="show" id="${loyaltyProgramInstance?.product?.id}">${loyaltyProgramInstance?.product?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
				</dl>

				<g:form>
					<g:hiddenField name="id" value="${loyaltyProgramInstance?.id}" />
					<div class="form-actions">
						<g:link class="btn" action="edit" id="${loyaltyProgramInstance?.id}">
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
