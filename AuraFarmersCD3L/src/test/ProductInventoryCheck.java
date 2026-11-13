package test;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.Category;
import model.Clerk;
import model.Inventory;
import model.InventoryItem;
import model.Manager;
import model.Product;
import model.StockMovement;
import persistence.InventoryFileManager;
import service.InventoryService;
import service.ProductService;

/*
 * ProductInventoryCheck class
 * 	- console test for the product and inventory logic (no GUI, no JUnit needed)
 * 	- run it as a normal Java application and look for "FAIL" lines
 * 	- it saves into a temporary folder, so it never touches the real "data" folder
 */

public class ProductInventoryCheck {
	
	// a block of code that is expected to throw an exception
	interface Action {void run();}
	
	static int passed = 0;
	static int failed = 0;
	
	/*
	 * check(String, boolean)
	 * 	- prints PASS or FAIL for one test
	 */
	static void check(String name, boolean condition) {
		if (condition) {
			passed++;
			System.out.println("PASS  " + name);
		} else {
			failed++;
			System.out.println("FAIL  " + name);
		} // end of if else statement
	} // end of check
	
	/*
	 * expectThrows(String, Class, Action)
	 * 	- passes only if the action throws that kind of exception
	 */
	static void expectThrows(String name, Class<? extends RuntimeException> type, Action action) {
		try {
			action.run();
			failed++;
			System.out.println("FAIL  " + name + " (no exception thrown)");
		} catch (RuntimeException e) {
			check(name, type.isInstance(e));
		} // end of try catch
	} // end of expectThrows
	
	/*
	 * buildService(InventoryFileManager)
	 * 	- a fresh inventory with 3 products: 1 Tomato Seeds (stock 10), 2 Pechay Seeds (stock 3), 3 Hand Trowel (stock 0)
	 */
	static InventoryService buildService(InventoryFileManager fm) {
		Inventory inventory = new Inventory();
		inventory.addItem(new InventoryItem(new Product(1, "Tomato Seeds", "Seeds for growing tomato plants", 35.0, Category.SEEDS, true, "Packet"), 10, 5));
		inventory.addItem(new InventoryItem(new Product(2, "Pechay Seeds", "Seeds for growing pechay", 25.0, Category.SEEDS, true, "Packet"), 3, 5));
		inventory.addItem(new InventoryItem(new Product(3, "Hand Trowel", "Small tool for digging", 100.0, Category.HAND_TOOLS), 0, 5));
		fm.saveItems(inventory.getItems());
		return new InventoryService(inventory, fm);
	} // end of buildService
	
