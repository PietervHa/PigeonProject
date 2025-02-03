package com.pieter.pigeonproject;

import com.pieter.pigeonproject.Classes.Navbar;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class StamKaartenPage {

    private Stage stage;
    private BorderPane mainLayout;

    public StamKaartenPage(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        // Create main layout using Navbar class
        Navbar navBarComponent = new Navbar(stage);
        mainLayout = navBarComponent.getLayout();

        // Create Stamkaarten Page Content
        Label titleLabel = new Label("Stamkaarten Page");
        mainLayout.setCenter(titleLabel);

        return new Scene(mainLayout, 1900, 1080);
    }
}
