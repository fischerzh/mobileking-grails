package ch.freebo

class LoyaltyProgram {
	
	String name
	Boolean status
	Boolean ranking
	Product product
	String linkToLoyaltyProvider

    static constraints = {
		loyaltyProgramLevels nullable:true
		linkToLoyaltyProvider nullable:true
		status nullable:true
		ranking nullable:true 
		users nullable:true
    }
	
	static hasMany = [loyaltyProgramLevels:LoyaltyProgramLevels, users:User]
	
	static belongsTo = User
	
	String toString()  {
		return name? name: ""
	}
	
}
