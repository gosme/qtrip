package qtriptest.pages;

import java.time.Duration;
import java.util.UUID;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RegisterPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By emailInput = By.id("floatingInput");
    private final By passwordInput = By.xpath(
            "//input[@id='floatingPassword' and not(contains(translate(@name,'CONFIRM','confirm'),'confirm'))"
                    + " and not(contains(translate(@placeholder,'CONFIRM','confirm'),'confirm'))]");
    private final By confirmPasswordInput = By.xpath(
            "//input[@id='floatingPassword' and (contains(translate(@name,'CONFIRM','confirm'),'confirm')"
                    + " or contains(translate(@placeholder,'CONFIRM','confirm'),'confirm'))]");
    private final By registerButton = By.xpath("//button[contains(.,'Register Now') or normalize-space()='Register Now']");
    private final By successIndicator = By.xpath("//*[contains(text(),'Registered successfully') or contains(text(),'Login Here')]");

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public String registerUser(String email, String password, String repeatPassword, boolean generateRandomEmail) {
        String resolvedEmail = generateRandomEmail ? appendRandomSuffix(email) : email;
        enterEmail(resolvedEmail);
        enterPassword(password);
        enterRepeatPassword(repeatPassword);
        clickRegisterButton();
        return resolvedEmail;
    }

    public boolean isPageLoaded() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput)).isDisplayed()
                && wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput)).isDisplayed()
                && wait.until(ExpectedConditions.visibilityOfElementLocated(confirmPasswordInput)).isDisplayed();
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

    public void enterRepeatPassword(String repeatPassword) {
        WebElement repeatPasswordField = wait.until(ExpectedConditions.elementToBeClickable(confirmPasswordInput));
        repeatPasswordField.clear();
        repeatPasswordField.sendKeys(repeatPassword);
    }

    public void clickRegisterButton() {
        wait.until(ExpectedConditions.elementToBeClickable(registerButton)).click();
    }

    public boolean verifyRegistrationSuccess() {
        try {
            return wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/pages/login"),
                    ExpectedConditions.visibilityOfElementLocated(successIndicator))) != null;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean verifyRegistrationFailure() {
        return getRegistrationFailureAlertText() != null;
    }

    public String acceptRegistrationFailurePopup() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();
            alert.accept();
            return alertText;
        } catch (TimeoutException | NoAlertPresentException e) {
            return null;
        }
    }

    public String handleRegistrationOutcome(String originalEmail, String password) {
        String failureAlertText = acceptRegistrationFailurePopup();
        if (failureAlertText != null && !failureAlertText.trim().isEmpty()) {
            return registerUser(originalEmail, password, password, true);
        }

        return originalEmail;
    }

    private String getRegistrationFailureAlertText() {
        try {
            return wait.until(ExpectedConditions.alertIsPresent()).getText();
        } catch (TimeoutException | NoAlertPresentException e) {
            return null;
        }
    }

    private String appendRandomSuffix(String email) {
        int delimiterIndex = email.indexOf('@');
        if (delimiterIndex < 0) {
            return email + "+" + UUID.randomUUID().toString();
        }

        String userName = email.substring(0, delimiterIndex);
        String domain = email.substring(delimiterIndex);
        return userName + "+" + UUID.randomUUID().toString() + domain;
    }
}
