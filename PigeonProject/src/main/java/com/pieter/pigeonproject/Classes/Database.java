package com.pieter.pigeonproject.Classes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private Connection conn;

    // Constructor creates the database connection
    public Database() {
        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pigeonprojects", "root", "");
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed!", e);
        }
    }

    // Method to get the connection so it can be used in other classes
    public Connection getConnection() {
        return conn;
    }
}
