package com.pieter.pigeonproject;

import com.pieter.pigeonproject.Classes.Navbar;
import com.pieter.pigeonproject.Classes.Database;
import com.pieter.pigeonproject.Controllers.AccountPageController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AccountPage {

    private final Stage stage; // Verwijzing naar het hoofdvenster (voor navigatie)
    private final Database db; // Database-object (wordt alleen doorgegeven)
    private final AccountPageController controller; // Backend controller voor deze pagina

    private String currentEmail;
    private String currentPassword;

    // Constructor: initialiseert de controller en haalt de gegevens op
    public AccountPage(Stage stage, Database db) {
        this.stage = stage;
        this.db = db;
        this.controller = new AccountPageController(db);
        loadAccountData(); // Vul currentEmail en currentPassword
    }

    // Bouwt de pagina-layout en retourneert een Scene die getoond kan worden in de stage
    public Scene getScene() {
        // Maak de hoofdlayout met navbar aan
        BorderPane mainLayout = new Navbar(stage, db).getLayout();

        // Grid voor de invoervelden en knoppen
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        // E-mailadres veld (niet bewerkbaar)
        Label emailLabel = new Label("E-mail:");
        TextField emailField = new TextField(currentEmail);
        emailField.setEditable(false);

        // Wachtwoord veld (niet bewerkbaar)
        Label passwordLabel = new Label("Wachtwoord:");
        PasswordField passwordField = new PasswordField();
        passwordField.setText(currentPassword);
        passwordField.setEditable(false);

        // Wijzig-knoppen
        Button changeEmailButton = new Button("E-mail wijzigen");
        Button changePasswordButton = new Button("Wachtwoord wijzigen");

        // Voeg labels, velden en knoppen toe aan het grid
        grid.add(emailLabel, 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(changeEmailButton, 2, 0);

        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(changePasswordButton, 2, 1);

        // Event handlers voor wijzigingen
        changeEmailButton.setOnAction(e -> showChangeDialog("email", emailField));
        changePasswordButton.setOnAction(e -> showChangeDialog("password", passwordField));

        // Plaats het grid in het midden van het scherm
        mainLayout.setCenter(grid);

        return new Scene(mainLayout, 1900, 1080);
    }

    // Haalt huidige accountgegevens op uit de backend (controller)
    private void loadAccountData() {
        String[] data = controller.fetchAccountData();
        currentEmail = data[0];
        currentPassword = data[1];
    }

    // Toont een invoerdialoog voor wijziging van e-mail of wachtwoord
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
                    boolean success = controller.updateAccountData(type, newValue);

                    if (success) {
                        field.setText(newValue); // Pas het veld aan in de UI
                        if (type.equals("email")) currentEmail = newValue;
                        else currentPassword = newValue;
                        showAlert("Succes", "Gegevens succesvol gewijzigd.");
                    } else {
                        showAlert("Fout", "Wijziging mislukt.");
                    }
                }
            });
        });
    }

    // Toont een eenvoudige melding aan de gebruiker
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
