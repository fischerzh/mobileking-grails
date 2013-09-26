package ch.freebo

class Product {

	String name
	String ean
	String imageLink
	String ingredients
	String size
	
    static constraints = {
		loyaltyProgram unique:true, nullable:true
//		shoppings nullable:true
		imageLink nullable:true
		ingredients nullable:true
		size nullable:true
    }
	
	static hasOne = [loyaltyProgram: LoyaltyProgram]

//	static hasMany = [shoppings:Shopping]
		
	static belongsTo = [productCategory:ProductCategory, manufacturer: Manufacturer]
		
	String toString()  {
		return name? name: ""
	}
	
}
