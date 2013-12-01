package ch.freebo

import grails.converters.JSON
import groovy.json.JsonBuilder
import grails.plugins.springsecurity.Secured


class DataGeneratorService {
	
	def springSecurityService
	
	def RankingService rankingService = new RankingService()
	
	def User user
	
	def setUser(User user)
	{
		this.user = user
	}
	
	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def registerUserAndDevice(regId, deviceType, deviceOs)
	{
		if(regId)
		{
			def Devices device = Devices.findByDeviceId(regId)
			if(device)
			{
				def deviceFound = false;
				user.devices.each {
					if(it == device)
						deviceFound = true;
				}
				if(deviceFound)
				{
					println "Device already registered with User! User cleared App and re-installed!"
				}
				else
				{
					println "Device registered with different User! Re-register!"
					user.addToDevices(device)
				}
			}
			else
			{
				device = new Devices(deviceId: regId, deviceType: deviceType.toString(),  deviceOs: deviceType.toString(), registrationDate: new Date()).save(failOnError:true)
				println "Device for User created:  " +device
				if(device)
					user.addToDevices(device)
			}

		}
		return user
	}
	
	def getAllOptInProductsForUser(User localUser)
	{
		def products = []
		def returnList = []

		def	userProdListOptIn = UserProduct.findByUser( user)
		userProdListOptIn.each {
			products.add(it.product)
		}
		println "optInList: " +products
		products.unique().each {
			def UserProduct userOptInProd = UserProduct.findByProductAndUser(it, localUser,[max:1, sort:"updated", order:"desc"])
			println "found Opt-in request: "+ userOptInProd
			if(userOptInProd.optIn)
				returnList.add(userOptInProd.product)
		}
		
		println "products for opt-in: " +returnList.unique()
		return returnList.unique()
	}
	
	def getJSONData()
	{
		HashMap jsonMap = new HashMap()
		
		rankingService.setUser(user)
		
		def products = []
		
		def userShopping = getAllOptInProductsForUser(user)
		
		def productListSize = 0
		if(userShopping)
			productListSize = userShopping.size()
		
		println userShopping
		
		jsonMap.products = userShopping.collect {Product prod ->
			//check if user has optIn
			def optIn = hasUserOptIn(prod, user)
			def isActive = isOptInActive(prod, user)
			
			//count products bought
			def pointsCollected = rankingService.calculatePointsForProduct(prod, user)
			//get product info
			def hersteller = prod.manufacturer.toString()
			def category = prod.productCategory.toString()
			
			def newRank = 0
			def oldRank = 0
			
			def newRankAchieved = false
			
			def crowns = {}
			def leaderBoard = []
			
			if(isActive)
			{
				
				//get rank information
				def UserRanking userrank = UserRanking.findByUserAndProduct(user, prod, [sort:"updated", order:"desc"])
	
				if(userrank)
				{
					newRank = userrank.rank
					oldRank = userrank.rankBefore
					newRankAchieved = userrank.newRank
					pointsCollected = userrank.pointsCollected
				}
	//			else
	//			{
	//				rankingService.calculateUserRanking(prod, user, null)
	//				userrank = UserRanking.findByUserAndProduct(user, prod, [sort:"updated", order:"desc"])
	//				newRank = userrank.rank
	//				oldRank = userrank.rankBefore
	//				newRankAchieved = userrank.newRank
	//			}
	//
					crowns = rankingService.getCrownsForProduct(prod, user).collect()
					leaderBoard = rankingService.getLeaderboardProduct(prod).collect()
			}
			
			return [id: prod.id, ean: prod.ean, name: prod.name, imagelink: prod.imageLink, optin: optIn, isactive: isActive, points: pointsCollected, ingredients: prod.ingredients, size: prod.size, producer: hersteller, userrank: newRank, olduserrank: oldRank, newrankachieved: newRankAchieved, category: category, leaderboard: leaderBoard]
		
		}
		
//		def leaderBoard = rankingService.getLeaderboardProduct()
//		jsonMap.leaderboard =  leaderBoard.collect {
//			def username = it.username.toString()
//			return [username: username, points: it.points, rank: it.rank]
//		}
		
//		def badges = rankingService.calculateBadges(productListSize).collect()
//		jsonMap.badges =  badges.unique().collect {
//			return [id: it.id, name: it.name, achieved: it.achieved, newachieved: it.newAchieved, achievementdate: it.achievementDate, group: it.badgeGroup]
//		}
		
		jsonMap.username = user.username
		
		println jsonMap
		return jsonMap
	}
	
	def hasUserOptIn(Product prod, User user)
	{
		def	userProdListOptIn = UserProduct.findByProductAndUser(prod, user, [max:1, sort:"updated", order:"desc"])
		
		def optIn = false
		
		if(userProdListOptIn)
		{
			println "optIn: " +userProdListOptIn.optIn
			if(userProdListOptIn.optIn)
				optIn = true
		}

		return optIn
	}
	
	def isOptInActive(Product prod, User user)
	{
		def	UserProduct userProdOptIn = UserProduct.findByProductAndUser(prod, user, [max:1, sort:"updated", order:"desc"])
		
		def isActive = false
		return userProdOptIn?userProdOptIn.isActive:false
	}

	
	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def updateUserInfo()
	{
		println "ProductController: updateUserInfo()"
		println "Params" + params
//		def user = User.findByUsername(params.username)
		def user = User.findByUsername(springSecurityService.currentUser.toString())
//		println "User: " +user
		
		if(user==null)
		{
			render([status: "FAILED", exception: "User nicht gefunden!"] as JSON)
		}
		
		if(params.ean)
		{
			def prod = Product.findByEan(params.ean)
			
			def pointsForProduct = rankingService.calculatePointsForProduct(prod, user)
			
			if(prod && pointsForProduct != 0)
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
				if(pointsForProduct == 0 && prod)
					render([status: "FAILED", exception: "Opt In fehlerhaft: Noch keine Eink�ufe vorhanden!"] as JSON)
				else
					render([status: "FAILED", exception: "Opt In/Out fehlerhaft: Produkt nicht gefunden!"] as JSON)
				
			}
		}
		
	}
}
