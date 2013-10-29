package ch.freebo

import groovy.time.TimeCategory;
import groovy.time.TimeDuration

class RankingService {

	NotificationSenderService senderService = new NotificationSenderService()
	ControlPanelController controlPanel = new ControlPanelController()
	
	User user
	
	Product product
	
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

//		if (oldPoints < oldPoints+shopping.qty)
//		{
//			def newRankAchieved = oldUserRank!=newUserRank?true:false
//			def userRanking = new UserRanking(rank: newUserRank,  rankBefore: oldUserRank, newRank: newRankAchieved,  pointsCollected: shopping.qty?shopping.qty:(newPoints-oldPoints), totalPointsCollected: oldPoints + shopping.qty, product: product,  user: user, updated: new Date())
//			if(userRanking.save(failOnError:true))
//			{
//				println "NEW user ranking: "  +userRanking.rank
//				if(newRankAchieved)
//				{
//					controlPanel.addMessages("RANG", "Gratuliere: Du hast einen neuen Rang erreicht!")
//					controlPanel.callGCMServiceMsg(user)
////					senderService.callGCMServiceMsg("Gratuliere: Du hast einen neuen Rang erreicht!", user)
//				}
//				
//			}
//		}
		
		
    }
	
	def calculatePointsForProduct(Product prod, User currentUser)
	{
		def nmbr = 0
		def prods = currentUser.shoppings.each { Shopping s->
			TimeDuration td = TimeCategory.minus(new Date(), s.date)
			println "TimeDifference: " +td
			s.productShoppings.each {ps ->
				if(ps.product == prod)
					nmbr = nmbr+ps.qty
			}
		}
		return nmbr
	}
	
	def calculatePointsForProductPerLocation(Product prod, User currentUser, Shopping shopping)
	{
		def nmbr = 0
		shopping.each { Shopping s->
				TimeDuration td = TimeCategory.minus(new Date(), s.date)
				println "TimeDifference: " +td
				
				s.productShoppings.each {ps ->
					if(ps.product == prod)
						nmbr = nmbr+ps.qty
				}
			}
		return nmbr
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
				def Integer totalPoints = oldRanking.totalPointsCollected
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
			  println "items: " +it
			  it.rank = rank 
			  if(it.user.id == currentUser.id)
  			  {
			      newUserRank = rank
  			  }
		  }
		  rank += items.size()
		}
		sendUpdatesForRank(groupedByRating, allUsersOptIn)
		println "new user rank: " +newUserRank
		return newUserRank
	}
	
	def sendUpdatesForRank(groupedRating, allRankings)
	{
		println "sendUpdatesForRank: " +groupedRating
		println "allRankings: " +allRankings
		groupedRating.each() { key, value ->
			def rankUser = value.getAt(0)['user']
			def points = value.getAt(0)['points']
			def newRank = value.getAt(0)['rank']
			def UserRanking oldRanking = UserRanking.findByUserAndProduct(rankUser, product, [sort:"updated", order:"desc"])
			println "Found Ranking: " +oldRanking
			def oldPoints = oldRanking?oldRanking.totalPointsCollected:0
			def oldRank = oldRanking?oldRanking.rank:0
				def newUserRanking = new UserRanking(rank: newRank,  rankBefore: oldRank, newRank: true,  pointsCollected: points-(oldRanking?oldRanking.totalPointsCollected:0), totalPointsCollected: points, product: product,  user: rankUser, updated: new Date())
				if(newUserRanking.save(failOnError:true))
				{
					println "New User Ranking: " +newUserRanking
					if(newRank < oldRank)
						controlPanel.addMessages("RANG", "Achtung: Du hast einen Rang verloren!")
					else if(newRank > oldRank)
						controlPanel.addMessages("RANG", "Gratuliere: Du hast einen neuen Rang erreicht!")
					if(newUserRanking.rank!=oldRank)
						controlPanel.callGCMServiceMsg(rankUser)
				}
//			}
		}
	}
	
	
	
	def findAllUsersOptInForProduct(Product prod)
	{
		def userRole = Role.findByAuthority('ROLE_USER')
		def users = UserRole.findAllByRole(userRole).user
		
		def usersList = []
		
		users.each { User user ->
			def userProdOptIn = UserProduct.findByUserAndProduct(user, prod, [max:1, sort:"updated", order:"desc"])
			if(userProdOptIn.optIn)
				usersList.add(user)
		}
		println "AllUsers Opt In for Product " + prod + " "+usersList
		return usersList
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
	
	def getCrownsForProduct(Product prod, User currentUser)
	{
		def salesPoint = " "
		def crowns =  []
		def dataPoS = []
		def allUsersOptIn = findAllUsersOptInForProduct(prod)
		Integer pointsPoS = 0
		allUsersOptIn.each{ User optInUser ->
				optInUser.shoppings.each { Shopping shopping ->
					salesPoint = shopping.retailer.toString()
					pointsPoS = pointsPoS+calculatePointsForProductPerLocation(prod, optInUser, shopping)
					
					dataPoS.add([user:optInUser, points:pointsPoS, location: shopping.retailer])
					
				}
				
			}
		
		println "getCrownsForProduct: " +dataPoS
				
		def rank = 1
		def newUserRank = 0
		def groupedByRating = dataPoS.groupBy({ -it.points})
//				crowns.add([rank: rank, crownstatus: crownstatus, salespoint: salesPoint])
		println "BEFORE: " + groupedByRating
		groupedByRating.sort().each { points, items ->
		  items.each { 
			  println "items: " +it
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
		println "top 10: " + percent_10 + "top 20: " +percent_20 + "top 30: " +percent_30
		if(newUserRank <= percent_10)
			crownstatus = 1
		else if(newUserRank <= percent_20)
			crownstatus = 2
		else if(newUserRank <= percent_30)
			crownstatus = 3
		
		crowns.add([rank: newUserRank, crownstatus: crownstatus, salespoint: salesPoint])
		println "Crown collected: " +crowns
		
		return crowns
	}
}
