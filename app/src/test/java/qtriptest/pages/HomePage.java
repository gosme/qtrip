package qtriptest.pages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HomePage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By banner = By.cssSelector("body");
    private final By citySearchInput = By.id("autocomplete");
    private final By autoCompleteOptions = By.cssSelector(".list-group .list-group-item, [role='option']");
    private final By adventureCards = By.cssSelector("#data .col, #data [class*='col-']");
    private final By invalidCityMessage = By.xpath("//*[contains(text(),'No City found') or contains(text(),'No Results Found')]");
    private final By loginButton = By.xpath("//a[contains(@href,'/pages/login') or normalize-space()='Login']");
    private final By registerButton = By.xpath("//a[contains(@href,'/pages/register') or normalize-space()='Register']");
    private final By logoutButton = By.xpath("//nav//div[contains(@class,'nav-link login register') and (normalize-space()='Logout' or contains(.,'Logout'))]");

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public String getTitle() {
        wait.until(ExpectedConditions.presenceOfElementLocated(banner));
        return driver.getTitle();
    }

    public void searchCity(String cityName) {
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(citySearchInput));
        searchBox.clear();
        searchBox.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        searchBox.sendKeys(cityName);
        waitForAutoCompleteState();
    }

    public List<String> fetchCityNamesFromCards() {
        List<WebElement> cards = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(adventureCards));
        List<String> cityNames = new ArrayList<String>();
        for (WebElement card : cards) {
            String cardText = card.getText().trim();
            if (!cardText.isEmpty()) {
                cityNames.add(cardText);
            }
        }

        return cityNames;
    }

    public boolean isInvalidCityMessageDisplayed() {
        List<WebElement> messages = driver.findElements(invalidCityMessage);
        return !messages.isEmpty() && messages.get(0).isDisplayed();
    }

    public boolean isValidCityDisplayedInAutoComplete(String cityName) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(autoCompleteOptions));
            for (WebElement option : driver.findElements(autoCompleteOptions)) {
                if (option.getText().trim().equalsIgnoreCase(cityName)) {
                    return true;
                }
            }
        } catch (TimeoutException e) {
            return false;
        }

        return false;
    }

    public void clickOnCity(String cityName) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(autoCompleteOptions));
        for (WebElement option : driver.findElements(autoCompleteOptions)) {
            if (option.getText().trim().equalsIgnoreCase(cityName)) {
                option.click();
                return;
            }
        }

        throw new NoSuchElementException("City not found in autocomplete: " + cityName);
    }

    public void clickLoginButton() {
        ensureLoggedOutBeforeNavigation();
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
    }

    public void clickRegisterButton() {
        ensureLoggedOutBeforeNavigation();
        wait.until(ExpectedConditions.elementToBeClickable(registerButton)).click();
    }

    public boolean isUserLoggedIn() {
        try {
            List<WebElement> logoutButtons = driver.findElements(logoutButton);
            for (WebElement logoutElement : logoutButtons) {
                try {
                    if (logoutElement.isDisplayed()) {
                        return true;
                    }
                } catch (StaleElementReferenceException e) {
                    List<WebElement> refreshedButtons = driver.findElements(logoutButton);
                    return !refreshedButtons.isEmpty() && refreshedButtons.get(0).isDisplayed();
                }
            }
            return false;
        } catch (StaleElementReferenceException e) {
            List<WebElement> refreshedButtons = driver.findElements(logoutButton);
            return !refreshedButtons.isEmpty() && refreshedButtons.get(0).isDisplayed();
        }
    }

    public boolean clickLogoutButton() {
        if (!isUserLoggedIn()) {
            return false;
        }

        wait.until(ExpectedConditions.elementToBeClickable(logoutButton)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(logoutButton));
        return true;
    }

    private void ensureLoggedOutBeforeNavigation() {
        if (isUserLoggedIn()) {
            clickLogoutButton();
        }
    }

    private void waitForAutoCompleteState() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(autoCompleteOptions),
                    ExpectedConditions.visibilityOfElementLocated(invalidCityMessage)));
        } catch (TimeoutException e) {
            // Let the caller decide how to assert when neither state is rendered in time.
        }
    }

}
