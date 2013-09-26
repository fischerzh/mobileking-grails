package ch.freebo

class Manufacturer {

	String name
	
    static constraints = {
		products nullable:true
    }
	
	static hasMany = [products: Product]
	
	String toString()  {
		return name? name: ""
	}
}
