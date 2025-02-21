package com.pieter.pigeonproject;

import com.pieter.pigeonproject.Classes.Database;
import com.pieter.pigeonproject.Classes.Navbar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class HokBestandPage {

    private Stage stage;
    private Database db;
    private BorderPane mainLayout;
    private ListView<HBox> pigeonListView;
    private ObservableList<HBox> pigeonList;

    // Initialiseert de HokBestandPage met het opgegeven stage en databaseverbinding.
    public HokBestandPage(Stage stage, Database db) {
        this.stage = stage;
        this.db = db;
    }

    // Laadt de navigatiebalk, haalt duiven op, en retourneert de scène.
    public Scene getScene() {
        Navbar navBarComponent = new Navbar(stage, db);
        mainLayout = navBarComponent.getLayout();

        pigeonListView = new ListView<>();
        pigeonList = FXCollections.observableArrayList();
        loadPigeons();

        Button addButton = new Button("Add Pigeon");
        addButton.setOnAction(e -> showAddPigeonDialog());

        VBox content = new VBox(10, pigeonListView, addButton);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new javafx.geometry.Insets(20));

        mainLayout.setCenter(content);
        return new Scene(mainLayout, 1900, 1080);
    }

    // Haalt de lijst met duiven uit de database op en vult de ListView.
    private void loadPigeons() {
        pigeonList.clear();
        String query = "SELECT ringnummer FROM duiven";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String ringnummer = rs.getString("ringnummer");
                HBox row = createPigeonRow(ringnummer);
                pigeonList.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        pigeonListView.setItems(pigeonList);
    }

    // Creëert een rij in de ListView met details en knoppen voor een duif.
    private HBox createPigeonRow(String ringnummer) {
        Label ringLabel = new Label(ringnummer);
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        Button btnView = new Button("👁 View");

        editButton.setOnAction(e -> showEditPigeonDialog(ringnummer));
        deleteButton.setOnAction(e -> deletePigeon(ringnummer));
        btnView.setOnAction(event -> showViewPigeonDialog(ringnummer));

        HBox row = new HBox(10, ringLabel, btnView, editButton, deleteButton);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    // Toont een dialoogvenster om een nieuwe duif toe te voegen.
    private void showAddPigeonDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add Pigeon");

        GridPane grid = createPigeonForm(null, false);
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            savePigeon(grid);
            dialog.close();
        });

        VBox layout = new VBox(10, grid, saveButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new javafx.geometry.Insets(20));

        Scene scene = new Scene(layout, 400, 400);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // Toont een dialoogvenster om een bestaande duif te bewerken.
    private void showEditPigeonDialog(String ringnummer) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Pigeon");

        GridPane grid = createPigeonForm(ringnummer, false);
        Button saveButton = new Button("Save Changes");
        saveButton.setOnAction(e -> {
            updatePigeon(grid, ringnummer);
            dialog.close();
        });

        VBox layout = new VBox(10, grid, saveButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new javafx.geometry.Insets(20));

        Scene scene = new Scene(layout, 400, 400);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // Toont een dialoogvenster om de details van een duif te bekijken.
    private void showViewPigeonDialog(String ringnummer) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("View Pigeon");

        GridPane grid = createPigeonForm(ringnummer, true); // 🔹 Pass 'true' for View Mode

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> dialog.close());

        VBox layout = new VBox(10, grid, closeButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new javafx.geometry.Insets(20));

        Scene scene = new Scene(layout, 400, 400);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // Haalt de stamkaarten op die gekoppeld zijn aan een specifieke duif.
    private List<String> getStamkaartenForPigeon(String ringnummer) {
        List<String> stamkaarten = new ArrayList<>();
        String sql = """
            SELECT s.naam 
            FROM stamkaarten s
            JOIN stamkaart_duiven sd ON s.stamkaart_id = sd.stamkaart_id
            WHERE sd.ringnummer = ?;
            """;

        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setString(1, ringnummer);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stamkaarten.add(rs.getString("naam"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stamkaarten;
    }

    // Creëert een invoerformulier voor een duif, met optionele weergavemodus.
    private GridPane createPigeonForm(String ringnummer, boolean isViewMode) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField ringnummerField = new TextField();
        ringnummerField.setPromptText("Ringnummer");
        TextField geboortejaarField = new TextField();
        geboortejaarField.setPromptText("Geboortejaar");
        TextField geslachtField = new TextField();
        geslachtField.setPromptText("Geslacht");
        TextField hokField = new TextField();
        hokField.setPromptText("Hok");
        TextField oudersField = new TextField();
        oudersField.setPromptText("Ouders");

        if (ringnummer != null) {
            String query = "SELECT * FROM duiven WHERE ringnummer = ?";
            try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
                stmt.setString(1, ringnummer);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        ringnummerField.setText(rs.getString("ringnummer"));
                        geboortejaarField.setText(rs.getString("geboortejaar"));
                        geslachtField.setText(rs.getString("geslacht"));
                        hokField.setText(rs.getString("hok"));
                        oudersField.setText(rs.getString("ouders"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // 🔹 Disable fields ONLY when viewing
        if (isViewMode) {
            ringnummerField.setDisable(true);
            geboortejaarField.setDisable(true);
            geslachtField.setDisable(true);
            hokField.setDisable(true);
            oudersField.setDisable(true);

            // 🔹 geef Stamkaarten ListView alleen wanneer de gebruiker view klikt
            Label stamkaartenLabel = new Label("Stamkaarten:");
            ListView<String> stamkaartListView = new ListView<>();
            ObservableList<String> stamkaarten = FXCollections.observableArrayList(getStamkaartenForPigeon(ringnummer));
            stamkaartListView.setItems(stamkaarten);
            grid.addRow(5, stamkaartenLabel, stamkaartListView);
        }

        grid.addRow(0, new Label("Ringnummer:"), ringnummerField);
        grid.addRow(1, new Label("Geboortejaar:"), geboortejaarField);
        grid.addRow(2, new Label("Geslacht:"), geslachtField);
        grid.addRow(3, new Label("Hok:"), hokField);
        grid.addRow(4, new Label("Ouders:"), oudersField);

        return grid;
    }

    // Slaat een nieuwe duif op in de database met de gegevens uit het formulier.
    private void savePigeon(GridPane grid) {
        String query = "INSERT INTO duiven (ringnummer, geboortejaar, geslacht, hok, ouders) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, ((TextField) grid.getChildren().get(1)).getText());
            stmt.setString(2, ((TextField) grid.getChildren().get(3)).getText());
            stmt.setString(3, ((TextField) grid.getChildren().get(5)).getText());
            stmt.setString(4, ((TextField) grid.getChildren().get(7)).getText());
            stmt.setString(5, ((TextField) grid.getChildren().get(9)).getText());
            stmt.executeUpdate();
            loadPigeons();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Werkt de gegevens van een bestaande duif bij in de database.
    private void updatePigeon(GridPane grid, String ringnummer) {
        String query = "UPDATE duiven SET ringnummer=?, geboortejaar=?, geslacht=?, hok=?, ouders=? WHERE ringnummer=?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            // haal de gegevens in de juiste volgorde op
            String newRingnummer = ((TextField) grid.getChildren().get(1)).getText().trim();
            String newGeboortejaar = ((TextField) grid.getChildren().get(3)).getText().trim();
            String newGeslacht = ((TextField) grid.getChildren().get(5)).getText().trim();
            String newHok = ((TextField) grid.getChildren().get(7)).getText().trim();
            String newOuders = ((TextField) grid.getChildren().get(9)).getText().trim();

            // valideer en paas geboortejaar als een integer
            int geboortejaar;
            try {
                geboortejaar = Integer.parseInt(newGeboortejaar);
            } catch (NumberFormatException e) {
                System.out.println("⚠ Invalid geboortejaar: " + newGeboortejaar);
                return; // Stop execution if geboortejaar is not a valid number
            }

            // zet parameters in de juiste volgorde
            stmt.setString(1, newRingnummer);
            stmt.setInt(2, geboortejaar);
            stmt.setString(3, newGeslacht);
            stmt.setString(4, newHok);
            stmt.setString(5, newOuders);
            stmt.setString(6, ringnummer); // WHERE condition

            // Execute update
            int rowsUpdated = stmt.executeUpdate();

            // refresh de duiven pagina als de update successvol was
            if (rowsUpdated > 0) {
                loadPigeons();
            } else {
                System.out.println("⚠ No rows updated! Check ringnummer.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Verwijdert een duif uit de database op basis van het ringnummer.
    private void deletePigeon(String ringnummer) {
        String query = "DELETE FROM duiven WHERE ringnummer = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, ringnummer);
            stmt.executeUpdate();
            loadPigeons();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


