
<%@ page import="ch.freebo.OptIn" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'optIn.label', default: 'Opt In/Out')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		
		<ul class="nav nav-pills">
  <li class="active">
    <a href="#">User Favorites (Opt-In/Out)</a>
  </li>
  <li><g:link class="create" controller="LogMessages" action="list">User Log Infos</g:link></li>
  <li><a href="#">User Achievements (Badges / Rank)</a></li>
</ul>
	
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
						
							<g:sortableColumn property="user" title="${message(code: 'optIn.isActive.label', default: 'User')}" />
						
							<g:sortableColumn property="product" title="${message(code: 'optIn.isActive.label', default: 'Produkt')}" />
						
							<g:sortableColumn property="isOptIn" title="${message(code: 'optIn.isOptIn.label', default: 'Opt-In')}" />

							<g:sortableColumn property="optInDate" title="${message(code: 'optIn.optInDate.label', default: 'Opt In Datum')}" />
						
							<g:sortableColumn property="optOutDate" title="${message(code: 'optIn.optOutDate.label', default: 'Opt Out Datum')}" />
						
							<g:sortableColumn property="isActive" title="${message(code: 'optIn.isActive.label', default: 'Produkt Aktiv')}" />
						
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${optInInstanceList}" var="optInInstance">
						<tr>
							<td>${fieldValue(bean: optInInstance, field: "user")}</td>

							<td>${fieldValue(bean: optInInstance, field: "product")}</td>
							
														<td><g:formatBoolean boolean="${optInInstance.optIn}" /></td>
						
							<td><g:formatDate date="${optInInstance.optInDate}" /></td>
						
							<td><g:formatDate date="${optInInstance.optOutDate}" /></td>
						
							<td><g:formatBoolean boolean="${optInInstance.isActive}" /></td>
						
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
