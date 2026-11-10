package com.uet.construction.fleetops;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class DriverTest {

    @Test
    @DisplayName("A valid Driver exposes its data through the getters")
    void testValidDriverGetters() {
        Driver driver = new Driver("D-001", "Ali Raza", "LIC-ABT-1001");

        assertEquals("D-001", driver.getDriverId());
        assertEquals("Ali Raza", driver.getFullName());
        assertEquals("LIC-ABT-1001", driver.getLicenseNumber());
    }

    @Test
    @DisplayName("Constructor rejects null arguments")
    void testConstructorRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Driver(null, "Ali Raza", "LIC-1"));
        assertThrows(IllegalArgumentException.class, () -> new Driver("D-001", null, "LIC-1"));
        assertThrows(IllegalArgumentException.class, () -> new Driver("D-001", "Ali Raza", null));
    }

    @Test
    @DisplayName("Constructor rejects blank and whitespace-only arguments")
    void testConstructorRejectsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Driver("", "Ali Raza", "LIC-1"));
        assertThrows(IllegalArgumentException.class, () -> new Driver("D-001", "   ", "LIC-1"));
        assertThrows(IllegalArgumentException.class, () -> new Driver("D-001", "Ali Raza", "\t"));
    }

    @Test
    @DisplayName("Setters update the state when the new value is valid")
    void testSettersUpdateState() {
        Driver driver = new Driver("D-001", "Ali Raza", "LIC-ABT-1001");

        driver.setDriverId("D-009");
        driver.setFullName("Ali R. Shah");
        driver.setLicenseNumber("LIC-ABT-2002");

        assertEquals("D-009", driver.getDriverId());
        assertEquals("Ali R. Shah", driver.getFullName());
        assertEquals("LIC-ABT-2002", driver.getLicenseNumber());
    }

    @Test
    @DisplayName("Setters reject invalid values and keep the old state")
    void testSettersRejectInvalid() {
        Driver driver = new Driver("D-001", "Ali Raza", "LIC-ABT-1001");

        assertThrows(IllegalArgumentException.class, () -> driver.setFullName(null));
        assertThrows(IllegalArgumentException.class, () -> driver.setLicenseNumber(" "));
        assertEquals("Ali Raza", driver.getFullName());
        assertEquals("LIC-ABT-1001", driver.getLicenseNumber());
    }

    @Test
    @DisplayName("toString returns the driver's profile")
    void testToString() {
        Driver driver = new Driver("D-001", "Ali Raza", "LIC-ABT-1001");

        String profile = driver.toString();

        assertTrue(profile.contains("D-001"));
        assertTrue(profile.contains("Ali Raza"));
        assertTrue(profile.contains("LIC-ABT-1001"));
    }
}
