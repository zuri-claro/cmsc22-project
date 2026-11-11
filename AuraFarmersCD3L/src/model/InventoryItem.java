package model;

/*
 * InventoryItem class
 */

public class InventoryItem {
	
	// ATTRIBUTES
    private final Product product;
    private int currentStock;
    private int lowStockThreshold;
    
    // METHODS
    /*
     * InventoryItem(Product, int, int)
     * 	- constructor method
     * 	- initializes product, currentStock, and lowStockThreshold
     */
    public InventoryItem(Product product, int currentStock, int lowStockThreshold) {

        this.product = product;
        this.currentStock = currentStock;
        this.lowStockThreshold = lowStockThreshold;
        
    } // end of constructor method

    /*
     * GETTERS
     * 	- getProduct()
     * 	- getCurrentStock()
     * 	- getLowStockThreshold()
	 * 	- getProductId()
	 * 	- getName()
	 * 	- getCategory()
	 * 	- getStatus()
     */
	public Product getProduct() {return product;}
	public int getCurrentStock() {return currentStock;}
	public int getLowStockThreshold() {return lowStockThreshold;}
	
	public int getProductId() {return product.getProductId();}
	public String getName() {return product.getName();}
	public Category getCategory() {return product.getCategory();}
	public String getStatus() {
		if (!product.isActive()) {
			return "Inactive";
		} else if (currentStock <= 0) {
			return "Out of Stock";
		} else if (isLowStock()) {
			return "Low Stock";
		} else {
			return "Available";
		} // end of if else statement
	} // end of getStatus
	
	/*
	 * SETTERS
	 * 	- setCurrentStock()
	 * 	- setLowStockThreshold()
	 */
    public void setCurrentStock(int quantity) {currentStock = quantity;}
    public void setLowStockThreshold(int threshold) {lowStockThreshold = threshold;}
    
	/*
	 * isLowStock()
	 * 	- if the inventory item's current stock of that product reached the threshold for low stock
	 */
    public boolean isLowStock() {
        return this.currentStock <= this.lowStockThreshold;
    } // end of isLowStock
    
    /*
     * addStock(int)
     * 	- adds the int quantity to currentStock
     */
    public void addStock(int quantity) {
        this.currentStock += quantity;
    } // end of addStock
    
} // end of InventoryItem class