	public static void main(String[] args) throws Exception {
		
		File folder = Files.createTempDirectory("agri_check").toFile();
		InventoryFileManager fm = new InventoryFileManager(folder.getPath());
		InventoryService inv = buildService(fm);
		ProductService products = new ProductService(inv);
		Inventory inventory = inv.getInventory();
		
		Manager manager = new Manager("manager", "manager123", "Mgr");
		Clerk clerk = new Clerk("clerk", "clerk123", "Clk");
		
		// ----- Product basics -----
		Product trowel = inventory.findByProductId(3).getProduct();
		check("default unit is Piece", trowel.getUnit().equals("Piece"));
		check("display id is AG003", trowel.getDisplayId().equals("AG003"));
		check("unit given in constructor is kept", inventory.findByProductId(1).getProduct().getUnit().equals("Packet"));
		
		// ----- addProduct -----
		Product rake = products.addProduct(manager, "  Garden Rake ", "  Metal rake ", 160.456, Category.HAND_TOOLS, " Piece ", 20, 4);
		check("new product gets next id (4)", rake.getProductId() == 4);
		check("name, description, unit are trimmed", rake.getName().equals("Garden Rake") && rake.getDescription().equals("Metal rake") && rake.getUnit().equals("Piece"));
		check("price rounded to 2 decimals", rake.getPrice() == 160.46);
		check("new product is active and in inventory with its stock", rake.isActive() && inventory.findByProductId(4).getCurrentStock() == 20 && inventory.findByProductId(4).getLowStockThreshold() == 4);
		StockMovement last = inv.getMovements().get(inv.getMovements().size() - 1);
		check("starting stock is logged as a movement", last.getProductId() == 4 && last.getQuantity() == 20 && last.getReason().equals("Initial stock") && last.getPerformedBy().equals("manager"));
		Product noStock = products.addProduct(manager, "Bamboo Stakes", "", 15, Category.PLANTING_MATERIALS, "Pack", 0, 5);
		check("zero starting stock logs no movement", inv.getMovements().get(inv.getMovements().size() - 1).getProductId() == 4 && noStock.getDescription().equals(""));
		
		expectThrows("clerk cannot add products", SecurityException.class, () -> products.addProduct(clerk, "X", "", 10, Category.SEEDS, "Pack", 1, 1));
		expectThrows("null user cannot add products", SecurityException.class, () -> products.addProduct(null, "X", "", 10, Category.SEEDS, "Pack", 1, 1));
		expectThrows("blank name rejected", IllegalArgumentException.class, () -> products.addProduct(manager, "  ", "", 10, Category.SEEDS, "Pack", 1, 1));
		expectThrows("zero price rejected", IllegalArgumentException.class, () -> products.addProduct(manager, "X", "", 0, Category.SEEDS, "Pack", 1, 1));
		expectThrows("negative price rejected", IllegalArgumentException.class, () -> products.addProduct(manager, "X", "", -5, Category.SEEDS, "Pack", 1, 1));
		expectThrows("NaN price rejected", IllegalArgumentException.class, () -> products.addProduct(manager, "X", "", Double.NaN, Category.SEEDS, "Pack", 1, 1));
		expectThrows("no category rejected", IllegalArgumentException.class, () -> products.addProduct(manager, "X", "", 10, null, "Pack", 1, 1));
		expectThrows("blank unit rejected", IllegalArgumentException.class, () -> products.addProduct(manager, "X", "", 10, Category.SEEDS, " ", 1, 1));
		expectThrows("negative starting stock rejected", IllegalArgumentException.class, () -> products.addProduct(manager, "X", "", 10, Category.SEEDS, "Pack", -1, 1));
		expectThrows("negative threshold rejected", IllegalArgumentException.class, () -> products.addProduct(manager, "X", "", 10, Category.SEEDS, "Pack", 1, -1));
		expectThrows("duplicate name rejected (ignores case)", IllegalArgumentException.class, () -> products.addProduct(manager, "tomato SEEDS", "", 10, Category.SEEDS, "Pack", 1, 1));
		check("failed adds created nothing", inventory.getItems().size() == 5);
		
		// ----- updateProduct -----
		products.updateProduct(manager, 4, "Steel Rake", "Sturdy", 175, Category.HAND_TOOLS, "Piece");
		check("update changes the details", rake.getName().equals("Steel Rake") && rake.getDescription().equals("Sturdy") && rake.getPrice() == 175.0);
		check("update does not touch stock or threshold", inventory.findByProductId(4).getCurrentStock() == 20 && inventory.findByProductId(4).getLowStockThreshold() == 4);
		products.updateProduct(manager, 4, "Steel Rake", "Sturdy", 180, Category.HAND_TOOLS, "Piece");
		check("a product can keep its own name", rake.getPrice() == 180.0);
		expectThrows("cannot rename to another product's name", IllegalArgumentException.class, () -> products.updateProduct(manager, 4, "Hand Trowel", "", 10, Category.HAND_TOOLS, "Piece"));
		expectThrows("bad update (price 0) rejected", IllegalArgumentException.class, () -> products.updateProduct(manager, 4, "Changed", "", 0, Category.HAND_TOOLS, "Piece"));
		check("failed update left the product untouched", rake.getName().equals("Steel Rake"));
		expectThrows("clerk cannot edit products", SecurityException.class, () -> products.updateProduct(clerk, 4, "X", "", 10, Category.SEEDS, "Pack"));
		expectThrows("unknown product id", IllegalArgumentException.class, () -> products.updateProduct(manager, 99, "X", "", 10, Category.SEEDS, "Pack"));
		
		// ----- activate / deactivate -----
		products.setProductActive(manager, 4, false);
		check("product can be deactivated", !rake.isActive());
		expectThrows("clerk cannot deactivate", SecurityException.class, () -> products.setProductActive(clerk, 4, true));
		check("failed reactivation left it inactive", !rake.isActive());
		products.setProductActive(manager, 4, true);
		check("product can be reactivated", rake.isActive());
		expectThrows("unknown product id (deactivate)", IllegalArgumentException.class, () -> products.setProductActive(manager, 99, false));
		
		// ----- search -----
		check("search by name", products.search("tomato", null, false).size() == 1);
		check("search by description", products.search("digging", null, false).size() == 1);
		check("search by display id", products.search("AG002", null, false).size() == 1);
		check("search ignores case and spaces around", products.search("  PECHAY ", null, false).size() == 1);
		check("filter by category", products.search(null, Category.SEEDS, false).size() == 2);
		check("text + category together", products.search("seeds", Category.HAND_TOOLS, false).size() == 0);
		check("empty search returns everything active", products.search("", null, false).size() == 5);
		products.setProductActive(manager, 3, false);
		check("inactive hidden when includeInactive is false", products.search(null, null, false).size() == 4);
		check("inactive shown when includeInactive is true", products.search(null, null, true).size() == 5);
		products.setProductActive(manager, 3, true);
		
		// ----- low stock counts (stock: Tomato 10/5, Pechay 3/5, Trowel 0/5, Rake 20/4, Bamboo 0/5) -----
		check("low stock count includes out of stock (Pechay, Trowel, Bamboo)", inventory.getLowStockCount() == 3);
		check("running low count excludes out of stock (Pechay only)", inventory.getRunningLowCount() == 1);
		check("out of stock count (Trowel, Bamboo)", inventory.getOutOfStockCount() == 2);
		products.setProductActive(manager, 2, false);
		check("inactive product leaves the low stock counts", inventory.getLowStockCount() == 2 && inventory.getRunningLowCount() == 0);
		products.setProductActive(manager, 2, true);
		
		// ----- hasEnoughStock -----
		check("hasEnoughStock true", inv.hasEnoughStock(1, 10));
		check("hasEnoughStock false when too many", !inv.hasEnoughStock(1, 11));
		check("hasEnoughStock false for 0 / unknown", !inv.hasEnoughStock(1, 0) && !inv.hasEnoughStock(99, 1));
		
		// ----- deductForSale -----
		int movementsBefore = inv.getMovements().size();
		Map<Integer, Integer> sale = new LinkedHashMap<>();
		sale.put(1, 2);
		sale.put(4, 1);
		inv.deductForSale(clerk, sale, "Sale TXN-0001");
		check("sale lowers stock of every item", inventory.findByProductId(1).getCurrentStock() == 8 && inventory.findByProductId(4).getCurrentStock() == 19);
		StockMovement m = inv.getMovements().get(inv.getMovements().size() - 1);
		check("one negative movement per item with reference", inv.getMovements().size() == movementsBefore + 2 && m.getQuantity() == -1 && m.getStockAfter() == 19 && m.getReason().equals("Sale TXN-0001") && m.getPerformedBy().equals("clerk"));
		
		Map<Integer, Integer> tooMuch = new LinkedHashMap<>();
		tooMuch.put(1, 3);   // fine on its own
		tooMuch.put(2, 5);   // Pechay only has 3
		int movementsNow = inv.getMovements().size();
		expectThrows("not enough stock is rejected", IllegalStateException.class, () -> inv.deductForSale(clerk, tooMuch, "Sale TXN-0002"));
		check("all or nothing: nothing changed when one item is short", inventory.findByProductId(1).getCurrentStock() == 8 && inventory.findByProductId(2).getCurrentStock() == 3 && inv.getMovements().size() == movementsNow);
		
		Map<Integer, Integer> inactiveSale = new LinkedHashMap<>();
		products.setProductActive(manager, 4, false);
		inactiveSale.put(4, 1);
		expectThrows("inactive product cannot be sold", IllegalArgumentException.class, () -> inv.deductForSale(clerk, inactiveSale, "Sale X"));
		Map<Integer, Integer> unknownSale = new LinkedHashMap<>();
		unknownSale.put(99, 1);
		expectThrows("unknown product cannot be sold", IllegalArgumentException.class, () -> inv.deductForSale(clerk, unknownSale, "Sale X"));
		Map<Integer, Integer> zeroSale = new LinkedHashMap<>();
		zeroSale.put(1, 0);
		expectThrows("quantity 0 rejected", IllegalArgumentException.class, () -> inv.deductForSale(clerk, zeroSale, "Sale X"));
		expectThrows("empty sale rejected", IllegalArgumentException.class, () -> inv.deductForSale(clerk, new LinkedHashMap<Integer, Integer>(), "Sale X"));
		expectThrows("null user rejected", IllegalArgumentException.class, () -> inv.deductForSale(null, sale, "Sale X"));
		
		// ----- restoreForVoid -----
		inv.restoreForVoid(clerk, sale, "Void TXN-0001");
		check("void puts the stock back (even for an inactive product)", inventory.findByProductId(1).getCurrentStock() == 10 && inventory.findByProductId(4).getCurrentStock() == 20);
		StockMovement v = inv.getMovements().get(inv.getMovements().size() - 1);
		check("restore is logged as a positive movement", v.getQuantity() == 1 && v.getStockAfter() == 20 && v.getReason().equals("Void TXN-0001"));
		Map<Integer, Integer> badRestore = new LinkedHashMap<>();
		badRestore.put(1, 2);
		badRestore.put(99, 1);
		int stockBeforeBad = inventory.findByProductId(1).getCurrentStock();
		expectThrows("restore with an unknown product rejected", IllegalArgumentException.class, () -> inv.restoreForVoid(clerk, badRestore, "Void X"));
		check("all or nothing for restore too", inventory.findByProductId(1).getCurrentStock() == stockBeforeBad);
		
		// ----- existing InventoryService methods still behave the same (the Inventory screen depends on them) -----
		check("restock by clerk still returns an error message", inv.restock(clerk, 1, 5, "x") != null);
		check("restock by manager still returns null on success", inv.restock(manager, 1, 5, "delivery") == null && inventory.findByProductId(1).getCurrentStock() == 15);
		check("adjust by manager still works", inv.adjust(manager, 1, 12, "count") == null && inventory.findByProductId(1).getCurrentStock() == 12);
		check("setThreshold by manager still works", inv.setThreshold(manager, 1, 7) == null && inventory.findByProductId(1).getLowStockThreshold() == 7);
		
		// ----- persistence: everything above was saved; load it into a brand new inventory -----
		InventoryFileManager fm2 = new InventoryFileManager(folder.getPath());
		List<InventoryItem> loaded = fm2.loadItems();
		check("all products were saved", loaded.size() == 5);
		InventoryItem loadedRake = null;
		for (InventoryItem item : loaded) {
			if (item.getProductId() == 4) {loadedRake = item;}
		} // end of for loop
		check("edited details and unit survive a restart", loadedRake != null && loadedRake.getName().equals("Steel Rake") && loadedRake.getProduct().getUnit().equals("Piece") && loadedRake.getProduct().getPrice() == 180.0 && !loadedRake.getProduct().isActive());
		check("stock survives a restart", loadedRake.getCurrentStock() == 20);
		check("stock movements survive a restart", fm2.loadMovements().size() == inv.getMovements().size());
		
		// ----- an OLD inventory file (8 fields, no unit) must still load -----
		File oldFolder = Files.createTempDirectory("agri_old").toFile();
		PrintWriter writer = new PrintWriter(new FileWriter(new File(oldFolder, "inventory.txt")));
		writer.println("1|Tomato Seeds|description|35.0|SEEDS|true|3|5");
		writer.close();
		List<InventoryItem> oldItems = new InventoryFileManager(oldFolder.getPath()).loadItems();
		check("old 8-field file still loads, unit defaults to Piece", oldItems.size() == 1 && oldItems.get(0).getProduct().getUnit().equals("Piece") && oldItems.get(0).getCurrentStock() == 3);
		
		System.out.println();
		System.out.println("Passed: " + passed + "   Failed: " + failed);
		if (failed > 0) {System.exit(1);}
		
	} // end of main
	
} // end of ProductInventoryCheck class