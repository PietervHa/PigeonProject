package com.pieter.pigeonproject;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import com.pieter.pigeonproject.Classes.Navbar;

public class HomePage {

    private Stage stage;
    private BorderPane mainLayout;

    public HomePage(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        // Create main layout
        Navbar navBarComponent = new Navbar(stage);
        mainLayout = navBarComponent.getLayout();

        // Create Home Screen Content
        Label homeLabel = new Label("Welcome to the Home Page!");
        mainLayout.setCenter(homeLabel);

        return new Scene(mainLayout, 1900, 1080);
    }
}