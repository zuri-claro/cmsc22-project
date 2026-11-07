package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*
 * Inventory class
 * 	- collects all stock records
 */

public class Inventory {
	
	// ATTRIBUTES
	private final ObservableList<InventoryItem> items = FXCollections.observableArrayList();
	
	// METHODS
	
	/*
	 * GETTERS
	 * 	- getItems()
	 */
    public ObservableList<InventoryItem> getItems() {return items;}
	
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
    
    /*
     * getLowStockCount()
     * 	- returns how many items are currently low in stock
     */
    public int getLowStockCount() {
    	
        int count = 0;
        for (InventoryItem item : items) {
            if (item.isLowStock()) {
                count++;
            } // end of if statement
        } // end of for loop
        return count;
        
    } // end of getLowStockCount
    
    /*
     * getActiveProductCount()
     * - Counts all products that are currently active
     */
    public int getActiveProductCount() {

        int count = 0;

        for (InventoryItem item : items) {

            if (item.getProduct().isActive()) {
                count++;
            }

        }

        return count;

    } // end of getActiveProductCount


    /*
     * getOutOfStockCount()
     * - Counts active products that have no remaining stock
     */
    public int getOutOfStockCount() {

        int count = 0;

        for (InventoryItem item : items) {

            if (item.getProduct().isActive()
                    && item.getCurrentStock() <= 0) {

                count++;
            }

        }

        return count;

    } // end of getOutOfStockCount
	
} // end of Inventory class