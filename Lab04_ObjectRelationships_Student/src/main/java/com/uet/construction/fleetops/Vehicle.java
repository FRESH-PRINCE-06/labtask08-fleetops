package com.uet.construction.fleetops;

import java.util.ArrayList;
import java.util.List;

/**
 * A transport vehicle in the FleetOps domain model.
 *
 * COMPOSITION (strong ownership, coincident lifecycle):
 *   - the Engine is built INSIDE the constructor from raw parameters;
 *   - the maintenance history is a private list of MaintenanceLog entries that
 *     can only grow through addMaintenanceRecord(...);
 *   - getEngine() and getMaintenanceHistory() return defensive copies, so the
 *     internal representation is never exposed (no aliasing).
 *
 * AGGREGATION (independent lifecycle, reference sharing):
 *   - the optional Driver is created outside and merely referenced here.
 */
public class Vehicle {

    private final String vin;
    private String model;
    private final Engine engine;                        // composed part
    private final List<MaintenanceLog> maintenanceLogs; // composed parts
    private Driver driver;                              // aggregated (optional)

    /**
     * Restricted instantiation: no existing Engine is accepted from outside.
     * The Engine is created here from the raw parameters, so the Vehicle is the
     * sole owner of that object.
     */
    public Vehicle(String vin, String model,
                   String engineNumber, int displacementCC, String fuelType) {
        this.vin = requireText(vin, "vin");
        this.model = requireText(model, "model");
        this.engine = new Engine(engineNumber, displacementCC, fuelType);
        this.maintenanceLogs = new ArrayList<>();
        this.driver = null;
    }

    public String getVin() {
        return vin;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = requireText(model, "model");
    }

    /** Defensive accessor: returns a deep copy, never this.engine. */
    public Engine getEngine() {
        return new Engine(this.engine);
    }

    /**
     * The ONLY way to add a maintenance entry. The MaintenanceLog is
     * instantiated internally so no outside reference to it can exist.
     */
    public void addMaintenanceRecord(String logId, String description, double cost) {
        MaintenanceLog entry = new MaintenanceLog(logId, description, cost);
        for (MaintenanceLog existing : maintenanceLogs) {
            if (existing.getLogId().equals(entry.getLogId())) {
                throw new IllegalArgumentException(
                        "A maintenance record with id '" + entry.getLogId() + "' already exists");
            }
        }
        maintenanceLogs.add(entry);
    }

    /**
     * Defensive accessor: returns a NEW list that contains a deep clone of every
     * entry. Clearing or editing the returned list (or its elements) has no
     * effect on the vehicle's real history.
     */
    public List<MaintenanceLog> getMaintenanceHistory() {
        List<MaintenanceLog> copy = new ArrayList<>(maintenanceLogs.size());
        for (MaintenanceLog entry : maintenanceLogs) {
            copy.add(new MaintenanceLog(entry));
        }
        return copy;
    }

    public int getMaintenanceCount() {
        return maintenanceLogs.size();
    }

    // ---- Aggregation: optional Driver ------------------------------------

    /** Assigns an already existing Driver (reference sharing, no copying). */
    public void assignDriver(Driver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("driver must not be null");
        }
        this.driver = driver;
    }

    /** Unlinks the Driver. The Driver object itself is not touched. */
    public Driver releaseDriver() {
        Driver previous = this.driver;
        this.driver = null;
        return previous;
    }

    /** Aggregation: the same shared Driver reference is returned on purpose. */
    public Driver getDriver() {
        return driver;
    }

    public boolean hasDriver() {
        return driver != null;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be null or blank");
        }
        return value.trim();
    }

    @Override
    public String toString() {
        return "Vehicle{vin=" + vin
                + ", model=" + model
                + ", engine=" + engine
                + ", maintenanceRecords=" + maintenanceLogs.size()
                + ", driver=" + (driver == null ? "none" : driver.getFullName()) + "}";
    }
}
