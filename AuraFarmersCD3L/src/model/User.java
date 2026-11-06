package model;

/*
 * User class
 * 	- abstract
 */

public abstract class User {
	
	// ATTRIBUTES
	private final String username; // final
	private String password;
	private String displayName;
	private final Role role; // final
	
	// METHODS
	/*
	 * User(String, String, String, String)
	 * 	- constructor method
	 * 	- initializes username, password, displayName, role
	 */
	protected User(String username, String password, String displayName, Role role) {
		
		this.username = username;
		this.password = password;
		this.displayName = displayName;
		this.role = role;
		
	} // end of constructor method
	
	/*
	 * GETTERS
	 * 	- getUsername()
	 * 	- getName()
	 * 	- getRole()
	 */
	public String getUsername() {return username;}
	public String getDisplayName() {return displayName;}
	public String getRole() {return role.toString();}
	
	/*
	 * checkPassword(String)
	 * 	- abstract method that checks if user input is same as user's password
	 */
	protected boolean checkPassword(String input) {
		return input.equals(this.password);
	} // end of checkPassword
	
	/*
	 * canModifyInventory()
	 * 	- returns true for manager, false for clerk
	 */
	public abstract boolean canModifyInventory();
	
	/*
	 * toString()
	 * 	- returns a string containing the user's full name and their role
	 */
    @Override
    public String toString() {
        return this.displayName + " (" + role + ")";
    } // end of toString
	
} // end of User class
