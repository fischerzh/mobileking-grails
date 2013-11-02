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
	
	RankingService rankingService = new RankingService()
	
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
			render([status: "FAILED", exception: "User nicht gefunden!"] as JSON)
		}

//		println user.shoppings.productShoppings.collect()
		
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
			
			user = registerUserAndDevice(user, params.regId)
			
			if(user.save(flush: true))
				new UserLogin( user: user, loginDate: date, success: true).save(failOnError:true)
			render json
		}
		else
		{
			new UserLogin( user: user, loginDate: date, success: false).save(failOnError:true)
			render([status: "FAILED", exception: "Login fehlerhaft!"] as JSON)
		}
	}
	
	
	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def getJSONData()
	{
		HashMap jsonMap = new HashMap()
		
		def products = []
		
		def userShopping = user.shoppings.each { s->
			s.productShoppings.each { ps->
				def optIn = hasUserOptIn(ps.product, user)
				if(optIn)
					products.addAll(ps.product)
			}
		}
		
//		def c = UserProduct.createCriteria()
//		def results = c.list {
//			eq("user", user)
//			and {
//				eq("optIn", true)
//			}
//			
//			order("updated", "desc")
//		}
//		println results
		
		Random random = new Random()
		
		def crowns = {}
		
		println "products (with OptIn)" +products
		if (products)
		{
			jsonMap.products = products.unique().collect {Product prod ->
				//check if user has optIn
				def optIn = hasUserOptIn(prod, user)
				if(optIn)
				{
					def newRank = 0
					def oldRank = 0
					//count products bought
					def hersteller = prod.manufacturer.toString()
					def category = prod.productCategory.toString()
					
					def pointsCollected = calculatePointsForProduct(prod)
					
					def UserRanking userrank = UserRanking.findByUserAndProduct(user, prod, [sort:"updated", order:"desc"])

					def newRankAchieved = false
					if(userrank)
					{
						newRank = userrank.rank
						oldRank = userrank.rankBefore
						newRankAchieved = userrank.newRank
					}
					else
					{
						rankingService.calculateUserRanking(prod, user, null)
						userrank = UserRanking.findByUserAndProduct(user, prod, [sort:"updated", order:"desc"])
						newRank = userrank.rank
						oldRank = userrank.rankBefore
						newRankAchieved = userrank.newRank
					}
					
					crowns = rankingService.getCrownsForProduct(prod, user).collect()
					
					return [id: prod.id, ean: prod.ean, name: prod.name, imagelink: prod.imageLink, optin: optIn, points: pointsCollected, ingredients: prod.ingredients, producer: hersteller, userrank: newRank, olduserrank: oldRank, newrankachieved: newRankAchieved, category: category, crowns: crowns]
				}
			}
			
			if(jsonMap.products)
				jsonMap.products.removeAll([null])
			
		}
		else
		{
			jsonMap.products = []
			jsonMap.products.add(
				[id: 0, ean: " ", name: "Dein Lieblingsprodukt", imagelink: "http://www.subulahanews.com/wp-content/uploads/2012/10/no-image-icon1.jpg", optin: true, points: 0, ingredients: "Inhaltsstoffe", producer: "Dein Lieblingshersteller", userrank: 1, olduserrank: 0, newrankachieved: true, category: "Produktkategorie"]
			)
		}
		
		println "jsonMap: " + jsonMap
		
		println "jsonMap.products, size: " + jsonMap.products.size()
			
//		jsonMap.recommendations = products.unique().collect { Product prod ->
//			def pointsCollected = calculatePointsForProduct(prod, user)
//			def hersteller = prod.manufacturer.toString()
//			def category = prod.productCategory.toString()
//			return [id: prod.id, name: prod.name, imagelink: prod.imageLink, points: pointsCollected, producer: hersteller, category: category]
//		}
//		jsonMap.recommendations = jsonMap.recommendations.sort {a, b -> b.points <=> a.points }
		
		
		def badges = calculateBadges(jsonMap.products.size()).collect()
		println "User-Badges:" +badges
		jsonMap.badges =  badges.unique().collect {
			return [id: it.id, name: it.name, achieved: it.achieved, newachieved: it.newAchieved, achievementdate: it.achievementDate, group: it.badgeGroup]
		}

		jsonMap.username = user.username
		jsonMap.status = "SUCCESS"
		jsonMap.exception = "Aktualisierung erfolgreich!"
		println jsonMap
		return jsonMap
	}
	
	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def hasUserOptIn(Product prod, User user)
	{
		def	userProdListOptIn = UserProduct.findByProductAndUser(prod, user, [sort:"updated", order:"desc"])
		
		def optIn = false
		
		if(userProdListOptIn)
		{
				optIn = userProdListOptIn.optIn
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
	def registerUserAndDevice(User user, regId)
	{
		if(regId)
		{
			def device = Devices.findByDeviceId(params.regId)
			if(device)
			{
				println "Device already registered! User cleared App and re-installed!"
			}
			else
			{
				device = new Devices(deviceId: params.regId, deviceType: params.deviceType,  deviceOs: params.deviceOs, registrationDate: new Date()).save(failOnError:true)
			}
			println "Device for User created:  " +device
			if(device)
				user.addToDevices(device)
		}
		return user
	}
	
	def calculateBadges(int productCount)
	{
		println "#productCount: " +productCount
		def userLogins = UserLogin.countByUser(user)
		println '#userLogins ' + userLogins
		
		def badges = []
		
		if(userLogins >= 0)
		{
			def badge = Badge.findByNameAndUser('Starter', user)
			if(!badge)
				badge = createBadge('Login', 'Starter')
			else
				badge.newAchieved = false
			badge.save(failOnError:true)
			badges.add(badge)
		}
		if(userLogins >= 50)
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
				def crownstatus = random.nextInt(2)+1
//				def rank = random.nextInt(15)
				crowns.add([rank: rank, crownstatus: crownstatus, salespoint: salesPoint])

		}
		return crowns
	}
	
	def createBadge(badgeGroup, badgeName)
	{
		def badge = new Badge(user: user, name: badgeName, achieved: true, achievementDate: new Date(), badgeGroup: badgeGroup, newAchieved: true )
		println "New Badge achieved: " +badge
//		if(user.devices)
//		{
//		println "Send message"
//			controlPanel.addMessages("BADGE", "GlŸckwunsch: Du hast einen neuen Badge!")
//			controlPanel.callGCMServiceMsg(user)
//		}
		
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
			render([status: "FAILED", exception: "User nicht gefunden!"] as JSON)
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
					def userProd = new UserProduct(user: user, product: prod, optIn: true, updated: new Date())
					println "Opt-in: " +userProd
					if(!userProd.save(failOnError:true))
					{
						render([status: "FAILED", exception: "Opt-In fehlerhaft: Produkt nicht gefunden!"] as JSON)
					}
				}
				//OPT OUT
				else if(params.optout)
				{
					def userProd = new UserProduct(user: user, product: prod, optIn: false, updated: new Date() )
					println "Opt-Out: " +userProd
					if(!userProd.save(failOnError:true))
					{
						render([status: "FAILED", exception: "Opt-Out fehlerhaft: Produkt nicht gefunden!"] as JSON)
					}
				}
				
				render([status: "SUCCESS", exception: "Opt In/Out erfolgreich: Produkt aktualisiert!"] as JSON)

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
