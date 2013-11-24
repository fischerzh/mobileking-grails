package ch.freebo

class ProductShoppings {
	
	Product product
	Float qty
	Float price
	
    static constraints = {
		price nullable:true, scale: 2
    }
	
	String toString()  {
		def productStr = product?product.toString(): " "
		def qty = qty?qty.toString(): " "
		def returnStr = qty.toString() + " x " + productStr
		return returnStr? returnStr: " "
	}
	

}
