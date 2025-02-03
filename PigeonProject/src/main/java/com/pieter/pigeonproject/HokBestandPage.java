package com.pieter.pigeonproject;

import com.pieter.pigeonproject.Classes.Navbar;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class HokBestandPage {

    private Stage stage;
    private BorderPane mainLayout;

    public HokBestandPage(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        // Create main layout using Navbar class
        Navbar navBarComponent = new Navbar(stage);
        mainLayout = navBarComponent.getLayout();

        // Page Content
        Label titleLabel = new Label("Hokbestand Page");
        mainLayout.setCenter(titleLabel);

        return new Scene(mainLayout, 1900, 1080);
    }
}
