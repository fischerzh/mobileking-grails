<%@ page import="ch.freebo.UserProduct" %>



<div class="fieldcontain ${hasErrors(bean: userProductInstance, field: 'optInDate', 'error')} ">
	<label for="optInDate">
		<g:message code="userProduct.optInDate.label" default="Opt In Date" />
		
	</label>
	<g:datePicker name="optInDate" precision="day"  value="${userProductInstance?.optInDate}" default="none" noSelection="['': '']" />
</div>

<div class="fieldcontain ${hasErrors(bean: userProductInstance, field: 'optOutDate', 'error')} ">
	<label for="optOutDate">
		<g:message code="userProduct.optOutDate.label" default="Opt Out Date" />
		
	</label>
	<g:datePicker name="optOutDate" precision="day"  value="${userProductInstance?.optOutDate}" default="none" noSelection="['': '']" />
</div>

<div class="fieldcontain ${hasErrors(bean: userProductInstance, field: 'isActive', 'error')} ">
	<label for="isActive">
		<g:message code="userProduct.isActive.label" default="Is Active" />
		
	</label>
	<g:checkBox name="isActive" value="${userProductInstance?.isActive}" />
</div>

<div class="fieldcontain ${hasErrors(bean: userProductInstance, field: 'optIn', 'error')} ">
	<label for="optIn">
		<g:message code="userProduct.optIn.label" default="Opt In" />
		
	</label>
	<g:checkBox name="optIn" value="${userProductInstance?.optIn}" />
</div>

<div class="fieldcontain ${hasErrors(bean: userProductInstance, field: 'product', 'error')} required">
	<label for="product">
		<g:message code="userProduct.product.label" default="Product" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="product" name="product.id" from="${ch.freebo.Product.list()}" optionKey="id" required="" value="${userProductInstance?.product?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userProductInstance, field: 'user', 'error')} required">
	<label for="user">
		<g:message code="userProduct.user.label" default="User" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="user" name="user.id" from="${ch.freebo.User.list()}" optionKey="id" required="" value="${userProductInstance?.user?.id}" class="many-to-one"/>
</div>

