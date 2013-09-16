package ch.freebo

class Shopping {

	Date date
	
    static constraints = {
		productShoppings nullable:true
    }
		
	static belongsTo = [location:Location, retailer:Retailer, user: User]
	
	static hasMany = [productShoppings: ProductShoppings]
	
	String toString()  {
		def userStr = user?user.toString(): " "
		def locationStr = retailer?retailer.toString(): " "
		def returnStr = date.toString() + " " + locationStr +  " " + "(User: " +userStr+" )"
		return returnStr? returnStr: " "
	}
	
	
}
