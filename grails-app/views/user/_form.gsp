<%@ page import="ch.freebo.User" %>



<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'username', 'error')} required">
	<label for="username">
		<g:message code="user.username.label" default="Username" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="username" required="" value="${userInstance?.username}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'password', 'error')} required">
	<label for="password">
		<g:message code="user.password.label" default="Password" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="password" required="" value="${userInstance?.password}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'loyaltyCards', 'error')} ">
	<label for="loyaltyCards">
		<g:message code="user.loyaltyCards.label" default="Loyalty Cards" />
		
	</label>
	<g:select name="loyaltyCards" from="${ch.freebo.LoyaltyCard.list()}" multiple="multiple" optionKey="id" size="5" value="${userInstance?.loyaltyCards*.id}" class="many-to-many"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'shoppings', 'error')} ">
	<label for="shoppings">
		<g:message code="user.shoppings.label" default="Shoppings" />
		
	</label>
	<g:select name="shoppings" from="${ch.freebo.Shopping.list()}" multiple="multiple" optionKey="id" size="5" value="${userInstance?.shoppings*.id}" class="many-to-many"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'loyaltyPrograms', 'error')} ">
	<label for="loyaltyPrograms">
		<g:message code="user.loyaltyPrograms.label" default="Loyalty Programs" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${userInstance?.loyaltyPrograms?}" var="l">
    <li><g:link controller="loyaltyProgram" action="show" id="${l.id}">${l?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="loyaltyProgram" action="create" params="['user.id': userInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'loyaltyProgram.label', default: 'LoyaltyProgram')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'accountExpired', 'error')} ">
	<label for="accountExpired">
		<g:message code="user.accountExpired.label" default="Account Expired" />
		
	</label>
	<g:checkBox name="accountExpired" value="${userInstance?.accountExpired}" />
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'accountLocked', 'error')} ">
	<label for="accountLocked">
		<g:message code="user.accountLocked.label" default="Account Locked" />
		
	</label>
	<g:checkBox name="accountLocked" value="${userInstance?.accountLocked}" />
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'enabled', 'error')} ">
	<label for="enabled">
		<g:message code="user.enabled.label" default="Enabled" />
		
	</label>
	<g:checkBox name="enabled" value="${userInstance?.enabled}" />
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'passwordExpired', 'error')} ">
	<label for="passwordExpired">
		<g:message code="user.passwordExpired.label" default="Password Expired" />
		
	</label>
	<g:checkBox name="passwordExpired" value="${userInstance?.passwordExpired}" />
</div>

