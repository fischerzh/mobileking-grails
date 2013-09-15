package ch.freebo

class LoyaltyCard {

	String name
	String username
	String password
	
	String number
	String link
	
    static constraints = {
    	username nullable:true
		password nullable:true
		link nullable:true
	}
	
	String toString()  {
		return name? name: ""
	}
}
