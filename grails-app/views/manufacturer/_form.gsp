<%@ page import="ch.freebo.Manufacturer" %>



<div class="fieldcontain ${hasErrors(bean: manufacturerInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="manufacturer.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${manufacturerInstance?.name}"/>
</div>

