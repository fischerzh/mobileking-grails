package ch.freebo

import org.springframework.dao.DataIntegrityViolationException

import ch.freebo.LoyaltyCard;

class LoyaltyCardController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [loyaltyCardInstanceList: LoyaltyCard.list(params), loyaltyCardInstanceTotal: LoyaltyCard.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[loyaltyCardInstance: new LoyaltyCard(params)]
			break
		case 'POST':
	        def loyaltyCardInstance = new LoyaltyCard(params)
	        if (!loyaltyCardInstance.save(flush: true)) {
	            render view: 'create', model: [loyaltyCardInstance: loyaltyCardInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'loyaltyCard.label', default: 'LoyaltyCard'), loyaltyCardInstance.id])
	        redirect action: 'show', id: loyaltyCardInstance.id
			break
		}
    }

    def show() {
        def loyaltyCardInstance = LoyaltyCard.get(params.id)
        if (!loyaltyCardInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'loyaltyCard.label', default: 'LoyaltyCard'), params.id])
            redirect action: 'list'
            return
        }

        [loyaltyCardInstance: loyaltyCardInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def loyaltyCardInstance = LoyaltyCard.get(params.id)
	        if (!loyaltyCardInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'loyaltyCard.label', default: 'LoyaltyCard'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [loyaltyCardInstance: loyaltyCardInstance]
			break
		case 'POST':
	        def loyaltyCardInstance = LoyaltyCard.get(params.id)
	        if (!loyaltyCardInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'loyaltyCard.label', default: 'LoyaltyCard'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (loyaltyCardInstance.version > version) {
	                loyaltyCardInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'loyaltyCard.label', default: 'LoyaltyCard')] as Object[],
	                          "Another user has updated this LoyaltyCard while you were editing")
	                render view: 'edit', model: [loyaltyCardInstance: loyaltyCardInstance]
	                return
	            }
	        }

	        loyaltyCardInstance.properties = params

	        if (!loyaltyCardInstance.save(flush: true)) {
	            render view: 'edit', model: [loyaltyCardInstance: loyaltyCardInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'loyaltyCard.label', default: 'LoyaltyCard'), loyaltyCardInstance.id])
	        redirect action: 'show', id: loyaltyCardInstance.id
			break
		}
    }

    def delete() {
        def loyaltyCardInstance = LoyaltyCard.get(params.id)
        if (!loyaltyCardInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'loyaltyCard.label', default: 'LoyaltyCard'), params.id])
            redirect action: 'list'
            return
        }

        try {
            loyaltyCardInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'loyaltyCard.label', default: 'LoyaltyCard'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'loyaltyCard.label', default: 'LoyaltyCard'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
