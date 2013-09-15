package ch.freebo

import org.springframework.dao.DataIntegrityViolationException

class ProductCategoryController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [productCategoryInstanceList: ProductCategory.list(params), productCategoryInstanceTotal: ProductCategory.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[productCategoryInstance: new ProductCategory(params)]
			break
		case 'POST':
	        def productCategoryInstance = new ProductCategory(params)
	        if (!productCategoryInstance.save(flush: true)) {
	            render view: 'create', model: [productCategoryInstance: productCategoryInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'productCategory.label', default: 'ProductCategory'), productCategoryInstance.id])
	        redirect action: 'show', id: productCategoryInstance.id
			break
		}
    }

    def show() {
        def productCategoryInstance = ProductCategory.get(params.id)
        if (!productCategoryInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'productCategory.label', default: 'ProductCategory'), params.id])
            redirect action: 'list'
            return
        }

        [productCategoryInstance: productCategoryInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def productCategoryInstance = ProductCategory.get(params.id)
	        if (!productCategoryInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'productCategory.label', default: 'ProductCategory'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [productCategoryInstance: productCategoryInstance]
			break
		case 'POST':
	        def productCategoryInstance = ProductCategory.get(params.id)
	        if (!productCategoryInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'productCategory.label', default: 'ProductCategory'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (productCategoryInstance.version > version) {
	                productCategoryInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'productCategory.label', default: 'ProductCategory')] as Object[],
	                          "Another user has updated this ProductCategory while you were editing")
	                render view: 'edit', model: [productCategoryInstance: productCategoryInstance]
	                return
	            }
	        }

	        productCategoryInstance.properties = params

	        if (!productCategoryInstance.save(flush: true)) {
	            render view: 'edit', model: [productCategoryInstance: productCategoryInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'productCategory.label', default: 'ProductCategory'), productCategoryInstance.id])
	        redirect action: 'show', id: productCategoryInstance.id
			break
		}
    }

    def delete() {
        def productCategoryInstance = ProductCategory.get(params.id)
        if (!productCategoryInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'productCategory.label', default: 'ProductCategory'), params.id])
            redirect action: 'list'
            return
        }

        try {
            productCategoryInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'productCategory.label', default: 'ProductCategory'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'productCategory.label', default: 'ProductCategory'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
