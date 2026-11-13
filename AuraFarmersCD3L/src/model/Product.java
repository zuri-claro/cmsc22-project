package model;

/*
 * Product class
 * 	- an item sold by the store (e.g. Tomato Seeds, Hand Trowel)
 * 	- how many are in stock is NOT stored here, that is kept in InventoryItem
 * 	- this class only holds the data; the rules (valid price, Manager-only edits, etc.)
 * 		are checked in ProductService
 */

public class Product {
	
	// ATTRIBUTES
	private static final String DEFAULT_UNIT = "Piece"; // used when no unit is given
	
	private final int productId;
	private String name;
	private String description;
	private double price;
	private boolean isActive;
	private Category category;
	private String unit; // unit of measurement, e.g. "Packet", "Bag", "Piece"
	
	// METHODS
	/*
	 * Product(int, String, String, double, Category)
	 * 	- constructor method
	 * 	- initializes productId, name, description, price, category
	 * 	- active by default, unit is "Piece"
	 */
	public Product(int productId, String name, String description, double price, Category category) {
		this(productId, name, description, price, category, true, DEFAULT_UNIT);
	} // end of constructor method
	
	/*
	 * Product(int, String, String, double, Category, boolean)
	 * 	- constructor method only used if not active by default
	 * 	- initializes productId, name, description, price, category, isActive
	 * 	- unit is "Piece"
	 */
	public Product(int productId, String name, String description, double price, Category category, boolean active) {
		this(productId, name, description, price, category, active, DEFAULT_UNIT);
	} // end of constructor method
	
	/*
	 * Product(int, String, String, double, Category, boolean, String)
	 * 	- constructor method that sets every attribute, including the unit of measurement
	 * 	- the other constructors call this one
	 */
	public Product(int productId, String name, String description, double price, Category category, boolean active, String unit) {
		
		this.productId = productId;
		this.name = name;
		this.description = description;
		this.price = price;
		this.category = category;
		this.isActive = active;
		this.unit = (unit == null || unit.trim().isEmpty()) ? DEFAULT_UNIT : unit.trim();
		
	} // end of constructor method
	
	/*
	 * SETTERS
	 * 	- setName(String)
	 * 	- setDescription(String)
	 * 	- setPrice(double)
	 * 	- setCategory(Category)
	 * 	- setUnit(String)
	 * 	- setStatus(boolean)
	 */
	public void setName(String name) {this.name = name;}
	public void setDescription(String description) {this.description = description;}
	public void setPrice(double price) {this.price = price;}
	public void setCategory(Category category) {this.category = category;}
	public void setUnit(String unit) {this.unit = unit;}
	public void setStatus(boolean status) {this.isActive = status;}
	
	/*
	 * GETTERS
	 * 	- getProductId()
	 * 	- getName()
	 * 	- getDescription()
	 * 	- getPrice()
	 * 	- getCategory()
	 * 	- getUnit()
	 */
	public int getProductId() {return productId;}
	public String getName() {return name;}
	public String getDescription() {return description;}
	public double getPrice() {return price;}
	public Category getCategory() {return category;}
	public String getUnit() {return unit;}
	
	/*
	 * isActive()
	 * 	- returns whether product is active or inactive
	 */
	public boolean isActive() {return this.isActive;}
	
	/*
	 * getDisplayId()
	 * 	- returns the id the way the business proposal writes it, e.g. 1 becomes "AG001"
	 */
	public String getDisplayId() {
		return String.format("AG%03d", productId);
	} // end of getDisplayId

	/*
	 * toString()
	 * 	- returns string containing product id and product name
	 */
    @Override
    public String toString() {
        return productId + " - " + name;
    } // end of toString

} // end of Product class