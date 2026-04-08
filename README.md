# QTrip QA V2

Automation test project for QTrip covering:
- UI tests with Selenium + TestNG
- API tests with RestAssured + TestNG
- Extent Reports for full-suite execution

## Stack
- Java
- Gradle Wrapper
- TestNG
- Selenium WebDriver
- RestAssured
- Apache POI
- Extent Reports

## Required JDK and Gradle

This repository currently uses:
- Gradle Wrapper: `9.1.0`
- Recommended JDK: `JDK 25`

The active wrapper is defined in:
- [gradle-wrapper.properties](/c:/Users/gosgo/crio/gokulesh02-ME_QTRIP_QA_V2/gradle/wrapper/gradle-wrapper.properties)

Use these environment commands in PowerShell before running Gradle:

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-25'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
$env:GRADLE_USER_HOME=(Join-Path (Get-Location) '.gradle-user-home')
```

## Project Structure
- `app/src/test/java/qtriptest/tests` - UI testcases
- `app/src/test/java/qtriptest/API_tests` - API testcases
- `app/src/test/java/qtriptest/pages` - page objects
- `app/src/test/java/qtriptest/wrappers` - wrapper utilities
- `app/src/test/java/qtriptest/reports` - Extent report support
- `app/src/test/resources/testng.xml` - TestNG suite

## Exact Run Commands

Compile tests:

```powershell
.\gradlew.bat testClasses
```

Run full suite from `testng.xml`:

```powershell
.\gradlew.bat test
```

Run a single UI testcase:

```powershell
.\gradlew.bat test --tests qtriptest.tests.Testcase01
```

Run a single API testcase:

```powershell
.\gradlew.bat test --tests qtriptest.API_tests.testCase_API_02
```

Run all API test classes:

```powershell
.\gradlew.bat test --tests "qtriptest.API_tests.*"
```

Run in headless mode:

```powershell
.\gradlew.bat test -Dheadless=true
```

Override the application URL:

```powershell
.\gradlew.bat test -Dapp.url=https://qtripdynamic-qa-frontend.vercel.app
```

Override the API base URL consumer path:

```powershell
$env:QTRIP_API_BASE_URL='https://qtrip-backend.labs.crio.do'
.\gradlew.bat test --tests "qtriptest.API_tests.*"
```

## Local vs Remote WebDriver

WebDriver mode is currently controlled by a hardcoded switch in:
- [DriverSingleton.java](/c:/Users/gosgo/crio/gokulesh02-ME_QTRIP_QA_V2/app/src/test/java/qtriptest/DriverSingleton.java)

Current setting:

```java
private static final boolean USE_REMOTE_DRIVER = true;
```

### Remote WebDriver Setup

Use remote mode when:
- Selenium Grid or remote standalone server is running
- A hub is available at `http://localhost:8082/wd/hub`, or you provide a custom remote URL

Remote URL precedence:
1. `-Dremote.url=...`
2. `SELENIUM_REMOTE_URL`
3. default `http://localhost:8082/wd/hub`

Example:

```powershell
$env:SELENIUM_REMOTE_URL='http://localhost:8082/wd/hub'
.\gradlew.bat test --tests qtriptest.tests.Testcase01
```

Or:

```powershell
.\gradlew.bat test -Dremote.url=http://localhost:8082/wd/hub
```

### Local WebDriver Setup

To run locally:
1. Open [DriverSingleton.java](/c:/Users/gosgo/crio/gokulesh02-ME_QTRIP_QA_V2/app/src/test/java/qtriptest/DriverSingleton.java)
2. Change:

```java
private static final boolean USE_REMOTE_DRIVER = false;
```

3. Ensure local Chrome is available and compatible with Selenium Manager / local driver resolution
4. Run:

```powershell
.\gradlew.bat test --tests qtriptest.tests.Testcase01
```

## TestNG Suite

The suite file is:
- [testng.xml](/c:/Users/gosgo/crio/gokulesh02-ME_QTRIP_QA_V2/app/src/test/resources/testng.xml)

It includes:
- UI tests
- API tests
- Extent report listener

## Reports

TestNG report path:
- `app/tmp/external_build/reports/tests/test/index.html`

Extent report path:
- `app/tmp/external_build/reports/extent/ExtentReport.html`

Extent report configuration:
- title: `QTrip Regression Suite`
- theme: `Standard`
- report name: `Regression test for QTrip`

## Notes
- UI tests require a working browser environment.
- API tests require connectivity to the QTrip backend.
- If remote execution is enabled and the Selenium hub is unavailable, UI tests will fail during setup.
- If local execution is enabled, Chrome startup behavior depends on the local machine browser setup.
