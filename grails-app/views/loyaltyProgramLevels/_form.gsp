<%@ page import="ch.freebo.LoyaltyProgramLevels" %>



<div class="fieldcontain ${hasErrors(bean: loyaltyProgramLevelsInstance, field: 'levelName', 'error')} ">
	<label for="levelName">
		<g:message code="loyaltyProgramLevels.levelName.label" default="Level Name" />
		
	</label>
	<g:textField name="levelName" value="${loyaltyProgramLevelsInstance?.levelName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: loyaltyProgramLevelsInstance, field: 'loyaltyProgram', 'error')} required">
	<label for="loyaltyProgram">
		<g:message code="loyaltyProgramLevels.loyaltyProgram.label" default="Loyalty Program" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="loyaltyProgram" name="loyaltyProgram.id" from="${ch.freebo.LoyaltyProgram.list()}" optionKey="id" required="" value="${loyaltyProgramLevelsInstance?.loyaltyProgram?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: loyaltyProgramLevelsInstance, field: 'pointLevelMax', 'error')} required">
	<label for="pointLevelMax">
		<g:message code="loyaltyProgramLevels.pointLevelMax.label" default="Point Level Max" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="pointLevelMax" type="number" value="${loyaltyProgramLevelsInstance.pointLevelMax}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: loyaltyProgramLevelsInstance, field: 'pointLevelMin', 'error')} required">
	<label for="pointLevelMin">
		<g:message code="loyaltyProgramLevels.pointLevelMin.label" default="Point Level Min" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="pointLevelMin" type="number" value="${loyaltyProgramLevelsInstance.pointLevelMin}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: loyaltyProgramLevelsInstance, field: 'pointLevelName', 'error')} ">
	<label for="pointLevelName">
		<g:message code="loyaltyProgramLevels.pointLevelName.label" default="Point Level Name" />
		
	</label>
	<g:textField name="pointLevelName" value="${loyaltyProgramLevelsInstance?.pointLevelName}"/>
</div>

