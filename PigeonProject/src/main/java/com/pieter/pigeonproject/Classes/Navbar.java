package com.pieter.pigeonproject.Classes;

import com.pieter.pigeonproject.AccountPage;
import com.pieter.pigeonproject.HokBestandPage;
import com.pieter.pigeonproject.LoginApplication;
import com.pieter.pigeonproject.StamKaartenPage;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Navbar {
    private VBox navBar;
    private Stage stage;

    public Navbar(Stage stage) {
        this.stage = stage;
        navBar = createNavBar();
    }

    private VBox createNavBar() {
        VBox navBar = new VBox(10);
        navBar.setPadding(new Insets(10));

        Button btnStamkaarten = new Button("Stamkaarten");
        Button btnHokbestand = new Button("Hokbestand");
        Button btnAccount = new Button("Account");
        Button btnLogout = new Button("Uitloggen");

        // Set button actions (You need to implement navigation to respective pages)
         btnStamkaarten.setOnAction(e -> stage.setScene(new StamKaartenPage(stage).getScene()));
         btnHokbestand.setOnAction(e -> stage.setScene(new HokBestandPage(stage).getScene()));
         btnAccount.setOnAction(e -> stage.setScene(new AccountPage(stage).getScene()));
        btnLogout.setOnAction(e -> {
            LoginApplication loginApp = new LoginApplication();
            loginApp.start(stage);
        });

        navBar.getChildren().addAll(btnStamkaarten, btnHokbestand, btnAccount, btnLogout);
        return navBar;
    }

    public VBox getNavBar() {
        return navBar;
    }
}