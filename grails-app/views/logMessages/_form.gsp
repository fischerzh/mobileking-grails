<%@ page import="ch.freebo.LogMessages" %>



<div class="fieldcontain ${hasErrors(bean: logMessagesInstance, field: 'messageId', 'error')} ">
	<label for="messageId">
		<g:message code="logMessages.messageId.label" default="Message Id" />
		
	</label>
	<g:textField name="messageId" value="${logMessagesInstance?.messageId}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: logMessagesInstance, field: 'createDate', 'error')} ">
	<label for="createDate">
		<g:message code="logMessages.createDate.label" default="Create Date" />
		
	</label>
	<g:textField name="createDate" value="${logMessagesInstance?.createDate}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: logMessagesInstance, field: 'action', 'error')} ">
	<label for="action">
		<g:message code="logMessages.action.label" default="Action" />
		
	</label>
	<g:textField name="action" value="${logMessagesInstance?.action}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: logMessagesInstance, field: 'message', 'error')} ">
	<label for="message">
		<g:message code="logMessages.message.label" default="Message" />
		
	</label>
	<g:textField name="message" value="${logMessagesInstance?.message}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: logMessagesInstance, field: 'logDate', 'error')} required">
	<label for="logDate">
		<g:message code="logMessages.logDate.label" default="Log Date" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="logDate" precision="day"  value="${logMessagesInstance?.logDate}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: logMessagesInstance, field: 'location', 'error')} ">
	<label for="location">
		<g:message code="logMessages.location.label" default="Location" />
		
	</label>
	<g:textField name="location" value="${logMessagesInstance?.location}"/>
</div>

