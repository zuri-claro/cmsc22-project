package app;

import model.Category;
import model.Clerk;
import model.Inventory;
import model.InventoryItem;
import model.Manager;
import model.Product;
import model.User;
import service.InventoryService;

import java.util.ArrayList;
import java.util.List;

/*
 * AgriSystem class
 * 	- required "System" class in project specs (i js renamed it to AgriSystem cuz java.System is already a thing)
 * 	- is a singleton, so only one instance can be created
 */

public class AgriSystem {
	
	// ATTRIBUTES
    private static AgriSystem instance; // only instance (for singleton class)

    private final List<User> users = new ArrayList<>();
    private final InventoryService inventoryService;
    private User currentUser;

    // METHODS
    /*
     * AgriSystem()
     * 	- constructor method
     */
    private AgriSystem() {

        Inventory inventory = new Inventory();
        this.addProducts(inventory);
        this.inventoryService = new InventoryService(inventory);
        
    } // end of constructor method
    
    /*
     * getInstance()
     * 	- necessary for singleton class
     */
    public static AgriSystem getInstance() {
        if (instance == null) {
            instance = new AgriSystem();
        } // end of if statement
        return instance;
    } // end of getInstance

    /*
     * GETTERS
     * 	- getUsers()
     * 	- getInventoryService()
     * 	- getCurrentUser()
     */
    public List<User> getUsers() {return users;}
    public InventoryService getInventoryService() {return inventoryService;}
    public User getCurrentUser() {return currentUser;}

    /*
     * addProducts(Inventory)
     * 	- tinatamad ako ya
     * 	- to put ALL our products so that our inventory is complete
     */
    private void addProducts(Inventory inventory) {
        add(inventory, 1, "Tomato Seeds", "delicious", 1, Category.SEEDS, 1, 1);
    } // end of addProducts
    
    /*
     * add(Inventory, int, String, String, double, Category, int, int)
     * 	- helper function for addProducts
     * 	- instantiates a Product object from the parameters and adds that product into the inventory
     */
    private void add(Inventory inventory, int id, String name, String description, double price, Category category, int stock, int threshold) {
        Product product = new Product(id, name, description, price, category);
        inventory.addItem(new InventoryItem(product, stock, threshold));
    } // end of add
    
} // end of AgriSystem class