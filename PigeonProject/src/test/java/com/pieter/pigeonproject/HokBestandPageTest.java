package com.pieter.pigeonproject;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javafx.stage.Stage;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class HokBestandPageTest {

    private HokBestandPage hokBestandPage;

    @BeforeEach
    void setUp() {
        // Simulate a database instance (pass null for now, since we're not testing DB logic)
        hokBestandPage = new HokBestandPage(new Stage(), null);
    }

    @Test
    void testCreatePigeonForm_EditMode() throws Exception {
        Platform.runLater(() -> {
            // Use reflection to invoke the private method
            Method createPigeonFormMethod = null;
            try {
                createPigeonFormMethod = HokBestandPage.class.getDeclaredMethod("createPigeonForm", String.class, boolean.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            createPigeonFormMethod.setAccessible(true);
            GridPane form = null;
            try {
                form = (GridPane) createPigeonFormMethod.invoke(hokBestandPage, null, false);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Retrieve the field for ringnummer and check if it's enabled in Edit Mode
            TextField ringnummerField = (TextField) form.getChildren().get(1);
            assertFalse(ringnummerField.isDisabled(), "Ringnummer field should be enabled in edit mode.");
        });
    }

    @Test
    void testCreatePigeonForm_ViewMode() throws Exception {
        Platform.runLater(() -> {
            // Use reflection to invoke the private method
            Method createPigeonFormMethod = null;
            try {
                createPigeonFormMethod = HokBestandPage.class.getDeclaredMethod("createPigeonForm", String.class, boolean.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            createPigeonFormMethod.setAccessible(true);
            GridPane form = null;
            try {
                form = (GridPane) createPigeonFormMethod.invoke(hokBestandPage, "12345", true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Retrieve the fields and check if they are disabled in View Mode
            TextField ringnummerField = (TextField) form.getChildren().get(1);
            TextField geboortejaarField = (TextField) form.getChildren().get(3);
            TextField geslachtField = (TextField) form.getChildren().get(5);
            TextField hokField = (TextField) form.getChildren().get(7);
            TextField oudersField = (TextField) form.getChildren().get(9);

            assertTrue(ringnummerField.isDisabled(), "Ringnummer field should be disabled in view mode.");
            assertTrue(geboortejaarField.isDisabled(), "Geboortejaar field should be disabled in view mode.");
            assertTrue(geslachtField.isDisabled(), "Geslacht field should be disabled in view mode.");
            assertTrue(hokField.isDisabled(), "Hok field should be disabled in view mode.");
            assertTrue(oudersField.isDisabled(), "Ouders field should be disabled in view mode.");
        });
    }
}