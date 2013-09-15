<%@ page import="ch.freebo.Shopping" %>



<div class="fieldcontain ${hasErrors(bean: shoppingInstance, field: 'products', 'error')} ">
	<label for="products">
		<g:message code="shopping.products.label" default="Products" />
		
	</label>
	
</div>

<div class="fieldcontain ${hasErrors(bean: shoppingInstance, field: 'date', 'error')} required">
	<label for="date">
		<g:message code="shopping.date.label" default="Date" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="date" precision="day"  value="${shoppingInstance?.date}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: shoppingInstance, field: 'place', 'error')} ">
	<label for="place">
		<g:message code="shopping.place.label" default="Place" />
		
	</label>
	<g:textField name="place" value="${shoppingInstance?.place}"/>
</div>

