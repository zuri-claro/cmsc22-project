package model;

/*
 * Clerk class
 * 	- extends User
 */

public class Clerk extends User{

	// METHODS
	/*
	 * Clerk(String, String, String)
	 * 	- constructor method
	 * 	- initializes username, password, displayName
	 */
	public Clerk(String username, String password, String fullName) {
		super(username, password, fullName, User.CLERK);
	} // end of constructor method
	
} // end of Clerk class