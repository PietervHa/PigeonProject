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

    public Navbar(Stage stage) {
        this.stage = stage;
        container = new BorderPane();
        navBar = createNavBar();

        // Initially show the navbar
        container.setLeft(navBar);

        // Toggle button
        Button toggleButton = new Button("☰");
        toggleButton.setOnAction(e -> toggleNavBar());

        HBox topBar = new HBox(toggleButton);
        topBar.setPadding(new Insets(10));

        container.setTop(topBar);
    }

    private VBox createNavBar() {
        VBox navBar = new VBox(10);
        navBar.setPadding(new Insets(10));
        navBar.setStyle("-fx-background-color: #333; -fx-padding: 15px;");

        Button btnHome = new Button("Home");
        Button btnStamkaarten = new Button("Stamkaarten");
        Button btnHokbestand = new Button("Hokbestand");
        Button btnAccount = new Button("Account");
        Button btnLogout = new Button("Uitloggen");

        // Set button actions to navigate between pages
        btnStamkaarten.setOnAction(e -> stage.setScene(new StamKaartenPage(stage).getScene()));
        btnHokbestand.setOnAction(e -> stage.setScene(new HokBestandPage(stage).getScene()));
        btnAccount.setOnAction(e -> stage.setScene(new AccountPage(stage).getScene()));
        btnHome.setOnAction(e -> stage.setScene(new HomePage(stage).getScene()));

        // Logout action with confirmation
        btnLogout.setOnAction(e -> showLogoutConfirmation());

        navBar.getChildren().addAll(btnHome, btnStamkaarten, btnHokbestand, btnAccount, btnLogout);
        return navBar;
    }

    private void toggleNavBar() {
        if (isNavBarOpen) {
            container.setLeft(null); // Hide navbar
        } else {
            container.setLeft(navBar); // Show navbar
        }
        isNavBarOpen = !isNavBarOpen;
    }

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
            // Logout and return to LoginApplication
            LoginApplication loginApp = new LoginApplication();
            loginApp.start(stage);
        }
        // If "Nee" is clicked, the alert just closes and nothing happens
    }

    public BorderPane getLayout() {
        return container;
    }
}