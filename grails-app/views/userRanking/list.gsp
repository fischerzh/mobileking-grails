
<%@ page import="ch.freebo.UserRanking" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'userRanking.label', default: 'UserRanking')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
				<ul class="nav nav-pills">
  <li >
   <g:link class="create" controller="optIn" action="list">User Favorites (Opt-In/Out)</g:link>
  </li>
  <li>  <g:link class="create" controller="logMessages" action="list">User Log Infos</g:link>
  </li>
    <li class="active"><a href="#">User Achievements (Badges / Rank)</a></li>
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
				
							<form name="selectBox">
							
											<g:select name="userRankingProduct" from="${productList}"  optionKey="id" onChange="selectBox.submit()"
											value="${userRankingProduct}"
					  noSelection="${['':'Select One...']}" />
					  
<%--							<g:hiddenField name="user" value="${user}" />--%>
					
			</form>
				
				
				<table class="table table-striped">
					<thead>
						<tr>
						
													<g:sortableColumn property="user" title="${message(code: 'user', default: 'User')}" />
						
							<g:sortableColumn property="updated" title="${message(code: 'userRanking.updated.label', default: 'Last Updated')}" />
						
							<g:sortableColumn property="pointsCollected" title="${message(code: 'userRanking.pointsCollected.label', default: 'Points Collected')}" />
							
							<g:sortableColumn property="pointsCollected" title="${message(code: 'userRanking.totalPointsCollected.label', default: 'Total Points')}" />
						
							<g:sortableColumn property="product" title="Products" default="Product" /></th>
						
							<g:sortableColumn property="rank" title="${message(code: 'userRanking.rank.label', default: 'Rank')}" />
						
							<g:sortableColumn property="rankBefore" title="${message(code: 'userRanking.rankBefore.label', default: 'Rank Before')}" />
						
							<th></th>
						</tr>
					</thead>
					<tbody>
					<g:each in="${userRankingInstanceList}" var="userRankingInstance">
						<tr>
						
							<td>${fieldValue(bean: userRankingInstance, field: "user")}</td>
						
							<td><g:formatDate date="${userRankingInstance.updated}" /></td>
						
							<td>${fieldValue(bean: userRankingInstance, field: "pointsCollected")}</td>
							
							<td>${fieldValue(bean: userRankingInstance, field: "pointsCollected")}</td>
						
							<td>${fieldValue(bean: userRankingInstance, field: "product")}</td>
						
							<td>${fieldValue(bean: userRankingInstance, field: "rank")}</td>
						
							<td>${fieldValue(bean: userRankingInstance, field: "rankBefore")}</td>
							
							<td class="link">
								<g:link action="show" id="${userRankingInstance.id}" class="btn btn-small">Show &raquo;</g:link>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<bootstrap:paginate total="${userRankingInstanceTotal}" />
				</div>
			</div>

		</div>
	</body>
</html>
