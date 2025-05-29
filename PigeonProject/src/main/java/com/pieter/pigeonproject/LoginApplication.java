package com.pieter.pigeonproject;

import com.pieter.pigeonproject.Controllers.LoginController;
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

/**
 * Frontend klasse voor login- en registratieschermen.
 * Behandelt alle UI-componenten en gebruikersinteracties.
 */
public class LoginApplication extends Application {

    private Scene loginScene;
    private Scene signUpScene;

    // Backend controller instantie voor login & registratie
    private LoginController loginController;

    @Override
    public void start(Stage stage) {
        loginController = new LoginController(new com.pieter.pigeonproject.Classes.Database());

        loginScene = createLoginScene(stage);
        signUpScene = createSignUpScene(stage);

        stage.setTitle("Login Screen");
        stage.setScene(loginScene);
        stage.show();
    }

    /**
     * Creëert de login-scène met e-mail, wachtwoord, en knoppen.
     */
    private Scene createLoginScene(Stage stage) {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Image logo = new Image(getClass().getResourceAsStream("images/Pigeon Logo.JPG"));
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(250);
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

            if (loginController.validateLogin(email, password)) {
                HomePage homePage = new HomePage(stage, loginController.getDb());
                stage.setScene(homePage.getScene());
            } else {
                showAlert("Login Failed", "Invalid email or password.");
            }
        });

        createAccountButton.setOnAction(event -> stage.setScene(signUpScene));

        return new Scene(root, 400, 350);
    }

    /**
     * Creëert het registratiescherm met velden en knoppen.
     */
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

            // Aanroep backend registratie
            if (loginController.registerUser(name, email, password)) {
                showAlert("Sign Up Successful", "Your account has been created.");
                stage.setScene(loginScene);
            } else {
                showAlert("Sign Up Failed", "An account with this email already exists.");
            }
        });

        backButton.setOnAction(event -> stage.setScene(loginScene));

        return new Scene(signUpPane, 400, 350);
    }

    /**
     * Toont een pop-upmelding.
     */
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
