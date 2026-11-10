package com.uet.construction.fleetops;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Verification driver for the FleetOps domain module.
 *
 * Test Case A proves AGGREGATION lifecycle independence (Depot/Vehicle and
 * Vehicle/Driver). Test Case B proves COMPOSITION lifetime binding and the
 * absence of representation exposure (Vehicle/Engine and Vehicle/MaintenanceLog).
 *
 * Every check prints PASS or FAIL. Run it with:  mvn exec:java
 * or from NetBeans: Run File on this class.
 */
public class FleetVerificationDriver {

    private final PrintStream out;
    private int passed = 0;
    private int failed = 0;

    public FleetVerificationDriver(PrintStream out) {
        this.out = out;
    }

    public static void main(String[] args) {
        FleetVerificationDriver driver = new FleetVerificationDriver(System.out);
        int failures = driver.runAll();
        if (failures > 0) {
            System.exit(1);
        }
    }

    /** Runs every test case and returns the number of failed assertions. */
    public int runAll() {
        passed = 0;
        failed = 0;
        out.println("==============================================================");
        out.println("  FleetOps - Fleet & Vehicle Management : Verification Driver");
        out.println("==============================================================");
        testCaseA();
        testCaseB();
        out.println();
        out.println("==============================================================");
        out.println("  SUMMARY: " + passed + " passed, " + failed + " failed");
        out.println("==============================================================");
        return failed;
    }

    public int getPassed() {
        return passed;
    }

    public int getFailed() {
        return failed;
    }

    // ------------------------------------------------------------------
    // Test Case A: Aggregation Lifecycle Independence
    // ------------------------------------------------------------------
    private void testCaseA() {
        out.println();
        out.println("--- TEST CASE A: Aggregation Lifecycle Independence ---");

        // Step 1: two Driver and two Vehicle objects created independently
        Driver driver1 = new Driver("D-001", "Ali Raza", "LIC-ABT-1001");
        Driver driver2 = new Driver("D-002", "Sara Khan", "LIC-ABT-1002");
        Vehicle vehicle1 = new Vehicle("VIN-1001", "Toyota Hilux", "ENG-2GD-001", 2400, "Diesel");
        Vehicle vehicle2 = new Vehicle("VIN-1002", "Suzuki Bolan", "ENG-F8B-002", 800, "Petrol");
        vehicle1.assignDriver(driver1);
        vehicle2.assignDriver(driver2);
        out.println("Created independently : " + driver1);
        out.println("                        " + driver2);
        out.println("                        " + vehicle1);
        out.println("                        " + vehicle2);

        // Step 2: depot houses the vehicles
        FleetDepot depot = new FleetDepot("DEP-01", "Abbottabad Central Depot");
        depot.assignVehicle(vehicle1);
        depot.assignVehicle(vehicle2);
        out.println("Depot created         : " + depot);
        check("A1 depot holds both vehicles after assignVehicle()", depot.getVehicleCount() == 2);
        check("A2 depot stores the SAME references (aggregation, not copies)",
                depot.getAssignedVehicles().get(0) == vehicle1
                && depot.getAssignedVehicles().get(1) == vehicle2);

        int heapId1 = System.identityHashCode(vehicle1);
        int heapId2 = System.identityHashCode(vehicle2);
        out.println("Identity hash BEFORE  : vehicle1=" + heapId1 + ", vehicle2=" + heapId2);

        // Step 3: the depot facility "closes down"
        depot = null;
        out.println("depot = null  (depot reference nullified)");

        // Step 4: prove the parts survive
        out.println("Vehicle 1 after close : " + vehicle1);
        out.println("Vehicle 2 after close : " + vehicle2);
        out.println("Driver 1 after close  : " + driver1);
        out.println("Driver 2 after close  : " + driver2);
        out.println("Identity hash AFTER   : vehicle1=" + System.identityHashCode(vehicle1)
                + ", vehicle2=" + System.identityHashCode(vehicle2));

        check("A3 vehicle1 still retains its state (VIN-1001, Toyota Hilux)",
                "VIN-1001".equals(vehicle1.getVin()) && "Toyota Hilux".equals(vehicle1.getModel()));
        check("A4 vehicle2 still retains its state (VIN-1002, Suzuki Bolan)",
                "VIN-1002".equals(vehicle2.getVin()) && "Suzuki Bolan".equals(vehicle2.getModel()));
        check("A5 same heap objects after depot = null (identity hash unchanged)",
                heapId1 == System.identityHashCode(vehicle1)
                && heapId2 == System.identityHashCode(vehicle2));
        check("A6 vehicles still reference their drivers",
                vehicle1.getDriver() == driver1 && vehicle2.getDriver() == driver2);
        check("A7 drivers still retain their state",
                "Ali Raza".equals(driver1.getFullName())
                && "LIC-ABT-1002".equals(driver2.getLicenseNumber()));
        check("A8 vehicle engines are still fully accessible",
                vehicle1.getEngine().getDisplacementCC() == 2400
                && "Petrol".equals(vehicle2.getEngine().getFuelType()));

        // Surviving vehicles can be reassigned to a different depot
        FleetDepot newDepot = new FleetDepot("DEP-02", "Havelian Satellite Depot");
        newDepot.assignVehicle(vehicle1);
        newDepot.assignVehicle(vehicle2);
        out.println("Reassigned to         : " + newDepot);
        check("A9 surviving vehicles can be reassigned to another depot",
                newDepot.getVehicleCount() == 2
                && newDepot.getAssignedVehicles().contains(vehicle1));

        // Driver leaves the fleet -> Vehicle remains intact
        Driver leaving = vehicle1.releaseDriver();
        out.println("Driver leaves fleet   : " + leaving);
        out.println("Vehicle 1 afterwards  : " + vehicle1);
        check("A10 vehicle remains intact after its driver leaves",
                !vehicle1.hasDriver() && "VIN-1001".equals(vehicle1.getVin())
                && newDepot.getAssignedVehicles().contains(vehicle1));
        check("A11 released driver still exists independently",
                leaving == driver1 && "D-001".equals(leaving.getDriverId()));
    }

