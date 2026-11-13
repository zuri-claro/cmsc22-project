package service;

import java.util.ArrayList;
import java.util.List;

import model.Category;
import model.Inventory;
import model.InventoryItem;
import model.Product;
import model.StockMovement;
import model.User;

/*
 * ProductService class
 * 	- real logic for product management (the product catalog)
 * 	- add a product, edit a product, activate/deactivate a product, search and filter products
 * 	- only a Manager can add, edit, or deactivate (checked here, not just by hiding buttons)
 * 	- every change is saved right away through InventoryService
 * 	- products are never deleted, only deactivated, so old transactions can still refer to them
 * 	- rule violations are thrown as exceptions with a message that is safe to show the user:
 * 		SecurityException: the user is not allowed
 * 		IllegalArgumentException: a value is missing or invalid
 * 	- stock numbers and the low-stock threshold are changed through InventoryService
 * 		(restock, adjust, setThreshold), not here
 */

public class ProductService {
	
	// ATTRIBUTES
	private final InventoryService inventoryService;
	private final Inventory inventory;
	
	// METHODS
	/*
	 * ProductService(InventoryService)
	 * 	- constructor method
	 * 	- uses the same inventory (and the same saving) as InventoryService,
	 * 		so products added here immediately show up everywhere
	 */
	public ProductService(InventoryService inventoryService) {
		
		this.inventoryService = inventoryService;
		this.inventory = inventoryService.getInventory();
		
	} // end of constructor method
	
	/*
	 * addProduct(User, String, String, double, Category, String, int, int)
	 * 	- Manager only
	 * 	- checks every value, gives the product the next free id, then creates the Product
	 * 		and its InventoryItem (starting stock and low-stock threshold)
	 * 	- if the starting stock is more than 0, it is recorded as a stock movement ("Initial stock")
	 * 	- returns the new product
	 * 	- possible errors: user is not a Manager, empty name or unit, price of 0 or less,
	 * 		no category, negative stock or threshold, another product already has that name
	 */
	public Product addProduct(User user, String name, String description, double price,
			Category category, String unit, int initialStock, int lowStockThreshold) {
		
		requireManager(user, "add products");
		validateDetails(name, price, category, unit);
		if (initialStock < 0) {
			throw new IllegalArgumentException("Starting stock cannot be negative.");
		} // end of if statement
		if (lowStockThreshold < 0) {
			throw new IllegalArgumentException("Low-stock threshold cannot be negative.");
		} // end of if statement
		
		String cleanName = name.trim();
		requireUniqueName(cleanName, -1); // -1: there is no existing product to ignore
		
		Product product = new Product(nextProductId(), cleanName, cleanText(description),
				roundToCentavos(price), category, true, unit.trim());
		inventory.addItem(new InventoryItem(product, initialStock, lowStockThreshold));
		
		if (initialStock > 0) {
			inventoryService.getMovements().add(new StockMovement(
					product.getProductId(), initialStock, initialStock, user.getUsername(), "Initial stock"));
		} // end of if statement
		
		inventoryService.saveAll();
		return product;
		
	} // end of addProduct
	
	/*
	 * updateProduct(User, int, String, String, double, Category, String)
	 * 	- Manager only
	 * 	- changes the name, description, price, category, and unit of an existing product
	 * 	- stock and threshold are NOT changed here
	 * 	- everything is checked first, so a bad value never leaves the product half-edited
	 * 	- possible errors: same as addProduct, plus unknown product id
	 */
	public void updateProduct(User user, int productId, String name, String description, double price,
			Category category, String unit) {
		
		requireManager(user, "edit products");
		InventoryItem item = findOrThrow(productId);
		validateDetails(name, price, category, unit);
		
		String cleanName = name.trim();
		requireUniqueName(cleanName, productId); // this product may keep its own name
		
		Product product = item.getProduct();
		product.setName(cleanName);
		product.setDescription(cleanText(description));
		product.setPrice(roundToCentavos(price));
		product.setCategory(category);
		product.setUnit(unit.trim());
		
		inventoryService.saveAll();
		
	} // end of updateProduct
	
	/*
	 * setProductActive(User, int, boolean)
	 * 	- Manager only
	 * 	- false: deactivates the product (no longer sold, hidden from the active catalog)
	 * 	- true: puts it back on sale
	 * 	- possible errors: user is not a Manager, unknown product id
	 */
	public void setProductActive(User user, int productId, boolean active) {
		
		requireManager(user, "activate or deactivate products");
		InventoryItem item = findOrThrow(productId);
		item.getProduct().setStatus(active);
		inventoryService.saveAll();
		
	} // end of setProductActive
	
	/*
	 * findItem(int)
	 * 	- returns the inventory item (product + stock) with that id, or null if there is none
	 */
	public InventoryItem findItem(int productId) {
		return inventory.findByProductId(productId);
	} // end of findItem
	
