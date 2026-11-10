package com.uet.construction.fleetops;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class VehicleTest {

    private Vehicle newVehicle() {
        return new Vehicle("VIN-2001", "Honda Civic", "ENG-L15B-777", 1500, "Petrol");
    }

    @Test
    @DisplayName("Vehicle builds its own Engine from raw constructor parameters")
    void testVehicleBuildsItsOwnEngine() {
        Vehicle vehicle = newVehicle();

        Engine engine = vehicle.getEngine();

        assertEquals("ENG-L15B-777", engine.getEngineNumber());
        assertEquals(1500, engine.getDisplacementCC());
        assertEquals("Petrol", engine.getFuelType());
    }

    @Test
    @DisplayName("No Vehicle constructor accepts an external Engine (restricted instantiation)")
    void testNoConstructorAcceptsEngine() {
        for (Constructor<?> constructor : Vehicle.class.getDeclaredConstructors()) {
            for (Class<?> parameterType : constructor.getParameterTypes()) {
                assertNotSame(Engine.class, parameterType);
            }
        }
    }

    @Test
    @DisplayName("Vehicle constructor rejects a blank VIN or an invalid engine")
    void testConstructorValidation() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle(" ", "Honda Civic", "ENG-1", 1500, "Petrol"));
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle("VIN-1", null, "ENG-1", 1500, "Petrol"));
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle("VIN-1", "Honda Civic", "ENG-1", 0, "Petrol"));
    }

    @Test
    @DisplayName("getEngine() returns a new copy on every call, never the internal instance")
    void testGetEngineReturnsDefensiveCopy() {
        Vehicle vehicle = newVehicle();

        Engine first = vehicle.getEngine();
        Engine second = vehicle.getEngine();

        assertNotSame(first, second);
        assertEquals(first, second);
    }

    @Test
    @DisplayName("Mutating the engine returned by getEngine() does not change the vehicle")
    void testLeakedEngineCannotChangeVehicle() {
        Vehicle vehicle = newVehicle();
        Engine leakedEngine = vehicle.getEngine();

        leakedEngine.setEngineNumber("TAMPERED");
        leakedEngine.setDisplacementCC(9999);
        leakedEngine.setFuelType("Rocket Fuel");

        Engine internal = vehicle.getEngine();
        assertEquals("ENG-L15B-777", internal.getEngineNumber());
        assertEquals(1500, internal.getDisplacementCC());
        assertEquals("Petrol", internal.getFuelType());
    }

    @Test
    @DisplayName("addMaintenanceRecord stores the entry inside the vehicle")
    void testAddMaintenanceRecord() {
        Vehicle vehicle = newVehicle();

        vehicle.addMaintenanceRecord("LOG-001", "Oil change", 45.50);

        List<MaintenanceLog> history = vehicle.getMaintenanceHistory();
        assertEquals(1, history.size());
        assertEquals("LOG-001", history.get(0).getLogId());
        assertEquals("Oil change", history.get(0).getServiceDescription());
        assertEquals(45.50, history.get(0).getServiceCost(), 0.0001);
    }

    @Test
    @DisplayName("addMaintenanceRecord rejects invalid data and duplicate ids")
    void testAddMaintenanceRecordValidation() {
        Vehicle vehicle = newVehicle();
        vehicle.addMaintenanceRecord("LOG-001", "Oil change", 45.50);

        assertThrows(IllegalArgumentException.class,
                () -> vehicle.addMaintenanceRecord("LOG-001", "Duplicate id", 10));
        assertThrows(IllegalArgumentException.class,
                () -> vehicle.addMaintenanceRecord("LOG-002", "", 10));
        assertThrows(IllegalArgumentException.class,
                () -> vehicle.addMaintenanceRecord("LOG-003", "Negative cost", -10));
        assertEquals(1, vehicle.getMaintenanceCount());
    }

    @Test
    @DisplayName("Clearing or editing the retrieved history never alters the internal history")
    void testHistoryIsDefensivelyCopied() {
        Vehicle vehicle = newVehicle();
        vehicle.addMaintenanceRecord("LOG-001", "Oil change", 45.50);
        vehicle.addMaintenanceRecord("LOG-002", "Brake pads", 120.00);

        List<MaintenanceLog> retrieved = vehicle.getMaintenanceHistory();
        retrieved.get(0).setServiceDescription("FORGED");
        retrieved.get(0).setServiceCost(0.0);
        retrieved.add(new MaintenanceLog("LOG-999", "Injected", 1.0));
        retrieved.clear();

        List<MaintenanceLog> fresh = vehicle.getMaintenanceHistory();
        assertEquals(2, fresh.size());
        assertEquals("Oil change", fresh.get(0).getServiceDescription());
        assertEquals(45.50, fresh.get(0).getServiceCost(), 0.0001);
        assertEquals("LOG-002", fresh.get(1).getLogId());
    }

    @Test
    @DisplayName("Each history request returns a different list holding different clones")
    void testHistoryReturnsIndependentClones() {
        Vehicle vehicle = newVehicle();
        vehicle.addMaintenanceRecord("LOG-001", "Oil change", 45.50);

        List<MaintenanceLog> first = vehicle.getMaintenanceHistory();
        List<MaintenanceLog> second = vehicle.getMaintenanceHistory();

        assertNotSame(first, second);
        assertNotSame(first.get(0), second.get(0));
        assertEquals(first.get(0), second.get(0));
    }

    @Test
    @DisplayName("Internal engine and log list are private fields (no direct access)")
    void testInternalFieldsArePrivate() throws Exception {
        for (String name : new String[] {"vin", "model", "engine", "maintenanceLogs", "driver"}) {
            Field field = Vehicle.class.getDeclaredField(name);
            assertTrue(Modifier.isPrivate(field.getModifiers()), name + " must be private");
        }
    }

    @Test
    @DisplayName("Driver is aggregated: the same reference is shared and survives release")
    void testDriverAggregation() {
        Vehicle vehicle = newVehicle();
        Driver driver = new Driver("D-001", "Ali Raza", "LIC-ABT-1001");

        vehicle.assignDriver(driver);
        assertTrue(vehicle.hasDriver());
        assertSame(driver, vehicle.getDriver());

        Driver released = vehicle.releaseDriver();
        assertSame(driver, released);
        assertFalse(vehicle.hasDriver());
        assertNull(vehicle.getDriver());
        assertEquals("Ali Raza", driver.getFullName());
        assertEquals("VIN-2001", vehicle.getVin());
    }

    @Test
    @DisplayName("assignDriver rejects null")
    void testAssignDriverRejectsNull() {
        Vehicle vehicle = newVehicle();

        assertThrows(IllegalArgumentException.class, () -> vehicle.assignDriver(null));
    }
}
