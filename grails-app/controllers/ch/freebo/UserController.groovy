package ch.freebo

import org.springframework.dao.DataIntegrityViolationException

import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import ch.freebo.UserRole

class UserController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']
	@Secured(['ROLE_ADMIN'])
    def index() {
        redirect action: 'list', params: params
    }
	@Secured(['ROLE_ADMIN'])
    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [userInstanceList: User.list(params), userInstanceTotal: User.count()]
    }

	
    def create() {
		switch (request.method) {
		case 'GET':
        	[userInstance: new User(params)]
			break
		case 'POST':
			println "User/create, POST: " +params
	        def userInstance = new User(params)
			
	        if (!userInstance.save(flush: true)) {
				def createFromApp = params.createFromApp
				if(createFromApp)
				{
					def errorMessage
					userInstance.errors.allErrors.each { 
						println it 
						errorMessage += it.toString().toLowerCase().contains("ch.freebo.User.username.unique.error")?"Username not uniqe":""
						errorMessage += it.toString().toLowerCase().contains("ch.freebo.User.email.unique.error")?"E-Mail not unique":""
					}
					println errorMessage
					println "createFromApp: " +params.createFromApp
					render([status: "Error: Username oder E-Mail bereits vorhanden"] as JSON)
//					render( status: 500, exception: "Create not successful") as JSON
				}
				else
				{
					render view: 'create', model: [userInstance: userInstance]
					return
				}

	        }
			def role
			
			//assign Role to user
			role = Role.findByAuthority('ROLE_USER')
			
			UserRole.create userInstance, role, true

			flash.message = message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), userInstance.id])
	        
			if(params.createFromApp)
			{
				println "Registration successful"
				render([status: 'Success: Create successful'] as JSON)
//				render( status: 201, exception: "Registration successful") as JSON
			}
			else
			{				
				redirect action: 'show', id: userInstance.id
			}
			break
		}
    }
	@Secured(['ROLE_ADMIN'])
    def show() {
        def userInstance = User.get(params.id)
        if (!userInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
            redirect action: 'list'
            return
        }

        [userInstance: userInstance]
    }
	@Secured(['ROLE_ADMIN'])
    def edit() {
		switch (request.method) {
		case 'GET':
	        def userInstance = User.get(params.id)
	        if (!userInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [userInstance: userInstance]
			break
		case 'POST':
	        def userInstance = User.get(params.id)
	        if (!userInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (userInstance.version > version) {
	                userInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'user.label', default: 'User')] as Object[],
	                          "Another user has updated this User while you were editing")
	                render view: 'edit', model: [userInstance: userInstance]
	                return
	            }
	        }

	        userInstance.properties = params

	        if (!userInstance.save(flush: true)) {
	            render view: 'edit', model: [userInstance: userInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'user.label', default: 'User'), userInstance.id])
	        redirect action: 'show', id: userInstance.id
			break
		}
    }
	@Secured(['ROLE_ADMIN'])
    def delete() {
        def userInstance = User.get(params.id)
        if (!userInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
            redirect action: 'list'
            return
        }

        try {
            userInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'user.label', default: 'User'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'user.label', default: 'User'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
