package ch.freebo

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured


@Secured(['ROLE_ADMIN'])
class ControlPanelController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']
	
	def androidGcmService
	
	def messageList = [:]
	
	def User user
	
	def callGCMService()
	{
		println params
		addMessages("MESSAGE", params.inputMessage)
		sendMessage();
	}
	
	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def callGCMServiceMsg(User user)
	{
		this.user = user
		println "this.user: " + this.user
		println params
		sendMessage();
	}
	
	def addMessages(String type, String message)
	{
		messageList[type] = message
	}
	
	
	def sendMessage = {
		def deviceList
		if(this.user)
		{
			def user = User.find(this.user)
			deviceList = user.devices
		}
		else
		{
			deviceList = Devices.all
		}
		def idList = []
		deviceList.each { 
			println it.deviceId
			idList.add(it.deviceId)
		}
		println "Device Ids: " + idList
		println "Device List: " +deviceList
		
		['deviceToken', 'messageKey', 'messageValue'].each {
				key -> params[key] = [idList].flatten().findAll { it }
		}
		
//		def messageList = [:]
		
//		messageList['STATUS'] = 'Neuer Status!'
//		messageList['BADGE'] = 'Neuer Badge!'
//		messageList['RANG'] = 'Neuer Rang!'
		
		println messageList
 		
		def messages = params.messageKey.inject([:]) {
				currentMessages, currentKey ->
				currentMessages << messageList//[ "1" : params.inputMessage]
		}
		
		println messages
		
		flash.message = message(code: 'default.created.message', args: [message(code: 'controlPanel.label', default: 'ControlPanel Message verschickt: '), params.inputMessage])
		
		androidGcmService.sendMessage(messages, params.deviceToken,"", grailsApplication.config.android.gcm.api.key).toString()
		if(this.user)
			return
		else
			redirect action: 'list'
	}
	

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
		
		def userRole = Role.findByAuthority('ROLE_USER')
		
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [controlPanelInstanceList: UserRole.findAllByRole(userRole).user, controlPanelInstanceTotal: User.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[controlPanelInstance: new ControlPanel(params)]
			break
		case 'POST':
	        def controlPanelInstance = new ControlPanel(params)
	        if (!controlPanelInstance.save(flush: true)) {
	            render view: 'create', model: [controlPanelInstance: controlPanelInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'controlPanel.label', default: 'ControlPanel'), controlPanelInstance.id])
	        redirect action: 'show', id: controlPanelInstance.id
			break
		}
    }

    def show() {
        def controlPanelInstance = ControlPanel.get(params.id)
        if (!controlPanelInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'controlPanel.label', default: 'ControlPanel'), params.id])
            redirect action: 'list'
            return
        }

        [controlPanelInstance: controlPanelInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def controlPanelInstance = ControlPanel.get(params.id)
	        if (!controlPanelInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'controlPanel.label', default: 'ControlPanel'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [controlPanelInstance: controlPanelInstance]
			break
		case 'POST':
	        def controlPanelInstance = ControlPanel.get(params.id)
	        if (!controlPanelInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'controlPanel.label', default: 'ControlPanel'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (controlPanelInstance.version > version) {
	                controlPanelInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'controlPanel.label', default: 'ControlPanel')] as Object[],
	                          "Another user has updated this ControlPanel while you were editing")
	                render view: 'edit', model: [controlPanelInstance: controlPanelInstance]
	                return
	            }
	        }

	        controlPanelInstance.properties = params

	        if (!controlPanelInstance.save(flush: true)) {
	            render view: 'edit', model: [controlPanelInstance: controlPanelInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'controlPanel.label', default: 'ControlPanel'), controlPanelInstance.id])
	        redirect action: 'show', id: controlPanelInstance.id
			break
		}
    }

    def delete() {
        def controlPanelInstance = ControlPanel.get(params.id)
        if (!controlPanelInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'controlPanel.label', default: 'ControlPanel'), params.id])
            redirect action: 'list'
            return
        }

        try {
            controlPanelInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'controlPanel.label', default: 'ControlPanel'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'controlPanel.label', default: 'ControlPanel'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
