
<%@ page import="ch.freebo.ControlPanel"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="bootstrap">
<g:set var="entityName"
	value="${message(code: 'controlPanel.label', default: 'ControlPanel')}" />
<title><g:message code="default.list.label" args="[entityName]" /></title>
</head>
<body>
	<div class="row-fluid">

		<div class="span9">

			<div class="page-header">
				<h1>
					<g:message code="default.label" default="Control Panel" />
				</h1>
			</div>

			<g:if test="${flash.message}">
				<bootstrap:alert class="alert-info">
					${flash.message}
				</bootstrap:alert>
			</g:if>
			<g:form method="post" 
        controller="controlPanel" action="callGCMService" id="${controlPanelInstanceList?.id}">
        
<%--        <g:select name="controlPanelInstanceList.username" from="${controlPanelInstanceList}" />--%>
        <g:textArea name="inputMessage" value="Send a text to the registered mobile Phones"/>
        <input type="submit" name="submit" />
</g:form>
			
			
<%--			<div class="span4">--%>
<%--				<g:select name="controlPanelInstanceList.username"--%>
<%--					from="${controlPanelInstanceList}" />--%>
<%----%>
<%--				<g:textArea name="inputMessage" id="inputMessage"></g:textArea>--%>
<%--			</div>--%>
<%--			<div class="span2">--%>
<%--				<g:link action="callGCMService" class="btn btn-small" params="">Send Message &raquo;</g:link>--%>
<%--				<button type="submit" class="btn btn-info" name="_action_call_GCMService">Submit</button>--%>
<%--			</div>--%>
			<div class="pagination">
				<bootstrap:paginate total="${controlPanelInstanceTotal}" />
			</div>
		</div>

	</div>
</body>
</html>
