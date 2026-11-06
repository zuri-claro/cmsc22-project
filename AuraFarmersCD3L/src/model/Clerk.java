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
		super(username, password, fullName, Role.CLERK);
	} // end of constructor method
	
	/*
	 * canModifyInventory()
	 * 	- returns false because class is clerk
	 */
    @Override
    public boolean canModifyInventory() {return false;}
	
} // end of Clerk class