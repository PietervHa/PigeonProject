package com.pieter.pigeonproject.Controllers;

import com.pieter.pigeonproject.Classes.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controller class voor alle database operaties rondom stamkaarten en gekoppelde duiven.
 */
public class StamKaartenController {

    private final Database db;

    public StamKaartenController(Database db) {
        this.db = db;
    }

    /**
     * Haalt alle stamkaarten op uit de database.
     * @return ObservableList met stamkaart namen.
     */
    public ObservableList<String> getAllStamkaarten() {
        ObservableList<String> stamkaarten = FXCollections.observableArrayList();
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT naam FROM stamkaarten");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                stamkaarten.add(rs.getString("naam"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stamkaarten;
    }

    /**
     * Haalt alle duiven op die gekoppeld zijn aan een specifieke stamkaart.
     * @param stamkaartNaam Naam van de stamkaart.
     * @return ObservableList met ringnummers van duiven.
     */
    public ObservableList<String> getDuivenForStamkaart(String stamkaartNaam) {
        ObservableList<String> duiven = FXCollections.observableArrayList();

        String sql = "SELECT d.ringnummer FROM duiven d " +
                "JOIN stamkaart_duiven sd ON d.ringnummer = sd.ringnummer " +
                "JOIN stamkaarten s ON sd.stamkaart_id = s.stamkaart_id " +
                "WHERE s.naam = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, stamkaartNaam);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                duiven.add(rs.getString("ringnummer"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return duiven;
    }

    /**
     * Voegt een nieuwe stamkaart toe aan de database.
     * @param naam Naam van de nieuwe stamkaart.
     * @throws SQLException Indien een database fout optreedt.
     */
    public boolean addStamkaart(String naam) {
        String sql = "INSERT INTO stamkaarten (naam) VALUES (?)";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, naam);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Wijzigt de naam van een bestaande stamkaart.
     * @param oudeNaam Oude naam van de stamkaart.
     * @param nieuweNaam Nieuwe naam voor de stamkaart.
     * @throws SQLException Indien een database fout optreedt.
     */
    public void renameStamkaart(String oudeNaam, String nieuweNaam) throws SQLException {
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE stamkaarten SET naam = ? WHERE naam = ?")) {
            stmt.setString(1, nieuweNaam);
            stmt.setString(2, oudeNaam);
            stmt.executeUpdate();
        }
    }

    /**
     * Verwijdert een stamkaart uit de database.
     * @param naam Naam van de stamkaart om te verwijderen.
     * @throws SQLException Indien een database fout optreedt.
     */
    public void deleteStamkaart(String naam) throws SQLException {
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM stamkaarten WHERE naam = ?")) {
            stmt.setString(1, naam);
            stmt.executeUpdate();
        }
    }

    /**
     * Voegt een duif toe aan een stamkaart met de aangegeven familiepositie.
     * @param stamkaartNaam Naam van de stamkaart.
     * @param ringnummer Ringnummer van de duif.
     * @param parentType Familiepositie (bijv. Child, Father, etc).
     * @throws SQLException Indien een database fout optreedt.
     * @throws IllegalArgumentException Indien de duif niet bestaat.
     */
    public void addPigeonToStamkaart(String stamkaartNaam, String ringnummer, String parentType) throws SQLException {
        try (Connection conn = db.getConnection()) {
            // Ophalen stamkaart_id
            PreparedStatement getStamkaartId = conn.prepareStatement("SELECT stamkaart_id FROM stamkaarten WHERE naam = ?");
            getStamkaartId.setString(1, stamkaartNaam);
            ResultSet rs = getStamkaartId.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Stamkaarten ID niet gevonden.");
            }
            int stamkaartId = rs.getInt("stamkaart_id");

            // Check of de duif bestaat
            PreparedStatement checkPigeon = conn.prepareStatement("SELECT 1 FROM duiven WHERE ringnummer = ?");
            checkPigeon.setString(1, ringnummer);
            ResultSet pigeonExists = checkPigeon.executeQuery();

            if (!pigeonExists.next()) {
                throw new IllegalArgumentException("Deze duif bestaat niet in de database.");
            }

            // Insert relatie stamkaart-duif
            PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO stamkaart_duiven (stamkaart_id, ringnummer, parent_type) VALUES (?, ?, ?)"
            );
            insert.setInt(1, stamkaartId);
            insert.setString(2, ringnummer);
            insert.setString(3, parentType);
            insert.executeUpdate();
        }
    }

    /**
     * Verwijdert een duif uit een stamkaart.
     * @param stamkaartNaam Naam van de stamkaart.
     * @param ringnummer Ringnummer van de duif.
     * @throws SQLException Indien een database fout optreedt.
     */
    public void removePigeonFromStamkaart(String stamkaartNaam, String ringnummer) throws SQLException {
        String sql = "DELETE FROM stamkaart_duiven WHERE ringnummer = ? AND stamkaart_id = " +
                "(SELECT stamkaart_id FROM stamkaarten WHERE naam = ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ringnummer);
            stmt.setString(2, stamkaartNaam);
            stmt.executeUpdate();
        }
    }
}
