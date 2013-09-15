<%@ page import="ch.freebo.ProductSegment" %>



<div class="fieldcontain ${hasErrors(bean: productSegmentInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="productSegment.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${productSegmentInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: productSegmentInstance, field: 'productCategory', 'error')} ">
	<label for="productCategory">
		<g:message code="productSegment.productCategory.label" default="Product Category" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${productSegmentInstance?.productCategory?}" var="p">
    <li><g:link controller="productCategory" action="show" id="${p.id}">${p?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="productCategory" action="create" params="['productSegment.id': productSegmentInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'productCategory.label', default: 'ProductCategory')])}</g:link>
</li>
</ul>

</div>

