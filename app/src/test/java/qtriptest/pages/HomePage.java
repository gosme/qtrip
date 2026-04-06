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
        searchBox.clear();
        searchBox.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        typeSlowly(searchBox, cityName);
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
        String targetCity = cityName.trim().toLowerCase();

        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                WebElement matchingOption = wait.until(driver -> {
                    List<WebElement> options = driver.findElements(autoCompleteOptions);
                    for (WebElement option : options) {
                        try {
                            if (option.isDisplayed() && option.getText().trim().equalsIgnoreCase(targetCity)) {
                                return option;
                            }
                        } catch (StaleElementReferenceException e) {
                            return null;
                        }
                    }
                    return null;
                });

                wait.until(ExpectedConditions.elementToBeClickable(matchingOption)).click();
                return;
            } catch (StaleElementReferenceException e) {
                if (attempt == 2) {
                    throw e;
                }
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

    public void clickReservationsButton() {
        wait.until(ExpectedConditions.elementToBeClickable(reservationsButton)).click();
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

    private void typeSlowly(WebElement element, String value) {
        for (char character : value.toCharArray()) {
            element.sendKeys(String.valueOf(character));
            sleepBriefly();
        }
    }

    private void sleepBriefly() {
        try {
            Thread.sleep(120);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while typing into city search input", e);
        }
    }

}
