package com.pieter.pigeonproject;

import com.pieter.pigeonproject.Classes.Database;
import com.pieter.pigeonproject.Classes.FamilyTreePopup;
import com.pieter.pigeonproject.Classes.Navbar;
import com.pieter.pigeonproject.Controllers.StamKaartenController;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * UI klasse voor het tonen en beheren van stamkaarten en gekoppelde duiven.
 * Verzorgt de user interface en interacties, roept backend controller aan voor data.
 */
public class StamKaartenPage {

    private final Stage stage;
    private final Database db;
    private final StamKaartenController controller;

    private BorderPane contentLayout;
    private ListView<String> stamkaartenList;
    private ListView<String> pigeonList;

    public StamKaartenPage(Stage stage, Database db) {
        this.stage = stage;
        this.db = db;
        this.controller = new StamKaartenController(db);  // Maak backend controller aan
    }

    /**
     * Bouwt en retourneert de scene met de UI.
     * @return de JavaFX Scene voor StamKaartenPage
     */
    public Scene getScene() {
        Navbar navbar = new Navbar(stage, db);
        BorderPane mainLayout = navbar.getLayout();

        contentLayout = new BorderPane();

        stamkaartenList = new ListView<>();
        pigeonList = new ListView<>();

        loadStamkaarten();

        // Left Pane: Stamkaarten List
        Label stamkaartenLabel = new Label("Stamkaarten");
        VBox leftPane = new VBox(10, stamkaartenLabel, stamkaartenList);
        leftPane.setPadding(new Insets(10));
        leftPane.setPrefWidth(300);

        // Right Pane: Pigeon List + Buttons
        Label pigeonLabel = new Label("Pigeons in Stamkaart");
        VBox pigeonListContainer = new VBox(10, pigeonLabel, pigeonList);
        pigeonListContainer.setPadding(new Insets(10));

        ScrollPane pigeonScrollPane = new ScrollPane(pigeonListContainer);
        pigeonScrollPane.setFitToWidth(true);
        pigeonScrollPane.setFitToHeight(true);
        pigeonScrollPane.setPrefWidth(1600);
        pigeonScrollPane.setPadding(new Insets(10));

        Button btnAdd = new Button("➕ Add Stamkaart");
        Button btnRename = new Button("✏ Rename");
        Button btnDelete = new Button("🗑 Delete");
        Button btnViewTree = new Button("Bekijk Stamkaart");

        HBox buttonBar = new HBox(10, btnAdd, btnRename, btnDelete, btnViewTree);
        buttonBar.setPadding(new Insets(10));

        Button btnAddPigeon = new Button("➕ Add Pigeon");
        Button btnRemovePigeon = new Button("❌ Remove Pigeon");

        HBox pigeonBar = new HBox(10, btnAddPigeon, btnRemovePigeon);
        pigeonBar.setPadding(new Insets(10));

        VBox rightPane = new VBox(10, pigeonScrollPane, pigeonBar);
        rightPane.setPadding(new Insets(10));
        rightPane.setMaxWidth(Double.MAX_VALUE);

        contentLayout.setLeft(leftPane);
        contentLayout.setCenter(rightPane);
        contentLayout.setTop(buttonBar);

        mainLayout.setCenter(contentLayout);

        // Event handlers koppelen
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
            familyTreePopup.showFamilyTree(selectedStamkaart);
        });

        btnAddPigeon.setOnAction(event -> addPigeonToStamkaart());
        btnRemovePigeon.setOnAction(event -> removePigeonFromStamkaart());

        return new Scene(mainLayout, 1900, 1080);
    }

    // Laadt stamkaarten in de lijst via de backend controller
    private void loadStamkaarten() {
        ObservableList<String> stamkaarten = controller.getAllStamkaarten();
        stamkaartenList.setItems(stamkaarten);
    }

    // Laadt duiven voor de geselecteerde stamkaart via de backend controller
    private void loadPigeonsForStamkaart(String stamkaartNaam) {
        ObservableList<String> duiven = controller.getDuivenForStamkaart(stamkaartNaam);
        pigeonList.setItems(duiven);
    }

    // Behandeling selectie stamkaart in lijst
    private void selectStamkaart() {
        String selected = stamkaartenList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            loadPigeonsForStamkaart(selected);
        }
    }

    // UI actie: Voeg nieuwe stamkaart toe via dialoog en controller
    private void createStamkaart() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nieuwe Stamkaart");
        dialog.setHeaderText("Voer een naam in voor de nieuwe stamkaart:");
        dialog.setContentText("Naam:");

        dialog.showAndWait().ifPresent(name -> {
            try {
                controller.addStamkaart(name);
                loadStamkaarten();
            } catch (Exception e) {
                showAlert("Error", "Fout bij toevoegen stamkaart: " + e.getMessage());
            }
        });
    }

    // UI actie: Hernoem stamkaart via dialoog en controller
    private void renameStamkaart() {
        String selected = stamkaartenList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog(selected);
        dialog.setTitle("Stamkaart hernoemen");
        dialog.setHeaderText("Nieuwe naam voor de stamkaart:");
        dialog.setContentText("Naam:");

        dialog.showAndWait().ifPresent(newName -> {
            try {
                controller.renameStamkaart(selected, newName);
                loadStamkaarten();
            } catch (Exception e) {
                showAlert("Error", "Fout bij hernoemen stamkaart: " + e.getMessage());
            }
        });
    }

    // UI actie: Verwijder geselecteerde stamkaart via controller
    private void deleteStamkaart() {
        String selected = stamkaartenList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Weet je zeker dat je deze stamkaart wilt verwijderen?",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    controller.deleteStamkaart(selected);
                    loadStamkaarten();
                } catch (Exception e) {
                    showAlert("Error", "Fout bij verwijderen stamkaart: " + e.getMessage());
                }
            }
        });
    }

    // UI actie: Voeg duif toe aan stamkaart via dialoog en controller
    private void addPigeonToStamkaart() {
        String selectedStamkaart = stamkaartenList.getSelectionModel().getSelectedItem();
        if (selectedStamkaart == null) {
            showAlert("Fout", "Selecteer eerst een stamkaart.");
            return;
        }

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Duif toevoegen");
        dialog.setHeaderText("Voer het ringnummer van de duif in en selecteer een familiepositie:");

        TextField ringnummerField = new TextField();
        ringnummerField.setPromptText("Ringnummer");

        ComboBox<String> parentTypeCombo = new ComboBox<>();
        parentTypeCombo.getItems().addAll("Child", "Father", "Mother", "FatherFather", "FatherMother",
                "MotherFather", "MotherMother");
        parentTypeCombo.getSelectionModel().selectFirst();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Ringnummer:"), 0, 0);
        grid.add(ringnummerField, 1, 0);
        grid.add(new Label("Familiepositie:"), 0, 1);
        grid.add(parentTypeCombo, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType addButtonType = new ButtonType("Toevoegen", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Pair<>(ringnummerField.getText(), parentTypeCombo.getValue());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            try {
                controller.addPigeonToStamkaart(selectedStamkaart, result.getKey(), result.getValue());
                loadPigeonsForStamkaart(selectedStamkaart);
            } catch (IllegalArgumentException e) {
                showAlert("Fout", e.getMessage());
            } catch (Exception e) {
                showAlert("Error", "Fout bij toevoegen duif: " + e.getMessage());
            }
        });
    }

    // UI actie: Verwijder duif uit stamkaart via controller
    private void removePigeonFromStamkaart() {
        String selectedStamkaart = stamkaartenList.getSelectionModel().getSelectedItem();
        String selectedPigeon = pigeonList.getSelectionModel().getSelectedItem();

        if (selectedStamkaart == null || selectedPigeon == null) {
            showAlert("Fout", "Selecteer eerst een stamkaart en een duif.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Weet je zeker dat je deze duif wilt verwijderen?",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    controller.removePigeonFromStamkaart(selectedStamkaart, selectedPigeon);
                    loadPigeonsForStamkaart(selectedStamkaart);
                } catch (Exception e) {
                    showAlert("Error", "Fout bij verwijderen duif: " + e.getMessage());
                }
            }
        });
    }

    // Helper methode om een alert te tonen aan de gebruiker
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
