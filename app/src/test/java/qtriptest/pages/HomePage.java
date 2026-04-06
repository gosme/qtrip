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

import qtriptest.wrappers.Wrappers;

public class HomePage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Wrappers wrappers;

    private final By banner = By.cssSelector("body");
    private final By citySearchInput = By.id("autocomplete");
    private final By autoCompleteOptions = By.xpath("//a[contains(@href,'/pages/adventures/?city=')]");
    private final By adventureCards = By.cssSelector("#data .col, #data [class*='col-']");
    private final By invalidCityMessage = By.xpath("//*[contains(text(),'No City found') or contains(text(),'No Results Found')]");
    private final By loginButton = By.xpath("//a[contains(@href,'/pages/login') or normalize-space()='Login']");
    private final By registerButton = By.xpath("//a[contains(@href,'/pages/register') or normalize-space()='Register']");
    private final By reservationsButton = By.xpath("//a[contains(@href,'/pages/reservations') or normalize-space()='Reservations' or contains(.,'Reservations')]");
    private final By logoutButton = By.xpath("//nav//div[contains(@class,'nav-link login register') and (normalize-space()='Logout' or contains(.,'Logout'))]");

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.wrappers = new Wrappers(driver);
    }

    public String getTitle() {
        wait.until(ExpectedConditions.presenceOfElementLocated(banner));
        return driver.getTitle();
    }

    public boolean isPageLoaded() {
        try {
            return wait.until(ExpectedConditions.and(
                    ExpectedConditions.presenceOfElementLocated(banner),
                    ExpectedConditions.visibilityOfElementLocated(citySearchInput),
                    ExpectedConditions.elementToBeClickable(citySearchInput))) != null;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void searchCity(String cityName) {
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(citySearchInput));
        searchBox.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        wrappers.sendKeys(citySearchInput, cityName, true);
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
        try {
            List<WebElement> messages = driver.findElements(invalidCityMessage);
            for (WebElement message : messages) {
                try {
                    if (message.isDisplayed()) {
                        return true;
                    }
                } catch (StaleElementReferenceException e) {
                    List<WebElement> refreshedMessages = driver.findElements(invalidCityMessage);
                    return !refreshedMessages.isEmpty() && refreshedMessages.get(0).isDisplayed();
                }
            }
            return false;
        } catch (StaleElementReferenceException e) {
            List<WebElement> refreshedMessages = driver.findElements(invalidCityMessage);
            return !refreshedMessages.isEmpty() && refreshedMessages.get(0).isDisplayed();
        }
    }

    public boolean waitForInvalidCityMessage() {
        try {
            return wait.ignoring(StaleElementReferenceException.class)
                    .until(ExpectedConditions.visibilityOfElementLocated(invalidCityMessage))
                    .isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isValidCityDisplayedInAutoComplete(String cityName) {
        try {
            By cityOption = By.xpath("//a[contains(@href,'/pages/adventures/?city=') and normalize-space()='"
                    + cityName + "']");
            return wrappers.findElementWithRetry(cityOption).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void clickOnCity(String cityName) {
        By cityOption = By.xpath("//a[contains(@href,'/pages/adventures/?city=') and normalize-space()='"
                + cityName + "']");
        if (!wrappers.click(cityOption)) {
            throw new NoSuchElementException("City not found in autocomplete: " + cityName);
        }
    }

    public void clickLoginButton() {
        ensureLoggedOutBeforeNavigation();
        wrappers.click(loginButton);
    }

    public void clickRegisterButton() {
        ensureLoggedOutBeforeNavigation();
        wrappers.click(registerButton);
    }

    public void clickReservationsButton() {
        wrappers.click(reservationsButton);
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

        if (!wrappers.click(logoutButton)) {
            return false;
        }
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
