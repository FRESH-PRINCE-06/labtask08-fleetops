package com.uet.construction.fleetops;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class EngineTest {

    @Test
    @DisplayName("Parameterized constructor stores all three attributes")
    void testConstructorStoresState() {
        Engine engine = new Engine("ENG-001", 1500, "Petrol");

        assertEquals("ENG-001", engine.getEngineNumber());
        assertEquals(1500, engine.getDisplacementCC());
        assertEquals("Petrol", engine.getFuelType());
    }

    @Test
    @DisplayName("Constructor rejects blank text and non-positive displacement")
    void testConstructorValidation() {
        assertThrows(IllegalArgumentException.class, () -> new Engine(null, 1500, "Petrol"));
        assertThrows(IllegalArgumentException.class, () -> new Engine("ENG-001", 1500, " "));
        assertThrows(IllegalArgumentException.class, () -> new Engine("ENG-001", 0, "Petrol"));
        assertThrows(IllegalArgumentException.class, () -> new Engine("ENG-001", -5, "Petrol"));
    }

    @Test
    @DisplayName("Copy constructor creates an equal but distinct object")
    void testCopyConstructorMakesDistinctEqualObject() {
        Engine original = new Engine("ENG-001", 1500, "Petrol");

        Engine copy = new Engine(original);

        assertNotSame(original, copy);
        assertEquals(original, copy);
        assertEquals("ENG-001", copy.getEngineNumber());
        assertEquals(1500, copy.getDisplacementCC());
        assertEquals("Petrol", copy.getFuelType());
    }

    @Test
    @DisplayName("Mutating the copy does not alter the original (deep copy)")
    void testCopyIsIndependentOfOriginal() {
        Engine original = new Engine("ENG-001", 1500, "Petrol");
        Engine copy = new Engine(original);

        copy.setEngineNumber("ENG-999");
        copy.setDisplacementCC(3000);
        copy.setFuelType("Diesel");

        assertEquals("ENG-001", original.getEngineNumber());
        assertEquals(1500, original.getDisplacementCC());
        assertEquals("Petrol", original.getFuelType());
    }

    @Test
    @DisplayName("Copy constructor rejects a null source")
    void testCopyConstructorRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Engine((Engine) null));
    }
}
