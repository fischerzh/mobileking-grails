<%@ page import="ch.freebo.ProductShoppings" %>



<div class="fieldcontain ${hasErrors(bean: productShoppingsInstance, field: 'price', 'error')} ">
	<label for="price">
		<g:message code="productShoppings.price.label" default="Price" />
		
	</label>
	<g:field name="price" value="${fieldValue(bean: productShoppingsInstance, field: 'price')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: productShoppingsInstance, field: 'product', 'error')} required">
	<label for="product">
		<g:message code="productShoppings.product.label" default="Product" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="product" name="product.id" from="${ch.freebo.Product.list()}" optionKey="id" required="" value="${productShoppingsInstance?.product?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: productShoppingsInstance, field: 'qty', 'error')} required">
	<label for="qty">
		<g:message code="productShoppings.qty.label" default="Qty" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="qty" type="number" value="${productShoppingsInstance.qty}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: productShoppingsInstance, field: 'shopping', 'error')} required">
	<label for="shopping">
		<g:message code="productShoppings.shopping.label" default="Shopping" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="shopping" name="shopping.id" from="${ch.freebo.Shopping.list()}" optionKey="id" required="" value="${productShoppingsInstance?.shopping?.id}" class="many-to-one"/>
</div>

