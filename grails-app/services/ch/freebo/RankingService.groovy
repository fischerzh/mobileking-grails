package ch.freebo

import groovy.time.TimeCategory;
import groovy.time.TimeDuration;
import static java.util.UUID.randomUUID;

class RankingService {

	NotificationSenderService senderService = new NotificationSenderService()
	ControlPanelController controlPanel = new ControlPanelController()
	
	User user
	
	Product product
	
	def setUser(User user)
	{
		this.user = user
	}
	
    def calculateUserRanking(Product inputProd, User inputUsr, ProductShoppings shopping) 
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
			/* NO RANKING EXISTS YET, RECALCULATE POINTS */
		    newPoints = calculatePointsForProduct(product, user)
			println "newPoints: " +newPoints

		}
		
		//Go through all users with Opt-in, check ranking (and send Notification if it has changed!)
		def usersForRanking = findAllUsersOptInForProduct(product)
		
		newUserRank = calculateRank(usersForRanking, user, shopping, newPoints)
		
    }
	
	def calculatePointsForProduct(Product prod, User currentUser)
	{
		Integer nmbr = 0
		def prods = currentUser.shoppings.each { Shopping s->
			TimeDuration td = TimeCategory.minus(new Date(), s.date)
			
			/** 
			 * @TODO: Only get the last 3months of points!! *
			 **/
			
			s.productShoppings.each {ps ->
				if(ps.product == prod)
					nmbr = nmbr+ps.qty
			}
		}
		return nmbr
	}
	
	def calculatePointsForProductPerLocation(Product prod, User currentUser, Shopping shopping)
	{
		Integer nmbr = 0
		shopping.each { Shopping s->
				TimeDuration td = TimeCategory.minus(new Date(), s.date)
				s.productShoppings.each {ps ->
					if(ps.product == prod)
						nmbr = nmbr+ps.qty
				}
			}
		return nmbr
	}
	
	def calculatePointsForAll(User currentUser)
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
	
	def calculateRank(allUsersOptIn, User currentUser, ProductShoppings shopping, newPoints)
	{
		def newUserRank = 1
		def isUserInRank = false
		def allRankings = []
		//for all users which are opt-in, find the old ranking
		allUsersOptIn.each { User user ->
			def UserRanking oldRanking = UserRanking.findByUserAndProduct(user, product, [sort:"updated", order:"desc"])
			if(oldRanking)
			{
				Integer totalPoints = oldRanking.totalPointsCollected
				if(user.id == currentUser.id){
					isUserInRank = true
					totalPoints = oldRanking.totalPointsCollected+(shopping?shopping.qty:0)
				}
				allRankings.add([user:user, points:totalPoints])
			}
			else
			{
				//NO RANKING EXISTS FOR CURRENT USER!!
				if(user.id == currentUser.id){
//					
//					def userRanking = new UserRanking(rank: 0,  rankBefore: 0, newRank: false,  pointsCollected: shopping?shopping.qty:0, totalPointsCollected: newPoints, product: product,  user: user, updated: new Date())
//					userRanking.save(failOnError:true)
					allRankings.add([user:user, points:newPoints])
					
				}

			}
		}
		def rank = 1
		def groupedByRating = allRankings.groupBy({ -it.points})
		if(!isUserInRank)
		{
			allRankings.add([currentUser, shopping?shopping.qty:0])
		}
		//sort and calculate the rank
		groupedByRating.sort().each { points, items ->
		  items.each { 
			  it.rank = rank 
			  if(it.user.id == currentUser.id)
  			  {
			      newUserRank = rank
  			  }
		  }
		  rank += items.size()
		}
		sendUpdatesForRank(groupedByRating, allUsersOptIn)
		return newUserRank
	}
	
	def sendUpdatesForRank(groupedRating, allRankings)
	{
		groupedRating.each { key, value ->
			def User rankUser = value.getAt(0)['user']
			def points = value.getAt(0)['points']
			def newRank = value.getAt(0)['rank']
			def UserRanking oldRanking = UserRanking.findByUserAndProduct(rankUser, product, [sort:"updated", order:"desc"])
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
							controlPanel.addMessages("RANG", "Gratuliere: Du hast einen neuen Rang erreicht!")
							controlPanel.callGCMServiceMsg(rankUser)
							def newLogMessage = new LogMessages(messageId: uuid, action: "NotificationSent", createDate: date.toString(), logDate: date, message: "Gratuliere: Du hast einen neuen Rang erreicht!")
							
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
							controlPanel.addMessages("RANG", "Achtung: Du hast einen Rang verloren!")
							controlPanel.callGCMServiceMsg(rankUser)
							def newLogMessage = new LogMessages(messageId: uuid, action: "NotificationSent", createDate: date.toString(), logDate: date, message: "Achtung: Du hast einen Rang verloren!")
							
							if(newLogMessage.save(failOnError:true))
							{
								rankUser.addToLogMessages(newLogMessage).save(failOnError:true)
							}
						}
					}
					controlPanel.deleteMessages()
				}
				
		}
	}
	
	
	
	def findAllUsersOptInForProduct(Product prod)
	{
		def userRole = Role.findByAuthority('ROLE_USER')
		def users = UserRole.findAllByRole(userRole).user
		
		def usersList = []
		
		users.each { User currentUser ->
			def userProdOptIn = UserProduct.findByUserAndProduct(currentUser, prod, [sort:"updated", order:"desc"])
			if(userProdOptIn)
			{
				if(userProdOptIn.optIn)
					usersList.add(currentUser)
			}

		}
		return usersList
	}
	
	
	def getCrownsForProduct(Product prod, User currentUser)
	{
		def salesPoint = " "
		def crowns =  []
		def dataPoS = []
		def allUsersOptIn = findAllUsersOptInForProduct(prod)
		Integer pointsPoS = 0
		allUsersOptIn.each{ User optInUser ->
				pointsPoS = 0
				optInUser.shoppings.each { Shopping shopping ->
					
					salesPoint = shopping.retailer.toString()
					pointsPoS = pointsPoS+calculatePointsForProductPerLocation(prod, optInUser, shopping)
					
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
					totalPointsForUser = calculatePointsForAll(currentUser)
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
		leaderBoard.sort{ it.rank }
//		println "Leaderboard after cleanup: " +leaderBoard
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
