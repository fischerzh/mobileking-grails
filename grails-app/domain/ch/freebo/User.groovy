package ch.freebo

class User {

	transient springSecurityService

	String username
	String password
	Boolean isActiveApp
	boolean enabled
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired

	static constraints = {
		username blank: false, unique: true
		password blank: false
		isActiveApp nullable:true
		loyaltyCards nullable:true
		shoppings nullable:true
//		loyaltyPrograms nullable:true
	}
	
	static hasMany = [loyaltyCards:LoyaltyCard, shoppings: Shopping] //loyaltyPrograms:LoyaltyProgram, 

	static mapping = {
		password column: '`password`'
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
