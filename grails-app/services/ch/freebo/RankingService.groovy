package ch.freebo

import org.codehaus.groovy.grails.commons.GrailsApplication;

import groovy.time.TimeCategory;
import groovy.time.TimeDuration;
import static java.util.UUID.randomUUID;

class RankingService {

	NotificationSenderService senderService = new NotificationSenderService()
//	ControlPanelController controlPanel = new ControlPanelController()
	
	User user
	
	Product product
	
	def setUser(User user)
	{
		this.user = user
	}
	
    def calculateUserRanking(Product inputProd, User inputUsr, Shopping shopping) 
	{	
		this.user = inputUsr
		this.product = inputProd

		def oldPoints = 0
		def newPoints = 0
		def oldUserRank = 0
		def newUserRank = 0
		def newPointsForPoS //Calculate points per PoS!
		
		def UserRanking currentRanking = UserRanking.findByUserAndProduct(user, product, [max:1, sort:"updated", order:"desc"])
		
		if(currentRanking)
		{
			/* RANKING EXISTS, OR EXISTED AFTER OPT-IN/OPT-OUT */
			oldPoints = currentRanking.totalPointsCollected
			oldUserRank = currentRanking.rank
			println "oldPoints: " +oldPoints
		}
		else
		{
			/* NO RANKING EXISTS YET, RE-CALCULATE POINTS */
		    newPoints = calculatePointsForProduct(product, user)
			println "newPoints: " +newPoints

		}
		
		//Go through all users with Opt-in, check ranking (and send Notification if it has changed!)
		def usersForRanking = findAllUsersOptInForProduct(product)
		
		newUserRank = calculateNewRank(usersForRanking, user, shopping, newPoints)
		
    }
	
	def calculateRankingForShopping(User inputUser, Shopping shopping)
	{
//		println "rankingService.calculateRankingForShopping(): " + inputUser + " Shopping: " + shopping
		def oldPoints = 0
		def newPoints = 0
		def newUserRankList = []
		
		def shoppingList = Shopping.find(shopping)
		
		shoppingList.productShoppings.each { ProductShoppings ps ->
			if(hasUserOptIn(ps.product, inputUser))
			{
				oldPoints = calculateCurrentPointsForProduct(ps.product, inputUser, shopping)
//				println "oldPoints: " +oldPoints
				newPoints = calculatePointsForProduct(ps.product, inputUser)
//				println "newPoints: " +newPoints
				println "oldPoints + qty = newPoints: " + oldPoints + " + " + ps.qty + " = "+  newPoints
				if(oldPoints + ps.qty == newPoints)
				{
					//Calculate new Rank for All Opt-In Users!
					def usersForRanking = findAllUsersOptInForProduct(ps.product)
					def newRank = calculateNewRank(usersForRanking, inputUser, ps.product, ps, newPoints)
					
					newUserRankList.add(newRank: newRank, product: ps.product)
					
				}
			}
		}
		
		println "newUserRankings: " + newUserRankList
		
		return newUserRankList
	}
	
	def calculateCurrentPointsForProduct(Product localProd, User currentUser, Shopping currentShopping)
	{
//		println "RankingService.calculateCurrentPointsForProduct(): " + localProd + currentUser, currentShopping
		Integer nmbr = 0
		def prods = currentUser.shoppings.each { Shopping s->
			/** Exclude current shopping to calculate current Points */
			if(!s.equals(currentShopping))
			{
				s.productShoppings.each {ps ->
					if(ps.product == localProd)
						nmbr = nmbr+ps.qty
				}
			}
		}
		return nmbr
	}
	
	
	def calculatePointsForProduct(Product localProd, User currentUser)
	{
		Integer nmbr = 0
		def prods = currentUser.shoppings.each { Shopping s->
			s.productShoppings.each {ps ->
				if(ps.product == localProd)
					nmbr = nmbr+ps.qty
			}
		}
		return nmbr
	}
	
