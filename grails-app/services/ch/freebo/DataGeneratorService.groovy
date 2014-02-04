package ch.freebo

import grails.converters.JSON
import groovy.json.JsonBuilder
import grails.plugins.springsecurity.Secured


class DataGeneratorService {

	def springSecurityService

	def RankingService rankingService = new RankingService()

	def User user

	def setUser(User user) {
		this.user = user
	}

	@Secured([
		'ROLE_USER',
		'IS_AUTHENTICATED_FULLY'
	])
	def registerUserAndDevice(regId, deviceType, deviceOs, deviceScreen) {
		if(regId) {
			def Devices device = Devices.findByDeviceId(regId)
			if(device) {
				def deviceFound = false;
				user.devices.each {
					if(it == device)
						deviceFound = true;
				}
				if(deviceFound) {
					println "Device already registered with User! User cleared App and re-installed!"
				}
				else {
					println "Device registered with different User! Re-register!"
					user.addToDevices(device)
				}
			}
			else {
				device = new Devices(deviceId: regId, deviceType: deviceType.toString(),  deviceOs: deviceOs.toString(), deviceScreen: deviceScreen.toString(), registrationDate: new Date()).save(failOnError:true)
				println "Device for User created:  " +device
				if(device)
					user.addToDevices(device)
			}
		}
		return user
	}

	def getAllOptInProductsForUser(User localUser) {
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

	def getJSONData() {
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
			def optIn = rankingService.hasUserOptIn(prod, user)
			def isActive = rankingService.isOptInActive(prod, user)

			//count products bought
			def pointsCollected = 0
			//get product info
			def hersteller = prod.manufacturer.toString()
			def category = prod.productCategory.toString()

			def newRank = 0
			def oldRank = 0

			def newRankAchieved = false

			def crowns = {}
			def leaderBoard = []

			//if the product is activated => Product purchased!
			if(isActive)
			{
				//get rank information
				def UserRanking userrank = UserRanking.findByUserAndProduct(user, prod, [sort:"updated", order:"desc"])

				if(userrank)
				{
					newRank = userrank.rank
					oldRank = userrank.rankBefore
					newRankAchieved = userrank.newRank
					pointsCollected = userrank.totalPointsCollected

					println "dataGeneratorService(), totalPointsCollected: " + pointsCollected

				}
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

		def salesreceipts = []
		def receipts = ScannedReceipt.findAllByUser(user).each { ScannedReceipt sr ->
			def pointOfSales = sr.shopping?sr.shopping.retailer:""
			def pointOfSales_image = sr.shopping?sr.shopping.retailer.imageLink:""
			def totalParts = ScannedReceipt.findAllByUserAndFileName(user, sr.fileName).size()
			def String purchaseDate = sr.purchaseDate?sr.purchaseDate.format("dd MMM yyyy HH:mm:ss"):null
			def shopItems = []
			if(sr.isApproved == 2 && sr.shopping)
			{
				sr.shopping.each { Shopping shop ->
					shop.productShoppings.each { ProductShoppings ps ->
						if(ps.isVerified)
							shopItems.add([name: ps.product.name, ean: ps.product.ean, quantity: ps.qty, price: ps.price])
					}
				}
				salesreceipts.add([salespoint: pointOfSales.toString(), purchasedate: purchaseDate, scandate: sr.scanDate, isapproved : sr.isApproved, rejectmessage:sr.rejectMessage, filename: sr.fileName, isuploaded: true, totalparts: totalParts, imagelink: pointOfSales_image, salesslipitems: shopItems])
			}
			else if (sr.isApproved == 1 || sr.isApproved == 0)
			{
				salesreceipts.add([salespoint: pointOfSales.toString(), purchasedate: purchaseDate, scandate: sr.scanDate, isapproved : sr.isApproved, rejectmessage:sr.rejectMessage, filename: sr.fileName, isuploaded: true, totalparts: totalParts, imagelink: pointOfSales_image, salesslipitems: shopItems])

			}
		}
		println "Receipts: " + salesreceipts.unique()
		//		def allReceipts = salesreceipts.each { it ->
		//			def shopItems = []
		//
		//
		//			return [shopping: sr, shoppingItems: shopItems]
		//		}

		jsonMap.salesslips = salesreceipts.unique()

		jsonMap.username = user.username
		
		jsonMap.userid = user.id

//		jsonMap.email = user.email

		jsonMap.avatarid = user.avatarId
		
		jsonMap.isnotification = user.isNotificationEnabled
		
		jsonMap.isanonymous = user.isAnonymous
		
		jsonMap.isactiveapp = user.isActiveApp
		
		println jsonMap
		return jsonMap
	}



}
