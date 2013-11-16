package ch.freebo

import es.osoco.android.gcm.AndroidGcmService;
import grails.converters.JSON
import groovy.json.JsonBuilder

import org.springframework.dao.DataIntegrityViolationException

import grails.plugins.springsecurity.Secured

@Secured(['ROLE_ADMIN'])
class ProductController {
	
	def springSecurityService
	
	DataGeneratorService dataGenerator = new DataGeneratorService()
	
	RankingService rankingService = new RankingService()
	
	ControlPanelController controlPanel = new ControlPanelController()

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']
	
	def User user

	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def loginFromApp() {
		
		render dataGenerator.loginFromApp(user, params)
	}
	
	
	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def getJSONData()
	{
		HashMap jsonMap = new HashMap()
		
		def products = []
		
		def crowns = {}
		
		def productListSize = 0
		
		def userShopping = user.shoppings.each { s->
			s.productShoppings.each { ps->
				def optIn = hasUserOptIn(ps.product, user)
				if(optIn)
					products.addAll(ps.product)
			}
		}
		
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
					
					return [id: prod.id, ean: prod.ean, name: prod.name, imagelink: prod.imageLink, optin: optIn, points: pointsCollected, ingredients: prod.ingredients, size: prod.size, producer: hersteller, userrank: newRank, olduserrank: oldRank, newrankachieved: newRankAchieved, category: category, crowns: crowns]
				}
			}
			
			if(jsonMap.products)
				jsonMap.products.removeAll([null])
			
			productListSize = jsonMap.products.size()
		}
		else
		{
			jsonMap.products = []
			jsonMap.products.add(
				[id: 0, ean: " ", name: "Dein Lieblingsprodukt", imagelink: "http://www.subulahanews.com/wp-content/uploads/2012/10/no-image-icon1.jpg", optin: true, points: 0, ingredients: "Inhaltsstoffe" , producer: "Dein Lieblingshersteller", userrank: 1, olduserrank: 0, newrankachieved: true, category: "Produktkategorie", size: "0g"]
			)
		}
		
//		jsonMap.recommendations = products.unique().collect { Product prod ->
//			def pointsCollected = calculatePointsForProduct(prod, user)
//			def hersteller = prod.manufacturer.toString()
//			def category = prod.productCategory.toString()
//			return [id: prod.id, name: prod.name, imagelink: prod.imageLink, points: pointsCollected, producer: hersteller, category: category]
//		}
//		jsonMap.recommendations = jsonMap.recommendations.sort {a, b -> b.points <=> a.points }
		
		def leaderBoard = rankingService.getLeaderboardRanking(user)
		jsonMap.leaderboard =  leaderBoard.collect {
			def username = it.username.toString()
			return [username: username, points: it.points, rank: it.rank]
		}
		
		def badges = calculateBadges(productListSize).collect()
		
//		println "User-Badges:" +badges
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

	
	def calculateBadges(int productCount)
	{
		println "#productCount: " +productCount
		def userLogins = UserLogin.countByUser(user)
		println '#userLogins ' + userLogins
		def shoppingCounts = Shopping.countByUser(user)
		
		def badges = []
		
		if(userLogins >= 0)
		{
			def badge = Badge.findByNameAndUser('Login1', user)
			if(!badge)
				badge = createBadge('Login', 'Login1')
			else
				badge.newAchieved = false
			badge.save(failOnError:true)
			badges.add(badge)
		}
		if(userLogins >= 50)
		{
			def badge = Badge.findByNameAndUser('Login2', user)
			if(!badge)
				badge = createBadge('Login', 'Login2')
			else
				badge.newAchieved = false
			badge.save(failOnError:true)
			badges.add(badge)
		}
		if(userLogins >= 100)
		{
			def badge = Badge.findByNameAndUser('Login3', user)
			if(!badge)
				badge = createBadge('Login', 'Login3')
			else
				badge.newAchieved = false
			badge.save(failOnError:true)
			badges.add(badge)
		}
		if(productCount > 0)
		{
			def badge = Badge.findByNameAndUser('OptIn1', user)
			if(!badge)
				badge = createBadge('OptIn', 'OptIn1')
			else
				badge.newAchieved = false
			badge.save(failOnError:true)
			badges.add(badge)
		}
		if(productCount >= 5)
		{
			def badge = Badge.findByNameAndUser('OptIn2', user)
			if(!badge)
				badge = createBadge('OptIn', 'OptIn2')
			else
				badge.newAchieved = false
			badge.save(failOnError:true)
			badges.add(badge)		
		}
		if(productCount >=10)
		{
			def badge = Badge.findByNameAndUser('OptIn3', user)
			if(!badge)
				badge = createBadge('OptIn', 'OptIn3')
			else
				badge.newAchieved = false
			badge.save(failOnError:true)
			badges.add(badge)		
		}
		if(shoppingCounts >=5)
		{
			def badge = Badge.findByNameAndUser('Shopping1', user)
			if(!badge)
				badge = createBadge('Shopping', 'Shopping1')
			else
				badge.newAchieved = false
			badge.save(failOnError:true)
			badges.add(badge)
		}
		if(shoppingCounts >= 10)
		{
			def badge = Badge.findByNameAndUser('Shopping2', user)
			if(!badge)
				badge = createBadge('Shopping', 'Shopping2')
			else
				badge.newAchieved = false
			badge.save(failOnError:true)
			badges.add(badge)
		}
		if(shoppingCounts >=30)
		{
			def badge = Badge.findByNameAndUser('Shopping3', user)
			if(!badge)
				badge = createBadge('Shopping', 'Shopping3')
			else
				badge.newAchieved = false
			badge.save(failOnError:true)
			badges.add(badge)
		}
//		println "List badges after calc: " + badges
		return badges
	}
	
	def createBadge(badgeGroup, badgeName)
	{
		def badge = new Badge(user: user, name: badgeName, achieved: true, achievementDate: new Date(), badgeGroup: badgeGroup, newAchieved: true )
//		println "New Badge achieved (not saved): " +badge
		
		return badge
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
