package service;

import java.util.ArrayList;
import java.util.List;

import model.Inventory;
import model.InventoryItem;
import model.StockMovement;
import model.User;

/*
 * InventoryService class
 * 	- real logic for inventory
 */

public class InventoryService {
	
	// ATTRIBUTES
	private final Inventory inventory;
	private final List<StockMovement> movements = new ArrayList<>();
	
	// METHODS
	/*
	 * InventoryService(Inventory)
	 * 	- constructor method
	 * 	- initializes inventory attribute
	 */
    public InventoryService(Inventory inventory) {
        this.inventory = inventory;
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
	public String restock(User user, int productId, int quantity) {
		
		// --- to change to error exception instead of js return string ---
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
        movements.add(new StockMovement(productId, quantity, item.getCurrentStock(), user.getUsername()));
        return null;
        
    } // end of restock

} // end of InventoryService class