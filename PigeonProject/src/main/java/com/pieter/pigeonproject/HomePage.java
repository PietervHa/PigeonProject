package com.pieter.pigeonproject;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import com.pieter.pigeonproject.Classes.Navbar;

public class HomePage {

    private Stage stage;
    private boolean isNavBarVisible = true;
    private BorderPane mainLayout;
    private Navbar navBarComponent;

    public HomePage(Stage stage) {
        this.stage = stage;
        this.navBarComponent = new Navbar(stage);
    }

    public Scene getHomeScene() {
        // Create main layout
        mainLayout = new BorderPane();

        // Create toggle button
        Button btnToggleNav = new Button("☰");
        btnToggleNav.setOnAction(e -> toggleNavBar());

        HBox topBar = new HBox(10, btnToggleNav);
        topBar.setPadding(new Insets(10));

        // Create Home Screen Content
        Label homeLabel = new Label("Welcome to the Home Page!");

        // Add components to the layout
        mainLayout.setTop(topBar);
        mainLayout.setLeft(navBarComponent.getNavBar());
        mainLayout.setCenter(homeLabel);

        return new Scene(mainLayout, 1900, 1080);
    }

    private void toggleNavBar() {
        if (isNavBarVisible) {
            mainLayout.setLeft(null);
        } else {
            mainLayout.setLeft(navBarComponent.getNavBar());
        }
        isNavBarVisible = !isNavBarVisible;
    }
}