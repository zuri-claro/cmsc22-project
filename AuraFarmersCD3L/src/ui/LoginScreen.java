package ui;

import app.AgriSystem;
import model.*;
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
        
        // title and subtitle
        Label title = new Label("Agriculture Data Management System");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        Label subtitle = new Label("Please log in");
        
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

        // TEMPORARY FOR TESTINGGGG
        Label hint = new Label("Test accounts: manager | manager123, clerk | clerk123");
        hint.setStyle("-fx-text-fill: gray;");

        setSpacing(12);
        setPadding(new Insets(30));
        setAlignment(Pos.CENTER);
        getChildren().addAll(title, subtitle, usernameField, passwordField, loginButton, errorLabel, hint);
        
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
        	//stage.setScene(new Scene(new ManagerDashboard(system), 900, 560));
        } else {
        	//stage.setScene(new Scene(new ClerkDashboard(system), 900, 560));
        } // end of if else statement
        
        // TEMPORARY FOR TESTING
        //stage.setScene(new Scene(new InventoryScreen(system), 900, 560));
        
        stage.setTitle("Agriculture Data Management System - Inventory");
        
    } // end of onLogin
    
} // end of LoginScreen class