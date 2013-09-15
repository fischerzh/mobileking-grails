package ch.freebo

class LoyaltyProgramLevels {
	
	String pointLevelName
	String levelName
	Integer pointLevelMin
	Integer pointLevelMax

    static constraints = {
		levelName nullable:true
    }
	
	static belongsTo = [loyaltyProgram:LoyaltyProgram]
}
