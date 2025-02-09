package com.pieter.pigeonproject;

import com.pieter.pigeonproject.Classes.Database;
import com.pieter.pigeonproject.Classes.Navbar;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class StamKaartenPage {

    private Stage stage;
    private BorderPane mainLayout;
    private Database db;

    public StamKaartenPage(Stage stage, Database db) {
        this.stage = stage;
        this.db = db;
    }

    public Scene getScene() {
        // Create main layout using Navbar class
        Navbar navBarComponent = new Navbar(stage, db);
        mainLayout = navBarComponent.getLayout();

        // Create Stamkaarten Page Content
        Label titleLabel = new Label("Stamkaarten Page");
        mainLayout.setCenter(titleLabel);

        return new Scene(mainLayout, 1900, 1080);
    }
}
