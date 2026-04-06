package qtriptest.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AdventurePage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By searchInput = By.id("search-adventures");
    private final By adventureCards = By.cssSelector("#data .col, #data [class*='col-']");
    private final By activityFilter = By.id("duration-select");
    private final By categoryFilter = By.id("category-select");
    private final By clearButton = By.xpath("//a[contains(.,'Clear')] | //button[contains(.,'Clear')]");

    public AdventurePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void searchCity(String adventureName) {
        WebElement searchField = wait.until(ExpectedConditions.elementToBeClickable(searchInput));
        searchField.clear();
        searchField.sendKeys(adventureName);
    }

    public void selectCity(String adventureName) {
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(adventureCards));
        for (WebElement card : driver.findElements(adventureCards)) {
            if (card.getText().toLowerCase().contains(adventureName.toLowerCase())) {
                card.click();
                return;
            }
        }

        throw new IllegalArgumentException("Adventure not available on the page: " + adventureName);
    }

    public void selectFilters(String duration) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(activityFilter));
        new Select(driver.findElement(activityFilter)).selectByVisibleText(duration);
    }

    public void selectCategory(String category) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(categoryFilter));
        new Select(driver.findElement(categoryFilter)).selectByVisibleText(category);
    }

    public void clearFilters() {
        clearByButtonOrReset(activityFilter);
    }

    public void clearCategory() {
        clearByButtonOrReset(categoryFilter);
    }

    public boolean verifyDataDisplayed() {
        return getAdventureCount() > 0;
    }

    public int getAdventureCount() {
        try {
            List<WebElement> cards = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(adventureCards));
            return cards.size();
        } catch (TimeoutException e) {
            return 0;
        }
    }

    private void clearByButtonOrReset(By selectLocator) {
        if (isClearButtonVisible()) {
            wait.until(ExpectedConditions.elementToBeClickable(clearButton)).click();
            return;
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(selectLocator));
        new Select(driver.findElement(selectLocator)).selectByIndex(0);
    }

    private boolean isClearButtonVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(clearButton)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }
}
