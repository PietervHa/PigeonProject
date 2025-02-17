package com.pieter.pigeonproject;

import com.pieter.pigeonproject.Classes.Database;
import com.pieter.pigeonproject.Classes.Navbar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StamKaartenPage {

    private Stage stage;
    private Database db;
    private BorderPane contentLayout;
    private ListView<String> stamkaartenList;
    private ObservableList<String> stamkaartenData;
    private ListView<String> pigeonList;
    private ObservableList<String> pigeonData;

    public StamKaartenPage(Stage stage, Database db) {
        this.stage = stage;
        this.db = db; // Keep the database connection
    }

    public Scene getScene() {
        // Initialize navbar
        Navbar navbar = new Navbar(stage, db);
        BorderPane mainLayout = navbar.getLayout();  // Use the Navbar layout

        // Content layout for stamkaarten and pigeons
        contentLayout = new BorderPane();

        // List for Stamkaarten
        stamkaartenList = new ListView<>();
        stamkaartenData = FXCollections.observableArrayList();
        pigeonList = new ListView<>();
        pigeonData = FXCollections.observableArrayList();

        loadStamkaarten();

        // Left: Stamkaarten List
        VBox leftPane = new VBox(10, new Label("Stamkaarten"), stamkaartenList);
        leftPane.setPadding(new Insets(10));

        // Right: Pigeon List in selected stamkaart
        VBox rightPane = new VBox(10, new Label("Pigeons in Stamkaart"), pigeonList);
        rightPane.setPadding(new Insets(10));

        // Buttons
        Button btnAdd = new Button("➕ Add Stamkaart");
        Button btnRename = new Button("✏ Rename");
        Button btnDelete = new Button("🗑 Delete");
        Button btnAddPigeon = new Button("➕ Add Pigeon");
        Button btnRemovePigeon = new Button("❌ Remove Pigeon");

        HBox buttonBar = new HBox(10, btnAdd, btnRename, btnDelete);
        HBox pigeonBar = new HBox(10, btnAddPigeon, btnRemovePigeon);
        buttonBar.setPadding(new Insets(10));
        pigeonBar.setPadding(new Insets(10));

        // Set up the content inside the content layout
        contentLayout.setLeft(leftPane);
        contentLayout.setRight(rightPane);
        contentLayout.setTop(buttonBar);
        contentLayout.setBottom(pigeonBar);

        // Add the content inside the navbar layout
        mainLayout.setCenter(contentLayout);

        // Event Handlers
        stamkaartenList.setOnMouseClicked(event -> selectStamkaart());
        btnAdd.setOnAction(event -> createStamkaart());
        btnRename.setOnAction(event -> renameStamkaart());
        btnDelete.setOnAction(event -> deleteStamkaart());
        btnAddPigeon.setOnAction(event -> addPigeonToStamkaart());
        btnRemovePigeon.setOnAction(event -> removePigeonFromStamkaart());

        return new Scene(mainLayout, 900, 600);
    }

    private void loadStamkaarten() {
        stamkaartenData.clear();
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT naam FROM stamkaarten");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                stamkaartenData.add(rs.getString("naam"));
            }
            stamkaartenList.setItems(stamkaartenData);
        } catch (SQLException e) {
            showAlert("Error", "Fout bij laden van stamkaarten: " + e.getMessage());
        }
    }

    private void selectStamkaart() {
        String selected = stamkaartenList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            loadPigeonsForStamkaart(selected);
        }
    }

    private void loadPigeonsForStamkaart(String stamkaartNaam) {
        pigeonData.clear();
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT d.ringnummer FROM duiven d " +
                             "JOIN stamkaart_duiven sd ON d.ringnummer = sd.ringnummer " +
                             "JOIN stamkaarten s ON sd.stamkaart_id = s.stamkaart_id " +
                             "WHERE s.naam = ?")) {

            stmt.setString(1, stamkaartNaam);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                pigeonData.add(rs.getString("ringnummer"));
            }
            pigeonList.setItems(pigeonData);
        } catch (SQLException e) {
            showAlert("Error", "Fout bij laden van duiven: " + e.getMessage());
        }
    }

    private void addPigeonToStamkaart() {
        String selectedStamkaart = stamkaartenList.getSelectionModel().getSelectedItem();
        if (selectedStamkaart == null) {
            showAlert("Fout", "Selecteer eerst een stamkaart.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Duif toevoegen");
        dialog.setHeaderText("Voer het ringnummer van de duif in:");
        dialog.setContentText("Ringnummer:");

        dialog.showAndWait().ifPresent(ringnummer -> {
            try (Connection conn = db.getConnection()) {
                // Get stamkaart_id
                PreparedStatement getStamkaartId = conn.prepareStatement("SELECT stamkaart_id FROM stamkaarten WHERE naam = ?");
                getStamkaartId.setString(1, selectedStamkaart);
                ResultSet rs = getStamkaartId.executeQuery();

                if (rs.next()) {
                    int stamkaartId = rs.getInt("stamkaart_id");

                    // Check if pigeon exists
                    PreparedStatement checkPigeon = conn.prepareStatement("SELECT * FROM duiven WHERE ringnummer = ?");
                    checkPigeon.setString(1, ringnummer);
                    ResultSet pigeonExists = checkPigeon.executeQuery();

                    if (!pigeonExists.next()) {
                        showAlert("Fout", "Deze duif bestaat niet in de database.");
                        return;
                    }

                    // Insert into stamkaart_duiven
                    PreparedStatement stmt = conn.prepareStatement("INSERT INTO stamkaart_duiven (stamkaart_id, ringnummer) VALUES (?, ?)");
                    stmt.setInt(1, stamkaartId);
                    stmt.setString(2, ringnummer);
                    stmt.executeUpdate();

                    loadPigeonsForStamkaart(selectedStamkaart);
                } else {
                    showAlert("Fout", "Stamkaarten ID niet gevonden.");
                }
            } catch (SQLException e) {
                showAlert("Error", "Fout bij toevoegen van duif: " + e.getMessage());
            }
        });
    }

    private void removePigeonFromStamkaart() {
        String selectedStamkaart = stamkaartenList.getSelectionModel().getSelectedItem();
        String selectedPigeon = pigeonList.getSelectionModel().getSelectedItem();

        if (selectedStamkaart == null) {
            showAlert("Error", "Selecteer een stamkaart voordat je een duif verwijdert.");
            return;
        }

        if (selectedPigeon == null) {
            showAlert("Error", "Selecteer een duif om te verwijderen.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Weet je zeker dat je deze duif wilt verwijderen?", ButtonType.YES, ButtonType.NO);

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try (Connection conn = db.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(
                             "DELETE FROM stamkaart_duiven WHERE ringnummer = ? " +
                                     "AND stamkaart_id = (SELECT stamkaart_id FROM stamkaarten WHERE naam = ?)")) {

                    stmt.setString(1, selectedPigeon);
                    stmt.setString(2, selectedStamkaart);
                    stmt.executeUpdate();

                    loadPigeonsForStamkaart(selectedStamkaart); // Refresh de lijst na verwijderen
                } catch (SQLException e) {
                    showAlert("Error", "Fout bij verwijderen van duif: " + e.getMessage());
                }
            }
        });
    }

    private void createStamkaart() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nieuwe Stamkaart");
        dialog.setHeaderText("Voer een naam in voor de nieuwe stamkaart:");
        dialog.setContentText("Naam:");

        dialog.showAndWait().ifPresent(name -> {
            try (Connection conn = db.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO stamkaarten (naam) VALUES (?)")) {
                stmt.setString(1, name);
                stmt.executeUpdate();
                loadStamkaarten();
            } catch (SQLException e) {
                showAlert("Error", "Fout bij toevoegen stamkaart: " + e.getMessage());
            }
        });
    }

    private void renameStamkaart() {
        String selected = stamkaartenList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog(selected);
        dialog.setTitle("Stamkaart hernoemen");
        dialog.setHeaderText("Nieuwe naam voor de stamkaart:");
        dialog.setContentText("Naam:");

        dialog.showAndWait().ifPresent(newName -> {
            try (Connection conn = db.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE stamkaarten SET naam = ? WHERE naam = ?")) {
                stmt.setString(1, newName);
                stmt.setString(2, selected);
                stmt.executeUpdate();
                loadStamkaarten();
            } catch (SQLException e) {
                showAlert("Error", "Fout bij hernoemen stamkaart: " + e.getMessage());
            }
        });
    }

    private void deleteStamkaart() {
        String selected = stamkaartenList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Weet je zeker dat je deze stamkaart wilt verwijderen?",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try (Connection conn = db.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM stamkaarten WHERE naam = ?")) {
                    stmt.setString(1, selected);
                    stmt.executeUpdate();
                    loadStamkaarten();
                } catch (SQLException e) {
                    showAlert("Error", "Fout bij verwijderen stamkaart: " + e.getMessage());
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}