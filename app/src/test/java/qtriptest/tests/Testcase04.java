package qtriptest.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import qtriptest.DP;
import qtriptest.pages.AdventureDetailsPage;
import qtriptest.pages.AdventurePage;
import qtriptest.pages.HomePage;
import qtriptest.pages.LoginPage;
import qtriptest.pages.RegisterPage;
import qtriptest.pages.ReservationsPage;

public class Testcase04 extends BaseTest {
    @Test(
            dataProvider = "data-provider",
            dataProviderClass = DP.class,
            groups = { "Reliability Flow" },
            priority = 4,
            enabled = true,
            description = "Test Case 4: Verify Reliability of Booking Flow")
    public void TestCase04(String newUserName, String password, String dataset1, String dataset2, String dataset3) {
        HomePage homePage = new HomePage(driver);

        // Step 1 and 2: Navigate to QTrip and create a new user.
        homePage.clickRegisterButton();
        RegisterPage registerPage = new RegisterPage(driver);
        Assert.assertTrue(registerPage.isPageLoaded(), "Register page did not load");
        String registeredEmail = registerPage.registerUser(newUserName, password, password, true);
        Assert.assertTrue(registerPage.verifyRegistrationSuccess(), "Registration failed");

        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page did not load");
        loginPage.loginUser(registeredEmail, password);
        Assert.assertTrue(homePage.isUserLoggedIn(), "User was not logged in");

        // Step 3 to 6: Make three adventure bookings using the provided datasets.
        createReservationFromDataset(dataset1);
        createReservationFromDataset(dataset2);
        createReservationFromDataset(dataset3);

        // Step 7: Click on the reservations page from the navigation bar.
        homePage.clickReservationsButton();

        // Step 8: Verify that all the bookings are displayed on the reservations page.
        ReservationsPage reservationsPage = new ReservationsPage(driver);
        Assert.assertTrue(reservationsPage.getReservationCount() >= 3,
                "Expected at least three reservations to be present");
    }

    private void createReservationFromDataset(String dataset) {
        String[] values = dataset.split(";");
        String city = values[0];
        String adventureName = values[1];
        String guestName = values[2];
        String date = values[3];
        String count = values[4];

        HomePage homePage = new HomePage(driver);
        homePage.searchCity(city);
        homePage.clickOnCity(city);

        AdventurePage adventurePage = new AdventurePage(driver);
        adventurePage.searchAdventure(adventureName);
        adventurePage.selectAdventure(adventureName);

        AdventureDetailsPage adventureDetailsPage = new AdventureDetailsPage(driver);
        adventureDetailsPage.enterName(guestName);
        adventureDetailsPage.enterDate(date);
        adventureDetailsPage.enterPerson(count);
        adventureDetailsPage.clickReserveButton();
        Assert.assertTrue(adventureDetailsPage.verifyReservationSuccess(), "Reservation was not successful");

        driver.get(qtriptest.DriverSingleton.getBaseUrl());
    }
}
