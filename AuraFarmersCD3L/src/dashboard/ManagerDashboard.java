package dashboard;

import model.Manager;
import model.Clerk;
import model.User;
import model.Inventory;
import model.InventoryItem;
import model.Product;
import model.StockMovement;

import service.InventoryService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.geometry.Insets;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

import app.AgriSystem;

/*
 * ManagerDashboard class
 * - Extends Dashboard
 * - Displays inventory information and management overview
 * - Allows the Manager to monitor Clerks
 */

public class ManagerDashboard extends Dashboard {

    // ATTRIBUTES
    private final Manager manager;
    private final InventoryService inventoryService;
    private final Inventory inventory;

    private final ObservableList<Clerk> clerks =
            FXCollections.observableArrayList();

    // Inventory statistics
    private final Label activeProductsValue = new Label("0");
    private final Label lowStockValue = new Label("0");
    private final Label outOfStockValue = new Label("0");

    // Sales statistics (to be connected in Milestone 3)
    private final Label totalSalesValue = new Label("Not available yet");
    private final Label completedTransactionsValue =
            new Label("Not available yet");

    // Actions for opening other screens
    private Consumer<Clerk> clerkDashboardAction;
    private Runnable inventoryAction;

    /*
     * ManagerDashboard(Manager, InventoryService, List<User>)
     * - Constructor method
     * - Receives the Manager, inventory service, and registered users
     */
    public ManagerDashboard(Manager manager,
                            InventoryService inventoryService,
                            List<User> users) {

        this.manager = manager;
        this.inventoryService = inventoryService;

        // Gets the same inventory used by InventoryService
        this.inventory = inventoryService.getInventory();

        // Gets only the users who are Clerks
        for (User user : users) {

            if (user instanceof Clerk) {
                clerks.add((Clerk) user);
            }

        }

        buildDashboard();
        updateStatistics();

    } // end of constructor method
    
    /*
     * buildDashboard()
     * - Creates the Manager Dashboard layout
     */
    private void buildDashboard() {

        // Header
        VBox header = createHeader(
            "Manager Dashboard",
            "Welcome, " + manager.getDisplayName()
        );

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        // Inventory overview
        Label inventoryTitle = new Label("Inventory Overview");
        inventoryTitle.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;"
        );

        HBox inventoryCards = createStatRow(
            createStatCard("Active Products", activeProductsValue),
            createStatCard("Low Stock Products", lowStockValue),
            createStatCard("Out of Stock Products", outOfStockValue)
        );

