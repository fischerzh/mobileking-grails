package ch.freebo

class Retailer {
	
	String name
	Location location
	String street
	String imageLink
	
    static constraints = {
		location nullable:true
		street nullable:true
		imageLink nullable:true
    }
	
	String toString()  {
		def returnName = location?location.toString():" "
		returnName = name + " " + returnName
		return returnName? returnName: " "
	}
}
