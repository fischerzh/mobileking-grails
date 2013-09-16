package ch.freebo

import org.springframework.dao.DataIntegrityViolationException

class UserProductController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [userProductInstanceList: UserProduct.list(params), userProductInstanceTotal: UserProduct.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[userProductInstance: new UserProduct(params)]
			break
		case 'POST':
	        def userProductInstance = new UserProduct(params)
	        if (!userProductInstance.save(flush: true)) {
	            render view: 'create', model: [userProductInstance: userProductInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'userProduct.label', default: 'UserProduct'), userProductInstance.id])
	        redirect action: 'show', id: userProductInstance.id
			break
		}
    }

    def show() {
        def userProductInstance = UserProduct.get(params.id)
        if (!userProductInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'userProduct.label', default: 'UserProduct'), params.id])
            redirect action: 'list'
            return
        }

        [userProductInstance: userProductInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def userProductInstance = UserProduct.get(params.id)
	        if (!userProductInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'userProduct.label', default: 'UserProduct'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [userProductInstance: userProductInstance]
			break
		case 'POST':
	        def userProductInstance = UserProduct.get(params.id)
	        if (!userProductInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'userProduct.label', default: 'UserProduct'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (userProductInstance.version > version) {
	                userProductInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'userProduct.label', default: 'UserProduct')] as Object[],
	                          "Another user has updated this UserProduct while you were editing")
	                render view: 'edit', model: [userProductInstance: userProductInstance]
	                return
	            }
	        }

	        userProductInstance.properties = params

	        if (!userProductInstance.save(flush: true)) {
	            render view: 'edit', model: [userProductInstance: userProductInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'userProduct.label', default: 'UserProduct'), userProductInstance.id])
	        redirect action: 'show', id: userProductInstance.id
			break
		}
    }

    def delete() {
        def userProductInstance = UserProduct.get(params.id)
        if (!userProductInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'userProduct.label', default: 'UserProduct'), params.id])
            redirect action: 'list'
            return
        }

        try {
            userProductInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'userProduct.label', default: 'UserProduct'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'userProduct.label', default: 'UserProduct'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
