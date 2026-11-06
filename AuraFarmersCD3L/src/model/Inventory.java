package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*
 * Inventory class
 */

public class Inventory {
	
	// ATTRIBUTES
	private final ObservableList<InventoryItem> items = FXCollections.observableArrayList();
	
	// METHODS
	/*
	 * addItem(InventoryItem)
	 * 	- adds item to ObservableList of items
	 */
    public void addItem(InventoryItem item) {
        items.add(item);
    } // end of addItem
    
    /*
     * findByProductId(int)
     * 	- returns the item (type InventoryItem) if productId exists in ObservableList
     */
    public InventoryItem findByProductId(int productId) {
    	
    	// iterate through ObservableList items
        for (InventoryItem item : items) {
        	// if productId matches an item
            if (item.getProduct().getProductId() == productId) {
                return item;
            } // end of if statement
        } // end of for loop
        
        return null;
        
    } // end of findByProductId
    
    // methods to add: getLowStockItems (returns list of items with low stock)
	
} // end of Inventory class