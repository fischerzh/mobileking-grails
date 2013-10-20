package ch.freebo

import es.osoco.android.gcm.AndroidGcmService;
import grails.converters.JSON
import groovy.json.JsonBuilder

import org.springframework.dao.DataIntegrityViolationException

import grails.plugins.springsecurity.Secured

@Secured(['ROLE_ADMIN', 'ROLE_MANUF'])
class ProductController {
	
	def springSecurityService
	
	def androidGcmService

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def loginFromApp() {
		println "ProductController: loginFromApp()"
		println "Params" + params
		
		println "User logged in: " + springSecurityService.currentUser
		
//		def user = User.findByUsername(params.username)
		def user = User.findByUsername(springSecurityService.currentUser.toString())
		println "User: " +user
		
		if(user==null)
		{
			response.status = 500
			render([error: 'Error 500'] as JSON)
		}

		println user.shoppings.productShoppings.collect()
//		println user.loyaltyPrograms.collect()
		
		
		def jsonExport = getJSONData(user)
		
		if(!user.isActiveApp)
		{
			jsonExport.isactiveapp = false
		}
		else
		{
			jsonExport.isactiveapp = true
		}
		
		def json = new JsonBuilder(jsonExport)
				
		
		println json.toPrettyString()
		//return product and user settings!!
		Date date = new Date()
		
		if(user)
		{
			user.isActiveApp = true
			user.regId = params.regId
			user.save(flush: true)
			new UserLogin( user: user, loginDate: date, success: true).save(failOnError:true)
			render json
		}
		else
		{
			new UserLogin( user: user, loginDate: date, success: false).save(failOnError:true)
			render( status: 500, exception: params.exception) as JSON
		}
	}
	
	def sendMessage = {
		['deviceToken', 'messageKey', 'messageValue'].each {
				key -> params[key] = ["APA91bE4eARfV4klHMq3K5SLASQovkBHf8EgIjT5RDotDwfBZrUVFhXEPvG5-hSQMDeVJ9js3tmDAQ6VnUz6-AbeFpld-oIZFD0zGz4egIQqbGU2F_nortx1D7aaPfrWnrx17n3zk7M5SLf3-eiqL9iALfqbrlPpAw"].flatten().findAll { it }
		}
		def messages = params.messageKey.inject([:]) {
				currentMessages, currentKey ->
				currentMessages << [ "1" : "Test Message from Grails"]
		}
		
		androidGcmService.sendMessage(messages, params.deviceToken,
				"", grailsApplication.config.android.gcm.api.key).toString()
	}
	
	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def getJSONData(User user)
	{
		HashMap jsonMap = new HashMap()
		
		def products = []
		
		def userShopping = user.shoppings.each { s->
			s.productShoppings.each { ps->
//				println "Add product: " +ps.product
				products.addAll(ps.product)
			}

		}
		
//		println userShopping
		Random random = new Random()
		
		def crowns = {}
		
		jsonMap.products = products.unique().collect {Product prod ->
			//check if user has optIn
			def optIn = hasUserOptIn(prod, user)
			if(optIn)
			{
				//count products bought
				def hersteller = prod.manufacturer.toString()
				def category = prod.productCategory.toString()
				def pointsCollected = calculatePointsForProduct(prod, user)
				def userrank = random.nextInt(10)
				crowns =  getCrownsForProduct(prod, user).collect()
//				println crowns
				return [id: prod.id, ean: prod.ean, name: prod.name, imagelink: prod.imageLink, optin: optIn, points: pointsCollected, ingredients: prod.ingredients, producer: hersteller, userrank: userrank, category: category, crowns: crowns]
			}
		}
		
		jsonMap.recommendations = products.unique().collect { Product prod ->
			def pointsCollected = calculatePointsForProduct(prod, user)
			def hersteller = prod.manufacturer.toString()
			def category = prod.productCategory.toString()
			return [id: prod.id, name: prod.name, imagelink: prod.imageLink, points: pointsCollected, producer: hersteller, category: category]
		}
		jsonMap.recommendations = jsonMap.recommendations.sort {a, b -> b.points <=> a.points }
//		println "json Recommendations "  +jsonMap.recommendations 
		jsonMap.products.removeAll([null])
		jsonMap.username = user.username
		
		println jsonMap
		return jsonMap
	}
	
	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
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
//			println "optIn: " +userProd.optIn
			if(userProd.optIn)
				optIn = true
		}

		return optIn
	}

	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def calculatePointsForProduct(Product prod, User user)
	{
		def nmbr = 0
		def prods = user.shoppings.each { s->
			s.productShoppings.each {ps ->
				if(ps.product == prod)
					nmbr = nmbr+ps.qty
			}
		}
//		println "calculatePointsForProduct: " +nmbr
		return nmbr
	}
	
	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def calculateGlobalProductRank(Product prod, User user)
	{
		
		
	}
	
	def getCrownsForProduct(Product prod, User user)
	{
		Random random = new Random()
		
	
//		return [rank: "1", crownstatus: "2", salespoint: "Migros Zurich HB"]
		def salesPoint = " "
		def crowns =  []
		user.shoppings.each { s ->
				salesPoint = s.retailer.toString()
				def userPoints = random.nextInt(2)+1
				def rank = random.nextInt(15)
				crowns.add([rank: rank, crownstatus: userPoints, salespoint: salesPoint])

		}
		return crowns
	}
	
	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def updateUserInfo()
	{
		println "ProductController: updateUserInfo()"
		println "Params" + params
//		def user = User.findByUsername(params.username)
		def user = User.findByUsername(springSecurityService.currentUser.toString())
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
		sendMessage();
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
