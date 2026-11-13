package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.Inventory;
import model.InventoryItem;
import model.StockMovement;
import model.User;
import persistence.InventoryFileManager;

/*
 * InventoryService class
 * 	- real logic for inventory
 */

public class InventoryService {
	
	// ATTRIBUTES
	private final Inventory inventory;
    private final InventoryFileManager fileManager;
    private final List<StockMovement> movements;
	
	// METHODS
	/*
	 * InventoryService(Inventory, InventoryFileManager)
	 * 	- constructor method
	 * 	- initializes inventory and fileManager
	 */
    public InventoryService(Inventory inventory, InventoryFileManager fileManager) {
    	
        this.inventory = inventory;
        this.fileManager = fileManager;
        this.movements = fileManager.loadMovements();
        
    } // end of constructor method
    
    /*
     * GETTERS
     * 	- getInventory()
     * 	- getMovements()
     */
	public Inventory getInventory() {return inventory;}
	public List<StockMovement> getMovements() {return movements;}
	
	/*
	 * restock(User, int, int, String)
	 * 	- allows manager to add stock to an existing product in the inventory
	 * 	- possible errors: null user, user is not manager, quantity is invalid, item not in inventory
	 */
	public String restock(User user, int productId, int quantity, String reason) {
		
		// error: if user is null or is a clerk (can't modify inventory)
        if (user == null || !user.canModifyInventory()) {
            return "Only a manager can modify the inventory.";
        } // end of if statement
        
        // error: quantity cannot be 0 or negative
        if (quantity <= 0) {
            return "Quantity must be greater than zero.";
        } // end of if statement
        
        // search for item in inventory
        InventoryItem item = inventory.findByProductId(productId);
        
        // error: item not found
        if (item == null) {
            return "Product not found: " + productId;
        } // end of if statement
        
        // if successful, set that stock item
        item.setCurrentStock(item.getCurrentStock() + quantity);
        movements.add(new StockMovement(productId, quantity, item.getCurrentStock(), user.getUsername(), reason));
        saveAll();
        return null;
        
    } // end of restock
	
	/*
	 * adjust(User, int, int)
	 * 	- allows manager to override (completely change, not just add) stock of product in inventory
	 * 	- possible errors: null user, user is not manager, quantity is invalid, item not in inventory
	 */
    public String adjust(User user, int productId, int newQuantity, String reason) {
    	
		// --- to change to error exception instead of js return string ---
		// error: if user is null or is a clerk (can't modify inventory)
        if (user == null || !user.canModifyInventory()) {
            return "Only a manager can modify the inventory.";
        } // end of if statement
        
        // error: quantity cannot be negative
        if (newQuantity < 0) {
            return "Stock cannot be negative.";
        } // end of if statement

        // search for item in inventory
        InventoryItem item = inventory.findByProductId(productId);
        
        // error: item not found
        if (item == null) {
            return "Product not found: " + productId;
        } // end of if statement
        
        // update stock of item
        int change = newQuantity - item.getCurrentStock();
        item.setCurrentStock(newQuantity);
        movements.add(new StockMovement(productId, change, item.getCurrentStock(), user.getUsername(), reason));
        saveAll();
        return null;
        
    } // end of adjust
    
    /*
     * setThreshold(User, int, int)
     * 	- lets manager set threshold
     */
    public String setThreshold(User user, int productId, int threshold) {
    		
    	// error: user not allowed to change inventory OR invalid threshold
        if (user == null || !user.canModifyInventory()) {
            return "Only a Manager can change inventory.";
        } // end of if statement
        if (threshold < 0) {
            return "Threshold cannot be negative.";
        } // end of if statement
        
        InventoryItem item = inventory.findByProductId(productId);
        if (item == null) {
            return "Unknown product: " + productId;
        } // end of if statement

        item.setLowStockThreshold(threshold);
        saveAll();
        return null;
        
    } // end of setThreshold
    
    /*
     * hasEnoughStock(int, int)
     * 	- returns true if the product exists and has at least that many units on hand
     * 	- handy for checking one item while it is being added to a cart, before checkout
     */
    public boolean hasEnoughStock(int productId, int quantity) {
    	
        InventoryItem item = inventory.findByProductId(productId);
        return item != null && quantity > 0 && quantity <= item.getCurrentStock();
        
    } // end of hasEnoughStock
    
