package model;

/*
 * User class
 * 	- abstract
 */

public abstract class User {
	
	// ATTRIBUTES
	private String username; // final
	private String password;
	private String name;
	private String role; // final
	
	public static final String CLERK = "CLERK";
	public static final String MANAGER = "MANAGER";
	
	// METHODS
	/*
	 * User(String, String, String, String)
	 * 	- constructor method
	 */
	protected User(String username, String password, String name, String role) {
		
		this.username = username;
		this.password = password;
		this.name = name;
		this.role = role;
		
	} // end of constructor method
	
	/*
	 * GETTERS
	 * 	- getUsername()
	 * 	- getName()
	 * 	- getRole()
	 */
	public String getUsername() {return username;}
	public String getName() {return name;}
	public String getRole() {return role;}
	
	/*
	 * checkPassword(String)
	 * 	- abstract method that checks if user input is same as user's password
	 */
	protected boolean checkPassword(String input) {
		return input.equals(this.password);
	} // end of checkPassword
	
} // end of User class
