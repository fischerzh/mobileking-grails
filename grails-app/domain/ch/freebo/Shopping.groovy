package ch.freebo

class Shopping {

	Date date
	
    static constraints = {
		productShoppings nullable:true
    }
		
	static belongsTo = [retailer:Retailer, user: User]
	
	static hasMany = [productShoppings: ProductShoppings]
	
	String toString()  {
		def userStr = user?user.toString(): " "
		def locationStr = retailer?retailer.toString(): " "
		def returnStr = date.toString() + " " + locationStr
		return returnStr? returnStr: " "
	}
	
	
}
