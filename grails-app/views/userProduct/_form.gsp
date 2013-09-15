<%@ page import="ch.freebo.UserProduct" %>



<div class="fieldcontain ${hasErrors(bean: userProductInstance, field: 'points', 'error')} ">
	<label for="points">
		<g:message code="userProduct.points.label" default="Points" />
		
	</label>
	<g:field name="points" type="number" value="${userProductInstance.points}"/>
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

