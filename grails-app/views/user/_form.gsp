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

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'email', 'error')} required">
	<label for="email">
		<g:message code="user.email.label" default="Email" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="email" name="email" required="" value="${userInstance?.email}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'isActiveApp', 'error')} ">
	<label for="isActiveApp">
		<g:message code="user.isActiveApp.label" default="Is Active App" />
		
	</label>
	<g:checkBox name="isActiveApp" value="${userInstance?.isActiveApp}" />
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
	
<ul class="one-to-many">
<g:each in="${userInstance?.shoppings?}" var="s">
    <li><g:link controller="shopping" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="shopping" action="create" params="['user.id': userInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'shopping.label', default: 'Shopping')])}</g:link>
</li>
</ul>

</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'devices', 'error')} ">
	<label for="devices">
		<g:message code="user.devices.label" default="Devices" />
		
	</label>
	<g:select name="devices" from="${ch.freebo.Devices.list()}" multiple="multiple" optionKey="id" size="5" value="${userInstance?.devices*.id}" class="many-to-many"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'badges', 'error')} ">
	<label for="badges">
		<g:message code="user.badges.label" default="Badges" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${userInstance?.badges?}" var="b">
    <li><g:link controller="badge" action="show" id="${b.id}">${b?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="badge" action="create" params="['user.id': userInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'badge.label', default: 'Badge')])}</g:link>
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

