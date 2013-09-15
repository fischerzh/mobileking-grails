package ch.freebo

class Manufacturer {

	String name
	
    static constraints = {
    }
	
	String toString()  {
		return name? name: ""
	}
}
