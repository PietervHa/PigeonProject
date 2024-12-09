package com.pieter.pigeonproject;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class HomePage {

    public Scene getHomeScene() {
        // Create the Home Screen
        StackPane homePane = new StackPane();
        Label homeLabel = new Label("Welcome to the Home Page!");
        homePane.getChildren().add(homeLabel);

        return new Scene(homePane, 960, 720);
    }
}