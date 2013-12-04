package ch.freebo

class Product {

	String name
	String ean
	String imageLink
	String ingredients
	String size
	
    static constraints = {
		loyaltyProgram unique:true, nullable:true
		manufacturer nullable:true
		productCategory nullable:true
		imageLink nullable:true
		ingredients nullable:true
		size nullable:true
    }
	
	static hasOne = [loyaltyProgram: LoyaltyProgram, manufacturer: Manufacturer]

	static belongsTo = [productCategory:ProductCategory]
		
	String toString()  {
		return name? name: ""
	}
	
}
