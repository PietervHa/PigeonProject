package com.pieter.pigeonproject.Classes;

import com.pieter.pigeonproject.Classes.Database;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FamilyTreePopup {

    private Database db;

    public FamilyTreePopup(Database db) {
        this.db = db;
    }

    public void showFamilyTree(String stamkaartNaam) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Stamkaart: " + stamkaartNaam);

        // Fetch family tree data
        String[] familyTreeData = fetchFamilyTree(stamkaartNaam);

        // Create GridPane for layout
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(20);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);

        // Populate the family tree
        addLabel(gridPane, familyTreeData[0], 4, 5);  // Child
        addLabel(gridPane, familyTreeData[1], 2, 4);  // Father
        addLabel(gridPane, familyTreeData[2], 6, 4);  // Mother
        addLabel(gridPane, familyTreeData[3], 1, 3);  // GrandFather A
        addLabel(gridPane, familyTreeData[4], 3, 3);  // GrandMother A
        addLabel(gridPane, familyTreeData[5], 5, 3);  // GrandFather B
        addLabel(gridPane, familyTreeData[6], 7, 3);  // GrandMother B
        addLabel(gridPane, familyTreeData[7], 0, 2);  // GreatGrandFather 1
        addLabel(gridPane, familyTreeData[8], 2, 2);  // GreatGrandMother 1
        addLabel(gridPane, familyTreeData[9], 4, 2);  // GreatGrandFather 2
        addLabel(gridPane, familyTreeData[10], 6, 2); // GreatGrandMother 2
        addLabel(gridPane, familyTreeData[11], 8, 2); // GreatGrandFather 3
        addLabel(gridPane, familyTreeData[12], 10, 2); // GreatGrandMother 3
        addLabel(gridPane, familyTreeData[13], 12, 2); // GreatGrandFather 4
        addLabel(gridPane, familyTreeData[14], 14, 2); // GreatGrandMother 4

        // Button to close the popup
        Button closeButton = new Button("Sluiten");
        closeButton.setOnAction(e -> popupStage.close());
        HBox buttonBox = new HBox(closeButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));

        VBox layout = new VBox(gridPane, buttonBox);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 600, 400);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private void addLabel(GridPane gridPane, String text, int col, int row) {
        Label label = new Label(text);
        label.setStyle("-fx-border-color: black; -fx-padding: 5; -fx-background-color: lightgray;");
        gridPane.add(label, col, row);
    }

    private String[] fetchFamilyTree(String stamkaartNaam) {
        String[] familyTree = new String[15]; // 15 positions for family members
        for (int i = 0; i < familyTree.length; i++) {
            familyTree[i] = "Unknown"; // Default to "Unknown"
        }

        String sql = """
                SELECT 
                    d1.ringnummer AS child,
                    d2.ringnummer AS father,
                    d3.ringnummer AS mother,
                    d4.ringnummer AS grandfather_a,
                    d5.ringnummer AS grandmother_a,
                    d6.ringnummer AS grandfather_b,
                    d7.ringnummer AS grandmother_b,
                    d8.ringnummer AS greatgrandfather_1,
                    d9.ringnummer AS greatgrandmother_1,
                    d10.ringnummer AS greatgrandfather_2,
                    d11.ringnummer AS greatgrandmother_2,
                    d12.ringnummer AS greatgrandfather_3,
                    d13.ringnummer AS greatgrandmother_3,
                    d14.ringnummer AS greatgrandfather_4,
                    d15.ringnummer AS greatgrandmother_4
                FROM stamkaart_duiven d1
                LEFT JOIN stamkaart_duiven d2 ON d2.parent_type = 'Father' AND d2.stamkaart_id = d1.stamkaart_id
                LEFT JOIN stamkaart_duiven d3 ON d3.parent_type = 'Mother' AND d3.stamkaart_id = d1.stamkaart_id
                LEFT JOIN stamkaart_duiven d4 ON d4.parent_type = 'GrandFather A' AND d4.stamkaart_id = d1.stamkaart_id
                LEFT JOIN stamkaart_duiven d5 ON d5.parent_type = 'GrandMother A' AND d5.stamkaart_id = d1.stamkaart_id
                LEFT JOIN stamkaart_duiven d6 ON d6.parent_type = 'GrandFather B' AND d6.stamkaart_id = d1.stamkaart_id
                LEFT JOIN stamkaart_duiven d7 ON d7.parent_type = 'GrandMother B' AND d7.stamkaart_id = d1.stamkaart_id
                LEFT JOIN stamkaart_duiven d8 ON d8.parent_type = 'GreatGrandFather 1' AND d8.stamkaart_id = d1.stamkaart_id
                LEFT JOIN stamkaart_duiven d9 ON d9.parent_type = 'GreatGrandMother 1' AND d9.stamkaart_id = d1.stamkaart_id
                LEFT JOIN stamkaart_duiven d10 ON d10.parent_type = 'GreatGrandFather 2' AND d10.stamkaart_id = d1.stamkaart_id
                LEFT JOIN stamkaart_duiven d11 ON d11.parent_type = 'GreatGrandMother 2' AND d11.stamkaart_id = d1.stamkaart_id
                LEFT JOIN stamkaart_duiven d12 ON d12.parent_type = 'GreatGrandFather 3' AND d12.stamkaart_id = d1.stamkaart_id
                LEFT JOIN stamkaart_duiven d13 ON d13.parent_type = 'GreatGrandMother 3' AND d13.stamkaart_id = d1.stamkaart_id
                LEFT JOIN stamkaart_duiven d14 ON d14.parent_type = 'GreatGrandFather 4' AND d14.stamkaart_id = d1.stamkaart_id
                LEFT JOIN stamkaart_duiven d15 ON d15.parent_type = 'GreatGrandMother 4' AND d15.stamkaart_id = d1.stamkaart_id
                WHERE d1.stamkaart_id = (SELECT stamkaart_id FROM stamkaarten WHERE naam = ?)
                """;

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, stamkaartNaam);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                for (int i = 0; i < familyTree.length; i++) {
                    familyTree[i] = rs.getString(i + 1) != null ? rs.getString(i + 1) : "Unknown";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return familyTree;
    }
}