    /*
     * deductForSale(User, Map<Integer, Integer>, String)
     * 	- lowers the stock of every product in a sale (called by the transaction logic after a sale)
     * 	- quantities maps productId -> units sold, e.g. {1=2, 5=1} means 2 of product 1 and 1 of product 5
     * 	- reference is written in the stock movement reason, e.g. "Sale TXN-0001"
     * 	- ALL OR NOTHING: every item is checked first, so if even one is invalid or short on stock,
     * 		an exception is thrown and NO stock changes at all
     * 	- does not decide who may sell, the transaction logic checks that
     * 	- possible errors:
     * 		IllegalArgumentException: null user, empty list, unknown product, quantity of 0 or less, inactive product
     * 		IllegalStateException: not enough stock for a product
     */
    public void deductForSale(User user, Map<Integer, Integer> quantities, String reference) {
    	
        checkStockChange(user, quantities);
        
        // PASS 1: check everything before changing anything
        for (Map.Entry<Integer, Integer> entry : quantities.entrySet()) {
        	
            InventoryItem item = inventory.findByProductId(entry.getKey());
            int quantity = entry.getValue();
            
            if (!item.getProduct().isActive()) {
                throw new IllegalArgumentException(item.getProduct().getName() + " is inactive and cannot be sold.");
            } // end of if statement
            if (quantity > item.getCurrentStock()) {
                throw new IllegalStateException("Not enough stock for " + item.getProduct().getName()
                        + ": requested " + quantity + ", available " + item.getCurrentStock() + ".");
            } // end of if statement
            
        } // end of for loop
        
        // PASS 2: everything is fine, so now lower the stock and record each movement
        for (Map.Entry<Integer, Integer> entry : quantities.entrySet()) {
        	
            InventoryItem item = inventory.findByProductId(entry.getKey());
            int quantity = entry.getValue();
            
            item.setCurrentStock(item.getCurrentStock() - quantity);
            movements.add(new StockMovement(entry.getKey(), -quantity, item.getCurrentStock(), user.getUsername(), reference));
            
        } // end of for loop
        saveAll();
        
    } // end of deductForSale
    
    /*
     * restoreForVoid(User, Map<Integer, Integer>, String)
     * 	- puts back the stock of a voided sale (called by the transaction logic when a sale is voided)
     * 	- quantities maps productId -> units to put back; reference is e.g. "Void TXN-0001"
     * 	- works even if the product has been deactivated since the sale
     * 	- ALL OR NOTHING, like deductForSale
     * 	- possible errors: IllegalArgumentException for null user, empty list, unknown product, quantity of 0 or less
     */
    public void restoreForVoid(User user, Map<Integer, Integer> quantities, String reference) {
    	
        checkStockChange(user, quantities);
        
        for (Map.Entry<Integer, Integer> entry : quantities.entrySet()) {
        	
            InventoryItem item = inventory.findByProductId(entry.getKey());
            int quantity = entry.getValue();
            
            item.setCurrentStock(item.getCurrentStock() + quantity);
            movements.add(new StockMovement(entry.getKey(), quantity, item.getCurrentStock(), user.getUsername(), reference));
            
        } // end of for loop
        saveAll();
        
    } // end of restoreForVoid
    
    /*
     * checkStockChange(User, Map<Integer, Integer>)
     * 	- private helper shared by deductForSale and restoreForVoid
     * 	- throws IllegalArgumentException if the user is null, there are no items,
     * 		or any product is unknown or has a quantity of 0 or less
     */
    private void checkStockChange(User user, Map<Integer, Integer> quantities) {
    	
        if (user == null) {
            throw new IllegalArgumentException("A user is required to change stock.");
        } // end of if statement
        if (quantities == null || quantities.isEmpty()) {
            throw new IllegalArgumentException("There are no items to update.");
        } // end of if statement
        
        for (Map.Entry<Integer, Integer> entry : quantities.entrySet()) {
        	
            if (entry.getKey() == null || inventory.findByProductId(entry.getKey()) == null) {
                throw new IllegalArgumentException("Product not found: " + entry.getKey());
            } // end of if statement
            if (entry.getValue() == null || entry.getValue() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero.");
            } // end of if statement
            
        } // end of for loop
        
    } // end of checkStockChange
    
    /*
     * saveAll()
     * 	- saves changes to file manager
     */
    public void saveAll() {
    	
        fileManager.saveItems(inventory.getItems());
        fileManager.saveMovements(movements);
        
    } // end of saveAll

} // end of InventoryService class