package ch.freebo

class Location {
	
	String name
	Integer plz

    static constraints = {
		plz nullable:true
    }
	
	String toString()  {
		return name? name: ""
	}
}
