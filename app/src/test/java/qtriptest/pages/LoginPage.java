package qtriptest.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import qtriptest.wrappers.Wrappers;

public class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Wrappers wrappers;

    private final By emailInput = By.id("floatingInput");
    private final By passwordInput = By.id("floatingPassword");
    private final By loginButton = By.xpath("//button[contains(.,'Login to QTrip') or normalize-space()='Login']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.wrappers = new Wrappers(driver);
    }

    public void loginUser(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLoginButton();
    }

    public boolean isPageLoaded() {
        try {
            return wait.ignoring(StaleElementReferenceException.class)
                    .until(ExpectedConditions.and(
                            ExpectedConditions.visibilityOfElementLocated(emailInput),
                            ExpectedConditions.visibilityOfElementLocated(passwordInput),
                            ExpectedConditions.visibilityOfElementLocated(loginButton))) != null;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void enterEmail(String email) {
        wrappers.sendKeys(emailInput, email);
    }

    public void enterPassword(String password) {
        wrappers.sendKeys(passwordInput, password);
    }

    public void clickLoginButton() {
        wrappers.click(loginButton);
    }
}
