package qtriptest.tests;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import qtriptest.DP;
import qtriptest.pages.HomePage;
import qtriptest.pages.LoginPage;
import qtriptest.pages.RegisterPage;

public class Testcase01 extends BaseTest {
    @Test(
            dataProvider = "data-provider",
            dataProviderClass = DP.class,
            groups = { "Login Flow" },
            priority = 1,
            enabled = true,
            description = "Test Case 1: Verify User Registration, Login, and Logout Flow")
    public void TestCase01(String username, String password) {
        HomePage homePage = new HomePage(driver);

        // Step 1: Navigate to the Home Page of QTrip.
        Assert.assertTrue(homePage.getTitle().toLowerCase().contains("qtrip"), "Home page title did not load");

        // Step 2: Click on the Register Page.
        homePage.clickRegisterButton();

        RegisterPage registerPage = new RegisterPage(driver);

        // Step 3: Verify that the registration page is displayed.
        Assert.assertTrue(registerPage.isPageLoaded(), "Register page did not load");

        // Step 4 and 5: Enter email, password, confirm password and register the user.
        Assert.assertNotNull(username);
        Assert.assertFalse(username.trim().isEmpty(), "Username should not be empty");
        Assert.assertNotNull(password);
        Assert.assertFalse(password.trim().isEmpty(), "Password should not be empty");

        Reporter.log(username, true);
        Reporter.log(password, true);
        String registeredEmail = username;

        // Step 4: Enter email, password and confirm password.
        registerPage.enterEmail(registeredEmail);
        registerPage.enterPassword(password);
        registerPage.enterRepeatPassword(password);

        // Step 5: Click on Register Now.
        registerPage.clickRegisterButton();

        // Step 6: Verify that the user is navigated to the Login Page.
        registeredEmail = registerPage.handleRegistrationOutcome(registeredEmail, password); // Comment this, if you do not want to generate random email for registration
        Assert.assertTrue(registerPage.verifyRegistrationSuccess(), "Registration was not successful");

        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page did not load after registration");

        // Step 7: Enter the created user credentials and login.
        loginPage.loginUser(registeredEmail, password);

        // Step 8: Verify that the user is logged in.
        Assert.assertTrue(homePage.isUserLoggedIn(), "User was not logged in after login");

        // Step 9: Click on the Logout Button.
        Assert.assertTrue(homePage.clickLogoutButton(), "Logout button was not available");

        // Step 10: Verify that the user is logged out.
        Assert.assertFalse(homePage.isUserLoggedIn(), "User is still logged in after logout");
    }
}