        // Sales overview
        Label salesTitle = new Label("Sales Overview");
        salesTitle.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;"
        );

        HBox salesCards = createStatRow(
            createStatCard("Total Sales", totalSalesValue),
            createStatCard(
                "Completed Transactions",
                completedTransactionsValue
            )
        );

        Label salesNote = new Label(
            "Sales information will be connected to the transaction module."
        );

        salesNote.setStyle("-fx-text-fill: #777777;");

        // Inventory table
        Label inventoryTitleTable = new Label("Current Inventory");
        inventoryTitleTable.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;"
        );

        TableView<InventoryItem> inventoryTable =
                createInventoryTable();

        // Stock movement table
        Label movementTitle = new Label("Recent Stock Movements");
        movementTitle.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;"
        );

        TableView<StockMovement> movementTable =
                createMovementTable();

        // Clerk monitoring
        Label clerkTitle = new Label("Clerk Monitoring");
        clerkTitle.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;"
        );

        TableView<Clerk> clerkTable = createClerkTable();

        // Inventory Management navigation button
        Button inventoryButton =
                new Button("Open Inventory Management");

        inventoryButton.setStyle(
            "-fx-background-color: #542020;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 10px 15px;"
        );

        inventoryButton.setOnAction(event -> {

            if (inventoryAction != null) {
                inventoryAction.run();
            }

        });

        // Add all sections to the content layout
        content.getChildren().addAll(
            inventoryTitle,
            inventoryCards,

            salesTitle,
            salesCards,
            salesNote,

            inventoryTitleTable,
            inventoryTable,

            movementTitle,
            movementTable,

            clerkTitle,
            clerkTable,

            inventoryButton
        );

        // Allows scrolling when the dashboard content is too long
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);

        this.setTop(header);
        this.setCenter(scrollPane);
        this.setStyle("-fx-background-color: #f4f4f4;");

    } // end of buildDashboard


    /*
     * createInventoryTable()
     * - Displays products and their current stock information
     */
    private TableView<InventoryItem> createInventoryTable() {

        TableView<InventoryItem> table = new TableView<>();

        TableColumn<InventoryItem, String> nameColumn =
                new TableColumn<>("Product Name");

        nameColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(
                cell.getValue().getProduct().getName()
            )
        );

        TableColumn<InventoryItem, String> categoryColumn =
                new TableColumn<>("Category");

        categoryColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(
                cell.getValue().getProduct().getCategory().toString()
            )
        );

        TableColumn<InventoryItem, String> priceColumn =
                new TableColumn<>("Price");

        priceColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(
                String.format("₱%.2f",
                    cell.getValue().getProduct().getPrice())
            )
        );

        TableColumn<InventoryItem, Integer> stockColumn =
                new TableColumn<>("Current Stock");

        stockColumn.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleIntegerProperty(
                cell.getValue().getCurrentStock()
            ).asObject()
        );

        TableColumn<InventoryItem, Integer> thresholdColumn =
                new TableColumn<>("Low Stock Threshold");

        thresholdColumn.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleIntegerProperty(
                cell.getValue().getLowStockThreshold()
            ).asObject()
        );

        TableColumn<InventoryItem, String> statusColumn =
                new TableColumn<>("Status");

        statusColumn.setCellValueFactory(cell -> {

            InventoryItem item = cell.getValue();
            String status;

            if (!item.getProduct().isActive()) {
                status = "Inactive";
            }
            else if (item.getCurrentStock() <= 0) {
                status = "Out of Stock";
            }
            else if (item.isLowStock()) {
                status = "Low Stock";
            }
            else {
                status = "Available";
            }

            return new SimpleStringProperty(status);

        });

        table.getColumns().addAll(
            nameColumn,
            categoryColumn,
            priceColumn,
            stockColumn,
            thresholdColumn,
            statusColumn
        );

        // Uses the same inventory data as InventoryService
        table.setItems(inventory.getItems());
        table.setPrefHeight(250);

        return table;

    } // end of createInventoryTable


    /*
     * createMovementTable()
     * - Displays the recorded changes in inventory stock
     */
    private TableView<StockMovement> createMovementTable() {

        TableView<StockMovement> table = new TableView<>();

        TableColumn<StockMovement, Integer> productIdColumn =
                new TableColumn<>("Product ID");

        productIdColumn.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleIntegerProperty(
                cell.getValue().getProductId()
            ).asObject()
        );

        TableColumn<StockMovement, Integer> quantityColumn =
                new TableColumn<>("Quantity Change");

        quantityColumn.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleIntegerProperty(
                cell.getValue().getQuantity()
            ).asObject()
        );

        TableColumn<StockMovement, Integer> stockColumn =
                new TableColumn<>("Stock After Change");

        stockColumn.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleIntegerProperty(
                cell.getValue().getStockAfter()
            ).asObject()
        );

        TableColumn<StockMovement, String> clerkColumn =
                new TableColumn<>("Performed By");

        clerkColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(
                cell.getValue().getPerformedBy()
            )
        );

        table.getColumns().addAll(
            productIdColumn,
            quantityColumn,
            stockColumn,
            clerkColumn
        );

        // Uses the stock movement records from InventoryService
        table.setItems(
            FXCollections.observableArrayList(
                inventoryService.getMovements()
            )
        );

        table.setPrefHeight(180);

        return table;

    } // end of createMovementTable


    /*
     * createClerkTable()
     * - Displays the registered Clerks
     */
    private TableView<Clerk> createClerkTable() {

        TableView<Clerk> table = new TableView<>();

        TableColumn<Clerk, String> nameColumn =
                new TableColumn<>("Clerk Name");

        nameColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(
                cell.getValue().getDisplayName()
            )
        );

        TableColumn<Clerk, String> usernameColumn =
                new TableColumn<>("Username");

        usernameColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(
                cell.getValue().getUsername()
            )
        );

        table.getColumns().addAll(
            nameColumn,
            usernameColumn
        );

        table.setItems(clerks);
        table.setPrefHeight(180);

        return table;

    } // end of createClerkTable


    /*
     * updateStatistics()
     * - Updates the inventory statistics shown on the dashboard
     */
    public void updateStatistics() {

        activeProductsValue.setText(
            String.valueOf(inventory.getActiveProductCount())
        );

        lowStockValue.setText(
            String.valueOf(inventory.getLowStockCount())
        );

        outOfStockValue.setText(
            String.valueOf(inventory.getOutOfStockCount())
        );

    } // end of updateStatistics


    /*
     * setClerkDashboardAction(Consumer<Clerk>)
     * - Sets the action for opening a selected Clerk's dashboard
     */
    public void setClerkDashboardAction(Consumer<Clerk> action) {

        this.clerkDashboardAction = action;

    } // end of setClerkDashboardAction


    /*
     * setInventoryAction(Runnable)
     * - Sets the action for opening the Inventory Management screen
     */
    public void setInventoryAction(Runnable action) {

        this.inventoryAction = action;

    } // end of setInventoryAction

} // end of ManagerDashboard
