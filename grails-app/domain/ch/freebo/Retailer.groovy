package ch.freebo

class Retailer {
	
	String name
	Location location
	
    static constraints = {
		location nullable:true
    }
	
	String toString()  {
		def returnName = location?location.toString():" "
		returnName = name + " " + returnName
		return returnName? returnName: " "
	}
}
