package ch.freebo

class ScannedReceipt {
	
	User user;
	String scanDate;
	Date purchaseDate;
	Date approveDate;
	String fileName;
	Integer filePart;
	Integer isApproved = 1;
	String rejectMessage

	static belongsTo = [shopping: Shopping]
	
    static constraints = {
		purchaseDate nullable:true
		approveDate nullable:true
		rejectMessage nullable:true, size:1..1000
		shopping nullable:true
    }
	
	String toString()  {
		return scanDate
	}
}
