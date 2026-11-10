# Lab 08 - Object-Oriented Programming (OOP) in Java

**Course:** Software Construction & Development (SE-206)
**University:** University of Engineering and Technology, Abbottabad Campus
**Semester:** 5th Semester Software Engineering - Fall 2026
**Instructor:** Engr. Rizwan Shah
**Student:** M. Mubashir Hassan (24ABSWE0009)

**Project:** `Lab04_ObjectRelationships_Student`
**Group Id:** `com.uet.construction`  |  **Package:** `com.uet.construction.fleetops`

## Problem scenario

Core domain module of **FleetOps**, an Enterprise Fleet Management System. It models vehicles,
their internal components, drivers and depots, and shows the difference between
**Composition** (strong ownership, coincident lifecycle) and **Aggregation** (independent
lifecycle, reference sharing), including prevention of **Representation Exposure** through
defensive copying.

## Task descriptions

| Task | Description | Files |
|------|-------------|-------|
| Task 1 | Maven project scaffolding and `.gitignore` (`target/`, `.nb-cache/`) | `pom.xml`, `.gitignore` |
| Task 2 | **Aggregation** - `Driver`, `Vehicle` <-> `FleetDepot`, `Vehicle` <-> `Driver` | `Driver.java`, `FleetDepot.java` |
| Task 3 | **Composition** + defensive copying - `Engine`, `MaintenanceLog`, `Vehicle` | `Engine.java`, `MaintenanceLog.java`, `Vehicle.java` |
| Task 4 | Verification driver (Test Case A and Test Case B) | `FleetVerificationDriver.java` |
| Extra  | JUnit 5 test suite (39 tests) | `src/test/java/...` |

### Relationship summary

| Relationship | Type | How it is enforced |
|---|---|---|
| Vehicle -> Engine | Composition | Engine is created inside the Vehicle constructor from raw parameters; `getEngine()` returns a copy made with `Engine(Engine other)` |
| Vehicle -> MaintenanceLog | Composition | Logs are created inside `addMaintenanceRecord(...)`; `getMaintenanceHistory()` returns a new list of cloned logs |
| FleetDepot -> Vehicle | Aggregation | Depot stores references to existing vehicles; `getAssignedVehicles()` is an unmodifiable view; releasing a vehicle never destroys it |
| Vehicle -> Driver | Aggregation | Driver is created outside and only referenced; releasing the driver leaves both objects intact |

## Project structure

```
Lab04_ObjectRelationships_Student/
|-- pom.xml
|-- .gitignore
|-- README.md
|-- docs/EXPECTED_OUTPUT.txt
`-- src/
    |-- main/java/com/uet/construction/fleetops/
    |   |-- Driver.java
    |   |-- Engine.java
    |   |-- MaintenanceLog.java
    |   |-- Vehicle.java
    |   |-- FleetDepot.java
    |   `-- FleetVerificationDriver.java
    `-- test/java/com/uet/construction/fleetops/
        |-- DriverTest.java
        |-- EngineTest.java
        |-- MaintenanceLogTest.java
        |-- VehicleTest.java
        |-- FleetDepotTest.java
        `-- FleetVerificationDriverTest.java
```

## How to run

**Requirements:** JDK 17 or newer, Apache Maven 3.8+ (bundled with Apache NetBeans).

### In Apache NetBeans
1. `File > Open Project...` and select the `Lab04_ObjectRelationships_Student` folder.
2. Run the verification driver: right-click `FleetVerificationDriver.java` > **Run File**
   (or press `Shift+F6`).
3. Run all JUnit tests: right-click the project > **Test** (or press `Alt+F6`).
   Results appear in the *Test Results* panel.

### From the command line
```bash
# compile and run all JUnit tests
mvn clean test

# run the verification driver (Test Case A and Test Case B)
mvn compile exec:java
```

The expected console output of the driver is stored in `docs/EXPECTED_OUTPUT.txt`.

## Git / GitHub

```bash
git init
git add .
git commit -m "Lab 08: FleetOps - Aggregation and Composition with defensive copying"
git branch -M main
git remote add origin https://github.com/<your-username>/<your-repo>.git
git push -u origin main
```

Remember to set the repository visibility to **Public**.