	def calculatePointsForProductPerLocation(Product localProd, User currentUser, Shopping shopping)
	{
		Integer nmbr = 0
		shopping.each { Shopping s->
				TimeDuration td = TimeCategory.minus(new Date(), s.date)
				s.productShoppings.each {ps ->
					if(ps.product == localProd)
						nmbr = nmbr+ps.qty
				}
			}
		return nmbr
	}
	
	def calculateTotalPoints(User currentUser)
	{
		Integer nmbr = 0
		currentUser.shoppings.each { Shopping s->
			TimeDuration td = TimeCategory.minus(new Date(), s.date)
			s.productShoppings.each {ps ->
				if(hasUserOptIn(ps.product, currentUser))
					nmbr = nmbr+ps.qty
			}
		}
		return nmbr
	}
	
	def hasUserOptIn(Product localProd, User user)
	{
//		println "hasUserOptIn: " + localProd +user
		def	userProdListOptIn = UserProduct.findByProductAndUser(localProd, user, [max:1, sort:"updated", order:"desc"])
		
		def optIn = false
		
		if(userProdListOptIn)
		{
			println "optIn: " +userProdListOptIn.optIn + ", User: " + user + ", Product:" +localProd
			if(userProdListOptIn.optIn)
				optIn = true
		}

		return optIn
	}
	
	def calculateNewRank(allUsersOptIn, User localUser, Product localProd, ProductShoppings shopping, newPoints)
	{
		def newUserRank = 1
		def isUserInRank = false
		def allRankings = []
		//for all users which are opt-in, always calculate the Points accumulated for the Product
		allUsersOptIn.each { User user ->
			Integer totalPoints = calculatePointsForProduct(localProd, user)
			println "RankingService.calculateNewRank(), totalPoints:" + totalPoints
			allRankings.add([user:user, points:totalPoints])
					
		}
		def rank = 1
		println "AllRankings: " +allRankings
		def groupedByRating = allRankings.groupBy({ -it.points})
		
		//sort and calculate the rank
		groupedByRating.sort().each { points, items ->
		  items.each { 
			  it.rank = rank 
			  println it.user
			  println localUser
			  if(it.user.id == localUser.id)
  			  {
			      newUserRank = rank
  			  }
		  }
		  rank += items.size()
		}
		
		groupedByRating.each { key, value ->
			def User rankUser = value.getAt(0)['user']
			def points = value.getAt(0)['points']
			def newRank = value.getAt(0)['rank']
			
			def UserRanking oldUserRanking = UserRanking.findByUserAndProduct(rankUser, localProd, [sort:"updated", order:"desc"])
			def oldRank = oldUserRanking?oldUserRanking.rank:0
			def newRankAchieved = oldUserRanking?(oldUserRanking!=newRank):false
			def UserRanking newUserRanking
			if(rankUser == localUser)
				newUserRanking = new UserRanking(rank: newRank,  rankBefore: oldRank, newRank: newRankAchieved,  pointsCollected: newPoints, totalPointsCollected: points, product: localProd,  user: rankUser, updated: new Date())
			else
				newUserRanking = new UserRanking(rank: newRank,  rankBefore: oldRank, newRank: newRankAchieved,  pointsCollected: 0, totalPointsCollected: points, product: localProd,  user: rankUser, updated: new Date())
			newUserRanking.save(failOnError:true)
		}
		
		println "groupedByRating: " +groupedByRating
		println "newUserRank: " +newUserRank
		return newUserRank
	}
	
	def addNotificationsToUser(User user, String message, String title)
	{
		def date = new Date()
		def uuid = randomUUID() as String
		def newLogMessage = new LogMessages(messageId: uuid, action: "NotificationSent", createDate: date.toString(), logDate: date, message: message).save(failOnError:true)
		
		senderService.addMessages(title, message)
	}
	
	def sendNotificationToUser(User user)
	{
		senderService.callGCMServiceMsg(user)
	}
	
