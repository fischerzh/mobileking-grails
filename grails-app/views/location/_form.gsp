<%@ page import="ch.freebo.Location" %>



<div class="fieldcontain ${hasErrors(bean: locationInstance, field: 'plz', 'error')} ">
	<label for="plz">
		<g:message code="location.plz.label" default="Plz" />
		
	</label>
	<g:field name="plz" type="number" value="${locationInstance.plz}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: locationInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="location.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${locationInstance?.name}"/>
</div>

