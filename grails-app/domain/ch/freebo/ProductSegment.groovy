package ch.freebo

class ProductSegment {
	
	String name

    static constraints = {
    }
	
	static hasMany = [productCategory:ProductCategory]
	
	String toString()  {
		return name? name: ""
	}
}
