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

public class Testcase03 extends BaseTest {
    @Test(
            dataProvider = "data-provider",
            dataProviderClass = DP.class,
            groups = { "Booking and Cancellation Flow" },
            priority = 3,
            enabled = false)
    public void TestCase03(String newUserName, String password, String searchCity, String adventureName,
            String guestName, String date, String count) {
        HomePage homePage = new HomePage(driver);

        // Step 1 and 2: Navigate to QTrip and create a new user.
        homePage.clickRegisterButton();
        RegisterPage registerPage = new RegisterPage(driver);
        Assert.assertTrue(registerPage.isPageLoaded(), "Register page did not load");
        String registeredEmail = registerPage.registerUser(newUserName, password, password, true);
        Assert.assertTrue(registerPage.verifyRegistrationSuccess(), "Registration failed");

        // Login with the same registered credentials to continue the flow efficiently.
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page did not load");
        loginPage.loginUser(registeredEmail, password);
        Assert.assertTrue(homePage.isUserLoggedIn(), "User was not logged in");

        // Step 3: Search for an adventure.
        homePage.searchCity(searchCity);
        homePage.clickOnCity(searchCity);
        AdventurePage adventurePage = new AdventurePage(driver);
        adventurePage.searchCity(adventureName);
        adventurePage.selectCity(adventureName);

        AdventureDetailsPage adventureDetailsPage = new AdventureDetailsPage(driver);

        // Step 4 and 5: Enter booking details and reserve the adventure.
        adventureDetailsPage.enterName(guestName);
        adventureDetailsPage.enterDate(date);
        adventureDetailsPage.enterPerson(count);
        adventureDetailsPage.clickReserveButton();
        Assert.assertTrue(adventureDetailsPage.verifyReservationSuccess(), "Reservation was not successful");

        // Step 6 and 7: Go to reservations page and capture the transaction ID.
        adventureDetailsPage.clickReservationsButton();
        ReservationsPage reservationsPage = new ReservationsPage(driver);
        String transactionId = reservationsPage.getTransactionID();
        Assert.assertFalse(transactionId.isEmpty(), "Transaction ID was not available");

        // Step 8 to 10: Cancel the reservation, refresh, and verify the transaction is removed.
        Assert.assertTrue(reservationsPage.cancelReservation(), "Reservation could not be cancelled");
        driver.navigate().refresh();
        Assert.assertFalse(reservationsPage.hasReservation(transactionId),
                "Cancelled reservation is still visible after refresh");
    }
}
