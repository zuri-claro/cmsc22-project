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
     */
	public Product getProduct() {return product;}
	public int getCurrentStock() {return currentStock;}
	public int getLowStockThreshold() {return lowStockThreshold;}
	
	/*
	 * SETTERS
	 * 	- setCurrentStock()
	 * 	- setLowStockThreshold()
	 */
    public void setStock(int quantity) {currentStock = quantity;}
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