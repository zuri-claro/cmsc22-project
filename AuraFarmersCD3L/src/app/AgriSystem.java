package app;

import model.Category;
import model.Clerk;
import model.Inventory;
import model.InventoryItem;
import model.Manager;
import model.Product;
import model.Role;
import model.User;
import persistence.InventoryFileManager;
import service.InventoryService;
import service.ProductService;

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

    private List<User> users = new ArrayList<>();
    private final InventoryService inventoryService;
    private final ProductService productService;
    private User currentUser;

    // METHODS
    
    // methods to add: sign up (creates account)
    
    /*
     * AgriSystem()
     * 	- constructor method
     */
    private AgriSystem() {
    	
        InventoryFileManager fileManager = new InventoryFileManager("data");
        Inventory inventory = new Inventory();
        
        // check if file is empty
        if (fileManager.hasSavedItems()) { // if the file isnt empty, load what was saved before
            for (InventoryItem item : fileManager.loadItems()) {
                inventory.addItem(item);
            } // end of for loop
            
        } else { // if empty, just add products
        	this.addProducts(inventory);
            fileManager.saveItems(inventory.getItems());
            
        } // end of if statement

        inventoryService = new InventoryService(inventory, fileManager);
        productService = new ProductService(inventoryService);
        
        // TEMPORARY FOR TESTINGGG - add users
        User manager = new Manager("manager","manager123","m");
        User clerk = new Clerk("clerk","clerk123","c");
        users.add(manager);
        users.add(clerk);
        
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
     * - getProductService()
     */
    public List<User> getUsers() {return users;}
    public InventoryService getInventoryService() {return inventoryService;}
    public User getCurrentUser() {return currentUser;}
    public ProductService getProductService() {return productService;}

    /*
     * login(String, String)
     * 	- returns User if that username and password is in users
     * 	- null if not
     */
    public User login(String username, String password) {
    	
        for (User user : users) {
            if (user.getUsername().equals(username) && user.checkPassword(password)) {
                currentUser = user;
                return user;
            } // end of if statement
        } // end of for loop
        return null;
        
    } // end of login()
    
    /*
     * addProducts(Inventory)
     * 	- hardcoded all products (the 17 products from the business proposal)
     * 	- by default, all stock is 0 and the low stock threshold is 5
     */
    private void addProducts(Inventory inventory) {
    	//  inventory, id, name, description, price, category, unit, stock, threshold
    	add(inventory, 1, "Tomato Seeds", "Seeds for growing tomato plants", 35.0, Category.SEEDS, "Packet", 0, 5);
    	add(inventory, 2, "Pechay Seeds", "Seeds for growing pechay", 25.0, Category.SEEDS, "Packet", 0, 5);
    	add(inventory, 3, "Sweet Corn Seeds", "Seeds for growing sweet corn", 50.0, Category.SEEDS, "Packet", 0, 5);
    	add(inventory, 4, "Organic Compost", "Organic material for improving soil conditions", 120.0, Category.FERTILIZERS_SOIL, "Bag", 0, 5);
    	add(inventory, 5, "Complete Fertilizer", "Fertilizer for general plant nutrient needs", 180.0, Category.FERTILIZERS_SOIL, "Bag", 0, 5);
    	add(inventory, 6, "Vermicast", "Earthworm-produced soil amendment", 100.0, Category.FERTILIZERS_SOIL, "Bag", 0, 5);
    	add(inventory, 7, "General-Purpose Insecticide", "Product intended to control specified insect pests", 150.0, Category.PESTICIDES, "Pack", 0, 5);
    	add(inventory, 8, "Garden Fungicide", "Product intended to control specified fungal diseases", 180.0, Category.PESTICIDES, "Pack", 0, 5);
    	add(inventory, 9, "Garden Herbicide", "Product intended to control specified weeds", 200.0, Category.PESTICIDES, "Bottle", 0, 5);
    	add(inventory, 10, "Hand Trowel", "Small tool for digging and transplanting", 100.0, Category.HAND_TOOLS, "Piece", 0, 5);
    	add(inventory, 11, "Pruning Shears", "Tool for trimming small branches and stems", 180.0, Category.HAND_TOOLS, "Piece", 0, 5);
    	add(inventory, 12, "Hand Cultivator", "Tool for loosening soil and removing weeds", 120.0, Category.HAND_TOOLS, "Piece", 0, 5);
    	add(inventory, 13, "Watering Can", "Container used for watering plants", 150.0, Category.WATERING, "Piece", 0, 5);
    	add(inventory, 14, "Hose Nozzle", "Attachment for controlling water flow", 90.0, Category.WATERING, "Piece", 0, 5);
    	add(inventory, 15, "Seedling Tray", "Tray for starting seeds and growing seedlings", 80.0, Category.PLANTING_MATERIALS, "Piece", 0, 5);
    	add(inventory, 16, "Nursery Pots", "Containers for growing young plants", 10.0, Category.PLANTING_MATERIALS, "Piece", 0, 5);
    	add(inventory, 17, "Plant Labels", "Labels for identifying plant varieties", 30.0, Category.PLANTING_MATERIALS, "Pack", 0, 5);
    } // end of addProducts
    
    /*
     * add(Inventory, int, String, String, double, Category, String, int, int)
     * 	- helper function for addProducts
     * 	- instantiates a Product object from the parameters and adds that product into the inventory
     */
    private void add(Inventory inventory, int id, String name, String description, double price, Category category, String unit, int stock, int threshold) {
        Product product = new Product(id, name, description, price, category, true, unit);
        inventory.addItem(new InventoryItem(product, stock, threshold));
    } // end of add
    
} // end of AgriSystem class