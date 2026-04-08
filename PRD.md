# QTrip QA V2 PRD

## Overview
QTrip QA V2 is an automated test project for validating the QTrip application across UI and API layers. The project uses Java, Gradle, TestNG, Selenium WebDriver, RestAssured, and Extent Reports. The goal is to provide reliable regression coverage for core user journeys and backend APIs while keeping the project structure stable and aligned with assessment expectations.

## Product Goals
- Validate critical QTrip user journeys through end-to-end UI automation.
- Validate documented QTrip backend APIs through automated API tests.
- Generate consistent execution artifacts, including TestNG output and a single Extent Report for the full suite.
- Keep the framework maintainable through shared wrappers, singleton-based lifecycle management, and page-object abstractions.

## Scope

### In Scope
- UI automation for the four QTrip testcases.
- API automation for the four documented QTrip API testcases.
- TestNG-based grouped execution for UI and API test suites.
- Excel-driven test data for UI tests.
- Extent Report generation with suite-wide aggregation and screenshots.
- Local and remote WebDriver support via a configurable singleton.

### Out of Scope
- Application feature development.
- CI/CD pipeline setup.
- Performance, load, or security testing.
- Mock server creation.

## Tech Stack
- Java
- Gradle
- TestNG
- Selenium WebDriver
- RestAssured
- Apache POI
- Extent Reports
- SLF4J simple binding for test runtime logging

## Functional Requirements

### UI Automation
The framework must support the following page objects and behaviors:
- `DriverSingleton` must manage a single WebDriver instance and support local or remote execution.
- `HomePage` must support navigation, city search, autocomplete handling, login/register/logout navigation, and reservations navigation.
- `LoginPage` must support direct login and field-level interactions.
- `RegisterPage` must support field-level registration steps, optional random-email registration, alert-based duplicate-user handling, and success verification.
- `AdventurePage` must support city/adventure selection, duration and category filters, adventure-name filtering, and filter clearing via sibling clear controls.
- `AdventureDetailsPage` must support reservation form entry and reservation confirmation.
- `ReservationsPage` must support fetching reservation data and cancellation.

### UI Testcases

#### Testcase01: Login Flow
- Navigate to home.
- Open register page.
- Verify register page.
- Enter email, password, and confirm password using field-level methods.
- Click register.
- Handle registration outcome and verify navigation to login.
- Login with the created user.
- Verify logged-in state.
- Logout.
- Verify logged-out state.

#### Testcase02: Search and Filter Flow
- Navigate to home.
- Search invalid city and verify no-match feedback.
- Search valid city and verify autocomplete.
- Open the city.
- Apply duration filter and verify data.
- Apply category filter and verify filtered data.
- Clear filters and verify unfiltered results.

#### Testcase03: Booking and Cancellation Flow
- Register a new user.
- Login with that user.
- Search city and select adventure.
- Create reservation.
- Verify reservation success.
- Open reservations page.
- Capture transaction ID.
- Cancel reservation.
- Refresh and verify the reservation is removed.

#### Testcase04: Reliability Flow
- Register a new user.
- Login with that user.
- Create three bookings from dataset-driven values.
- Open reservations from the navigation bar.
- Verify all bookings are present.

### API Automation
API tests must use the documented QTrip backend endpoints.

#### API 01: Register and Login
- Register a new user.
- Login with that user.
- Verify HTTP 201 responses.
- Verify `success=true`, token, and user id in login response.

#### API 02: City Search
- Search for `beng` using the cities search endpoint.
- Verify HTTP 200.
- Verify exactly one result.
- Verify description contains `100+ Places`.
- Validate response against a JSON schema.

#### API 03: Reservation Creation
- Register and login a new user.
- Resolve city and adventure identifiers.
- Create a reservation through the API.
- Verify HTTP 200 on booking.
- Verify the booking is returned by the reservations endpoint.

#### API 04: Duplicate User Validation
- Register a new user.
- Attempt duplicate registration with the same email.
- Verify HTTP 400.
- Verify response contains `Email already exists`.

## Data and Test Management
- UI tests must use `DP.java` with Excel-driven input from the resources dataset.
- API tests may generate dynamic data, especially unique email addresses.
- Test steps should call the method that most closely matches the wording of the requirement.

## Reporting Requirements
- Execution must use `testng.xml`.
- Testcases must be grouped as:
  - `Login Flow`
  - `Search and Filter flow`
  - `Booking and Cancellation Flow`
  - `Reliability Flow`
  - `API Tests`
- A single Extent Report must be generated for the full suite execution.
- Extent Report settings:
  - Report Title: `QTrip Regression Suite`
  - Theme: `Standard`
  - Report Name: `Regression test for QTrip`
- The framework must attach:
  - a final-page screenshot after each testcase outcome when a browser session exists
  - failure screenshots for testcase failures when a browser session exists

## Non-Functional Requirements
- Avoid changing project structure unnecessarily.
- Do not remove existing code to implement fixes unless explicitly approved.
- Prefer stable locators, wrapper-based interactions, and retry-safe element lookup.
- Keep compatibility with the project’s active Gradle setup and current dependency set.

## Execution Notes
- UI tests depend on a working browser/grid environment.
- API tests depend on connectivity to the documented QTrip backend.
- The project should support local browser execution and Selenium Grid execution.

## Deliverables
- Working UI automation suite.
- Working API automation suite.
- TestNG suite configuration.
- Extent report output under the build-report path.
- Shared wrappers and singleton utilities for maintainable execution.
