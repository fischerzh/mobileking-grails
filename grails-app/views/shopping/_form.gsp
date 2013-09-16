<%@ page import="ch.freebo.Shopping" %>



<div class="fieldcontain ${hasErrors(bean: shoppingInstance, field: 'productShoppings', 'error')} ">
	<label for="productShoppings">
		<g:message code="shopping.productShoppings.label" default="Product Shoppings" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${shoppingInstance?.productShoppings?}" var="p">
    <li><g:link controller="productShoppings" action="show" id="${p.id}">${p?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="productShoppings" action="create" params="['shopping.id': shoppingInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'productShoppings.label', default: 'ProductShoppings')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: shoppingInstance, field: 'date', 'error')} required">
	<label for="date">
		<g:message code="shopping.date.label" default="Date" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="date" precision="day"  value="${shoppingInstance?.date}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: shoppingInstance, field: 'location', 'error')} required">
	<label for="location">
		<g:message code="shopping.location.label" default="Location" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="location" name="location.id" from="${ch.freebo.Location.list()}" optionKey="id" required="" value="${shoppingInstance?.location?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: shoppingInstance, field: 'retailer', 'error')} required">
	<label for="retailer">
		<g:message code="shopping.retailer.label" default="Retailer" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="retailer" name="retailer.id" from="${ch.freebo.Retailer.list()}" optionKey="id" required="" value="${shoppingInstance?.retailer?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: shoppingInstance, field: 'user', 'error')} required">
	<label for="user">
		<g:message code="shopping.user.label" default="User" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="user" name="user.id" from="${ch.freebo.User.list()}" optionKey="id" required="" value="${shoppingInstance?.user?.id}" class="many-to-one"/>
</div>

