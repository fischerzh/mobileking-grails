package ch.freebo

class Location {
	
	String name
	Integer plz

    static constraints = {
    }
	
	String toString()  {
		def returnName = "CH-"+plz+" "+name
		return returnName? returnName: ""
	}
	
	String toShortString()  {
		return name? name: ""
	}
}
