package ui;

import java.util.List;

import app.AgriSystem;
import model.*;
import service.InventoryService;
import dashboard.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*
 * LoginScreen class
 */

public class LoginScreen extends VBox {
	
	// ATTRIBUTES
    private final AgriSystem system;
    private final Stage stage;

    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Label errorLabel = new Label();
    
    // METHODS
    /*
     * LoginScreen(AgriSystem, Stage)
     */
    public LoginScreen(AgriSystem system, Stage stage) {
    	
        this.system = system;
        this.stage = stage;
        
        // prompt for username
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(250);
        
        // prompt for password
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(250);
        
        // button for login
        Button loginButton = new Button("Log In");
        loginButton.setOnAction(e -> onLogin());

        errorLabel.setStyle("-fx-text-fill: red;"); 

        setSpacing(12);
        setPadding(new Insets(30));
        setAlignment(Pos.CENTER);
        getChildren().addAll(usernameField, passwordField, loginButton, errorLabel);
        
    } // end of LoginScreen
    
    /*
     * onLogin()
     */
    private void onLogin() {
    	
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        // ERROR: text fields cannot be empty
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter your username and password.");
            return;
        } // end of if statement

        User user = system.login(username, password);
        if (user == null) { // if user not found in users list
        	
            errorLabel.setText("Wrong username or password.");
            passwordField.clear();
            return;
            
        } // end of if statement
        
        // open either manager dashboard or clerk dashboard
        if (user.getRole().equals(Role.MANAGER)) {
        	ManagerDashboard managerDashboard = new ManagerDashboard((Manager) user, system.getInventoryService(), system.getUsers());
        	managerDashboard.setInventoryAction(() -> stage.setScene(new Scene(new InventoryScreen(system), 900, 560)));
        	stage.setScene(new Scene(managerDashboard, 900, 560));
        } else {
        	stage.setScene(new Scene(new ClerkDashboard((Clerk) user, system.getInventoryService()), 900, 560));
        	
        } // end of if else statement
        
        // TEMPORARY FOR TESTING
        //stage.setScene(new Scene(new InventoryScreen(system), 900, 560));
        
        stage.setTitle("Agriculture Data Management System - Inventory");
        
    } // end of onLogin
    
} // end of LoginScreen class