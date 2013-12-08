package ch.freebo

class ScannedReceipt {
	
	User user;
	String scanDate;
	Date purchaseDate;
	Date approveDate;
	String fileName;
	Integer filePart;
	Integer isApproved = 1;

	static belongsTo = [shopping: Shopping]
	
    static constraints = {
		purchaseDate nullable:true
		approveDate nullable:true
		shopping nullable:true
    }
	
	String toString()  {
		return scanDate
	}
}
