package model;

/*
 * Manager class
 * 	- extends User
 * 	- final class
 */

public final class Manager extends User{

	// METHODS
	/*
	 * Manager(String, String, String)
	 * 	- constructor method
	 * 	- initializes username, password, displayName
	 */
	public Manager(String username, String password, String fullName) {
		super(username, password, fullName, User.MANAGER);
	} // end of constructor method
	
	/*
	 * canModifyInventory()
	 * 	- returns true because class is manager
	 */
    @Override
    public boolean canModifyInventory() {return true;}
	
} // end of Manager class