	def clearNotifications()
	{
		senderService.deleteMessages()
	}
	
	
	def sendUpdatesForRank(groupedRating, allRankings, Product localProd)
	{
		groupedRating.each { key, value ->
			def User rankUser = value.getAt(0)['user']
			def points = value.getAt(0)['points']
			def newRank = value.getAt(0)['rank']
			def UserRanking oldRanking = UserRanking.findByUserAndProduct(rankUser, localProd, [sort:"updated", order:"desc"])
			println "Found Ranking for User: "+rankUser +" Rank before:" +oldRanking
			def oldPoints = oldRanking?oldRanking.totalPointsCollected:0
			def oldRank = oldRanking?oldRanking.rank:0
				def newUserRanking = new UserRanking(rank: newRank,  rankBefore: oldRank, newRank: true,  pointsCollected: points-(oldRanking?oldRanking.totalPointsCollected:0), totalPointsCollected: points, product: product,  user: rankUser, updated: new Date())
				if(newUserRanking.save(failOnError:true))
				{
					def date = new Date()
					def uuid = randomUUID() as String
					if(oldRank > newRank)
					{
						if(newUserRanking.rank!=oldRank)
						{
							senderService.addMessages("RANG", "Gratuliere: Du hast einen neuen Rang erreicht!")
							senderService.callGCMServiceMsg(rankUser)
							def newLogMessage = new LogMessages(messageId: uuid, action: "NotificationSent", createDate: date.toString(), logDate: date, message: "New Rank for Product: "+ localProd)
							
							if(newLogMessage.save(failOnError:true))
							{
								rankUser.addToLogMessages(newLogMessage).save(failOnError:true)
							}
						}
					}
					else if(oldRank < newRank)
					{
						if(newUserRanking.rank!=oldRank)
						{
							senderService.addMessages("RANG", "Achtung: Du hast einen Rang verloren!")
							senderService.callGCMServiceMsg(rankUser)
							def newLogMessage = new LogMessages(messageId: uuid, action: "NotificationSent", createDate: date.toString(), logDate: date, message: "Lost Rank for Product: " +localProd)
							
							if(newLogMessage.save(failOnError:true))
							{
								rankUser.addToLogMessages(newLogMessage).save(failOnError:true)
							}
						}
					}
					senderService.deleteMessages()
				}
				
		}
	}
	
	
	
	def findAllUsersOptInForProduct(Product localProd)
	{
		def userRole = Role.findByAuthority('ROLE_USER')
		def users = UserRole.findAllByRole(userRole).user
		
		def usersList = []
		
		users.each { User currentUser ->
			def userProdOptIn = UserProduct.findByUserAndProduct(currentUser, localProd, [sort:"updated", order:"desc"])
			if(userProdOptIn)
			{
				if(userProdOptIn.optIn)
					usersList.add(currentUser)
			}

		}
		return usersList
	}
	
	
	def getCrownsForProduct(Product localProduct, User currentUser)
	{
		def salesPoint = " "
		def crowns =  []
		def dataPoS = []
		def allUsersOptIn = findAllUsersOptInForProduct(localProduct)
		Integer pointsPoS = 0
		allUsersOptIn.each{ User optInUser ->
				pointsPoS = 0
				optInUser.shoppings.each { Shopping shopping ->
					
					salesPoint = shopping.retailer.toString()
					pointsPoS = pointsPoS+calculatePointsForProductPerLocation(localProduct, optInUser, shopping)
					
					dataPoS.add([user:optInUser, points:pointsPoS, location: shopping.retailer])
				}
			}
		
		def rank = 1
		def newUserRank = 0
		def groupedByRating = dataPoS.groupBy({ -it.points})
//				crowns.add([rank: rank, crownstatus: crownstatus, salespoint: salesPoint])
		groupedByRating.sort().each { points, items ->
		  items.each { 
			  it.rank = rank 
			  if(it.user.id == currentUser.id)
  			  {
			      newUserRank = rank
				  salesPoint = it.location.toString()
  			  }
		  }
		  rank += items.size()
		}
		
		def currentUsersCnt = groupedByRating.size()
		Double percent_10 = Math.ceil(currentUsersCnt * 0.1)
		Double percent_20 = Math.max(2.0, Math.ceil(currentUsersCnt * 0.2)).toDouble()
		Double percent_30 = Math.max(3.0, Math.ceil(currentUsersCnt * 0.3)).toDouble()
		
		def crownstatus = 0
		if(newUserRank <= percent_10)
			crownstatus = 1
		else if(newUserRank <= percent_20)
			crownstatus = 2
		else if(newUserRank <= percent_30)
			crownstatus = 3
		
		crowns.add([rank: newUserRank, crownstatus: crownstatus, salespoint: salesPoint])
		println "Crowns: " +crowns
		return crowns
	}
	
