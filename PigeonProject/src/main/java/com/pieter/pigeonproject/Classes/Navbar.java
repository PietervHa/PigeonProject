package com.pieter.pigeonproject.Classes;

import com.pieter.pigeonproject.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Optional;

public class Navbar {
    private VBox navBar;
    private BorderPane container;
    private boolean isNavBarOpen = true;
    private Stage stage;
    private Database db;

    // Constructor die de stage en database ontvangt en de navbar instelt
    public Navbar(Stage stage, Database db) {
        this.stage = stage;
        this.db = db;
        container = new BorderPane();
        navBar = createNavBar();

        container.setLeft(navBar);

        Button toggleButton = new Button("☰");
        toggleButton.setOnAction(e -> toggleNavBar());

        HBox topBar = new HBox(toggleButton);
        topBar.setPadding(new Insets(10));

        container.setTop(topBar);
    }

    // Maakt de navigatiebalk aan met knoppen
    private VBox createNavBar() {
        VBox navBar = new VBox(10);
        navBar.setPadding(new Insets(10));
        navBar.setStyle("-fx-background-color: #333; -fx-padding: 15px;");

        Button btnHome = new Button("Home");
        Button btnStamkaarten = new Button("Stamkaarten");
        Button btnHokbestand = new Button("Hokbestand");
        Button btnAccount = new Button("Account");
        Button btnLogout = new Button("Uitloggen");

        btnStamkaarten.setOnAction(e -> stage.setScene(new StamKaartenPage(stage, db).getScene()));
        btnHokbestand.setOnAction(e -> stage.setScene(new HokBestandPage(stage, db).getScene()));
        btnAccount.setOnAction(e -> stage.setScene(new AccountPage(stage, db).getScene()));
        btnHome.setOnAction(e -> stage.setScene(new HomePage(stage, db).getScene()));


        btnLogout.setOnAction(e -> showLogoutConfirmation());

        navBar.getChildren().addAll(btnHome, btnStamkaarten, btnHokbestand, btnAccount, btnLogout);
        return navBar;
    }

    // Wisselt de zichtbaarheid van de navigatiebalk
    private void toggleNavBar() {
        if (isNavBarOpen) {
            container.setLeft(null);
        } else {
            container.setLeft(navBar);
        }
        isNavBarOpen = !isNavBarOpen;
    }

    // Toont een bevestigingsdialoog voor uitloggen
    private void showLogoutConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bevestiging");
        alert.setHeaderText("Bent u zeker dat u wilt uitloggen?");
        alert.setContentText("Kies een optie:");

        ButtonType buttonLogout = new ButtonType("Log uit");
        ButtonType buttonCancel = new ButtonType("Nee");

        alert.getButtonTypes().setAll(buttonLogout, buttonCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonLogout) {
            LoginApplication loginApp = new LoginApplication();
            loginApp.start(stage);
        }

    }

    // Geeft de layout van de container terug
    public BorderPane getLayout() {
        return container;
    }
}