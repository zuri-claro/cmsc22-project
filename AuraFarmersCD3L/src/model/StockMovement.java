package model;

/*
 * StockMovement class
 * 	- this will track who changed which stock, by how much, why, etc.
 */

public class StockMovement {
	
	// ATTRIBUTES
    private final int productId;
    private final int quantity;
    private final int newStock;
    private final String performedBy;
    // might add: timestamp

    // METHODS
    /*
     * StockMovement(String, int, int, int, String, String)
     * 	- constructor method
     * 	- initializes all attributes :P
     */
    public StockMovement(int productId, int quantity, int stockAfter, String performedBy) {
        
        this.productId = productId;
        this.quantity = quantity;
        this.newStock = stockAfter;
        this.performedBy = performedBy;
        
    } // end of constructor method
    
    /*
     * GETTERS
     * 	- getProductId()
     * 	- getQuantityChange()
     * 	- getStockAfter()
     * 	- getReason()
     * 	- getPerformedBy()
     */
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public int getStockAfter() { return newStock; }
    public String getPerformedBy() { return performedBy; }	
	
} // end of StockMovement class
