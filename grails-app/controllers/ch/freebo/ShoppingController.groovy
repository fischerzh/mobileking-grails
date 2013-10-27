package ch.freebo

import org.springframework.dao.DataIntegrityViolationException

import grails.plugins.springsecurity.Secured


@Secured(['ROLE_ADMIN'])
class ShoppingController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

	def RankingService rankingService
	
    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [shoppingInstanceList: Shopping.list(params), shoppingInstanceTotal: Shopping.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[shoppingInstance: new Shopping(params)]
			break
		case 'POST':
	        def shoppingInstance = new Shopping(params)
			
	        if (!shoppingInstance.save(flush: true)) {
	            render view: 'create', model: [shoppingInstance: shoppingInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'shopping.label', default: 'Shopping'), shoppingInstance.id])
	        redirect action: 'show', id: shoppingInstance.id
			break
		}
    }

    def show() {
        def shoppingInstance = Shopping.get(params.id)
        if (!shoppingInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'shopping.label', default: 'Shopping'), params.id])
            redirect action: 'list'
            return
        }

        [shoppingInstance: shoppingInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def shoppingInstance = Shopping.get(params.id)
	        if (!shoppingInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'shopping.label', default: 'Shopping'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [shoppingInstance: shoppingInstance]
			break
		case 'POST':
	        def shoppingInstance = Shopping.get(params.id)
	        if (!shoppingInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'shopping.label', default: 'Shopping'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (shoppingInstance.version > version) {
	                shoppingInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'shopping.label', default: 'Shopping')] as Object[],
	                          "Another user has updated this Shopping while you were editing")
	                render view: 'edit', model: [shoppingInstance: shoppingInstance]
	                return
	            }
	        }

	        shoppingInstance.properties = params

	        if (!shoppingInstance.save(flush: true)) {
	            render view: 'edit', model: [shoppingInstance: shoppingInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'shopping.label', default: 'Shopping'), shoppingInstance.id])
	        redirect action: 'show', id: shoppingInstance.id
			break
		}
    }

    def delete() {
        def shoppingInstance = Shopping.get(params.id)
        if (!shoppingInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'shopping.label', default: 'Shopping'), params.id])
            redirect action: 'list'
            return
        }

        try {
            shoppingInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'shopping.label', default: 'Shopping'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'shopping.label', default: 'Shopping'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
