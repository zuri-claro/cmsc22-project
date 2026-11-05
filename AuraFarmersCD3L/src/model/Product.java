package model;

/*
 * Clerk class
 * 	- extends User
 */

public class Product {
	
	// ATTRIBUTES
	private final int productId;
	private String name;
	private String description;
	private double price;
	private boolean status;
	// to add: category, units
	
	// METHODS
	/*
	 * Product(int, String, String, double)
	 * 	- constructor method
	 * 	- initializes productId, name, description, price
	 */
	public Product(int productId, String name, String description, double price) {
		
		this.productId = productId;
		this.name = name;
		this.description = description;
		this.price = price;
		this.status = true; // active by default
		
	} // end of constructor method
	
	/*
	 * SETTERS
	 * 	- setName(String)
	 * 	- setPrice(double)
	 * 	- setStatus(boolean)
	 */
	public void setName(String name) {this.name = name;}
	public void setPrice(double price) {this.price = price;}
	public void setStatus(boolean status) {this.status = status;}
	
	/*
	 * isActive()
	 * 	- returns whether product is active or inactive
	 */
	public boolean isActive() {return this.status;}

} // end of Product class
