package com.pieter.pigeonproject.Controllers;

import com.pieter.pigeonproject.Classes.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Backend controller voor login- en registratiefunctionaliteit.
 * Behandelt alle database-interacties rondom gebruikersauthenticatie.
 */
public class LoginController {

    private Database db;

    public LoginController(Database db) {
        this.db = db;
    }

    public Database getDb() {
        return db;
    }

    /**
     * Valideert login door te controleren of het e-mailadres en wachtwoord bestaan in de database.
     * @param email e-mailadres van de gebruiker
     * @param password wachtwoord van de gebruiker
     * @return true als gebruiker bestaat en wachtwoord klopt, anders false
     */
    public boolean validateLogin(String email, String password) {
        String query = "SELECT * FROM users WHERE mail = ? AND password = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Foutafhandeling kan hier uitgebreid worden
        }
        return false;
    }

    /**
     * Registreert een nieuwe gebruiker als het e-mailadres nog niet bestaat.
     * @param name naam van de gebruiker
     * @param email e-mailadres
     * @param password wachtwoord
     * @return true als registratie succesvol, anders false
     */
    public boolean registerUser(String name, String email, String password) {
        String checkQuery = "SELECT * FROM users WHERE mail = ?";
        String insertQuery = "INSERT INTO users (name, mail, password) VALUES (?, ?, ?)";

        try {
            try (PreparedStatement checkStmt = db.getConnection().prepareStatement(checkQuery)) {
                checkStmt.setString(1, email);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        return false;  // Gebruiker bestaat al
                    }
                }
            }

            try (PreparedStatement insertStmt = db.getConnection().prepareStatement(insertQuery)) {
                insertStmt.setString(1, name);
                insertStmt.setString(2, email);
                insertStmt.setString(3, password);
                insertStmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Foutafhandeling kan hier uitgebreid worden
        }
        return false;
    }
}
