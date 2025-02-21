package com.pieter.pigeonproject;

import com.pieter.pigeonproject.Classes.Database;
import com.pieter.pigeonproject.Classes.FamilyTreePopup;
import com.pieter.pigeonproject.Classes.Navbar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;

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
        this.db = db; // Database connectie behouden
    }

    public Scene getScene() {
        // Initializeer de navbar
        Navbar navbar = new Navbar(stage, db);
        BorderPane mainLayout = navbar.getLayout();  // Use the Navbar layout

        // Content layout voor stamkaarten en duiven
        contentLayout = new BorderPane();

        // List voor Stamkaarten
        stamkaartenList = new ListView<>();
        stamkaartenData = FXCollections.observableArrayList();
        pigeonList = new ListView<>();
        pigeonData = FXCollections.observableArrayList();

        loadStamkaarten();

        // Left Pane: Stamkaarten List
        Label stamkaartenLabel = new Label("Stamkaarten");
        VBox leftPane = new VBox(10, stamkaartenLabel, stamkaartenList);
        leftPane.setPadding(new Insets(10));
        leftPane.setPrefWidth(300); // Keep it a fixed width

        // Right Pane: Pigeon List
        Label pigeonLabel = new Label("Pigeons in Stamkaart");
        VBox pigeonListContainer = new VBox(10, pigeonLabel, pigeonList);
        pigeonListContainer.setPadding(new Insets(10));

        // ScrollPane zodat je kan scrollen in de pigeon list
        ScrollPane pigeonScrollPane = new ScrollPane(pigeonListContainer);
        pigeonScrollPane.setFitToWidth(true);
        pigeonScrollPane.setFitToHeight(true);
        pigeonScrollPane.setPrefWidth(1600); // Allow it to take most of the screen
        pigeonScrollPane.setPadding(new Insets(10));

        // Buttons voor Stamkaarten
        Button btnAdd = new Button("➕ Add Stamkaart");
        Button btnRename = new Button("✏ Rename");
        Button btnDelete = new Button("🗑 Delete");
        Button btnViewTree = new Button("Bekijk Stamkaart");

        HBox buttonBar = new HBox(10, btnAdd, btnRename, btnDelete, btnViewTree);
        buttonBar.setPadding(new Insets(10));

        // Buttons voor Pigeons
        Button btnAddPigeon = new Button("➕ Add Pigeon");
        Button btnRemovePigeon = new Button("❌ Remove Pigeon");

        HBox pigeonBar = new HBox(10, btnAddPigeon, btnRemovePigeon);
        pigeonBar.setPadding(new Insets(10));

        // Right Pane: Pigeon List + Buttons
        VBox rightPane = new VBox(10, pigeonScrollPane, pigeonBar);
        rightPane.setPadding(new Insets(10));
        rightPane.setMaxWidth(Double.MAX_VALUE);

        // Layout Setup
        contentLayout.setLeft(leftPane);   // Stamkaarten on the left
        contentLayout.setCenter(rightPane); // Pigeons on the right
        contentLayout.setTop(buttonBar);    // Stamkaarten buttons on top

        // de content toevoegen aan de navbar layout
        mainLayout.setCenter(contentLayout);

        // Event Handlers
        stamkaartenList.setOnMouseClicked(event -> selectStamkaart());
        btnAdd.setOnAction(event -> createStamkaart());
        btnRename.setOnAction(event -> renameStamkaart());
        btnDelete.setOnAction(event -> deleteStamkaart());
        btnViewTree.setOnAction(event -> {
            String selectedStamkaart = stamkaartenList.getSelectionModel().getSelectedItem();

            if (selectedStamkaart == null) {
                showAlert("Fout", "Selecteer een stamkaart om het familieoverzicht te bekijken.");
                return;
            }

            FamilyTreePopup familyTreePopup = new FamilyTreePopup(db);
            familyTreePopup.showFamilyTree(selectedStamkaart); // Pass the selected stamkaart name
        });
        btnAddPigeon.setOnAction(event -> addPigeonToStamkaart());
        btnRemovePigeon.setOnAction(event -> removePigeonFromStamkaart());

        return new Scene(mainLayout, 1900, 1080);
    }

    // Haalt alle stamkaarten op uit de database en vult de lijst met stamkaarten.
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

    // Laadt de bijbehorende duiven wanneer een stamkaart wordt geselecteerd.
    private void selectStamkaart() {
        String selected = stamkaartenList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            loadPigeonsForStamkaart(selected);
        }
    }

    // Haalt alle duiven op die gekoppeld zijn aan een specifieke stamkaart.
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

    // Voegt een duif toe aan een stamkaart en koppelt deze met de juiste familiepositie.
    private void addPigeonToStamkaart() {
        String selectedStamkaart = stamkaartenList.getSelectionModel().getSelectedItem();
        if (selectedStamkaart == null) {
            showAlert("Fout", "Selecteer eerst een stamkaart.");
            return;
        }

        // Creeer dialog elements
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Duif toevoegen");
        dialog.setHeaderText("Voer het ringnummer van de duif in en selecteer een familiepositie:");

        // Ringnummer input field
        TextField ringnummerField = new TextField();
        ringnummerField.setPromptText("Ringnummer");

        // Parent selection dropdown
        ComboBox<String> parentTypeBox = new ComboBox<>();
        parentTypeBox.getItems().addAll(
                "Child", "Father", "Mother",
                "GrandFather A", "GrandMother A", "GrandFather B", "GrandMother B",
                "GreatGrandFather 1", "GreatGrandMother 1", "GreatGrandFather 2", "GreatGrandMother 2",
                "GreatGrandFather 3", "GreatGrandMother 3", "GreatGrandFather 4", "GreatGrandMother 4"
        );
        parentTypeBox.setValue("Child"); // standaard

        // Layout voor de input velden
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Ringnummer:"), 0, 0);
        grid.add(ringnummerField, 1, 0);
        grid.add(new Label("Familiepositie:"), 0, 1);
        grid.add(parentTypeBox, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // voeg ok and cancel knoppen toe
        ButtonType okButton = new ButtonType("Toevoegen", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                return new Pair<>(ringnummerField.getText(), parentTypeBox.getValue());
            }
            return null;
        });

        // Handle user input
        dialog.showAndWait().ifPresent(result -> {
            String ringnummer = result.getKey();
            String parentType = result.getValue();

            if (ringnummer.isEmpty()) {
                showAlert("Fout", "Voer een ringnummer in.");
                return;
            }

            try (Connection conn = db.getConnection()) {
                // Get stamkaart_id
                PreparedStatement getStamkaartId = conn.prepareStatement("SELECT stamkaart_id FROM stamkaarten WHERE naam = ?");
                getStamkaartId.setString(1, selectedStamkaart);
                ResultSet rs = getStamkaartId.executeQuery();

                if (rs.next()) {
                    int stamkaartId = rs.getInt("stamkaart_id");

                    // controleerd of de duif bestaat
                    PreparedStatement checkPigeon = conn.prepareStatement("SELECT * FROM duiven WHERE ringnummer = ?");
                    checkPigeon.setString(1, ringnummer);
                    ResultSet pigeonExists = checkPigeon.executeQuery();

                    if (!pigeonExists.next()) {
                        showAlert("Fout", "Deze duif bestaat niet in de database.");
                        return;
                    }

                    // Insert into stamkaart_duiven with parent type
                    PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO stamkaart_duiven (stamkaart_id, ringnummer, parent_type) VALUES (?, ?, ?)"
                    );
                    stmt.setInt(1, stamkaartId);
                    stmt.setString(2, ringnummer);
                    stmt.setString(3, parentType);
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

    // Verwijdert een geselecteerde duif uit een stamkaart.
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

    // Voegt een nieuwe stamkaart toe aan de database.
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

    // Wijzigt de naam van een bestaande stamkaart.
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

    // Verwijdert een geselecteerde stamkaart uit de database.
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

    // Toont een pop-up bericht met een titel en tekst.
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}