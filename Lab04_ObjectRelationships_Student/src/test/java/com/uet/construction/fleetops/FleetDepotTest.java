package com.uet.construction.fleetops;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FleetDepotTest {

    private Vehicle newVehicle(String vin) {
        return new Vehicle(vin, "Toyota Hilux", "ENG-" + vin, 2400, "Diesel");
    }

    @Test
    @DisplayName("A new depot has an id, a location and no vehicles")
    void testNewDepot() {
        FleetDepot depot = new FleetDepot("DEP-01", "Abbottabad Central Depot");

        assertEquals("DEP-01", depot.getDepotId());
        assertEquals("Abbottabad Central Depot", depot.getLocationName());
        assertEquals(0, depot.getVehicleCount());
        assertTrue(depot.getAssignedVehicles().isEmpty());
    }

    @Test
    @DisplayName("Constructor rejects null or blank depot data")
    void testConstructorValidation() {
        assertThrows(IllegalArgumentException.class, () -> new FleetDepot(null, "Location"));
        assertThrows(IllegalArgumentException.class, () -> new FleetDepot("DEP-01", "  "));
    }

    @Test
    @DisplayName("assignVehicle stores the same pre-existing reference (no copy)")
    void testAssignVehicleKeepsSameReference() {
        FleetDepot depot = new FleetDepot("DEP-01", "Abbottabad Central Depot");
        Vehicle vehicle = newVehicle("VIN-1");

        depot.assignVehicle(vehicle);

        assertEquals(1, depot.getVehicleCount());
        assertSame(vehicle, depot.getAssignedVehicles().get(0));
    }

    @Test
    @DisplayName("assignVehicle rejects null and duplicate assignments")
    void testAssignVehicleValidation() {
        FleetDepot depot = new FleetDepot("DEP-01", "Abbottabad Central Depot");
        Vehicle vehicle = newVehicle("VIN-1");
        depot.assignVehicle(vehicle);

        assertThrows(IllegalArgumentException.class, () -> depot.assignVehicle(null));
        assertThrows(IllegalArgumentException.class, () -> depot.assignVehicle(vehicle));
        assertEquals(1, depot.getVehicleCount());
    }

    @Test
    @DisplayName("releaseVehicle unlinks the vehicle without destroying it")
    void testReleaseVehicleDoesNotDestroyVehicle() {
        FleetDepot depot = new FleetDepot("DEP-01", "Abbottabad Central Depot");
        Vehicle vehicle = newVehicle("VIN-1");
        depot.assignVehicle(vehicle);

        boolean released = depot.releaseVehicle(vehicle);

        assertTrue(released);
        assertEquals(0, depot.getVehicleCount());
        assertEquals("VIN-1", vehicle.getVin());
        assertEquals("ENG-VIN-1", vehicle.getEngine().getEngineNumber());
    }

    @Test
    @DisplayName("releaseVehicle returns false for a vehicle that is not assigned")
    void testReleaseUnknownVehicle() {
        FleetDepot depot = new FleetDepot("DEP-01", "Abbottabad Central Depot");

        assertFalse(depot.releaseVehicle(newVehicle("VIN-X")));
    }

    @Test
    @DisplayName("getAssignedVehicles returns an unmodifiable view")
    void testAssignedVehiclesIsUnmodifiable() {
        FleetDepot depot = new FleetDepot("DEP-01", "Abbottabad Central Depot");
        depot.assignVehicle(newVehicle("VIN-1"));
        List<Vehicle> view = depot.getAssignedVehicles();

        assertThrows(UnsupportedOperationException.class, () -> view.add(newVehicle("VIN-2")));
        assertThrows(UnsupportedOperationException.class, view::clear);
        assertThrows(UnsupportedOperationException.class, () -> view.remove(0));
        assertEquals(1, depot.getVehicleCount());
    }

    @Test
    @DisplayName("Vehicles and drivers survive when the depot reference is set to null")
    void testVehiclesSurviveDepotClosure() {
        Driver driver = new Driver("D-001", "Ali Raza", "LIC-ABT-1001");
        Vehicle vehicle = newVehicle("VIN-1");
        vehicle.assignDriver(driver);
        FleetDepot depot = new FleetDepot("DEP-01", "Abbottabad Central Depot");
        depot.assignVehicle(vehicle);

        depot = null;

        assertNull(depot);
        assertEquals("VIN-1", vehicle.getVin());
        assertSame(driver, vehicle.getDriver());
        assertEquals("Ali Raza", driver.getFullName());
    }

    @Test
    @DisplayName("A vehicle from a closed depot can be reassigned to another depot")
    void testVehicleCanBeReassigned() {
        Vehicle vehicle = newVehicle("VIN-1");
        FleetDepot oldDepot = new FleetDepot("DEP-01", "Abbottabad Central Depot");
        FleetDepot newDepot = new FleetDepot("DEP-02", "Havelian Satellite Depot");
        oldDepot.assignVehicle(vehicle);

        oldDepot.releaseVehicle(vehicle);
        newDepot.assignVehicle(vehicle);

        assertEquals(0, oldDepot.getVehicleCount());
        assertEquals(1, newDepot.getVehicleCount());
        assertSame(vehicle, newDepot.getAssignedVehicles().get(0));
    }
}
