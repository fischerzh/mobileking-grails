package ch.freebo

import org.springframework.dao.DataIntegrityViolationException


import grails.plugins.springsecurity.Secured


@Secured(['ROLE_ADMIN'])
class LogMessagesController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']
	
	def userRole = Role.findByAuthority('ROLE_USER')

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
		
		def userLogList = UserRole.findAllByRole(userRole).user
		println userLogList
        [userLogList: userLogList, logMessagesInstanceTotal: userLogList.size()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[logMessagesInstance: new LogMessages(params)]
			break
		case 'POST':
	        def logMessagesInstance = new LogMessages(params)
	        if (!logMessagesInstance.save(flush: true)) {
	            render view: 'create', model: [logMessagesInstance: logMessagesInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'logMessages.label', default: 'LogMessages'), logMessagesInstance.id])
	        redirect action: 'show', id: logMessagesInstance.id
			break
		}
    }

    def show() {
        def logMessagesInstance = LogMessages.get(params.id)
        if (!logMessagesInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'logMessages.label', default: 'LogMessages'), params.id])
            redirect action: 'list'
            return
        }

        [logMessagesInstance: logMessagesInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def logMessagesInstance = LogMessages.get(params.id)
	        if (!logMessagesInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'logMessages.label', default: 'LogMessages'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [logMessagesInstance: logMessagesInstance]
			break
		case 'POST':
	        def logMessagesInstance = LogMessages.get(params.id)
	        if (!logMessagesInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'logMessages.label', default: 'LogMessages'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (logMessagesInstance.version > version) {
	                logMessagesInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'logMessages.label', default: 'LogMessages')] as Object[],
	                          "Another user has updated this LogMessages while you were editing")
	                render view: 'edit', model: [logMessagesInstance: logMessagesInstance]
	                return
	            }
	        }

	        logMessagesInstance.properties = params

	        if (!logMessagesInstance.save(flush: true)) {
	            render view: 'edit', model: [logMessagesInstance: logMessagesInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'logMessages.label', default: 'LogMessages'), logMessagesInstance.id])
	        redirect action: 'show', id: logMessagesInstance.id
			break
		}
    }

    def delete() {
        def logMessagesInstance = LogMessages.get(params.id)
        if (!logMessagesInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'logMessages.label', default: 'LogMessages'), params.id])
            redirect action: 'list'
            return
        }

        try {
            logMessagesInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'logMessages.label', default: 'LogMessages'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'logMessages.label', default: 'LogMessages'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
