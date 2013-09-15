
<%@ page import="ch.freebo.ProductCategory" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="bootstrap">
		<g:set var="entityName" value="${message(code: 'productCategory.label', default: 'ProductCategory')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="row-fluid">
			
			<div class="span3">
				<div class="well">
					<ul class="nav nav-list">
						<li class="nav-header">${entityName}</li>
						<li>
							<g:link class="list" action="list">
								<i class="icon-list"></i>
								<g:message code="default.list.label" args="[entityName]" />
							</g:link>
						</li>
						<li>
							<g:link class="create" action="create">
								<i class="icon-plus"></i>
								<g:message code="default.create.label" args="[entityName]" />
							</g:link>
						</li>
					</ul>
				</div>
			</div>
			
			<div class="span9">

				<div class="page-header">
					<h1><g:message code="default.show.label" args="[entityName]" /></h1>
				</div>

				<g:if test="${flash.message}">
				<bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
				</g:if>

				<dl>
				
					<g:if test="${productCategoryInstance?.products}">
						<dt><g:message code="productCategory.products.label" default="Products" /></dt>
						
							<g:each in="${productCategoryInstance.products}" var="p">
							<dd><g:link controller="product" action="show" id="${p.id}">${p?.encodeAsHTML()}</g:link></dd>
							</g:each>
						
					</g:if>
				
					<g:if test="${productCategoryInstance?.name}">
						<dt><g:message code="productCategory.name.label" default="Name" /></dt>
						
							<dd><g:fieldValue bean="${productCategoryInstance}" field="name"/></dd>
						
					</g:if>
				
					<g:if test="${productCategoryInstance?.productSegment}">
						<dt><g:message code="productCategory.productSegment.label" default="Product Segment" /></dt>
						
							<dd><g:link controller="productSegment" action="show" id="${productCategoryInstance?.productSegment?.id}">${productCategoryInstance?.productSegment?.encodeAsHTML()}</g:link></dd>
						
					</g:if>
				
				</dl>

				<g:form>
					<g:hiddenField name="id" value="${productCategoryInstance?.id}" />
					<div class="form-actions">
						<g:link class="btn" action="edit" id="${productCategoryInstance?.id}">
							<i class="icon-pencil"></i>
							<g:message code="default.button.edit.label" default="Edit" />
						</g:link>
						<button class="btn btn-danger" type="submit" name="_action_delete">
							<i class="icon-trash icon-white"></i>
							<g:message code="default.button.delete.label" default="Delete" />
						</button>
					</div>
				</g:form>

			</div>

		</div>
	</body>
</html>