	def getLeaderboardRanking(User inputUser)
	{
		def leaderBoard = []
		
		def userRole = Role.findByAuthority('ROLE_USER')
		def users = UserRole.findAllByRole(userRole).user
		def usersList = []
		def int totalPointsForUser= 0
		
		users.each { User currentUser ->
				if(currentUser.shoppings)
				{
					totalPointsForUser = calculateTotalPoints(currentUser)
					usersList.add([user: currentUser, points: totalPointsForUser])
				}
		}
		
		def groupedByRating = usersList.groupBy({ -it.points})
		def rank = 1
		groupedByRating.sort().each { points, items ->
		  items.each { 
			  println "items: " +it
			  it.rank = rank 
		  }
		  rank += items.size()
		}
		
		groupedByRating.each { key, value ->
			if(value.size()>1)
			{
				value.eachWithIndex { obj, i ->
					def rankUser = obj['user']
					def points = obj['points']
					def newRank = obj['rank']
					if(newRank in 1..3 || rankUser == inputUser)
						leaderBoard.add([username: rankUser, points: points, rank: newRank])
				}
			}
			else
			{
				def rankUser = value.getAt(0)['user']
				def points = value.getAt(0)['points']
				def newRank = value.getAt(0)['rank']
				if(newRank in 1..3 || rankUser == inputUser)
					leaderBoard.add([username: rankUser, points: points, rank: newRank])
			}

		}
//		leaderBoard.sort{ it.rank }
//		println "Leaderboard after cleanup: " +leaderBoard
		return leaderBoard
	}
	
	def getAllOptInProductsForUser(User localUser)
	{
		def products = []
		if(localUser.shoppings)
		{
			def userShopping = localUser.shoppings.each { s->
				s.productShoppings.each { ps->
					def optIn = hasUserOptIn(ps.product, localUser)
					if(optIn)
						products.addAll(ps.product)
				}
			}
		}
		
		return products.unique()
	}
	
	def getLeaderboardProduct(Product localProd)
	{
		def leaderBoard = []
		
		def userRole = Role.findByAuthority('ROLE_USER')
		def users = UserRole.findAllByRole(userRole).user
		def usersList = []
		def int totalPointsForUser= 0
		
		def usersForRanking = findAllUsersOptInForProduct(localProd)
		
		usersForRanking.each { User currentUser ->
				totalPointsForUser = calculatePointsForProduct(localProd, currentUser)
				usersList.add([user: currentUser, points: totalPointsForUser])
		}
		println "getLeaderBoardProduct: " +usersList
		
		def groupedByRating = usersList.groupBy({ -it.points})
		def rank = 1
		groupedByRating.sort().each { points, items ->
		  items.each {
			  println "items: " +it
			  it.rank = rank
		  }
		  rank += items.size()
		}
		
		groupedByRating.each { key, value ->
			if(value.size()>1)
			{
				value.eachWithIndex { obj, i ->
					def rankUser = obj['user'].toString()
					def Integer points = obj['points']
					def Integer newRank = obj['rank']
//					if(newRank in 1..3 || rankUser == inputUser)
						leaderBoard.add([username: rankUser, points: points, rank: newRank])
				}
			}
			else
			{
				def rankUser = value.getAt(0)['user'].toString()
				def Integer points = value.getAt(0)['points']
				def Integer newRank = value.getAt(0)['rank']
//				if(newRank in 1..3 || rankUser == inputUser)
					leaderBoard.add([username: rankUser, points: points, rank: newRank])
			}

		}
		leaderBoard.sort{ it.rank }
		println "Leaderboard after cleanup: " +leaderBoard
		return leaderBoard
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
}
