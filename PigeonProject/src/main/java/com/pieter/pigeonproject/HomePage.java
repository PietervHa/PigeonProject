package com.pieter.pigeonproject;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomePage {

    private Stage stage;
    private VBox navBar;
    private boolean isNavBarVisible = true;

    public HomePage(Stage stage) {
        this.stage = stage;
        stage.setTitle("Home Page");
    }

    public Scene getHomeScene() {
        // Create main layout
        BorderPane mainLayout = new BorderPane();

        // Create sidebar navigation
        navBar = new VBox(10);
        navBar.setPadding(new Insets(10));

        Button btnToggleNav = new Button("☰"); // Toggle button for nav bar
        btnToggleNav.setOnAction(e -> toggleNavBar(mainLayout));

        Button btnStamkaarten = new Button("Stamkaarten");
        Button btnHokbestand = new Button("Hokbestand");
        Button btnAccount = new Button("Account");
        Button btnLogout = new Button("Uitloggen");

        btnLogout.setOnAction(e -> {
            LoginApplication loginApp = new LoginApplication();
            loginApp.start(stage);
        });

        navBar.getChildren().addAll(btnStamkaarten, btnHokbestand, btnAccount, btnLogout);

        HBox topBar = new HBox(10, btnToggleNav);
        topBar.setPadding(new Insets(10));

        // Create Home Screen Content
        Label homeLabel = new Label("Welcome to the Home Page!");

        // Add components to the layout
        mainLayout.setTop(topBar);
        mainLayout.setLeft(navBar);
        mainLayout.setCenter(homeLabel);

        return new Scene(mainLayout, 960, 720);

    }

    private void toggleNavBar(BorderPane layout) {
        if (isNavBarVisible) {
            layout.setLeft(null);
        } else {
            layout.setLeft(navBar);
        }
        isNavBarVisible = !isNavBarVisible;
    }
}