package ch.freebo

class Badge {
	
	User user
	
	String name
	Boolean achieved = false
	Boolean newAchieved
	Date achievementDate
	String badgeGroup
	
    static constraints = {
    	name unique:true
	}
	
	String toString()  {
		return name? name: ""
	}
	
	static belongsTo = [user : User]
}
