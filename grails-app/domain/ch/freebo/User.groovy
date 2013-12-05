package ch.freebo

class User {

	transient springSecurityService

	String username
	String password
	String email
	Boolean isActiveApp = false
	Boolean isNotificationEnabled
	Boolean isAnonymous
	boolean enabled
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired

	static constraints = {
		username blank: false, unique: true
		password blank: false
		email email: true, blank: false, unique:true
		isActiveApp nullable:true
		loyaltyCards nullable:true
		shoppings nullable:true
		devices nullable:true
		badges nullable:true
		logMessages nullable:true
	}
	
	static hasMany = [loyaltyCards:LoyaltyCard, shoppings: Shopping, devices: Devices, badges: Badge, logMessages: LogMessages]

	static mapping = {
		password column: '`password`'
		isNotificationEnabled defaultValue: true
	}

	Set<Role> getAuthorities() {
		UserRole.findAllByUser(this).collect { it.role } as Set
	}

	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}

	protected void encodePassword() {
		password = springSecurityService.encodePassword(password)
	}
	
	
	String toString()  {
		return username? username: ""
	}
	
	
}
