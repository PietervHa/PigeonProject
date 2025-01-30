package com.pieter.pigeonproject;

import com.pieter.pigeonproject.Classes.Navbar;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class HokBestandPage {

    private Stage stage;

    public HokBestandPage(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        // Create main layout
        BorderPane mainLayout = new BorderPane();

        // Add the reusable NavBar
        Navbar navBar = new Navbar(stage);
        mainLayout.setLeft(navBar.getNavBar());

        // Page content
        Label titleLabel = new Label("Hokbestand Page");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-padding: 20px;");
        mainLayout.setCenter(titleLabel);

        return new Scene(mainLayout, 1900, 1080);
    }
}
