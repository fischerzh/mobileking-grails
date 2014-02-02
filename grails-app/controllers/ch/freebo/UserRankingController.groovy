package ch.freebo

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured

@Secured(['ROLE_ADMIN'])
class UserRankingController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
		def userRankingInstanceList = []
		def productList = Product.findAll()
		
		if(params.userRankingProduct)
		{
			def prod = Product.findById(params.userRankingProduct)
			
//			userRankingInstanceList = UserRanking.findAllByProduct(prod, [sort:"updated", order:"desc"])
			userRankingInstanceList = UserRanking.createCriteria().list {
				eq('product', prod)
				order('updated', 'desc')
				order('rank','asc')
			}
			
		}
		else
			userRankingInstanceList = UserRanking.list(params)
        [userRankingInstanceList: userRankingInstanceList, userRankingInstanceTotal: userRankingInstanceList.size(), productList: productList, userRankingProduct: params.userRankingProduct]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[userRankingInstance: new UserRanking(params)]
			break
		case 'POST':
	        def userRankingInstance = new UserRanking(params)
	        if (!userRankingInstance.save(flush: true)) {
	            render view: 'create', model: [userRankingInstance: userRankingInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'userRanking.label', default: 'UserRanking'), userRankingInstance.id])
	        redirect action: 'show', id: userRankingInstance.id
			break
		}
    }

    def show() {
        def userRankingInstance = UserRanking.get(params.id)
        if (!userRankingInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'userRanking.label', default: 'UserRanking'), params.id])
            redirect action: 'list'
            return
        }

        [userRankingInstance: userRankingInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def userRankingInstance = UserRanking.get(params.id)
	        if (!userRankingInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'userRanking.label', default: 'UserRanking'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [userRankingInstance: userRankingInstance]
			break
		case 'POST':
	        def userRankingInstance = UserRanking.get(params.id)
	        if (!userRankingInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'userRanking.label', default: 'UserRanking'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (userRankingInstance.version > version) {
	                userRankingInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'userRanking.label', default: 'UserRanking')] as Object[],
	                          "Another user has updated this UserRanking while you were editing")
	                render view: 'edit', model: [userRankingInstance: userRankingInstance]
	                return
	            }
	        }

	        userRankingInstance.properties = params

	        if (!userRankingInstance.save(flush: true)) {
	            render view: 'edit', model: [userRankingInstance: userRankingInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'userRanking.label', default: 'UserRanking'), userRankingInstance.id])
	        redirect action: 'show', id: userRankingInstance.id
			break
		}
    }

    def delete() {
        def userRankingInstance = UserRanking.get(params.id)
        if (!userRankingInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'userRanking.label', default: 'UserRanking'), params.id])
            redirect action: 'list'
            return
        }

        try {
            userRankingInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'userRanking.label', default: 'UserRanking'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'userRanking.label', default: 'UserRanking'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
