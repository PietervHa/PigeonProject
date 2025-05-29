package com.pieter.pigeonproject.Controllers;

import com.pieter.pigeonproject.Classes.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountPageController {

    private final Database db; // Database verbinding (meegegeven door de frontend)

    // Constructor ontvangt de database referentie
    public AccountPageController(Database db) {
        this.db = db;
    }

    // Haalt e-mail en wachtwoord op van gebruiker met id = 1
    public String[] fetchAccountData() {
        String[] result = new String[]{"", ""}; // [0] = email, [1] = wachtwoord
        String query = "SELECT mail, password FROM users WHERE id = 1";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                result[0] = rs.getString("mail");
                result[1] = rs.getString("password");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log de fout voor debuggen
        }

        return result;
    }

    // Werkt e-mail of wachtwoord bij voor gebruiker met id = 1
    public boolean updateAccountData(String type, String newValue) {
        String column = type.equals("email") ? "mail" : "password";
        String query = "UPDATE users SET " + column + " = ? WHERE id = 1";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newValue);
            stmt.executeUpdate();
            return true; // Update geslaagd

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Update mislukt
        }
    }
}
