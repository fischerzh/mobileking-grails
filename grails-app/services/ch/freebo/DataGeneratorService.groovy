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

		def	userProdListOptIn = OptIn.findAllByUser( localUser)
		println "userProdListOptIn: " +userProdListOptIn
		userProdListOptIn.each {
			products.add(it.product)
		}
		products.unique().each {
			def OptIn userOptInProd = OptIn.findByProductAndUser(it, localUser,[max:1, sort:"lastUpdated", order:"desc"])
			if(userOptInProd.optIn)
				returnList.add(userOptInProd.product)
		}
		
		log.debug( "products for opt-in: " +returnList.unique())
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
			println "dataGeneratorService(). pointsCollected" + pointsCollected
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
//					crowns = rankingService.getCrownsForProduct(prod, user).collect()
			}
			
			leaderBoard = rankingService.getLeaderboardProduct(prod).collect()
			
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
		def	userProdListOptIn = OptIn.findByProductAndUser(prod, user, [max:1, sort:"lastUpdated", order:"desc"])
		
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
		def	OptIn userProdOptIn = OptIn.findByProductAndUser(prod, user, [max:1, sort:"lastUpdated", order:"desc"])
		
		def isActive = false
		return userProdOptIn?userProdOptIn.isActive:false
	}


}
