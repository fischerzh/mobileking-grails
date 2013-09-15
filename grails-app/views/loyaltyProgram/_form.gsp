<%@ page import="ch.freebo.LoyaltyProgram" %>



<div class="fieldcontain ${hasErrors(bean: loyaltyProgramInstance, field: 'loyaltyProgramLevels', 'error')} ">
	<label for="loyaltyProgramLevels">
		<g:message code="loyaltyProgram.loyaltyProgramLevels.label" default="Loyalty Program Levels" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${loyaltyProgramInstance?.loyaltyProgramLevels?}" var="l">
    <li><g:link controller="loyaltyProgramLevels" action="show" id="${l.id}">${l?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="loyaltyProgramLevels" action="create" params="['loyaltyProgram.id': loyaltyProgramInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'loyaltyProgramLevels.label', default: 'LoyaltyProgramLevels')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: loyaltyProgramInstance, field: 'linkToLoyaltyProvider', 'error')} ">
	<label for="linkToLoyaltyProvider">
		<g:message code="loyaltyProgram.linkToLoyaltyProvider.label" default="Link To Loyalty Provider" />
		
	</label>
	<g:textField name="linkToLoyaltyProvider" value="${loyaltyProgramInstance?.linkToLoyaltyProvider}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: loyaltyProgramInstance, field: 'status', 'error')} ">
	<label for="status">
		<g:message code="loyaltyProgram.status.label" default="Status" />
		
	</label>
	<g:checkBox name="status" value="${loyaltyProgramInstance?.status}" />
</div>

<div class="fieldcontain ${hasErrors(bean: loyaltyProgramInstance, field: 'ranking', 'error')} ">
	<label for="ranking">
		<g:message code="loyaltyProgram.ranking.label" default="Ranking" />
		
	</label>
	<g:checkBox name="ranking" value="${loyaltyProgramInstance?.ranking}" />
</div>

<div class="fieldcontain ${hasErrors(bean: loyaltyProgramInstance, field: 'users', 'error')} ">
	<label for="users">
		<g:message code="loyaltyProgram.users.label" default="Users" />
		
	</label>
	<g:select id="users" name="users.id" from="${ch.freebo.User.list()}" optionKey="id" value="${loyaltyProgramInstance?.users?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: loyaltyProgramInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="loyaltyProgram.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${loyaltyProgramInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: loyaltyProgramInstance, field: 'product', 'error')} required">
	<label for="product">
		<g:message code="loyaltyProgram.product.label" default="Product" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="product" name="product.id" from="${ch.freebo.Product.list()}" optionKey="id" required="" value="${loyaltyProgramInstance?.product?.id}" class="many-to-one"/>
</div>

