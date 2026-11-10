package com.uet.construction.fleetops;

import java.util.Objects;

/**
 * One maintenance entry in a {@link Vehicle}'s private service history.
 *
 * COMPOSITION: a log entry has no meaning outside its parent Vehicle. Entries
 * are created only by Vehicle.addMaintenanceRecord(...) and are exposed to
 * outsiders only as defensive copies.
 */
public class MaintenanceLog {

    private String logId;
    private String serviceDescription;
    private double serviceCost;

    public MaintenanceLog(String logId, String serviceDescription, double serviceCost) {
        this.logId = requireText(logId, "logId");
        this.serviceDescription = requireText(serviceDescription, "serviceDescription");
        this.serviceCost = requireNonNegative(serviceCost);
    }

    /** Copy constructor used for defensive duplication. */
    public MaintenanceLog(MaintenanceLog other) {
        if (other == null) {
            throw new IllegalArgumentException("MaintenanceLog to copy must not be null");
        }
        this.logId = other.logId;
        this.serviceDescription = other.serviceDescription;
        this.serviceCost = other.serviceCost;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = requireText(logId, "logId");
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = requireText(serviceDescription, "serviceDescription");
    }

    public double getServiceCost() {
        return serviceCost;
    }

    public void setServiceCost(double serviceCost) {
        this.serviceCost = requireNonNegative(serviceCost);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be null or blank");
        }
        return value.trim();
    }

    private static double requireNonNegative(double value) {
        if (Double.isNaN(value) || value < 0) {
            throw new IllegalArgumentException("serviceCost must not be negative");
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MaintenanceLog)) {
            return false;
        }
        MaintenanceLog other = (MaintenanceLog) o;
        return Double.compare(serviceCost, other.serviceCost) == 0
                && logId.equals(other.logId)
                && serviceDescription.equals(other.serviceDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logId, serviceDescription, serviceCost);
    }

    @Override
    public String toString() {
        return "MaintenanceLog{id=" + logId
                + ", description=" + serviceDescription
                + ", cost=" + String.format("%.2f", serviceCost) + "}";
    }
}
