package ch.freebo

class LogMessages {

	String messageId
	String createDate
	Date logDate
	String location
	String action
	String message
	
    static constraints = {
		createDate nullable:false
		action nullable:false
		message nullable:false
		logDate nullable:false
		location nullable:true
    }
}
