package ch.freebo

class ProductCategory {
	
	String name
	
	
    static constraints = {
		products nullable:true
    }
	
	static hasMany = [products:Product]
	
	static belongsTo = [productSegment:ProductSegment]
	
	String toString()  {
		return name? name: ""
	}
}
