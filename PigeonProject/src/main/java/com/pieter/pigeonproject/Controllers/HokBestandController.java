package com.pieter.pigeonproject.Controllers;

import com.pieter.pigeonproject.Classes.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HokBestandController {

    private final Database db;

    public HokBestandController(Database db) {
        this.db = db;
    }

    // Ophalen van alle ringnummers
    public ObservableList<String> getAllRingnummers() {
        ObservableList<String> ringnummers = FXCollections.observableArrayList();
        String query = "SELECT ringnummer FROM duiven";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ringnummers.add(rs.getString("ringnummer"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ringnummers;
    }

    // Ophalen van duifgegevens
    public ResultSet getPigeonDetails(String ringnummer) {
        String query = "SELECT ringnummer, geboortejaar, geslacht, hok, ouders FROM duiven WHERE ringnummer = ?";
        try {
            PreparedStatement stmt = db.getConnection().prepareStatement(query);
            stmt.setString(1, ringnummer);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Stamkaarten ophalen gekoppeld aan een duif
    public List<String> getStamkaarten(String ringnummer) {
        List<String> stamkaarten = new ArrayList<>();
        String sql = """
            SELECT s.naam
            FROM stamkaarten s
            JOIN stamkaart_duiven sd ON s.stamkaart_id = sd.stamkaart_id
            WHERE sd.ringnummer = ?""";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
            stmt.setString(1, ringnummer);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stamkaarten.add(rs.getString("naam"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stamkaarten;
    }

    // Nieuwe duif opslaan
    public void savePigeon(String ringnummer, int geboortejaar, String geslacht, String hok, String ouders) {
        String query = "INSERT INTO duiven (ringnummer, geboortejaar, geslacht, hok, ouders) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, ringnummer);
            stmt.setInt(2, geboortejaar);
            stmt.setString(3, geslacht);
            stmt.setString(4, hok);
            stmt.setString(5, ouders);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Duif bijwerken
    public void updatePigeon(String oldRingnummer, String ringnummer, int geboortejaar, String geslacht, String hok, String ouders) {
        String query = "UPDATE duiven SET ringnummer=?, geboortejaar=?, geslacht=?, hok=?, ouders=? WHERE ringnummer=?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, ringnummer);
            stmt.setInt(2, geboortejaar);
            stmt.setString(3, geslacht);
            stmt.setString(4, hok);
            stmt.setString(5, ouders);
            stmt.setString(6, oldRingnummer);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Duif verwijderen
    public void deletePigeon(String ringnummer) {
        String query = "DELETE FROM duiven WHERE ringnummer = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, ringnummer);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
