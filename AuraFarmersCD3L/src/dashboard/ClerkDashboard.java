package dashboard;

/*
 * ClerkDashboard
 * 	- extends Dashboard
 */

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

import model.*;
import service.InventoryService;

public class ClerkDashboard extends Dashboard {
	// attributes
    private final Clerk clerk;
    private final Inventory inventory;
    private final InventoryService inventoryService;
    private final Label salesTodayValue = new Label("0");
    private final Label transactionsValue = new Label("0");
    private final Label voidedValue = new Label("0");
    
    // for new purchase entries
    private TableView<InventoryItem> productCatalog;
    private final Label selectedProductValue = new Label("None");
    private final VBox purchaseItems = new VBox(5);
    private final Label totalAmountValue = new Label("₱0.00");
    private double totalAmount = 0.0;
    private final Label changeValue = new Label("₱0.00");
    
    private double salesToday = 0.0;
    private int transactionCount = 0;
    
    private final ObservableList<SalesTransaction> transactionHistory = FXCollections.observableArrayList();
    
    //constructor
    public ClerkDashboard(Clerk clerk, InventoryService inventoryService) {
			this.clerk = clerk;
			this.inventoryService = inventoryService;
			this.inventory = inventoryService.getInventory();
			buildDashboard();
    }
    
    // methods
    private void buildDashboard() {
        setTop(createHeader("Clerk Dashboard", "Welcome, " + clerk.getDisplayName()));

        //keeping track of current sales, number of transactions, and number of voided transactions
        HBox stats = createStatRow(
        		createStatCard("Sales Today", salesTodayValue),
        		createStatCard("Transactions", transactionsValue),
        		createStatCard("Voided", voidedValue)
        );

        // buttons
        Button historyBtn = new Button("Transaction History");
        Button voidBtn = new Button("Void Transaction");

        //creates a table for viewing the transaction history
        TableView<SalesTransaction> historyTable = createTransactionTable();
        historyTable.setVisible(false);
        historyTable.setManaged(false);
        
        // when clicking the Transaction History button, the transaction history appears/disappears
        historyBtn.setOnAction(event -> {
            boolean isVisible = historyTable.isVisible();
            historyTable.setVisible(!isVisible);
            historyTable.setManaged(!isVisible);
        });
        historyBtn.setPrefWidth(200);
        
        // select a row on the transaction history table. once selected, if void btn is clicked, that transaction becomes void
        voidBtn.setOnAction(event -> {
            SalesTransaction selectedTransaction = historyTable.getSelectionModel().getSelectedItem();
            
            if (selectedTransaction == null) return;

            // removes transaction from history
            transactionHistory.remove(selectedTransaction);
            
            // deducts the selected transaction's sales from the current total sales of the day
            salesToday -= selectedTransaction.getTotalAmount();
            salesTodayValue.setText(String.format("₱%.2f", salesToday));

            // decrements the transaction count by 1
            transactionCount--;
            transactionsValue.setText(String.valueOf(transactionCount));

            // gets current number of voided transactions and increments it by 1
            int currentVoided = Integer.parseInt(voidedValue.getText());
            voidedValue.setText(String.valueOf(currentVoided + 1));
        });
        voidBtn.setPrefWidth(200);
        
        VBox actions = new VBox(
        		15,
        		historyBtn,
        		voidBtn,
        		historyTable
        );
        
       
        Label catalogLabel = new Label("Product Catalog");

        // creates a product catalog table
        productCatalog = createProductCatalog();
        
        // creates a separate section for processing purchases
        VBox purchaseEntry = createPurchaseEntry();
        
        VBox mainContent = new VBox(
        		20,
                stats,
                catalogLabel,
                productCatalog,
                purchaseEntry,
                actions
        );

        mainContent.setPadding(new Insets(20));
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        setCenter(scrollPane);
    }
    
    // creates a product catalog showing the product name, category, price, available stock, and availability status
    private TableView<InventoryItem> createProductCatalog() {

        TableView<InventoryItem> table = new TableView<>();

        TableColumn<InventoryItem, String> productColumn =
            new TableColumn<>("Product Name");

        productColumn.setCellValueFactory(cell ->
    		new SimpleStringProperty(
                cell.getValue().getProduct().getName()));

        TableColumn<InventoryItem, String> categoryColumn =
            new TableColumn<>("Category");

        categoryColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(
                cell.getValue().getProduct().getCategory().toString()));

        TableColumn<InventoryItem, String> priceColumn =
            new TableColumn<>("Price");

        priceColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(
                "₱" + cell.getValue().getProduct().getPrice()));

        TableColumn<InventoryItem, Integer> stockColumn =
            new TableColumn<>("Available Stock");

        stockColumn.setCellValueFactory(cell ->
            new SimpleIntegerProperty(
                cell.getValue().getCurrentStock()).asObject());

        TableColumn<InventoryItem, String> statusColumn =
            new TableColumn<>("Status");

        statusColumn.setCellValueFactory(cell -> {
            InventoryItem item = cell.getValue();
            String status;

            if (item.getCurrentStock() <= 0) {
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
                productColumn,
                categoryColumn,
                priceColumn,
                stockColumn,
                statusColumn);

        table.setItems(inventory.getItems());

        table.setMinHeight(250);
        table.setPrefHeight(250);
        
        return table;
    }
    
    // acts as an online cash register/cart for processing online purchases
    private VBox createPurchaseEntry() {
        Label purchaseLabel = new Label("Purchase Entry");
        Label selectedProductLabel = new Label("Selected Product:");
        Label currentPurchaseLabel = new Label("Current Purchase");
        Label totalLabel = new Label("Total Amount:");

        Button completeBtn = new Button("Complete Transaction");
        
        TextField quantityField = new TextField();
        quantityField.setPromptText("Enter Quantity");

        Label cashReceivedLabel = new Label("Cash Received:");

        TextField cashReceivedField = new TextField();
        cashReceivedField.setPromptText("Enter Cash Amount");

        Label changeLabel = new Label("Change:");
        
        productCatalog.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                selectedProductValue.setText(newItem.getProduct().getName());
            }
        });

        Button addButton = new Button("Add Item");

        addButton.setOnAction(event -> {
            InventoryItem product = productCatalog.getSelectionModel().getSelectedItem();
            
            if(product == null) {
                return;
            }

            int quantity = Integer.parseInt(quantityField.getText());
            double itemTotal = product.getProduct().getPrice() * quantity;

            totalAmount += itemTotal;
            totalAmountValue.setText(String.format("₱%.2f", totalAmount));
            
            purchaseItems.getChildren().add(new Label(product.getProduct().getName() + " x " + quantity));
        });
        
        completeBtn.setOnAction(event -> {
            if (totalAmount <= 0) return;

            double cashReceived;
            try {
                cashReceived = Double.parseDouble(cashReceivedField.getText());
            }
            catch(NumberFormatException e) {
                return;
            }

            if (cashReceived < totalAmount) {
                changeValue.setText("Insufficient Cash");
                return;
            }

            double change = cashReceived - totalAmount;

            changeValue.setText(String.format("₱%.2f", change));

            SalesTransaction transaction = new SalesTransaction(clerk.getDisplayName(), totalAmount, cashReceived, change);
            transactionHistory.add(transaction);
            
            salesToday += totalAmount;
            salesTodayValue.setText(String.format("₱%.2f", salesToday));

            transactionCount++;
            transactionsValue.setText(String.valueOf(transactionCount));

            purchaseItems.getChildren().clear();
            totalAmount = 0.0;
            totalAmountValue.setText("₱0.00");
            productCatalog.getSelectionModel().clearSelection();
            selectedProductValue.setText("None");

            quantityField.clear();
            cashReceivedField.clear();
        });
        
        VBox purchaseEntry = new VBox(
            10,
            purchaseLabel,
            selectedProductLabel,
            selectedProductValue,
            quantityField,
            addButton,
            currentPurchaseLabel,
            purchaseItems,
            totalLabel,
            totalAmountValue,
            cashReceivedLabel,
            cashReceivedField,
            changeLabel,
            changeValue,
            completeBtn
        );
        return purchaseEntry;
    }
    
    // creates a transaction table which allows the user to view transaction history and details
    private TableView<SalesTransaction> createTransactionTable() {
        TableColumn<SalesTransaction, String> dateColumn =
            new TableColumn<>("Date");

        dateColumn.setCellValueFactory(cell ->
        	new SimpleStringProperty(
            		cell.getValue().getTransactionDate().toString()));
        
        
        TableColumn<SalesTransaction, String> timeColumn =
            new TableColumn<>("Time");

        timeColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(
            		cell.getValue().getTransactionTime().toString()));
        
        
        TableView<SalesTransaction> table = new TableView<>();

        TableColumn<SalesTransaction, String> clerkColumn =
            new TableColumn<>("Clerk");

        clerkColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(
            		cell.getValue().getClerkName()));
        
        
        TableColumn<SalesTransaction, String> totalColumn =
            new TableColumn<>("Total");

        totalColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(
                String.format("₱%.2f", cell.getValue().getTotalAmount())
            ));

        
        TableColumn<SalesTransaction, String> cashColumn =
            new TableColumn<>("Cash Received");

        cashColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(
        		String.format("₱%.2f",cell.getValue().getCashReceived())
            ));
        
        
        TableColumn<SalesTransaction, String> changeColumn =
            new TableColumn<>("Change");

        changeColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(
                String.format("₱%.2f",cell.getValue().getChangeGiven())
            ));
        
        dateColumn.setPrefWidth(125);
        timeColumn.setPrefWidth(125);
        clerkColumn.setPrefWidth(125);
        totalColumn.setPrefWidth(125);
        cashColumn.setPrefWidth(125);
        changeColumn.setPrefWidth(125);
        
        table.getColumns().addAll(
                dateColumn,
                timeColumn,
                clerkColumn,
                totalColumn,
                cashColumn,
                changeColumn
        );

        table.setItems(transactionHistory);

        return table;
    }
  
} // end of ClerkDashboard class