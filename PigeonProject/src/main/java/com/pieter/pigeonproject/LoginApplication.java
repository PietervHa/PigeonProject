package com.pieter.pigeonproject;

import com.pieter.pigeonproject.Classes.Database;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginApplication extends Application {

    private Scene loginScene;
    private Scene signUpScene;
    private Database db; // Database object for reuse

    @Override
    public void start(Stage stage) {
        db = new Database(); // Create the database connection once

        // Initialize scenes for login and sign-up
        loginScene = createLoginScene(stage);
        signUpScene = createSignUpScene(stage);

        // Setup the stage
        stage.setTitle("Login Screen");
        stage.setScene(loginScene); // Start with the login scene
        stage.show();
    }

    private Scene createLoginScene(Stage stage) {
        GridPane loginPane = new GridPane();
        loginPane.setPadding(new Insets(20));
        loginPane.setVgap(10);
        loginPane.setHgap(10);
        loginPane.setAlignment(Pos.CENTER);

        // Add UI components
        Label usernameLabel = new Label("Email:");
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

        // Set button actions
        loginButton.setOnAction(event -> {
            String email = usernameField.getText();
            String password = passwordField.getText();

            if (validateLogin(email, password)) {
                // Navigate to HomePage if login is successful
                HomePage homePage = new HomePage(stage, db);
                stage.setScene(homePage.getScene());
            } else {
                showAlert("Login Failed", "Invalid email or password.");
            }
        });

        createAccountButton.setOnAction(event -> stage.setScene(signUpScene));

        return new Scene(loginPane, 320, 240);
    }

    private Scene createSignUpScene(Stage stage) {
        GridPane signUpPane = new GridPane();
        signUpPane.setPadding(new Insets(20));
        signUpPane.setVgap(10);
        signUpPane.setHgap(10);
        signUpPane.setAlignment(Pos.CENTER);

        // Add UI components
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button signUpButton = new Button("Sign Up");
        Button backButton = new Button("Back");

        signUpPane.add(nameLabel, 0, 0);
        signUpPane.add(nameField, 1, 0);
        signUpPane.add(emailLabel, 0, 1);
        signUpPane.add(emailField, 1, 1);
        signUpPane.add(passwordLabel, 0, 2);
        signUpPane.add(passwordField, 1, 2);
        signUpPane.add(signUpButton, 0, 3);
        signUpPane.add(backButton, 1, 3);

        // Set button actions
        signUpButton.setOnAction(event -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();

            if (registerUser(name, email, password)) {
                showAlert("Sign Up Successful", "Your account has been created.");
                stage.setScene(loginScene); // Return to login page
            } else {
                showAlert("Sign Up Failed", "An account with this email already exists.");
            }
        });

        backButton.setOnAction(event -> stage.setScene(loginScene));

        return new Scene(signUpPane, 320, 240);
    }

    private boolean validateLogin(String email, String password) {
        String query = "SELECT * FROM users WHERE mail = ? AND password = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // If a row is found, login is valid
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Unable to connect to the database.");
        }
        return false;
    }

    private boolean registerUser(String name, String email, String password) {
        String checkQuery = "SELECT * FROM users WHERE mail = ?";
        String insertQuery = "INSERT INTO users (name, mail, password) VALUES (?, ?, ?)";

        try {
            // Check if email already exists
            try (PreparedStatement checkStmt = db.getConnection().prepareStatement(checkQuery)) {
                checkStmt.setString(1, email);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        return false; // Email already exists
                    }
                }
            }

            // Insert new user into database
            try (PreparedStatement insertStmt = db.getConnection().prepareStatement(insertQuery)) {
                insertStmt.setString(1, name);
                insertStmt.setString(2, email);
                insertStmt.setString(3, password);
                insertStmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Unable to connect to the database.");
        }
        return false;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}