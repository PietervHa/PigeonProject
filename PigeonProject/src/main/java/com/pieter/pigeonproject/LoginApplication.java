package com.pieter.pigeonproject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LoginApplication extends Application {

    @Override
    public void start(Stage stage) {
        // Create Login Screen
        GridPane loginPane = new GridPane();
        loginPane.setPadding(new Insets(20));
        loginPane.setVgap(10);
        loginPane.setHgap(10);
        loginPane.setAlignment(Pos.CENTER);

        // Add UI components
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        Button createAccountButton = new Button("Create Account");

        loginPane.add(usernameLabel, 0, 0);
        loginPane.add(usernameField, 1, 0);
        loginPane.add(passwordLabel, 0, 1);
        loginPane.add(passwordField, 1, 1);
        loginPane.add(loginButton, 0, 2);
        loginPane.add(createAccountButton, 1, 2);

        Scene loginScene = new Scene(loginPane, 320, 240);

        // Set button actions
        loginButton.setOnAction(event -> {
            // Navigate to HomePage
            HomePage homePage = new HomePage();
            stage.setScene(homePage.getHomeScene());
        });

        createAccountButton.setOnAction(event -> System.out.println("Account creation logic here"));

        // Setup the stage
        stage.setTitle("Login Screen");
        stage.setScene(loginScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}