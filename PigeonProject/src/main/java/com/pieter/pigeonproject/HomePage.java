package com.pieter.pigeonproject;

import com.pieter.pigeonproject.Classes.Database;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.pieter.pigeonproject.Classes.Navbar;

public class HomePage {

    private Stage stage;
    private BorderPane mainLayout;
    private Database db;

    // Initialisatie van de HomePage met het opgegeven stage en databaseverbinding.
    public HomePage(Stage stage, Database db) {
        this.stage = stage;
        this.db = db;
    }

    // Laadt de navigatiebalk, stelt de achtergrondafbeelding in en retourneert de scène.
    public Scene getScene() {
        Navbar navBarComponent = new Navbar(stage, db);
        mainLayout = navBarComponent.getLayout();

        // achtergrond afbeelding laden
        Image backgroundImage = new Image(getClass().getResourceAsStream("images/Pigeon Sitting.webp"));
        BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, false, false);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        mainLayout.setBackground(new Background(background));

        Label homeLabel = new Label("Welcome to the Home Page!");
        mainLayout.setCenter(homeLabel);

        return new Scene(mainLayout, 1900, 1080);
    }
}