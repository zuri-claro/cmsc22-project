package model;

/*
 * StockMovement class
 * 	- this will track who changed which stock, by how much, why, etc.
 */

public class StockMovement {
	
	// ATTRIBUTES
	private final String movementId;
    private final String productId;
    private final int quantityChange;
    private final int newStock;
    private final String reason;
    private final String performedBy;
    // might add: timestamp

    // METHODS
    /*
     * StockMovement(String, String, int, int, String, String)
     * 	- constructor method
     * 	- initializes all attributes :P
     */
    public StockMovement(String movementId, String productId, int quantityChange, int stockAfter, String reason, String performedBy) {
        
    	this.movementId = movementId;
        this.productId = productId;
        this.quantityChange = quantityChange;
        this.newStock = stockAfter;
        this.reason = reason;
        this.performedBy = performedBy;
        
    } // end of constructor method
    
    /*
     * GETTERS
     * 	- getMovementId()
     * 	- getProductId()
     * 	- getQuantityChange()
     * 	- getStockAfter()
     * 	- getReason()
     * 	- getPerformedBy()
     */
    public String getMovementId() { return movementId; }
    public String getProductId() { return productId; }
    public int getQuantityChange() { return quantityChange; }
    public int getStockAfter() { return newStock; }
    public String getReason() { return reason; }
    public String getPerformedBy() { return performedBy; }	
	
} // end of StockMovement class
