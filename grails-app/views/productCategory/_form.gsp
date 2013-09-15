<%@ page import="ch.freebo.ProductCategory" %>



<div class="fieldcontain ${hasErrors(bean: productCategoryInstance, field: 'products', 'error')} ">
	<label for="products">
		<g:message code="productCategory.products.label" default="Products" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${productCategoryInstance?.products?}" var="p">
    <li><g:link controller="product" action="show" id="${p.id}">${p?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="product" action="create" params="['productCategory.id': productCategoryInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'product.label', default: 'Product')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: productCategoryInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="productCategory.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${productCategoryInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: productCategoryInstance, field: 'productSegment', 'error')} required">
	<label for="productSegment">
		<g:message code="productCategory.productSegment.label" default="Product Segment" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="productSegment" name="productSegment.id" from="${ch.freebo.ProductSegment.list()}" optionKey="id" required="" value="${productCategoryInstance?.productSegment?.id}" class="many-to-one"/>
</div>