	/*
	 * search(String, Category, boolean)
	 * 	- returns the items that match ALL of the given filters
	 * 	- text: matches part of the product's name, description, or id (e.g. "tomato", "seeds", "AG001");
	 * 		null or empty means any text
	 * 	- category: only that category; null means all categories
	 * 	- includeInactive: false = only active products (what a Clerk should see),
	 * 		true = also inactive ones (what the Manager sees)
	 */
	public List<InventoryItem> search(String text, Category category, boolean includeInactive) {
		
		String query = text == null ? "" : text.trim().toLowerCase();
		List<InventoryItem> results = new ArrayList<>();
		
		for (InventoryItem item : inventory.getItems()) {
			
			Product product = item.getProduct();
			
			if (!includeInactive && !product.isActive()) {
				continue;
			} // end of if statement
			if (category != null && product.getCategory() != category) {
				continue;
			} // end of if statement
			if (!query.isEmpty() && !matches(product, query)) {
				continue;
			} // end of if statement
			
			results.add(item);
			
		} // end of for loop
		
		return results;
		
	} // end of search
	
	/*
	 * matches(Product, String)
	 * 	- private helper: true if the (lower-case) query is part of the product's name, description, or id
	 */
	private boolean matches(Product product, String query) {
		
		return lower(product.getName()).contains(query)
				|| lower(product.getDescription()).contains(query)
				|| lower(product.getDisplayId()).contains(query);
		
	} // end of matches
	
	/*
	 * requireManager(User, String)
	 * 	- throws SecurityException unless the user is a Manager
	 * 	- uses canModifyInventory(), which is true only for a Manager
	 */
	private void requireManager(User user, String action) {
		
		if (user == null || !user.canModifyInventory()) {
			throw new SecurityException("Only a Manager can " + action + ".");
		} // end of if statement
		
	} // end of requireManager
	
	/*
	 * validateDetails(String, double, Category, String)
	 * 	- throws IllegalArgumentException with a friendly message if a value is not acceptable
	 */
	private void validateDetails(String name, double price, Category category, String unit) {
		
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("Product name cannot be empty.");
		} // end of if statement
		if (category == null) {
			throw new IllegalArgumentException("Please choose a category.");
		} // end of if statement
		if (unit == null || unit.trim().isEmpty()) {
			throw new IllegalArgumentException("Unit cannot be empty (e.g. Packet, Bag, Piece).");
		} // end of if statement
		if (Double.isNaN(price) || Double.isInfinite(price) || price <= 0) {
			throw new IllegalArgumentException("Price must be greater than zero.");
		} // end of if statement
		
	} // end of validateDetails
	
	/*
	 * requireUniqueName(String, int)
	 * 	- throws IllegalArgumentException if a DIFFERENT product already has this name
	 * 	- ignoreProductId is the product being edited (so it can keep its own name); use -1 when adding
	 */
	private void requireUniqueName(String name, int ignoreProductId) {
		
		for (InventoryItem item : inventory.getItems()) {
			
			Product other = item.getProduct();
			if (other.getProductId() != ignoreProductId && other.getName().equalsIgnoreCase(name)) {
				throw new IllegalArgumentException("A product named \"" + other.getName() + "\" already exists.");
			} // end of if statement
			
		} // end of for loop
		
	} // end of requireUniqueName
	
	/*
	 * findOrThrow(int)
	 * 	- returns the inventory item with that product id, or throws IllegalArgumentException
	 */
	private InventoryItem findOrThrow(int productId) {
		
		InventoryItem item = inventory.findByProductId(productId);
		if (item == null) {
			throw new IllegalArgumentException("Product not found: " + productId);
		} // end of if statement
		return item;
		
	} // end of findOrThrow
	
	/*
	 * nextProductId()
	 * 	- returns one more than the biggest product id in use (1 if there are no products yet)
	 */
	private int nextProductId() {
		
		int highest = 0;
		for (InventoryItem item : inventory.getItems()) {
			highest = Math.max(highest, item.getProduct().getProductId());
		} // end of for loop
		return highest + 1;
		
	} // end of nextProductId
	
	/*
	 * cleanText(String)
	 * 	- returns the trimmed text, or "" if it is null
	 */
	private String cleanText(String text) {
		return text == null ? "" : text.trim();
	} // end of cleanText
	
	/*
	 * lower(String)
	 * 	- returns the text in lower case, or "" if it is null
	 */
	private String lower(String text) {
		return text == null ? "" : text.toLowerCase();
	} // end of lower
	
	/*
	 * roundToCentavos(double)
	 * 	- rounds a peso amount to 2 decimal places
	 */
	private double roundToCentavos(double amount) {
		return Math.round(amount * 100.0) / 100.0;
	} // end of roundToCentavos
	
} // end of ProductService class