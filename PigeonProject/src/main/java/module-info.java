module com.pieter.pigeonproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.pieter.pigeonproject to javafx.fxml;
    exports com.pieter.pigeonproject;
}