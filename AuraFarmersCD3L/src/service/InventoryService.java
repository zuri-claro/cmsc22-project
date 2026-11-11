package service;

import java.util.ArrayList;
import java.util.List;

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
     * saveAll()
     * 	- saves changes to file manager
     */
    public void saveAll() {
    	
        fileManager.saveItems(inventory.getItems());
        fileManager.saveMovements(movements);
        
    } // end of saveAll

} // end of InventoryService class