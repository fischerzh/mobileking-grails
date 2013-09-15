
<%@ page import="ch.freebo.LoyaltyProgramLevels" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'loyaltyProgramLevels.label', default: 'LoyaltyProgramLevels')}" />
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
				
					<g:if test="${loyaltyProgramLevelsInstance?.levelName}">
						<dt><g:message code="loyaltyProgramLevels.levelName.label" default="Level Name" /></dt>
						
							<dd><g:fieldValue bean="${loyaltyProgramLevelsInstance}" field="levelName"/></dd>
						
					</g:if>
				
					<g:if test="${loyaltyProgramLevelsInstance?.loyaltyProgram}">
						<dt><g:message code="loyaltyProgramLevels.loyaltyProgram.label" default="Loyalty Program" /></dt>
						
							<dd><g:link controller="loyaltyProgram" action="show" id="${loyaltyProgramLevelsInstance?.loyaltyProgram?.id}">${loyaltyProgramLevelsInstance?.loyaltyProgram?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${loyaltyProgramLevelsInstance?.pointLevelMax}">
						<dt><g:message code="loyaltyProgramLevels.pointLevelMax.label" default="Point Level Max" /></dt>
						
							<dd><g:fieldValue bean="${loyaltyProgramLevelsInstance}" field="pointLevelMax"/></dd>
						
					</g:if>
				
					<g:if test="${loyaltyProgramLevelsInstance?.pointLevelMin}">
						<dt><g:message code="loyaltyProgramLevels.pointLevelMin.label" default="Point Level Min" /></dt>
						
							<dd><g:fieldValue bean="${loyaltyProgramLevelsInstance}" field="pointLevelMin"/></dd>
						
					</g:if>
				
					<g:if test="${loyaltyProgramLevelsInstance?.pointLevelName}">
						<dt><g:message code="loyaltyProgramLevels.pointLevelName.label" default="Point Level Name" /></dt>
						
							<dd><g:fieldValue bean="${loyaltyProgramLevelsInstance}" field="pointLevelName"/></dd>
						
					</g:if>
				
				</dl>

				<g:form>
					<g:hiddenField name="id" value="${loyaltyProgramLevelsInstance?.id}" />
					<div class="form-actions">
						<g:link class="btn" action="edit" id="${loyaltyProgramLevelsInstance?.id}">
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
