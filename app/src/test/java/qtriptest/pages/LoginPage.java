package qtriptest.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By emailInput = By.id("floatingInput");
    private final By passwordInput = By.id("floatingPassword");
    private final By loginButton = By.xpath("//button[contains(.,'Login to QTrip') or normalize-space()='Login']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void loginUser(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLoginButton();
    }

    public boolean isPageLoaded() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput)).isDisplayed()
                && wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput)).isDisplayed()
                && wait.until(ExpectedConditions.visibilityOfElementLocated(loginButton)).isDisplayed();
    }

    public void enterEmail(String email) {
        WebElement emailField = wait.until(ExpectedConditions.elementToBeClickable(emailInput));
        emailField.clear();
        emailField.sendKeys(email);
    }

    public void enterPassword(String password) {
        WebElement passwordField = wait.until(ExpectedConditions.elementToBeClickable(passwordInput));
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    public void clickLoginButton() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
    }
}
