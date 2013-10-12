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
//		shoppings nullable:true
		imageLink nullable:true
		ingredients nullable:true
		size nullable:true
    }
	
	static hasOne = [loyaltyProgram: LoyaltyProgram, manufacturer: Manufacturer]

//	static hasMany = [shoppings:Shopping]
		
	static belongsTo = [productCategory:ProductCategory]
		
	String toString()  {
		return name? name: ""
	}
	
}
