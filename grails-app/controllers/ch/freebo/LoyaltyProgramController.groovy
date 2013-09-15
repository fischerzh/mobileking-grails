package ch.freebo

import org.springframework.dao.DataIntegrityViolationException

class LoyaltyProgramController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [loyaltyProgramInstanceList: LoyaltyProgram.list(params), loyaltyProgramInstanceTotal: LoyaltyProgram.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[loyaltyProgramInstance: new LoyaltyProgram(params)]
			break
		case 'POST':
	        def loyaltyProgramInstance = new LoyaltyProgram(params)
	        if (!loyaltyProgramInstance.save(flush: true)) {
	            render view: 'create', model: [loyaltyProgramInstance: loyaltyProgramInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'loyaltyProgram.label', default: 'LoyaltyProgram'), loyaltyProgramInstance.id])
	        redirect action: 'show', id: loyaltyProgramInstance.id
			break
		}
    }

    def show() {
        def loyaltyProgramInstance = LoyaltyProgram.get(params.id)
        if (!loyaltyProgramInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'loyaltyProgram.label', default: 'LoyaltyProgram'), params.id])
            redirect action: 'list'
            return
        }

        [loyaltyProgramInstance: loyaltyProgramInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def loyaltyProgramInstance = LoyaltyProgram.get(params.id)
	        if (!loyaltyProgramInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'loyaltyProgram.label', default: 'LoyaltyProgram'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [loyaltyProgramInstance: loyaltyProgramInstance]
			break
		case 'POST':
	        def loyaltyProgramInstance = LoyaltyProgram.get(params.id)
	        if (!loyaltyProgramInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'loyaltyProgram.label', default: 'LoyaltyProgram'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (loyaltyProgramInstance.version > version) {
	                loyaltyProgramInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'loyaltyProgram.label', default: 'LoyaltyProgram')] as Object[],
	                          "Another user has updated this LoyaltyProgram while you were editing")
	                render view: 'edit', model: [loyaltyProgramInstance: loyaltyProgramInstance]
	                return
	            }
	        }

	        loyaltyProgramInstance.properties = params

	        if (!loyaltyProgramInstance.save(flush: true)) {
	            render view: 'edit', model: [loyaltyProgramInstance: loyaltyProgramInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'loyaltyProgram.label', default: 'LoyaltyProgram'), loyaltyProgramInstance.id])
	        redirect action: 'show', id: loyaltyProgramInstance.id
			break
		}
    }

    def delete() {
        def loyaltyProgramInstance = LoyaltyProgram.get(params.id)
        if (!loyaltyProgramInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'loyaltyProgram.label', default: 'LoyaltyProgram'), params.id])
            redirect action: 'list'
            return
        }

        try {
            loyaltyProgramInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'loyaltyProgram.label', default: 'LoyaltyProgram'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'loyaltyProgram.label', default: 'LoyaltyProgram'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
