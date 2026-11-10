package com.uet.construction.fleetops;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

public class FleetVerificationDriverTest {

    private String output;
    private int failures;
    private int passes;

    private void runDriver() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        FleetVerificationDriver driver = new FleetVerificationDriver(stream);
        failures = driver.runAll();
        passes = driver.getPassed();
        output = buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("The verification driver finishes with zero failed assertions")
    void testDriverHasNoFailures() {
        runDriver();

        assertEquals(0, failures);
        assertFalse(output.contains("[FAIL]"));
        assertTrue(passes > 0);
    }

    @Test
    @DisplayName("Test Case A (aggregation lifecycle independence) is executed and printed")
    void testCaseAIsPrinted() {
        runDriver();

        assertTrue(output.contains("TEST CASE A"));
        assertTrue(output.contains("depot = null"));
        assertTrue(output.contains("[PASS] A5"));
    }

    @Test
    @DisplayName("Test Case B (composition and defensive copying) is executed and printed")
    void testCaseBIsPrinted() {
        runDriver();

        assertTrue(output.contains("TEST CASE B"));
        assertTrue(output.contains("leakedEngine"));
        assertTrue(output.contains("[PASS] B7"));
        assertTrue(output.contains("[PASS] B9"));
    }
}
