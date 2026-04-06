package qtriptest.tests;

import java.net.MalformedURLException;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import qtriptest.DriverSingleton;
import qtriptest.wrappers.Wrappers;

public abstract class BaseTest {
    protected WebDriver driver;
    protected Wrappers wrappers;

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws MalformedURLException {
        driver = DriverSingleton.getDriver();
        wrappers = new Wrappers(driver);
        wrappers.navigateToURL(DriverSingleton.getBaseUrl());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverSingleton.closeDriver();
    }
}
