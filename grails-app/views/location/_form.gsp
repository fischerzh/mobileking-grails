<%@ page import="ch.freebo.Location" %>



<div class="fieldcontain ${hasErrors(bean: locationInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="location.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${locationInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: locationInstance, field: 'plz', 'error')} required">
	<label for="plz">
		<g:message code="location.plz.label" default="Plz" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="plz" type="number" value="${locationInstance.plz}" required=""/>
</div>

