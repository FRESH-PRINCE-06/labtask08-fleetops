package com.uet.construction.fleetops;

import java.util.Objects;

/**
 * The Engine of a {@link Vehicle}.
 *
 * COMPOSITION: an Engine has no meaning outside its Vehicle. Only the Vehicle
 * constructs it, and the Vehicle never hands out its internal instance; callers
 * only ever receive copies made with the copy constructor below.
 */
public class Engine {

    private String engineNumber;
    private int displacementCC;
    private String fuelType;

    public Engine(String engineNumber, int displacementCC, String fuelType) {
        this.engineNumber = requireText(engineNumber, "engineNumber");
        this.displacementCC = requirePositive(displacementCC);
        this.fuelType = requireText(fuelType, "fuelType");
    }

    /**
     * Copy constructor used for defensive (deep) cloning.
     * All fields are immutable values (String, int), so copying them yields a
     * completely independent Engine.
     */
    public Engine(Engine other) {
        if (other == null) {
            throw new IllegalArgumentException("Engine to copy must not be null");
        }
        this.engineNumber = other.engineNumber;
        this.displacementCC = other.displacementCC;
        this.fuelType = other.fuelType;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = requireText(engineNumber, "engineNumber");
    }

    public int getDisplacementCC() {
        return displacementCC;
    }

    public void setDisplacementCC(int displacementCC) {
        this.displacementCC = requirePositive(displacementCC);
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = requireText(fuelType, "fuelType");
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be null or blank");
        }
        return value.trim();
    }

    private static int requirePositive(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("displacementCC must be greater than zero");
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Engine)) {
            return false;
        }
        Engine other = (Engine) o;
        return displacementCC == other.displacementCC
                && engineNumber.equals(other.engineNumber)
                && fuelType.equals(other.fuelType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(engineNumber, displacementCC, fuelType);
    }

    @Override
    public String toString() {
        return "Engine{number=" + engineNumber
                + ", displacementCC=" + displacementCC
                + ", fuelType=" + fuelType + "}";
    }
}
