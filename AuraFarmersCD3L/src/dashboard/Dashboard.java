package dashboard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/*
 * Dashboard class
 * - Parent class for ManagerDashboard and ClerkDashboard
 * - Contains reusable dashboard components
 */

public class Dashboard extends BorderPane {

    // Creates the header used by the dashboards
    protected VBox createHeader(String title, String subtitle) {

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 26px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #eeeeee;"
        );

        VBox header = new VBox(5, titleLabel, subtitleLabel);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #542020;");

        return header;
    }

    // Creates a card for displaying dashboard statistics
    protected VBox createStatCard(String title, Label value) {

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #666666;"
        );

        value.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #542020;"
        );

        VBox card = new VBox(10, titleLabel, value);
        card.setPadding(new Insets(15));
        card.setPrefWidth(190);
        card.setAlignment(Pos.CENTER_LEFT);

        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #dddddd;" +
            "-fx-border-radius: 6px;" +
            "-fx-background-radius: 6px;"
        );

        return card;
    }

    // Places statistic cards in one row
    protected HBox createStatRow(VBox... cards) {

        HBox row = new HBox(15, cards);
        row.setAlignment(Pos.CENTER_LEFT);

        return row;
    }

} // end of Dashboard class
