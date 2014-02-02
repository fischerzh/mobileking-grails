package ch.freebo

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured


@Secured(['ROLE_ADMIN'])
class OptInController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [optInInstanceList: OptIn.list(params), optInInstanceTotal: OptIn.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[optInInstance: new OptIn(params)]
			break
		case 'POST':
	        def optInInstance = new OptIn(params)
	        if (!optInInstance.save(flush: true)) {
	            render view: 'create', model: [optInInstance: optInInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'optIn.label', default: 'OptIn'), optInInstance.id])
	        redirect action: 'show', id: optInInstance.id
			break
		}
    }

    def show() {
        def optInInstance = OptIn.get(params.id)
        if (!optInInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'optIn.label', default: 'OptIn'), params.id])
            redirect action: 'list'
            return
        }

        [optInInstance: optInInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def optInInstance = OptIn.get(params.id)
	        if (!optInInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'optIn.label', default: 'OptIn'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [optInInstance: optInInstance]
			break
		case 'POST':
	        def optInInstance = OptIn.get(params.id)
	        if (!optInInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'optIn.label', default: 'OptIn'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (optInInstance.version > version) {
	                optInInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'optIn.label', default: 'OptIn')] as Object[],
	                          "Another user has updated this OptIn while you were editing")
	                render view: 'edit', model: [optInInstance: optInInstance]
	                return
	            }
	        }

	        optInInstance.properties = params

	        if (!optInInstance.save(flush: true)) {
	            render view: 'edit', model: [optInInstance: optInInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'optIn.label', default: 'OptIn'), optInInstance.id])
	        redirect action: 'show', id: optInInstance.id
			break
		}
    }

    def delete() {
        def optInInstance = OptIn.get(params.id)
        if (!optInInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'optIn.label', default: 'OptIn'), params.id])
            redirect action: 'list'
            return
        }

        try {
            optInInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'optIn.label', default: 'OptIn'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'optIn.label', default: 'OptIn'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
