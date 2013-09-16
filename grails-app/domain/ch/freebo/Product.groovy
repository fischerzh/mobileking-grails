package ch.freebo

class Product {

	String name
	String ean
	String imageLink
	String ingredients
	
    static constraints = {
		loyaltyProgram unique:true, nullable:true
//		shoppings nullable:true
		imageLink nullable:true
		ingredients nullable:true
    }
	
	static hasOne = [loyaltyProgram: LoyaltyProgram]

//	static hasMany = [shoppings:Shopping]
		
	static belongsTo = [productCategory:ProductCategory]
		
	String toString()  {
		return name? name: ""
	}
	
}
