package ch.freebo

import org.springframework.dao.DataIntegrityViolationException

class ControlPanelController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']
	
	def androidGcmService
	
	def callGCMService()
	{
		println params
		sendMessage();
	}
	
	def sendMessage = {
		['deviceToken', 'messageKey', 'messageValue'].each {
				key -> params[key] = ["APA91bE4eARfV4klHMq3K5SLASQovkBHf8EgIjT5RDotDwfBZrUVFhXEPvG5-hSQMDeVJ9js3tmDAQ6VnUz6-AbeFpld-oIZFD0zGz4egIQqbGU2F_nortx1D7aaPfrWnrx17n3zk7M5SLf3-eiqL9iALfqbrlPpAw"].flatten().findAll { it }
		}
		def messages = params.messageKey.inject([:]) {
				currentMessages, currentKey ->
				currentMessages << [ "1" : params.inputMessage]
		}
		
		androidGcmService.sendMessage(messages, params.deviceToken,"", grailsApplication.config.android.gcm.api.key).toString()
		render action: 'list', params: params
	}

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [controlPanelInstanceList: User.list(params), controlPanelInstanceTotal: User.count()]
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
