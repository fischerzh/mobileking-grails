<%@ page import="ch.freebo.LoyaltyCard" %>



<div class="fieldcontain ${hasErrors(bean: loyaltyCardInstance, field: 'username', 'error')} ">
	<label for="username">
		<g:message code="loyaltyCard.username.label" default="Username" />
		
	</label>
	<g:textField name="username" value="${loyaltyCardInstance?.username}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: loyaltyCardInstance, field: 'password', 'error')} ">
	<label for="password">
		<g:message code="loyaltyCard.password.label" default="Password" />
		
	</label>
	<g:textField name="password" value="${loyaltyCardInstance?.password}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: loyaltyCardInstance, field: 'link', 'error')} ">
	<label for="link">
		<g:message code="loyaltyCard.link.label" default="Link" />
		
	</label>
	<g:textField name="link" value="${loyaltyCardInstance?.link}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: loyaltyCardInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="loyaltyCard.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${loyaltyCardInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: loyaltyCardInstance, field: 'number', 'error')} ">
	<label for="number">
		<g:message code="loyaltyCard.number.label" default="Number" />
		
	</label>
	<g:textField name="number" value="${loyaltyCardInstance?.number}"/>
</div>

