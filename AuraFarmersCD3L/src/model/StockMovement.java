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
    private final String reason;
    // might add: timestamp

    // METHODS
    /*
     * StockMovement(int, int, int, String, String)
     * 	- constructor method
     * 	- initializes all attributes
     */
    public StockMovement(int productId, int quantity, int stockAfter, String performedBy, String reason) {
        
        this.productId = productId;
        this.quantity = quantity;
        this.newStock = stockAfter;
        this.performedBy = performedBy;
        this.reason = reason;
        
    } // end of constructor method
    
    /*
     * GETTERS
     * 	- getProductId()
     * 	- getQuantityChange()
     * 	- getStockAfter()
     * 	- getReason()
     * 	- getPerformedBy()
     * 	- getReason()
     */
    public int getProductId() {return productId;}
    public int getQuantity() {return quantity;}
    public int getStockAfter() {return newStock;}
    public String getPerformedBy() {return performedBy;}	
    public String getReason() {return reason;}
	
} // end of StockMovement class
