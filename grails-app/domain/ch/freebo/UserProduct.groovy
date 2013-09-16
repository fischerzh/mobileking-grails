package ch.freebo

import java.util.Formatter.DateTime;

class UserProduct {

	Product product
	User user
	
	Date optInDate
	Date optOutDate
	Boolean isActive
	Boolean optIn
	Integer version
	
    static constraints = {
    	optInDate nullable:true
		optOutDate nullable:true
		isActive nullable:true
		version nullable:true
	}
	
	def afterInsert() {
		version+=1;
		Date date = new Date()
		if(optIn)
		{
			isActive = true
			optInDate = date;
		}
		else
		{
			isActive = false
			optOutDate = date;
		}
	}
}
