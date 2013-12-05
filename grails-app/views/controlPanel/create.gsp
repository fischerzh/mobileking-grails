<%@ page import="ch.freebo.ControlPanel"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="bootstrap">
<g:set var="entityName"
	value="${message(code: 'controlPanel.label', default: 'ControlPanel')}" />
<title><g:message code="default.label"
		default="Shopping Simulator" /></title>
</head>
<body>
	<div class="row-fluid">

		<div class="span3">
			<div class="well">
				<ul class="nav nav-list">
					<li class="nav-header">
						${entityName}
					</li>
					<li><g:link class="list" action="list">
							<i class="icon-list"></i>
							<g:message code="default.label" default="Message Panel" />
						</g:link></li>
					<li class="active"><g:link class="create" action="create">
							<i class="icon-list"></i>
							<g:message code="default.label" default="Shopping Simulator" />
						</g:link></li>
				</ul>
			</div>
		</div>

		<div class="span9">

			<div class="page-header">
				<h1>
					<g:message code="default.label" default="Shopping Simulator" />
				</h1>
			</div>

			<g:if test="${flash.message}">
				<bootstrap:alert class="alert-info">
					${flash.message}
				</bootstrap:alert>
			</g:if>


			<fieldset>
				<g:form class="form-horizontal" action="create">

					<div class="span9">
						<table class="table table-striped">
							<thead>
								<tr>
								<th></th>
									<th class="header"><g:message code="shopping.label"
											default="Anzahl" /></th>

									<th class="header"><g:message code="shopping.label"
											default="Preis" /></th>

									<th class="header"><g:message code="shopping.label"
											default="Produkt" /></th>
<%----%>
<%--									<th class="header"><g:message code="shopping.check"--%>
<%--											default="Ja/Nein" /></th>--%>


								</tr>
							</thead>


							<tbody>
								<g:each in="${productListForShopping}" var="item">
									<tr>
										<td width="50%">
    										<img alt="" src="${fieldValue(bean: item, field: "imageLink")}" width="50%" height="75px">

										</td>
										<td width="10%"><g:field type="number" name="anzahl" value="${anzahl}" /></td>
										<td width="10%"><g:field type="text" name="preis" value="${anzahl}" /></td>
										<td width="30%">
										<g:hiddenField name="product" value="${item.id}"/>
											${fieldValue(bean: item, field: "name")}
										</td>

<%--										<td><g:checkBox name="kaufen" value="${einkaufen}" /></td>--%>

									</tr>
								</g:each>
							</tbody>
						</table>
						

					</div>

					<div class="span9">
						Benutzer:
						<g:select name="userList.username" from="${userList}" optionKey="id"/>
						Einzelhändler (PoS):
						<g:select name="retailerList.name" from="${retailerList}" optionKey="id"/>
						<br/>
						Message schicken:
						<g:checkBox name="messageActive"/>

						<fieldset>
							<%--							<f:all bean="controlPanelInstance"/>--%>
							<div class="form-actions">
								<button type="submit" class="btn btn-primary">
									<i class="icon-ok icon-white"></i>
									<g:message code="default.button.label"
										default="Einkauf erfassen" />
								</button>
							</div>
						</fieldset>
				</g:form>
			</fieldset>

		</div>

	</div>
</body>
</html>
