package ch.freebo

import es.osoco.android.gcm.AndroidGcmService;
import grails.converters.JSON
import groovy.json.JsonBuilder

import org.springframework.dao.DataIntegrityViolationException

import grails.plugins.springsecurity.Secured

@Secured(['ROLE_ADMIN'])
class ProductController {
	
	def springSecurityService
	
	JSONGeneratorService jsonGenerator = new JSONGeneratorService()
	
	ControlPanelController controlPanel = new ControlPanelController()

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']
	
	def User user

	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def loginFromApp() {
		println "ProductController: loginFromApp()"
		println "Params" + params
		
//		jsonGenerator.loginFromApp()
		
		println "User logged in: " + springSecurityService.currentUser
		
		user = User.findByUsername(springSecurityService.currentUser.toString())
		println "User: " +user
		
		if(user==null)
		{
			response.status = 500
			render([error: 'Error 500'] as JSON)
		}

		println user.shoppings.productShoppings.collect()
		
		def jsonExport = getJSONData()
		
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
			if(params.regId)
			{
//				println "Registration Params: " + params.regId, params.deviceType, params.deviceOs
				def device = Devices.findByDeviceId(params.regId)
				if(device)
				{
					println "Device already registered! User cleared App and re-installed!"
				}	
				else
				{
					device new Devices(deviceId: params.regId, deviceType: params.deviceType,  deviceOs: params.deviceOs).save(failOnError:true)
				}
				println "Device for User created:  " +device
				if(device)
					user.addToDevices(device)
			}
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
	
	
	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def getJSONData()
	{
		HashMap jsonMap = new HashMap()
		
		def products = []
		
		def userShopping = user.shoppings.each { s->
			s.productShoppings.each { ps->
//				println "Add product: " +ps.product
				products.addAll(ps.product)
			}

		}
		
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
				def pointsCollected = calculatePointsForProduct(prod)
				def UserRanking userrank = UserRanking.findByUserAndProduct(user, prod, [sort:"updated", order:"desc"])
				def newRank = 0
				def newRankAchieved = false
				if(userrank)
				{
					newRank = userrank.rank
					newRankAchieved = userrank.newRank
				}
				crowns =  getCrownsForProduct(prod, user).collect()
//				println crowns
				return [id: prod.id, ean: prod.ean, name: prod.name, imagelink: prod.imageLink, optin: optIn, points: pointsCollected, ingredients: prod.ingredients, producer: hersteller, userrank: newRank, newrankachieved: newRankAchieved, category: category, crowns: crowns]
			}
		}
		
//		jsonMap.recommendations = products.unique().collect { Product prod ->
//			def pointsCollected = calculatePointsForProduct(prod, user)
//			def hersteller = prod.manufacturer.toString()
//			def category = prod.productCategory.toString()
//			return [id: prod.id, name: prod.name, imagelink: prod.imageLink, points: pointsCollected, producer: hersteller, category: category]
//		}
//		jsonMap.recommendations = jsonMap.recommendations.sort {a, b -> b.points <=> a.points }
		
		jsonMap.products.removeAll([null])
		
		def badges = calculateBadges(jsonMap.products.size()).collect()
		println "User-Badges:" +badges
		jsonMap.badges =  badges.unique().collect {
			return [id: it.id, name: it.name, achieved: it.achieved, newachieved: it.newAchieved, achievementdate: it.achievementDate, group: it.badgeGroup]
		}
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
			if(userProd.optIn)
				optIn = true
		}

		return optIn
	}

	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def calculatePointsForProduct(Product prod)
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
	def calculateGlobalProductRank(Product prod)
	{
		
		
	}
	
	def calculateBadges(int productCount)
	{
		println "#productCount: " +productCount
		def userLogins = UserLogin.countByUser(user)
		println '#userLogins ' + userLogins
		
		def badges = []
		
		if(userLogins >= 42)
		{
			def badge = Badge.findByNameAndUser('Shopper', user)
			if(!badge)
				badge = createBadge('Login', 'Shopper')
			else
				badge.newAchieved = false
			badge.save(failOnError:true)
			badges.add(badge)
		}
		if(userLogins >= 100)
		{
			def badge = Badge.findByNameAndUser('Shoppaholic', user)
			if(!badge)
				badge = createBadge('Login', 'Shoppaholic')
			else
				badge.newAchieved = false
			badge.save(failOnError:true)
			badges.add(badge)
		}
		if(productCount > 0)
		{
			def badge = Badge.findByNameAndUser('Lonely', user)
			if(!badge)
				badge = createBadge('OptIn', 'Lonely')
			else
				badge.newAchieved = false
			badge.save(failOnError:true)
			badges.add(badge)
		}
		if(productCount >= 2)
		{
			def badge = Badge.findByNameAndUser('Lover', user)
			if(!badge)
				badge = createBadge('OptIn', 'Lover')
			else
				badge.newAchieved = false
			badge.save(failOnError:true)
			badges.add(badge)		
		}
		if(productCount >=10)
		{
			def badge = Badge.findByNameAndUser('Addicted', user)
			if(!badge)
				badge = createBadge('OptIn', 'Addicted')
			else
				badge.newAchieved = false
			badge.save(failOnError:true)
			badges.add(badge)		
		}
		println "List badges after calc: " + badges
		return badges
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
	
	def createBadge(badgeGroup, badgeName)
	{
		def badge = new Badge(user: user, name: badgeName, achieved: true, achievementDate: new Date(), badgeGroup: badgeGroup, newAchieved: true )
		println "Send message"
		controlPanel.callGCMServiceMsg('New Badge', user)
		
		return badge
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
