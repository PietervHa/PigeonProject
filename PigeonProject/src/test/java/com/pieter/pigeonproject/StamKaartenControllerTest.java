package com.pieter.pigeonproject;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.pieter.pigeonproject.Classes.Database;
import com.pieter.pigeonproject.Controllers.StamKaartenController;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.sql.*;
import java.util.List;

public class StamKaartenControllerTest {

    @Mock
    private Database mockDb;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private StamKaartenController controller;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(mockDb.getConnection()).thenReturn(mockConnection);
        controller = new StamKaartenController(mockDb);
    }

    @Test
    public void testGetAllStamkaarten_returnsCorrectList() throws Exception {
        String expectedQuery = "SELECT naam FROM stamkaarten";

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Simuleer 2 stamkaarten in resultset
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("naam")).thenReturn("Stamkaart1", "Stamkaart2");

        List<String> stamkaarten = controller.getAllStamkaarten();

        assertEquals(2, stamkaarten.size());
        assertEquals("Stamkaart1", stamkaarten.get(0));
        assertEquals("Stamkaart2", stamkaarten.get(1));

        verify(mockConnection).prepareStatement(expectedQuery);
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    public void testAddStamkaart_insertsCorrectly() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // 1 rij beïnvloed = success

        boolean result = controller.addStamkaart("NieuweStamkaart");

        verify(mockPreparedStatement).setString(1, "NieuweStamkaart");
        verify(mockPreparedStatement).executeUpdate();

        assertTrue(result);
    }

    @Test
    public void testAddStamkaart_failsWhenNoRowsAffected() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0); // Geen rij beïnvloed

        boolean result = controller.addStamkaart("MislukteStamkaart");

        verify(mockPreparedStatement).setString(1, "MislukteStamkaart");
        verify(mockPreparedStatement).executeUpdate();

        assertFalse(result);
    }

    @Test
    public void testAddStamkaart_throwsException_returnsFalse() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("DB error"));

        boolean result = controller.addStamkaart("ExceptionStamkaart");

        verify(mockPreparedStatement).setString(1, "ExceptionStamkaart");
        verify(mockPreparedStatement).executeUpdate();

        assertFalse(result);
    }
}
