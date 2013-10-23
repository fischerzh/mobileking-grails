package ch.freebo

import org.springframework.dao.DataIntegrityViolationException

import ch.freebo.LoyaltyProgramLevels;

import grails.plugins.springsecurity.Secured


@Secured(['ROLE_ADMIN'])
class LoyaltyProgramLevelsController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [loyaltyProgramLevelsInstanceList: LoyaltyProgramLevels.list(params), loyaltyProgramLevelsInstanceTotal: LoyaltyProgramLevels.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[loyaltyProgramLevelsInstance: new LoyaltyProgramLevels(params)]
			break
		case 'POST':
	        def loyaltyProgramLevelsInstance = new LoyaltyProgramLevels(params)
	        if (!loyaltyProgramLevelsInstance.save(flush: true)) {
	            render view: 'create', model: [loyaltyProgramLevelsInstance: loyaltyProgramLevelsInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'loyaltyProgramLevels.label', default: 'LoyaltyProgramLevels'), loyaltyProgramLevelsInstance.id])
	        redirect action: 'show', id: loyaltyProgramLevelsInstance.id
			break
		}
    }

    def show() {
        def loyaltyProgramLevelsInstance = LoyaltyProgramLevels.get(params.id)
        if (!loyaltyProgramLevelsInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'loyaltyProgramLevels.label', default: 'LoyaltyProgramLevels'), params.id])
            redirect action: 'list'
            return
        }

        [loyaltyProgramLevelsInstance: loyaltyProgramLevelsInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def loyaltyProgramLevelsInstance = LoyaltyProgramLevels.get(params.id)
	        if (!loyaltyProgramLevelsInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'loyaltyProgramLevels.label', default: 'LoyaltyProgramLevels'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [loyaltyProgramLevelsInstance: loyaltyProgramLevelsInstance]
			break
		case 'POST':
	        def loyaltyProgramLevelsInstance = LoyaltyProgramLevels.get(params.id)
	        if (!loyaltyProgramLevelsInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'loyaltyProgramLevels.label', default: 'LoyaltyProgramLevels'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (loyaltyProgramLevelsInstance.version > version) {
	                loyaltyProgramLevelsInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'loyaltyProgramLevels.label', default: 'LoyaltyProgramLevels')] as Object[],
	                          "Another user has updated this LoyaltyProgramLevels while you were editing")
	                render view: 'edit', model: [loyaltyProgramLevelsInstance: loyaltyProgramLevelsInstance]
	                return
	            }
	        }

	        loyaltyProgramLevelsInstance.properties = params

	        if (!loyaltyProgramLevelsInstance.save(flush: true)) {
	            render view: 'edit', model: [loyaltyProgramLevelsInstance: loyaltyProgramLevelsInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'loyaltyProgramLevels.label', default: 'LoyaltyProgramLevels'), loyaltyProgramLevelsInstance.id])
	        redirect action: 'show', id: loyaltyProgramLevelsInstance.id
			break
		}
    }

    def delete() {
        def loyaltyProgramLevelsInstance = LoyaltyProgramLevels.get(params.id)
        if (!loyaltyProgramLevelsInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'loyaltyProgramLevels.label', default: 'LoyaltyProgramLevels'), params.id])
            redirect action: 'list'
            return
        }

        try {
            loyaltyProgramLevelsInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'loyaltyProgramLevels.label', default: 'LoyaltyProgramLevels'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'loyaltyProgramLevels.label', default: 'LoyaltyProgramLevels'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
