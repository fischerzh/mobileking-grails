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
	
	String toString()  {
		def deviceType = deviceType?deviceType.toString(): " "
		def deviceOs = deviceOs?deviceOs.toString(): " "
		def returnStr = deviceType.toString() + " Android " + deviceOs;
		return returnStr? returnStr: " "
	}
}
