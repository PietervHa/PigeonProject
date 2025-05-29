package com.pieter.pigeonproject;

import com.pieter.pigeonproject.Classes.Database;
import com.pieter.pigeonproject.Classes.Navbar;
import com.pieter.pigeonproject.Controllers.HokBestandController;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HokBestandPage {

    private final Stage stage;
    private final Database db;
    private final HokBestandController controller;
    private BorderPane mainLayout;
    private ListView<HBox> pigeonListView;

    public HokBestandPage(Stage stage, Database db) {
        this.stage = stage;
        this.db = db;
        this.controller = new HokBestandController(db);
    }

    public Scene getScene() {
        Navbar navBar = new Navbar(stage, db);
        mainLayout = navBar.getLayout();

        pigeonListView = new ListView<>();
        loadPigeons();

        Button addButton = new Button("Add Pigeon");
        addButton.setOnAction(e -> showAddDialog());

        VBox content = new VBox(10, pigeonListView, addButton);
        content.setPadding(new javafx.geometry.Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        mainLayout.setCenter(content);
        return new Scene(mainLayout, 1900, 1080);
    }

    private void loadPigeons() {
        pigeonListView.getItems().clear();
        ObservableList<String> ringnummers = controller.getAllRingnummers();

        for (String ring : ringnummers) {
            Label ringLabel = new Label(ring);
            Button view = new Button("👁 View");
            Button edit = new Button("Edit");
            Button delete = new Button("Delete");

            view.setOnAction(e -> showViewDialog(ring));
            edit.setOnAction(e -> showEditDialog(ring));
            delete.setOnAction(e -> {
                controller.deletePigeon(ring);
                loadPigeons();
            });

            HBox row = new HBox(10, ringLabel, view, edit, delete);
            row.setAlignment(Pos.CENTER_LEFT);
            pigeonListView.getItems().add(row);
        }
    }

    private void showAddDialog() {
        showFormDialog("Add Pigeon", null, false);
    }

    private void showEditDialog(String ringnummer) {
        showFormDialog("Edit Pigeon", ringnummer, false);
    }

    private void showViewDialog(String ringnummer) {
        showFormDialog("View Pigeon", ringnummer, true);
    }

    private void showFormDialog(String title, String ringnummer, boolean isViewOnly) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(title);

        TextField ringField = new TextField();
        TextField jaarField = new TextField();
        TextField geslachtField = new TextField();
        TextField hokField = new TextField();
        TextField oudersField = new TextField();

        GridPane form = new GridPane();
        form.setVgap(10);
        form.setHgap(10);
        form.setAlignment(Pos.CENTER);
        form.addRow(0, new Label("Ringnummer:"), ringField);
        form.addRow(1, new Label("Geboortejaar:"), jaarField);
        form.addRow(2, new Label("Geslacht:"), geslachtField);
        form.addRow(3, new Label("Hok:"), hokField);
        form.addRow(4, new Label("Ouders:"), oudersField);

        if (ringnummer != null) {
            ResultSet rs = controller.getPigeonDetails(ringnummer);
            try {
                if (rs != null && rs.next()) {
                    ringField.setText(rs.getString("ringnummer"));
                    jaarField.setText(String.valueOf(rs.getInt("geboortejaar")));
                    geslachtField.setText(rs.getString("geslacht"));
                    hokField.setText(rs.getString("hok"));
                    oudersField.setText(rs.getString("ouders"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (isViewOnly) {
            ringField.setDisable(true);
            jaarField.setDisable(true);
            geslachtField.setDisable(true);
            hokField.setDisable(true);
            oudersField.setDisable(true);
        }

        VBox layout = new VBox(10, form);
        layout.setPadding(new javafx.geometry.Insets(20));
        layout.setAlignment(Pos.CENTER);

        if (!isViewOnly) {
            Button saveButton = new Button(ringnummer == null ? "Save" : "Update");
            saveButton.setOnAction(e -> {
                try {
                    String newRing = ringField.getText().trim();
                    int jaar = Integer.parseInt(jaarField.getText().trim());
                    String geslacht = geslachtField.getText().trim();
                    String hok = hokField.getText().trim();
                    String ouders = oudersField.getText().trim();

                    if (ringnummer == null) {
                        controller.savePigeon(newRing, jaar, geslacht, hok, ouders);
                    } else {
                        controller.updatePigeon(ringnummer, newRing, jaar, geslacht, hok, ouders);
                    }

                    dialog.close();
                    loadPigeons();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            layout.getChildren().add(saveButton);
        } else {
            // Voeg ook stamkaartenlijst toe indien viewOnly
            Label label = new Label("Stamkaarten:");
            ListView<String> lv = new ListView<>();
            lv.getItems().addAll(controller.getStamkaarten(ringnummer));
            layout.getChildren().addAll(label, lv);
        }

        layout.getChildren().add(new Button("Close") {{
            setOnAction(e -> dialog.close());
        }});

        dialog.setScene(new Scene(layout, 400, 500));
        dialog.showAndWait();
    }
}
