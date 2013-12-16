package ch.freebo


class Shopping {
	
	ScannedReceipt receipt
	Date date
	
    static constraints = {
		productShoppings nullable:true
		receipt nullable:true
    }
		
	static belongsTo = [retailer:Retailer, user: User]
	
	static hasMany = [productShoppings: ProductShoppings]
	
	String toString()  {
		def userStr = user?user.toString(): " "
		def locationStr = retailer?retailer.toString(): " "
		def returnStr = date.toString() + " " + locationStr + " ("+userStr+")"
		return returnStr? returnStr: " "
	}
	
	
	
}
