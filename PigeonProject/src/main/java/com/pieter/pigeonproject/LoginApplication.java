package com.pieter.pigeonproject;

import com.pieter.pigeonproject.Classes.Database;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginApplication extends Application {

    private Scene loginScene;
    private Scene signUpScene;
    private Database db;

    @Override
    // Start de JavaFX-toepassing, initialiseert de database en stelt de eerste scène in.
    public void start(Stage stage) {
        db = new Database();

        loginScene = createLoginScene(stage);
        signUpScene = createSignUpScene(stage);

        stage.setTitle("Login Screen");
        stage.setScene(loginScene);
        stage.show();
    }

    // Creëert de inlogscène met invoervelden voor e-mail en wachtwoord, een login-knop en een knop om een nieuw account aan te maken.
    private Scene createLoginScene(Stage stage) {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        // Het logo laden en tonen
        Image logo = new Image(getClass().getResourceAsStream("images/Pigeon Logo.JPG"));
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(250);  // Resize width
        logoView.setPreserveRatio(true);

        GridPane loginPane = new GridPane();
        loginPane.setPadding(new Insets(20));
        loginPane.setVgap(10);
        loginPane.setHgap(10);
        loginPane.setAlignment(Pos.CENTER);

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

        root.getChildren().addAll(logoView, loginPane);

        loginButton.setOnAction(event -> {
            String email = usernameField.getText();
            String password = passwordField.getText();

            if (validateLogin(email, password)) {
                HomePage homePage = new HomePage(stage, db);
                stage.setScene(homePage.getScene());
            } else {
                showAlert("Login Failed", "Invalid email or password.");
            }
        });

        createAccountButton.setOnAction(event -> stage.setScene(signUpScene));

        return new Scene(root, 400, 350);
    }

    // Creëert de aanmeldscène met invoervelden voor naam, e-mail en wachtwoord, een knop om een account aan te maken en een terug-knop naar het inlogscherm.
    private Scene createSignUpScene(Stage stage) {
        GridPane signUpPane = new GridPane();
        signUpPane.setPadding(new Insets(20));
        signUpPane.setVgap(10);
        signUpPane.setHgap(10);
        signUpPane.setAlignment(Pos.CENTER);

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

        signUpButton.setOnAction(event -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();

            if (registerUser(name, email, password)) {
                showAlert("Sign Up Successful", "Your account has been created.");
                stage.setScene(loginScene);
            } else {
                showAlert("Sign Up Failed", "An account with this email already exists.");
            }
        });

        backButton.setOnAction(event -> stage.setScene(loginScene));

        return new Scene(signUpPane, 400, 350);
    }

    // Controleert of de ingevoerde inloggegevens overeenkomen met een bestaande gebruiker in de database.
    private boolean validateLogin(String email, String password) {
        String query = "SELECT * FROM users WHERE mail = ? AND password = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Unable to connect to the database.");
        }
        return false;
    }

    // Voegt een nieuwe gebruiker toe aan de database als het e-mailadres nog niet in gebruik is.
    private boolean registerUser(String name, String email, String password) {
        String checkQuery = "SELECT * FROM users WHERE mail = ?";
        String insertQuery = "INSERT INTO users (name, mail, password) VALUES (?, ?, ?)";

        try {
            try (PreparedStatement checkStmt = db.getConnection().prepareStatement(checkQuery)) {
                checkStmt.setString(1, email);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        return false;
                    }
                }
            }

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

    // Toont een pop-upmelding met een titel en bericht om de gebruiker te informeren over een actie of foutmelding.
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