package ch.freebo

import org.springframework.dao.DataIntegrityViolationException

class ProductShoppingsController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [productShoppingsInstanceList: ProductShoppings.list(params), productShoppingsInstanceTotal: ProductShoppings.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[productShoppingsInstance: new ProductShoppings(params)]
			break
		case 'POST':
	        def productShoppingsInstance = new ProductShoppings(params)
	        if (!productShoppingsInstance.save(flush: true)) {
	            render view: 'create', model: [productShoppingsInstance: productShoppingsInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'productShoppings.label', default: 'ProductShoppings'), productShoppingsInstance.id])
	        redirect action: 'show', id: productShoppingsInstance.id
			break
		}
    }

    def show() {
        def productShoppingsInstance = ProductShoppings.get(params.id)
        if (!productShoppingsInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'productShoppings.label', default: 'ProductShoppings'), params.id])
            redirect action: 'list'
            return
        }

        [productShoppingsInstance: productShoppingsInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def productShoppingsInstance = ProductShoppings.get(params.id)
	        if (!productShoppingsInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'productShoppings.label', default: 'ProductShoppings'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [productShoppingsInstance: productShoppingsInstance]
			break
		case 'POST':
	        def productShoppingsInstance = ProductShoppings.get(params.id)
	        if (!productShoppingsInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'productShoppings.label', default: 'ProductShoppings'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (productShoppingsInstance.version > version) {
	                productShoppingsInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'productShoppings.label', default: 'ProductShoppings')] as Object[],
	                          "Another user has updated this ProductShoppings while you were editing")
	                render view: 'edit', model: [productShoppingsInstance: productShoppingsInstance]
	                return
	            }
	        }

	        productShoppingsInstance.properties = params

	        if (!productShoppingsInstance.save(flush: true)) {
	            render view: 'edit', model: [productShoppingsInstance: productShoppingsInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'productShoppings.label', default: 'ProductShoppings'), productShoppingsInstance.id])
	        redirect action: 'show', id: productShoppingsInstance.id
			break
		}
    }

    def delete() {
        def productShoppingsInstance = ProductShoppings.get(params.id)
        if (!productShoppingsInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'productShoppings.label', default: 'ProductShoppings'), params.id])
            redirect action: 'list'
            return
        }

        try {
            productShoppingsInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'productShoppings.label', default: 'ProductShoppings'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'productShoppings.label', default: 'ProductShoppings'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
