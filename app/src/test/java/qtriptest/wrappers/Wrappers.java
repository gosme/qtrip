package qtriptest.wrappers;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Wrappers {
    private static final int MAX_FIND_ATTEMPTS = 3;

    private final WebDriver driver;
    private final WebDriverWait wait;

    public Wrappers(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean click(By locator) {
        try {
            WebElement element = findElementWithRetry(locator);
            return click(element);
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean click(WebElement element) {
        if (element == null) {
            return false;
        }

        try {
            scrollIntoView(element);
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
            return true;
        } catch (StaleElementReferenceException | TimeoutException e) {
            return false;
        }
    }

    public boolean sendKeys(By locator, String text) {
        return sendKeys(locator, text, false);
    }

    public boolean sendKeysDate(By locator, String text) {
        for (int attempt = 0; attempt < MAX_FIND_ATTEMPTS; attempt++) {
            try {
                WebElement element = findElementWithRetry(locator);
                scrollIntoView(element);
                wait.until(ExpectedConditions.elementToBeClickable(element));
                sleepBriefly();
                element.clear();
                sleepBriefly();
                element.sendKeys(text);
                return true;
            } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException
                    | TimeoutException e) {
                if (attempt == MAX_FIND_ATTEMPTS - 1) {
                    return false;
                }
                sleepBriefly();
            }
        }

        return false;
    }

    public boolean sendKeys(By locator, String text, boolean typeSlowly) {
        for (int attempt = 0; attempt < MAX_FIND_ATTEMPTS; attempt++) {
            try {
                WebElement element = findElementWithRetry(locator);
                scrollIntoView(element);
                wait.until(ExpectedConditions.elementToBeClickable(element));
                sleepBriefly();
                element.clear();
                sleepBriefly();

                if (typeSlowly) {
                    for (char character : text.toCharArray()) {
                        element.sendKeys(String.valueOf(character));
                        sleepBriefly();
                    }
                } else {
                    element.sendKeys(text);
                }
                return true;
            } catch (NoSuchElementException | StaleElementReferenceException | ElementNotInteractableException
                    | TimeoutException e) {
                if (attempt == MAX_FIND_ATTEMPTS - 1) {
                    return false;
                }
                sleepBriefly();
            }
        }

        return false;
    }

    public boolean navigateToURL(String url) {
        try {
            if (!driver.getCurrentUrl().equals(url)) {
                driver.get(url);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public WebElement findElementWithRetry(By locator) {
        RuntimeException lastException = null;
        for (int attempt = 0; attempt < MAX_FIND_ATTEMPTS; attempt++) {
            try {
                return wait.ignoring(StaleElementReferenceException.class)
                        .until(ExpectedConditions.presenceOfElementLocated(locator));
            } catch (TimeoutException | NoSuchElementException | StaleElementReferenceException e) {
                lastException = new NoSuchElementException("Unable to find element after retry: " + locator, e);
            }
        }

        throw lastException == null
                ? new NoSuchElementException("Unable to find element after retry: " + locator)
                : lastException;
    }

    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});",
                element);
    }

    private void sleepBriefly() {
        try {
            Thread.sleep(120);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while typing into input field", e);
        }
    }
}
