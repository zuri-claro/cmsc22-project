package persistence;

import model.Category;
import model.InventoryItem;
import model.Product;
import model.StockMovement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
 * InventoryFileManager
 * 	- manages files so the data is saved
 */

public class InventoryFileManager {
	
	// ATTRIBUTES
    private static final String SEPARATOR = "|";

    private final File itemsFile;
    private final File movementsFile;
    
    // METHODS
    /*
     * InventoryFileManager(String)
     * 	- constructor method
     */
    public InventoryFileManager(String folderName) {
    	
        File folder = new File(folderName);
        folder.mkdirs(); // creates the folder if it doesnt exist yet
        itemsFile = new File(folder, "inventory.txt");
        movementsFile = new File(folder, "stock_movements.txt");
        
    } // end of InventoryFileManager
    
    /*
     * hasSavedItems()
     * 	- returns if the program has been run before and saved its inventory.
     */
    public boolean hasSavedItems() {
        return itemsFile.exists();
    } // end of hasSavedItems

    /*
     * saveItems(List<InventoryItem>)
     * 	- saves list of items in inventory into file "inventory.txt"
     */
    public void saveItems(List<InventoryItem> items) {
    	
        try {
        	
        	PrintWriter writer = new PrintWriter(new FileWriter(itemsFile));
        	// print all attributes of each inventory item product
            for (InventoryItem item : items) {
                Product product = item.getProduct();
                writer.println(product.getProductId() + SEPARATOR + clean(product.getName()) + SEPARATOR + clean(product.getDescription()) + SEPARATOR + product.getPrice() + SEPARATOR + product.getCategory().name() + SEPARATOR + product.isActive() + SEPARATOR + item.getCurrentStock() + SEPARATOR + item.getLowStockThreshold() + SEPARATOR + clean(product.getUnit()));            } // end of for loop
            writer.close();
            
        } catch (IOException e) {
            System.out.println("Could not save inventory: " + e.getMessage());
            
        } // end of try catch
        
    } // end of saveItems
    
    /*
     * loadItems()
     * 	- loads from inventory.txt back into the items list
     */
    public List<InventoryItem> loadItems() {
    	
        List<InventoryItem> items = new ArrayList<>();
        
        // error: items file is empty/never saved before
        if (!itemsFile.exists()) {
            return items;
        } // end of if statement

        try {
        	
        	BufferedReader reader = new BufferedReader(new FileReader(itemsFile));
            String line;
            while ((line = reader.readLine()) != null) {
            	
            	// skip whitespace
                if (line.trim().isEmpty()) {continue;}
                
                // divide each line into 9 parts (older files only have 8, the unit is missing)
                String[] parts = line.split("\\|", -1);
                
                // error: not divided properly
                if (parts.length != 8 && parts.length != 9) {
                    continue;
                } // end of if statement
                
                try {
                	
                	// get all attributes
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    String description = parts[2];
                    double price = Double.parseDouble(parts[3]);
                    Category category = Category.valueOf(parts[4]);
                    boolean status = Boolean.parseBoolean(parts[5]);
                    int stock = Integer.parseInt(parts[6]);
                    int threshold = Integer.parseInt(parts[7]);
                    String unit = (parts.length == 9) ? parts[8] : ""; // empty becomes "Piece" in Product
                    
                    // create new product and inventory item using those attributes
                    Product product = new Product(id, name, description, price, category, status, unit);
                    items.add(new InventoryItem(product, stock, threshold));
                    
                } catch (RuntimeException e) {
                } // end of try catch
                                
            } // end of while loop
            reader.close();
            
        } catch (IOException e) {
            System.out.println("Could not read inventory: " + e.getMessage());
        } // end of try catch
        
        return items;
        
    } // end of loadItems

    /*
     * saveMovements(List<StockMovement>)
     * 	- to save all stock movements into file stock_movements.txt
     */
    public void saveMovements(List<StockMovement> movements) {
        try {
        	
        	PrintWriter writer = new PrintWriter(new FileWriter(movementsFile));
            for (StockMovement m : movements) {
                writer.println(m.getProductId() + SEPARATOR + m.getQuantity() + SEPARATOR + m.getStockAfter() + SEPARATOR + clean(m.getPerformedBy()) + SEPARATOR + clean(m.getReason()) + SEPARATOR);
            } // end of for loop
        	writer.close();
        	
        } catch (IOException e) {
            System.out.println("Could not save stock movements: " + e.getMessage());
        } // end of try catch
    } // end of saveMovements
    
    /*
     * loadMovements()
     * 	- from stock_movements.txt back to list of stock movements
     */
    public List<StockMovement> loadMovements() {
    	
        List<StockMovement> movements = new ArrayList<>();
        if (!movementsFile.exists()) {
            return movements;
        } // end of if statement

        try {
        	
        	BufferedReader reader = new BufferedReader(new FileReader(movementsFile));
            String line;
            while ((line = reader.readLine()) != null) {
            	
                if (line.trim().isEmpty()) {
                    continue;
                } // end of if statement
                
                String[] parts = line.split("\\|", -1);
                if (parts.length != 6) {
                    System.out.println("Skipped a bad line in movements file: " + line);
                    continue;
                } // end of if statement
                
                try {
                    int productId = Integer.parseInt(parts[0]);
                    int quantity = Integer.parseInt(parts[1]);
                    int stockAfter = Integer.parseInt(parts[2]);
                    String performedBy = parts[3];
                    String reason = parts[4];

                    movements.add(new StockMovement(productId, quantity, stockAfter, performedBy, reason));
                } catch (RuntimeException e) {
                    System.out.println("Skipped a bad line in movements file: " + line);
                } // end of try catch
                
            } // end of while loop
            reader.close();
            
        } catch (IOException e) {
            System.out.println("Could not read stock movements: " + e.getMessage());
        } // end of try catch
        
        return movements;
        
    } // end of loadMovements

    /*
     * clean(String)
     * 	- removes the separator and line breaks from text
     */
    private String clean(String text) {
    	
        if (text == null) {
            return "";
        } // end of if statement
        return text.replace("|", "/").replace("\n", " ").replace("\r", " ");
        
    } // end of clean
    
} // end of InventoryFileManager class