
<%@ page import="ch.freebo.UserRanking" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'userRanking.label', default: 'UserRanking')}" />
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
				
					<g:if test="${userRankingInstance?.pointOfSales}">
						<dt><g:message code="userRanking.pointOfSales.label" default="Point Of Sales" /></dt>
						
							<dd><g:link controller="retailer" action="show" id="${userRankingInstance?.pointOfSales?.id}">${userRankingInstance?.pointOfSales?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${userRankingInstance?.newRank}">
						<dt><g:message code="userRanking.newRank.label" default="New Rank" /></dt>
						
							<dd><g:formatBoolean boolean="${userRankingInstance?.newRank}" /></dd>
						
					</g:if>
				
					<g:if test="${userRankingInstance?.pointsCollected}">
						<dt><g:message code="userRanking.pointsCollected.label" default="Points Collected" /></dt>
						
							<dd><g:fieldValue bean="${userRankingInstance}" field="pointsCollected"/></dd>
						
					</g:if>
				
					<g:if test="${userRankingInstance?.product}">
						<dt><g:message code="userRanking.product.label" default="Product" /></dt>
						
							<dd><g:link controller="product" action="show" id="${userRankingInstance?.product?.id}">${userRankingInstance?.product?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
					<g:if test="${userRankingInstance?.rank}">
						<dt><g:message code="userRanking.rank.label" default="Rank" /></dt>
						
							<dd><g:fieldValue bean="${userRankingInstance}" field="rank"/></dd>
						
					</g:if>
				
					<g:if test="${userRankingInstance?.rankBefore}">
						<dt><g:message code="userRanking.rankBefore.label" default="Rank Before" /></dt>
						
							<dd><g:fieldValue bean="${userRankingInstance}" field="rankBefore"/></dd>
						
					</g:if>
				
					<g:if test="${userRankingInstance?.totalPointsCollected}">
						<dt><g:message code="userRanking.totalPointsCollected.label" default="Total Points Collected" /></dt>
						
							<dd><g:fieldValue bean="${userRankingInstance}" field="totalPointsCollected"/></dd>
						
					</g:if>
				
					<g:if test="${userRankingInstance?.updated}">
						<dt><g:message code="userRanking.updated.label" default="Updated" /></dt>
						
							<dd><g:formatDate date="${userRankingInstance?.updated}" /></dd>
						
					</g:if>
				
					<g:if test="${userRankingInstance?.user}">
						<dt><g:message code="userRanking.user.label" default="User" /></dt>
						
							<dd><g:link controller="user" action="show" id="${userRankingInstance?.user?.id}">${userRankingInstance?.user?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
				</dl>

				<g:form>
					<g:hiddenField name="id" value="${userRankingInstance?.id}" />
					<div class="form-actions">
						<g:link class="btn" action="edit" id="${userRankingInstance?.id}">
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
