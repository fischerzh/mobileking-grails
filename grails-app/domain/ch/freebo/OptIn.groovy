package ch.freebo

import java.util.Formatter.DateTime;

class OptIn {

	Product product
	User user
	
	Date optInDate
	Date optOutDate
	Date lastUpdated
	Boolean isActive
	Boolean optIn
	
    static constraints = {
    	optInDate nullable:true
		optOutDate nullable:true
		isActive nullable:true
	}
	
	static mapping = {
		autoTimestamp true
	}
	
	def afterInsert() {
		if(optIn)
		{
//			isActive = true
			if(!optInDate)
				optInDate = lastUpdated;
		}
		else
		{
			if(!optOutDate)
				optOutDate = lastUpdated;
		}
	}
}
