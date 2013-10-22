package ch.freebo

class Devices {

	String deviceId
	String deviceType
	String deviceScreen
	String deviceOs
	
    static constraints = {
		deviceType nullable:true
		deviceScreen nullable:true
		deviceOs nullable:true
    }
}
