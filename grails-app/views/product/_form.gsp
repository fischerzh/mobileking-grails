<%@ page import="ch.freebo.Product" %>



<div class="fieldcontain ${hasErrors(bean: productInstance, field: 'loyaltyProgram', 'error')} ">
	<label for="loyaltyProgram">
		<g:message code="product.loyaltyProgram.label" default="Loyalty Program" />
		
	</label>
	<g:select id="loyaltyProgram" name="loyaltyProgram.id" from="${ch.freebo.LoyaltyProgram.list()}" optionKey="id" value="${productInstance?.loyaltyProgram?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: productInstance, field: 'imageLink', 'error')} ">
	<label for="imageLink">
		<g:message code="product.imageLink.label" default="Image Link" />
		
	</label>
	<g:textField name="imageLink" value="${productInstance?.imageLink}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: productInstance, field: 'shoppings', 'error')} ">
	<label for="shoppings">
		<g:message code="product.shoppings.label" default="Shoppings" />
		
	</label>
	<g:select name="shoppings" from="${ch.freebo.Shopping.list()}" multiple="multiple" optionKey="id" size="5" value="${productInstance?.shoppings*.id}" class="many-to-many"/>
</div>

<div class="fieldcontain ${hasErrors(bean: productInstance, field: 'ingredients', 'error')} ">
	<label for="ingredients">
		<g:message code="product.ingredients.label" default="Ingredients" />
		
	</label>
	<g:textField name="ingredients" value="${productInstance?.ingredients}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: productInstance, field: 'ean', 'error')} ">
	<label for="ean">
		<g:message code="product.ean.label" default="Ean" />
		
	</label>
	<g:textField name="ean" value="${productInstance?.ean}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: productInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="product.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${productInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: productInstance, field: 'productCategory', 'error')} required">
	<label for="productCategory">
		<g:message code="product.productCategory.label" default="Product Category" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="productCategory" name="productCategory.id" from="${ch.freebo.ProductCategory.list()}" optionKey="id" required="" value="${productInstance?.productCategory?.id}" class="many-to-one"/>
</div>

