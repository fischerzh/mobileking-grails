package ch.freebo

class User {

	transient springSecurityService

	String username
	String password
	boolean enabled
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired

	static constraints = {
		username blank: false, unique: true
		password blank: false
		loyaltyCards nullable:true
		shoppings nullable:true
		loyaltyPrograms nullable:true
	}
	
	static hasMany = [loyaltyCards:LoyaltyCard, loyaltyPrograms:LoyaltyProgram, shoppings: Shopping]

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
