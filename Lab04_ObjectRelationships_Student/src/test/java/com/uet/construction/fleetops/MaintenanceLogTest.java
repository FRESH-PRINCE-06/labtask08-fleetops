package com.uet.construction.fleetops;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class MaintenanceLogTest {

    @Test
    @DisplayName("Constructor stores id, description and cost")
    void testConstructorStoresState() {
        MaintenanceLog log = new MaintenanceLog("LOG-001", "Oil change", 45.50);

        assertEquals("LOG-001", log.getLogId());
        assertEquals("Oil change", log.getServiceDescription());
        assertEquals(45.50, log.getServiceCost(), 0.0001);
    }

    @Test
    @DisplayName("Constructor rejects blank text and negative cost")
    void testConstructorValidation() {
        assertThrows(IllegalArgumentException.class, () -> new MaintenanceLog("", "Oil change", 10));
        assertThrows(IllegalArgumentException.class, () -> new MaintenanceLog("LOG-001", null, 10));
        assertThrows(IllegalArgumentException.class, () -> new MaintenanceLog("LOG-001", "Oil change", -1));
    }

    @Test
    @DisplayName("Copy constructor creates an equal but distinct object")
    void testCopyConstructor() {
        MaintenanceLog original = new MaintenanceLog("LOG-001", "Oil change", 45.50);

        MaintenanceLog copy = new MaintenanceLog(original);

        assertNotSame(original, copy);
        assertEquals(original, copy);
    }

    @Test
    @DisplayName("Mutating the copy does not alter the original")
    void testCopyIsIndependentOfOriginal() {
        MaintenanceLog original = new MaintenanceLog("LOG-001", "Oil change", 45.50);
        MaintenanceLog copy = new MaintenanceLog(original);

        copy.setServiceDescription("FORGED");
        copy.setServiceCost(0.0);

        assertEquals("Oil change", original.getServiceDescription());
        assertEquals(45.50, original.getServiceCost(), 0.0001);
    }
}
