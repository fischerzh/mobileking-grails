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
			oldPoints = currentRanking.totalPointsCollected
			oldUserRank = currentRanking.rank
			println "oldPoints: " +oldPoints
		}
		else
		{
		    newPoints = calculatePointsForProduct(product)
			println "newPoints: " +newPoints
		}
		
		//Go through all users with Opt-in, check ranking (and send Notification if it has changed!)
		def usersForRanking = findAllUsersOptInForProduct(product)
		
		newUserRank = calculateRank(usersForRanking, user, shopping)

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
	
	def calculatePointsForProduct(Product prod)
	{
		def nmbr = 0
		def prods = user.shoppings.each { Shopping s->
			TimeDuration td = TimeCategory.minus(new Date(), s.date)
			println "TimeDifference: " +td
			s.productShoppings.each {ps ->
				if(ps.product == prod)
					nmbr = nmbr+ps.qty
			}
		}
		return nmbr
	}
	
	def calculateRank(allUsersOptIn, User currentUser, ProductShoppings shopping)
	{
		def newUserRank = 1
		def isUserInRank = false
		def allRankings = []
		allUsersOptIn.each { User user ->
			def UserRanking oldRanking = UserRanking.findByUserAndProduct(user, product, [sort:"updated", order:"desc"])
			if(oldRanking)
			{
				def Integer totalPoints = oldRanking.totalPointsCollected
				if(user.id == currentUser.id){
					isUserInRank = true
					totalPoints = oldRanking.totalPointsCollected+shopping.qty
				}
				allRankings.add([user:user, points:totalPoints])

			}
		}
		println "All Rankings: " +allRankings
		def rank = 1
		def groupedByRating = allRankings.groupBy({ -it.points})
		println "Before: " +groupedByRating
		if(!isUserInRank)
		{
			allRankings.add([currentUser, shopping.qty])
			println "Ranking Added User: " +allRankings
			
		}
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
		println "After: " + groupedByRating
		println "User Rank"  + newUserRank
		
		sendUpdatesForRank(groupedByRating, allUsersOptIn)
		
		return newUserRank
	}
	
	def sendUpdatesForRank(groupedRating, allRankings)
	{
		println "All Rankings: " +allRankings
		groupedRating.each() { key, value ->
			println "points: "+ (-key)
			println value
			println "user: " +value.getAt(0)['user']
			def rankUser = value.getAt(0)['user']
			def points = value.getAt(0)['points']
			def newRank = value.getAt(0)['rank']
			println "new Rank:" +newRank + " newPoints: " +points
			def UserRanking oldRanking = UserRanking.findByUserAndProduct(rankUser, product, [sort:"updated", order:"desc"])
			println "old Rank:" +oldRanking.rank +" oldPoints: " +oldRanking.totalPointsCollected
//			if(newRank!=oldRanking.rank)
//			{
				def newUserRanking = new UserRanking(rank: newRank,  rankBefore: oldRanking.rank, newRank: true,  pointsCollected: points-oldRanking.totalPointsCollected, totalPointsCollected: points, product: oldRanking.product,  user: rankUser, updated: new Date())
				if(newUserRanking.save(failOnError:true))
				{
					if(newUserRanking.rank < oldRanking.rank)
						controlPanel.addMessages("RANG", "Achtung: Du hast einen Rang verloren!")
					else if(newUserRanking.rank > oldRanking.rank)
						controlPanel.addMessages("RANG", "Gratuliere: Du hast einen neuen Rang erreicht!")
					if(newUserRanking.rank!=oldRanking.rank)
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
			println "optIn: " +userProd.optIn
			if(userProd.optIn)
				optIn = true
		}

		return optIn
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
}
