<%@ page import="ch.freebo.Retailer" %>



<div class="fieldcontain ${hasErrors(bean: retailerInstance, field: 'location', 'error')} ">
	<label for="location">
		<g:message code="retailer.location.label" default="Location" />
		
	</label>
	<g:select id="location" name="location.id" from="${ch.freebo.Location.list()}" optionKey="id" value="${retailerInstance?.location?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: retailerInstance, field: 'street', 'error')} ">
	<label for="street">
		<g:message code="retailer.street.label" default="Street" />
		
	</label>
	<g:textField name="street" value="${retailerInstance?.street}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: retailerInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="retailer.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${retailerInstance?.name}"/>
</div>

