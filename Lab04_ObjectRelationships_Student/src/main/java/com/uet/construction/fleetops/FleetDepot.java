package com.uet.construction.fleetops;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An organizational depot that houses and coordinates Vehicles.
 *
 * AGGREGATION: the depot stores references to Vehicles that were created
 * elsewhere. If the depot is closed (or set to null) the Vehicles are NOT
 * destroyed; they remain valid objects that can be assigned to another depot.
 */
public class FleetDepot {

    private final String depotId;
    private final String locationName;
    private final List<Vehicle> vehicles = new ArrayList<>();

    public FleetDepot(String depotId, String locationName) {
        this.depotId = requireText(depotId, "depotId");
        this.locationName = requireText(locationName, "locationName");
    }

    public String getDepotId() {
        return depotId;
    }

    public String getLocationName() {
        return locationName;
    }

    /** Accepts a pre-existing Vehicle created outside the depot. */
    public void assignVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("vehicle must not be null");
        }
        if (vehicles.contains(vehicle)) {
            throw new IllegalArgumentException(
                    "Vehicle " + vehicle.getVin() + " is already assigned to depot " + depotId);
        }
        vehicles.add(vehicle);
    }

    /**
     * Unlinks the vehicle from this depot. The Vehicle itself is neither
     * deallocated nor modified.
     *
     * @return true if the vehicle was assigned here and has been unlinked
     */
    public boolean releaseVehicle(Vehicle vehicle) {
        return vehicles.remove(vehicle);
    }

    /** Unmodifiable view: callers cannot bypass assignVehicle/releaseVehicle. */
    public List<Vehicle> getAssignedVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    public int getVehicleCount() {
        return vehicles.size();
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be null or blank");
        }
        return value.trim();
    }

    @Override
    public String toString() {
        return "FleetDepot{id=" + depotId
                + ", location=" + locationName
                + ", vehicles=" + vehicles.size() + "}";
    }
}
