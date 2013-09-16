package ch.freebo

class LoyaltyProgram {
	
	String name
	Boolean active
	Boolean status
	Boolean ranking
	String linkToLoyaltyProvider
	
	Product product
	
    static constraints = {
		loyaltyProgramLevels nullable:true
		linkToLoyaltyProvider nullable:true
		active nullable:true
		status nullable:true
		ranking nullable:true 
    }
	
	static hasMany = [loyaltyProgramLevels:LoyaltyProgramLevels]
			
	String toString()  {
		return name? name: ""
	}
	
}
