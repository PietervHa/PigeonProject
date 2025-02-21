package com.pieter.pigeonproject;

import com.pieter.pigeonproject.Classes.Navbar;
import com.pieter.pigeonproject.Classes.Database;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountPage {

    private Stage stage;
    private BorderPane mainLayout;
    private Database db;
    private String currentEmail;
    private String currentPassword;

    // Initialiseert de AccountPage en haalt accountgegevens op.
    public AccountPage(Stage stage, Database db) {
        this.stage = stage;
        this.db = new Database();
        fetchAccountData();
    }

    // Laadt de navigatiebalk, toont accountgegevens en retourneert de scène.
    public Scene getScene() {
        // Navbar toevoegen
        Navbar navBarComponent = new Navbar(stage, db);
        mainLayout = navBarComponent.getLayout();

        // GridPane voor de content
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Labels & velden
        Label emailLabel = new Label("E-mail:");
        TextField emailField = new TextField(currentEmail);
        emailField.setEditable(false);

        Label passwordLabel = new Label("Wachtwoord:");
        PasswordField passwordField = new PasswordField();
        passwordField.setText(currentPassword);
        passwordField.setEditable(false);

        // Knoppen
        Button changeEmailButton = new Button("E-mail wijzigen");
        Button changePasswordButton = new Button("Wachtwoord wijzigen");

        // Voeg elementen toe aan het grid
        grid.add(emailLabel, 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(changeEmailButton, 2, 0);

        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(changePasswordButton, 2, 1);

        // Event Handlers voor bewerken
        changeEmailButton.setOnAction(e -> showChangeDialog("email", emailField));
        changePasswordButton.setOnAction(e -> showChangeDialog("password", passwordField));

        mainLayout.setCenter(grid);
        return new Scene(mainLayout, 1900, 1080);
    }

    // Haalt de e-mail en het wachtwoord van de gebruiker op uit de database.
    private void fetchAccountData() {
        String query = "SELECT mail, password FROM users WHERE id = 1"; // Pas ID aan indien nodig

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                currentEmail = rs.getString("mail");
                currentPassword = rs.getString("password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Fout", "Kan accountgegevens niet ophalen.");
        }
    }

    // Toont een dialoogvenster waarmee de gebruiker zijn e-mail of wachtwoord kan wijzigen.
    private void showChangeDialog(String type, TextField field) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Wijzigen " + (type.equals("email") ? "E-mail" : "Wachtwoord"));
        dialog.setHeaderText("Voer je nieuwe " + (type.equals("email") ? "e-mail" : "wachtwoord") + " in:");
        dialog.setContentText(type.equals("email") ? "Nieuw e-mail:" : "Nieuw wachtwoord:");

        dialog.showAndWait().ifPresent(newValue -> {
            // Bevestigingsdialoog
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Bevestigen");
            confirm.setHeaderText("Weet je zeker dat je je " + (type.equals("email") ? "e-mail" : "wachtwoord") + " wilt wijzigen?");
            confirm.setContentText("Nieuwe waarde: " + newValue);

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    updateAccountData(type, newValue);
                    field.setText(newValue);
                }
            });
        });
    }

    // Werkt de e-mail of het wachtwoord van de gebruiker bij in de database.
    private void updateAccountData(String type, String newValue) {
        String query = "UPDATE users SET " + (type.equals("email") ? "mail" : "password") + " = ? WHERE id = 1";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newValue);
            stmt.executeUpdate();
            showAlert("Succes", (type.equals("email") ? "E-mail" : "Wachtwoord") + " succesvol gewijzigd!");

            // Update de lokale variabelen
            if (type.equals("email")) {
                currentEmail = newValue;
            } else {
                currentPassword = newValue;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Fout", "Kan " + (type.equals("email") ? "e-mail" : "wachtwoord") + " niet wijzigen.");
        }
    }

    // Toont een informatieve melding aan de gebruiker.
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}