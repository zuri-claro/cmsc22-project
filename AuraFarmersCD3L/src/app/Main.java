package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.LoginScreen;

public class Main extends Application{
	
	@Override
    public void start(Stage stage) {
		AgriSystem system = AgriSystem.getInstance();
		
        stage.setTitle("Agriculture Data Management System");
        stage.setScene(new Scene(new LoginScreen(system, stage), 900, 560));
        stage.show();
		
	} // end of start
	
    public static void main(String[] args) {
    	
        launch(args);
        
    } // end of main
	
} // end of Main class
