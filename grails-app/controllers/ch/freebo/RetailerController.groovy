package ch.freebo

import org.springframework.dao.DataIntegrityViolationException

class RetailerController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [retailerInstanceList: Retailer.list(params), retailerInstanceTotal: Retailer.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[retailerInstance: new Retailer(params)]
			break
		case 'POST':
	        def retailerInstance = new Retailer(params)
	        if (!retailerInstance.save(flush: true)) {
	            render view: 'create', model: [retailerInstance: retailerInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'retailer.label', default: 'Retailer'), retailerInstance.id])
	        redirect action: 'show', id: retailerInstance.id
			break
		}
    }

    def show() {
        def retailerInstance = Retailer.get(params.id)
        if (!retailerInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'retailer.label', default: 'Retailer'), params.id])
            redirect action: 'list'
            return
        }

        [retailerInstance: retailerInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def retailerInstance = Retailer.get(params.id)
	        if (!retailerInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'retailer.label', default: 'Retailer'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [retailerInstance: retailerInstance]
			break
		case 'POST':
	        def retailerInstance = Retailer.get(params.id)
	        if (!retailerInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'retailer.label', default: 'Retailer'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (retailerInstance.version > version) {
	                retailerInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'retailer.label', default: 'Retailer')] as Object[],
	                          "Another user has updated this Retailer while you were editing")
	                render view: 'edit', model: [retailerInstance: retailerInstance]
	                return
	            }
	        }

	        retailerInstance.properties = params

	        if (!retailerInstance.save(flush: true)) {
	            render view: 'edit', model: [retailerInstance: retailerInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'retailer.label', default: 'Retailer'), retailerInstance.id])
	        redirect action: 'show', id: retailerInstance.id
			break
		}
    }

    def delete() {
        def retailerInstance = Retailer.get(params.id)
        if (!retailerInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'retailer.label', default: 'Retailer'), params.id])
            redirect action: 'list'
            return
        }

        try {
            retailerInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'retailer.label', default: 'Retailer'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'retailer.label', default: 'Retailer'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
