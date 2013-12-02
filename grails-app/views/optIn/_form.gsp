<%@ page import="ch.freebo.OptIn" %>



<div class="fieldcontain ${hasErrors(bean: optInInstance, field: 'optInDate', 'error')} ">
	<label for="optInDate">
		<g:message code="optIn.optInDate.label" default="Opt In Date" />
		
	</label>
	<g:datePicker name="optInDate" precision="day"  value="${optInInstance?.optInDate}" default="none" noSelection="['': '']" />
</div>

<div class="fieldcontain ${hasErrors(bean: optInInstance, field: 'optOutDate', 'error')} ">
	<label for="optOutDate">
		<g:message code="optIn.optOutDate.label" default="Opt Out Date" />
		
	</label>
	<g:datePicker name="optOutDate" precision="day"  value="${optInInstance?.optOutDate}" default="none" noSelection="['': '']" />
</div>

<div class="fieldcontain ${hasErrors(bean: optInInstance, field: 'isActive', 'error')} ">
	<label for="isActive">
		<g:message code="optIn.isActive.label" default="Is Active" />
		
	</label>
	<g:checkBox name="isActive" value="${optInInstance?.isActive}" />
</div>

<div class="fieldcontain ${hasErrors(bean: optInInstance, field: 'optIn', 'error')} ">
	<label for="optIn">
		<g:message code="optIn.optIn.label" default="Opt In" />
		
	</label>
	<g:checkBox name="optIn" value="${optInInstance?.optIn}" />
</div>

<div class="fieldcontain ${hasErrors(bean: optInInstance, field: 'product', 'error')} required">
	<label for="product">
		<g:message code="optIn.product.label" default="Product" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="product" name="product.id" from="${ch.freebo.Product.list()}" optionKey="id" required="" value="${optInInstance?.product?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: optInInstance, field: 'user', 'error')} required">
	<label for="user">
		<g:message code="optIn.user.label" default="User" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="user" name="user.id" from="${ch.freebo.User.list()}" optionKey="id" required="" value="${optInInstance?.user?.id}" class="many-to-one"/>
</div>

