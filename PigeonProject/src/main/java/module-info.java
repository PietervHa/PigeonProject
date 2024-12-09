module com.pieter.pigeonproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.pieter.pigeonproject to javafx.fxml;
    exports com.pieter.pigeonproject;
}