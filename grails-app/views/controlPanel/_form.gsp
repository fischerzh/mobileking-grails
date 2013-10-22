<%@ page import="ch.freebo.ControlPanel" %>



<div class="fieldcontain ${hasErrors(bean: controlPanelInstance, field: 'users', 'error')} required">
	<label for="users">
		<g:message code="controlPanel.users.label" default="Users" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="users" name="users.id" from="${ch.freebo.User.list()}" optionKey="id" required="" value="${controlPanelInstance?.users?.id}" class="many-to-one"/>
</div>

