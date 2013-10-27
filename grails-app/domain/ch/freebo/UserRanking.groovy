package ch.freebo

class UserRanking {
	
	User 	 user
	Product  product
	Retailer pointOfSales
	
	Integer  rank
	Integer  rankBefore
	Date     updated
	
	Integer	 pointsCollected
	Integer  totalPointsCollected

	Boolean  newRank
	
    static constraints = {
		pointOfSales nullable:true
    }
}
