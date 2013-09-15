package ch.freebo

class Shopping {

	Date date
	String place
	
    static constraints = {
		products nullable:true
    }
	
	static hasMany = [products:Product]
	
	static belongsTo = Product
	
	String toString()  {
		return place? place: ""
	}
	
}
