package ch.freebo

import grails.converters.JSON
import groovy.json.JsonBuilder
import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured


class ProductController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

	@Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
	def loginFromApp() {
		println "ProductController: loginFromApp()"
		println "Params" + params
		def user = User.findByUsername(params.username)
		println "User: " +user
		
		if(user==null)
		{
			response.status = 500
			render([error: 'Error 500'] as JSON)
		}

		println user.shoppings.productShoppings.collect()
//		println user.loyaltyPrograms.collect()
		
		
		def jsonExport = getJSONData(user)
		
		def json = new JsonBuilder(jsonExport)
				
		println json.toPrettyString()
		//return product and user settings!!
		if(user)
			render json
		else
			render( status: 500, exception: params.exception) as JSON
	}
	
	def getJSONData(User user)
	{
		HashMap jsonMap = new HashMap()
		
		def products = []
		
		def userShopping = user.shoppings.each { s->
			s.productShoppings.each { ps->
				println "Add product: " +ps.product
				products.addAll(ps.product)
			}

		}
		
		println userShopping
		
		jsonMap.products = products.unique().collect {prod ->
			//check if user has optIn
			def optIn = hasUserOptIn(prod, user)
			if(optIn)
			{
				//count products bought
				def pointsCollected = calculatePointsForProduct(prod, user)
				return [id: prod.id, ean: prod.ean, name: prod.name, imagelink: prod.imageLink, optin: optIn, points: pointsCollected, ingredients: prod.ingredients]
			}
		}
		
		jsonMap.products.removeAll([null])
		println jsonMap
		jsonMap.username = user.username
		
		println jsonMap
		return jsonMap
	}
	
	def hasUserOptIn(Product prod, User user)
	{
		def userProdListOptOut = UserProduct.findAllByProductAndUser(prod, user, [max:1, sort:"optOutDate", order:"desc"])
		def	userProdListOptIn = UserProduct.findAllByProductAndUser(prod, user, [max:1, sort:"optInDate", order:"desc"])
		
		def userProd
		
		if(userProdListOptOut && userProdListOptIn)
			userProd = userProdListOptOut[0].optOutDate > userProdListOptIn[0].optInDate ? userProdListOptOut[0] : userProdListOptIn[0]
		else if(userProdListOptOut)
			userProd = userProdListOptOut[0]
		else if(userProdListOptIn)
			userProd = userProdListOptIn[0]
		
		def optIn = false
		
		if(userProd)
		{
			println "optIn: " +userProd.optIn
			if(userProd.optIn)
				optIn = true
		}

		return optIn
	}

	
	def calculatePointsForProduct(Product prod, User user)
	{
		def nmbr = 0
		def prods = user.shoppings.each { s->
			s.productShoppings.each {ps ->
				if(ps.product == prod)
					nmbr = nmbr+ps.qty
			}
		}
		println "calculatePointsForProduct: " +nmbr
		return nmbr
	}
	
	def calculateProductRank(Product prod, User user)
	{
		
		
	}
	
	@Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
	def updateUserInfo()
	{
		println "ProductController: updateUserInfo()"
		println "Params" + params
		def user = User.findByUsername(params.username)
		println "User: " +user
		
		if(user==null)
		{
			response.status = 500
			render([error: 'Error 500'] as JSON)
		}
		
		if(params.ean)
		{
			def prod = Product.findByEan(params.ean)
			
			if(prod)
			{
				println "Found product for Opt-In: " + prod.name
				//OPT IN
				if(params.optin)
				{
					def userProd = new UserProduct(user: user, product: prod, optIn: true)
					println "Opt-in: " +userProd
					if(!userProd.save(failOnError:true))
					{
						render( status: 500, exception: params.exception) as JSON
					}
				}
				//OPT OUT
				else if(params.optout)
				{
					def userProd = new UserProduct(user: user, product: prod, optIn: false )
					println "Opt-Out: " +userProd
					if(!userProd.save(failOnError:true))
					{
						render( status: 500, exception: params.exception) as JSON
					}
				}
				
				render([response: 'OK 201'] as JSON)
				
			}
			else
			{
				println "Product not found for Opt-In!"
			}
		}
		
	}
	
	def callGCMService()
	{
		
	}
	
    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [productInstanceList: Product.list(params), productInstanceTotal: Product.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[productInstance: new Product(params)]
			break
		case 'POST':
	        def productInstance = new Product(params)
	        if (!productInstance.save(flush: true)) {
	            render view: 'create', model: [productInstance: productInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'product.label', default: 'Product'), productInstance.id])
	        redirect action: 'show', id: productInstance.id
			break
		}
    }

    def show() {
        def productInstance = Product.get(params.id)
        if (!productInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'product.label', default: 'Product'), params.id])
            redirect action: 'list'
            return
        }

        [productInstance: productInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def productInstance = Product.get(params.id)
	        if (!productInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'product.label', default: 'Product'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [productInstance: productInstance]
			break
		case 'POST':
	        def productInstance = Product.get(params.id)
	        if (!productInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'product.label', default: 'Product'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (productInstance.version > version) {
	                productInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'product.label', default: 'Product')] as Object[],
	                          "Another user has updated this Product while you were editing")
	                render view: 'edit', model: [productInstance: productInstance]
	                return
	            }
	        }

	        productInstance.properties = params

	        if (!productInstance.save(flush: true)) {
	            render view: 'edit', model: [productInstance: productInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'product.label', default: 'Product'), productInstance.id])
	        redirect action: 'show', id: productInstance.id
			break
		}
    }

    def delete() {
        def productInstance = Product.get(params.id)
        if (!productInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'product.label', default: 'Product'), params.id])
            redirect action: 'list'
            return
        }

        try {
            productInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'product.label', default: 'Product'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'product.label', default: 'Product'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
