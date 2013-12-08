package ch.freebo

class LogMessages {

	User user
	String messageId
	String createDate
	Date logDate
	String location
	String userAction
	String message
	
    static constraints = {
		createDate nullable:false
		userAction nullable:false
		message nullable:false
		logDate nullable:false
		location nullable:true
    }
}
