package com.pieter.pigeonproject.Classes;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {

    @Test
    void databaseConnectie() {
        Database database = new Database();
        try (Connection connection = database.getConnection()) {
            assertNotNull(connection, "The database connection should not be null.");
            assertFalse(connection.isClosed(), "The database connection should be open.");
        } catch (SQLException e) {
            fail("Database connection test failed: " + e.getMessage());
        }
    }
}