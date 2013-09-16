package ch.freebo

import org.springframework.dao.DataIntegrityViolationException

import ch.freebo.ProductSegment;

class ProductSegmentController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [productSegmentInstanceList: ProductSegment.list(params), productSegmentInstanceTotal: ProductSegment.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[productSegmentInstance: new ProductSegment(params)]
			break
		case 'POST':
	        def productSegmentInstance = new ProductSegment(params)
	        if (!productSegmentInstance.save(flush: true)) {
	            render view: 'create', model: [productSegmentInstance: productSegmentInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'productSegment.label', default: 'ProductSegment'), productSegmentInstance.id])
	        redirect action: 'show', id: productSegmentInstance.id
			break
		}
    }

    def show() {
        def productSegmentInstance = ProductSegment.get(params.id)
        if (!productSegmentInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'productSegment.label', default: 'ProductSegment'), params.id])
            redirect action: 'list'
            return
        }

        [productSegmentInstance: productSegmentInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def productSegmentInstance = ProductSegment.get(params.id)
	        if (!productSegmentInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'productSegment.label', default: 'ProductSegment'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [productSegmentInstance: productSegmentInstance]
			break
		case 'POST':
	        def productSegmentInstance = ProductSegment.get(params.id)
	        if (!productSegmentInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'productSegment.label', default: 'ProductSegment'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (productSegmentInstance.version > version) {
	                productSegmentInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'productSegment.label', default: 'ProductSegment')] as Object[],
	                          "Another user has updated this ProductSegment while you were editing")
	                render view: 'edit', model: [productSegmentInstance: productSegmentInstance]
	                return
	            }
	        }

	        productSegmentInstance.properties = params

	        if (!productSegmentInstance.save(flush: true)) {
	            render view: 'edit', model: [productSegmentInstance: productSegmentInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'productSegment.label', default: 'ProductSegment'), productSegmentInstance.id])
	        redirect action: 'show', id: productSegmentInstance.id
			break
		}
    }

    def delete() {
        def productSegmentInstance = ProductSegment.get(params.id)
        if (!productSegmentInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'productSegment.label', default: 'ProductSegment'), params.id])
            redirect action: 'list'
            return
        }

        try {
            productSegmentInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'productSegment.label', default: 'ProductSegment'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'productSegment.label', default: 'ProductSegment'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
