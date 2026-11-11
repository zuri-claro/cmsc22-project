package ui;

import app.AgriSystem;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Category;
import model.InventoryItem;
import model.User;
import service.InventoryService;

import java.util.Optional;

/*
 * InventoryScreen
 * 	- extends BorderPane
 * 	- can be viewed by all users but only managers can use buttons
 */
public class InventoryScreen extends BorderPane {
	
	// ATTRIBUTES
    private final InventoryService service;
    private final User user;

    private final TableView<InventoryItem> table = new TableView<>();
    private final FilteredList<InventoryItem> filteredItems;
    private final TextField searchField = new TextField();
    private final ComboBox<String> categoryBox = new ComboBox<>();
    private final CheckBox lowOnlyBox = new CheckBox("Low stock only");
    private final Label banner = new Label();

    private static final String ALL_CATEGORIES = "All Categories";
    
    // METHODS
    /*
     * InventoryScreen(AgriSystem)
     * 	- constructor method
     */
    public InventoryScreen(AgriSystem system) {
    	
        service = system.getInventoryService();
        user = system.getCurrentUser();
        filteredItems = new FilteredList<>(service.getInventory().getItems());

        setupTable();
        setTop(createTopPart());
        setCenter(table);
        setBottom(createBottomPart());
        refresh();
        
    } // end of InventoryScreen

    /*
     * createTopPart()
     */
    private VBox createTopPart() {
    	
        Label title = new Label("Inventory");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label loggedIn = new Label("Logged in as: " + user);

        banner.setMaxWidth(Double.MAX_VALUE);
        banner.setPadding(new Insets(8));

        searchField.setPromptText("Search by name or ID");
        searchField.setPrefWidth(240);

        categoryBox.getItems().add(ALL_CATEGORIES);
        for (Category c : Category.values()) {
            categoryBox.getItems().add(c.toString());
        } // end of for loop
        categoryBox.setValue(ALL_CATEGORIES);

        // reapply the filter whenever one of the three controls changes.
        searchField.setOnKeyReleased(e -> applyFilter());
        categoryBox.setOnAction(e -> applyFilter());
        lowOnlyBox.setOnAction(e -> applyFilter());

        HBox controls = new HBox(10, searchField, categoryBox, lowOnlyBox);
        VBox top = new VBox(8, title, loggedIn, banner, controls);
        top.setPadding(new Insets(10));
        return top;
        
    } // end of createTopPart
    
    /*
     * createBottomPart()
     */
    private HBox createBottomPart() {
    	
        HBox bottom = new HBox(10);
        bottom.setPadding(new Insets(10));

        if (user.canModifyInventory()) {
            Button restockButton = new Button("Restock Item");
            Button adjustButton = new Button("Stock Adjustment");
            Button thresholdButton = new Button("Set Low-Stock Threshold");

            restockButton.setOnAction(e -> onRestock());
            adjustButton.setOnAction(e -> onAdjust());
            thresholdButton.setOnAction(e -> onThreshold());

            bottom.getChildren().addAll(restockButton, adjustButton, thresholdButton);
        } else {
            bottom.getChildren().add(new Label("Read-only view: only a Manager can restock or adjust."));
        } // end of if else statement
        
        return bottom;
        
    } // end of createBottomPart
    
    /*
     * setupTable()
     */
    private void setupTable() {
    	
        table.getColumns().add(makeColumn("Product ID", 90, "productId"));
        table.getColumns().add(makeColumn("Product Name", 200, "name"));
        table.getColumns().add(makeColumn("Category", 190, "category"));
        table.getColumns().add(makeColumn("Current Stock", 100, "currentStock"));
        table.getColumns().add(makeColumn("Low-Stock Threshold", 130, "lowStockThreshold"));
        table.getColumns().add(makeColumn("Status", 80, "status"));

        // SortedList lets user click a column header to sort
        SortedList<InventoryItem> sortedItems = new SortedList<>(filteredItems);
        sortedItems.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedItems);