    // ------------------------------------------------------------------
    // Test Case B: Composition Lifetime Binding & Encapsulation
    // ------------------------------------------------------------------
    private void testCaseB() {
        out.println();
        out.println("--- TEST CASE B: Composition Lifetime Binding & Encapsulation ---");

        // Step 1: Vehicle builds its own Engine internally
        Vehicle vehicle = new Vehicle("VIN-2001", "Honda Civic", "ENG-L15B-777", 1500, "Petrol");
        out.println("Created               : " + vehicle);

        boolean engineInConstructor = false;
        for (Constructor<?> c : Vehicle.class.getConstructors()) {
            for (Class<?> parameterType : c.getParameterTypes()) {
                if (parameterType == Engine.class) {
                    engineInConstructor = true;
                }
            }
        }
        check("B1 no public Vehicle constructor accepts an external Engine", !engineInConstructor);

        // Step 2: store the value returned by getEngine()
        Engine leakedEngine = vehicle.getEngine();
        Engine secondCall = vehicle.getEngine();
        out.println("leakedEngine          : " + leakedEngine
                + "  (identity hash " + System.identityHashCode(leakedEngine) + ")");
        out.println("getEngine() 2nd call  : " + secondCall
                + "  (identity hash " + System.identityHashCode(secondCall) + ")");
        check("B2 getEngine() never returns the same object twice (always a copy)",
                leakedEngine != secondCall);
        check("B3 the copy starts with the same state as the internal engine",
                leakedEngine.equals(secondCall));

        // Step 3: attempt to mutate the leaked engine
        leakedEngine.setEngineNumber("TAMPERED-000");
        leakedEngine.setDisplacementCC(9999);
        leakedEngine.setFuelType("Rocket Fuel");
        out.println("leakedEngine mutated  : " + leakedEngine);
        out.println("Vehicle's real engine : " + vehicle.getEngine());

        // Step 4: verify the internal engine did not change
        Engine real = vehicle.getEngine();
        check("B4 internal engine number unchanged", "ENG-L15B-777".equals(real.getEngineNumber()));
        check("B5 internal displacement unchanged", real.getDisplacementCC() == 1500);
        check("B6 internal fuel type unchanged", "Petrol".equals(real.getFuelType()));
        check("B7 defensive copying is active (leaked copy differs from internal state)",
                !leakedEngine.equals(real));

        // Step 5: maintenance history
        vehicle.addMaintenanceRecord("LOG-001", "Engine oil and filter change", 45.50);
        vehicle.addMaintenanceRecord("LOG-002", "Front brake pad replacement", 120.00);
        List<MaintenanceLog> history = vehicle.getMaintenanceHistory();
        out.println("History fetched       : " + history);
        check("B8 history contains the two records added through the vehicle", history.size() == 2);

        history.get(0).setServiceDescription("FORGED ENTRY");
        history.get(0).setServiceCost(0.0);
        history.add(new MaintenanceLog("LOG-999", "Injected by outside caller", 1.0));
        history.clear();
        out.println("After clear()/mutate  : retrieved list size = " + history.size());

        List<MaintenanceLog> fresh = vehicle.getMaintenanceHistory();
        out.println("Vehicle's real history: " + fresh);
        check("B9 clearing the retrieved list did not clear the internal history",
                fresh.size() == 2 && vehicle.getMaintenanceCount() == 2);
        check("B10 mutating a retrieved entry did not alter the internal entry",
                "Engine oil and filter change".equals(fresh.get(0).getServiceDescription())
                && fresh.get(0).getServiceCost() == 45.50);
        check("B11 the injected entry never reached the internal history",
                fresh.stream().noneMatch(log -> "LOG-999".equals(log.getLogId())));
        check("B12 two history requests return independent list objects",
                vehicle.getMaintenanceHistory() != vehicle.getMaintenanceHistory());
    }

    private void check(String description, boolean condition) {
        if (condition) {
            passed++;
            out.println("  [PASS] " + description);
        } else {
            failed++;
            out.println("  [FAIL] " + description);
        }
    }
}
