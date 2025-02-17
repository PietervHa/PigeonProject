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

public class HokBestandPage {

    private Stage stage;
    private Database db;
    private BorderPane mainLayout;
    private ListView<HBox> pigeonListView;
    private ObservableList<HBox> pigeonList;

    public HokBestandPage(Stage stage, Database db) {
        this.stage = stage;
        this.db = db;
    }

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

    private void showAddPigeonDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add Pigeon");

        GridPane grid = createPigeonForm(null);
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

    private void showEditPigeonDialog(String ringnummer) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Pigeon");

        GridPane grid = createPigeonForm(ringnummer);
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

    private void showViewPigeonDialog(String ringnummer) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("View Pigeon");

        GridPane grid = createPigeonForm(ringnummer);

        // Disable all input fields
        for (javafx.scene.Node node : grid.getChildren()) {
            if (node instanceof TextField) {
                ((TextField) node).setDisable(true);
            }
        }

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> dialog.close());

        VBox layout = new VBox(10, grid, closeButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new javafx.geometry.Insets(20));

        Scene scene = new Scene(layout, 400, 400);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private GridPane createPigeonForm(String ringnummer) {
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
        TextField stamkaartnummerField = new TextField();
        stamkaartnummerField.setPromptText("Stamkaartnummer");

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
                        stamkaartnummerField.setText(rs.getString("stamkaartnummer"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        grid.addRow(0, new Label("Ringnummer:"), ringnummerField);
        grid.addRow(1, new Label("Geboortejaar:"), geboortejaarField);
        grid.addRow(2, new Label("Geslacht:"), geslachtField);
        grid.addRow(3, new Label("Hok:"), hokField);
        grid.addRow(4, new Label("Ouders:"), oudersField);
        grid.addRow(5, new Label("Stamkaartnummer:"), stamkaartnummerField);

        return grid;
    }

    private void savePigeon(GridPane grid) {
        String query = "INSERT INTO duiven (ringnummer, geboortejaar, geslacht, hok, ouders, stamkaartnummer) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, ((TextField) grid.getChildren().get(1)).getText());
            stmt.setString(2, ((TextField) grid.getChildren().get(3)).getText());
            stmt.setString(3, ((TextField) grid.getChildren().get(5)).getText());
            stmt.setString(4, ((TextField) grid.getChildren().get(7)).getText());
            stmt.setString(5, ((TextField) grid.getChildren().get(9)).getText());
            stmt.setString(6, ((TextField) grid.getChildren().get(11)).getText());
            stmt.executeUpdate();
            loadPigeons();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updatePigeon(GridPane grid, String ringnummer) {
        String query = "UPDATE duiven SET geboortejaar=?, geslacht=?, hok=?, ouders=?, stamkaartnummer=? WHERE ringnummer=?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, ((TextField) grid.getChildren().get(3)).getText());
            stmt.setString(2, ((TextField) grid.getChildren().get(5)).getText());
            stmt.setString(3, ((TextField) grid.getChildren().get(7)).getText());
            stmt.setString(4, ((TextField) grid.getChildren().get(9)).getText());
            stmt.setString(5, ((TextField) grid.getChildren().get(11)).getText());
            stmt.setString(6, ringnummer);
            stmt.executeUpdate();
            loadPigeons();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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