        // color low-stock rows red
        table.setRowFactory(tv -> new TableRow<InventoryItem>() {
            @Override
            protected void updateItem(InventoryItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || !item.isLowStock()) {
                    setStyle("");
                } else {
                    setStyle("-fx-control-inner-background: #ffd9d9;");
                } // end of if else statement
            } // end of updateItem
        }); // end of .setRowFactory
        
    } // end of setupTable
    
    /*
     * makeColumn(String, int, String)	
     */
    private TableColumn<InventoryItem, Object> makeColumn(String title, int width, String getterName) {
        
    	TableColumn<InventoryItem, Object> column = new TableColumn<>(title);
        column.setPrefWidth(width);
        column.setCellValueFactory(new PropertyValueFactory<>(getterName));
        return column;
        
    } // end of makeColumn

    /*
     * applyFilter()
     */
    private void applyFilter() {
        String text = searchField.getText().trim().toLowerCase();
        String category = categoryBox.getValue();
        boolean lowOnly = lowOnlyBox.isSelected();

        filteredItems.setPredicate(item -> {
            boolean matchesText = text.isEmpty()
                    || item.getProduct().getName().toLowerCase().contains(text);
            boolean matchesCategory = category == null
                    || category.equals(ALL_CATEGORIES)
                    || item.getProduct().getCategory().toString().equals(category);
            boolean matchesLow = !lowOnly || item.isLowStock();
            return matchesText && matchesCategory && matchesLow;
        }); // end of .setPredicate
    } // end of applyFilter

    /*
     * refresh()
     */
    private void refresh() {
        applyFilter();
        table.refresh();

        int lowCount = service.getInventory().getLowStockCount();
        if (lowCount > 0) {
            banner.setText(lowCount + " item(s) at or below the low-stock threshold - consider restocking.");
            banner.setStyle("-fx-background-color: #ffe9b3; -fx-font-weight: bold;");
        } else {
            banner.setText("All stock levels are good.");
            banner.setStyle("-fx-background-color: #d8f3dc;");
        } // end of if else statement
        
    } // end of refresh

    /*
     * onRestock()
     */
    private void onRestock() {
        InventoryItem item = getSelectedItem();
        if (item == null) {
            return;
        } // end of if statement
        String quantityText = ask("Restock Item", item.getProduct().getName(), "Quantity received:", "");
        if (quantityText == null) {
            return;
        }
        String note = ask("Restock Item", item.getProduct().getName(), "Note (optional):", "");
        if (note == null) {
            return;
        } // end of if statement

        try {
            int quantity = Integer.parseInt(quantityText.trim());
            String error = service.restock(user, item.getProduct().getProductId(), quantity, note);
            if (error != null) {
                showError(error);
            }
        } catch (NumberFormatException e) {
            showError("Please enter a whole number.");
        } // end of try catch
        refresh();
    } // end of onRestock
    
    /*
     * onAdjust()
     */
    private void onAdjust() {
        InventoryItem item = getSelectedItem();
        if (item == null) {
            return;
        } // end of if statement
        String header = item.getProduct().getName() + " (current: " + item.getCurrentStock() + ")";
        String quantityText = ask("Stock Adjustment", header, "New stock quantity:", "");
        if (quantityText == null) {
            return;
        } // end of if statement
        String reason = ask("Stock Adjustment", header, "Reason (required):", "");
        if (reason == null) {
            return;
        } // end of if statement

        try {
            int newQuantity = Integer.parseInt(quantityText.trim());
            String error = service.adjust(user, item.getProduct().getProductId(), newQuantity, reason);
            if (error != null) {
                showError(error);
            }
        } catch (NumberFormatException e) {
            showError("Please enter a whole number.");
        } // end of try catch
        refresh();
    } // end of onAdjust
    
    /*
     * onThreshold()
     */
    private void onThreshold() {
        InventoryItem item = getSelectedItem();
        if (item == null) {
            return;
        } // end of if statement
        String text = ask("Low-Stock Threshold", item.getProduct().getName(),
                "New threshold:", String.valueOf(item.getLowStockThreshold()));
        if (text == null) {
            return;
        } // end of if statement

        try {
            int threshold = Integer.parseInt(text.trim());
            String error = service.setThreshold(user, item.getProduct().getProductId(), threshold);
            if (error != null) {
                showError(error);
            }
        } catch (NumberFormatException e) {
            showError("Please enter a whole number.");
        } // end of try catch
        refresh();
    } // end of onThreshold

    /*
     * getSelectedItem()
     * 	- returns either the selected row or null
     */
    private InventoryItem getSelectedItem() {
        InventoryItem item = table.getSelectionModel().getSelectedItem();
        if (item == null) {
            showError("Select a product in the table first.");
        } // end of if statement
        return item;
    } // end of getSelectedItem()
    
    /*
     * ask(String, String, String, String)
     * 	- gets user input
     */
    private String ask(String title, String header, String label, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(label);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        } // end of if statement
        return null;
    } // end of ask
    
    /*
     * showError(String)
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    } // end of showError
} // end of InventoryScreen class