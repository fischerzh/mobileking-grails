<%@ page import="ch.freebo.ControlPanel"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="bootstrap">
<g:set var="entityName"
	value="${message(code: 'controlPanel.label', default: 'ControlPanel')}" />
<title><g:message code="default.label"
		default="Shopping Matcher" /></title>

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
							<g:message code="default.label" default="Shopping Matcher" />
						</g:link></li>
				</ul>
			</div>
		</div>
		<div class="span9">
		<div class="page-header">
				<h1>
					<g:message code="default.label" default="Shopping Matcher" />
				</h1>
			</div>
			 </div>
		</div>
		
		<div class="row-fluid" >
		
		<div class="span3" style="height: 500px; overflow: auto">
			<h5>Gescannte Einkaufszettel (pending)</h5>
			<form name="selectBox">
				<g:select name="selectedScannedReceipt" from="${salesSlips}"
					optionKey="id" noSelection="['':'Wähle einen Einkaufszettel ..']" onChange="selectBox.submit()"
					value="${selectedScannedReceipt}" />
					
							<g:hiddenField name="user" value="${user}" />
					
			</form>
<%--			<g:select name="salesSlipsId" from="${salesSlips}" optionKey="id" value="${selectedScannedReceipt}" noSelection="['':'Wähle einen Einkaufszettel']"/>--%>
<%--			<g:link controller="controlPanel" action="create" params="[selectedScannedReceipt: ${salesSlipsId}]" class="btn btn-small">Show &raquo;</g:link>--%>
			<g:each in="${imageList}" var="img">
			
  		<img class="thumbnail" src='${createLink(controller: "controlPanel", action: "displayImage", params:[img: img.name, user: user])}' />
			</g:each>
		</div>

		<div class="span6">

		

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

							<g:hiddenField name="user" value="${params.user}" />
							<g:hiddenField name="selectedScannedReceipt" value="${selectedScannedReceipt}" />


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

<%--										<td><g:checkBox name="isVerified" value="${isVerified}" /></td>--%>

									</tr>
								</g:each>
							</tbody>
						</table>
						

					</div>

					<div class="span9">
<%--						Benutzer:--%>
<%--						<g:select name="userList.username" from="${userList}" optionKey="id"/>--%>
						Einzelhändler (PoS):
						<g:select name="retailerList.name" from="${retailerList}" optionKey="id"/>
						<br/>
						<g:textArea name="rejectMessage" style='width: 100%; height: 50px;'></g:textArea>
												<br/>
						
<%--						Message schicken:--%>
<%--						<g:checkBox name="messageActive"/>--%>
						Einkaufsdatum<g:datePicker name="shoppingDate" value="${new Date()}"
              noSelection="['':'-Choose-']"/>
              <br/>
              Einkauf verifizieren: 
						<g:select name="salesVerified" from="${['Reject', 'Verify']}" />

						<fieldset>
							<%--							<f:all bean="controlPanelInstance"/>--%>
							<div class="form-actions">
								<button type="submit" class="btn btn-primary">
									<i class="icon-ok icon-white"></i>
									<g:message code="default.button.label"
										default="Einkauf verifizieren" />
								</button>
							</div>
						</fieldset>
				</g:form>
			</fieldset>

		</div>

	</div>
</body>
</html>
