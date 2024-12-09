/*package com.pieter.pigeonproject;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class SignUpPage {

    public Scene getSignUpScene(Stage stage) {
        // Create Sign-Up Screen
        GridPane signUpPane = new GridPane();
        signUpPane.setPadding(new Insets(20));
        signUpPane.setVgap(10);
        signUpPane.setHgap(10);
        signUpPane.setAlignment(Pos.CENTER);

        // Add UI components
        Label usernameLabel = new Label("New Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("New Password:");
        PasswordField passwordField = new PasswordField();
        Button signUpButton = new Button("Sign Up");
        Button backButton = new Button("Back");

        signUpPane.add(usernameLabel, 0, 0);
        signUpPane.add(usernameField, 1, 0);
        signUpPane.add(passwordLabel, 0, 1);
        signUpPane.add(passwordField, 1, 1);
        signUpPane.add(signUpButton, 0, 2);
        signUpPane.add(backButton, 1, 2);

        // Set button actions
        signUpButton.setOnAction(event -> {
            System.out.println("Account creation logic here!");
        });

        backButton.setOnAction(event -> {
            // Navigate back to Login Page
            LoginApplication loginApplication = new LoginApplication();
            loginApplication.start(stage);
        });

        return new Scene(signUpPane, 320, 240);
    }
}*/