package qtriptest.pages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
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
    private final By durationFilter = By.id("duration-select");
    private final By categoryFilter = By.id("category-select");
    private final By searchClearButton = By.xpath(
            "//input[@id='search-adventures']/following-sibling::*[contains(translate(normalize-space(.),'CLEAR','clear'),'clear')]");
    private final By durationClearButton = By.xpath(
            "//select[@id='duration-select']/following-sibling::*[contains(translate(normalize-space(.),'CLEAR','clear'),'clear')]");
    private final By categoryClearButton = By.xpath(
            "//select[@id='category-select']/following-sibling::*[contains(translate(normalize-space(.),'CLEAR','clear'),'clear')]");

    public AdventurePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void searchAdventure(String adventureName) {
        WebElement searchField = wait.until(ExpectedConditions.elementToBeClickable(searchInput));
        searchField.clear();
        searchField.sendKeys(adventureName);
    }

    public void clearSearchFilter() {
        clearBySiblingOrInput(searchInput, searchClearButton);
    }

    public void selectAdventure(String adventureName) {
        String targetAdventure = adventureName.toLowerCase();

        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                WebElement matchingCard = wait.until(driver -> {
                    List<WebElement> cards = driver.findElements(adventureCards);
                    for (WebElement card : cards) {
                        try {
                            if (card.isDisplayed() && card.getText().toLowerCase().contains(targetAdventure)) {
                                return card;
                            }
                        } catch (StaleElementReferenceException e) {
                            return null;
                        }
                    }
                    return null;
                });

                wait.until(ExpectedConditions.elementToBeClickable(matchingCard)).click();
                return;
            } catch (StaleElementReferenceException e) {
                if (attempt == 2) {
                    throw e;
                }
            }
        }

        throw new IllegalArgumentException("Adventure not available on the page: " + adventureName);
    }

    public void selectFilters(String duration) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(durationFilter));
        new Select(driver.findElement(durationFilter)).selectByVisibleText(duration);
    }

    public void selectCategory(String category) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(categoryFilter));
        new Select(driver.findElement(categoryFilter)).selectByVisibleText(category);
    }

    public void clearFilters() {
        clearBySiblingOrSelect(durationFilter, durationClearButton);
    }

    public void clearCategory() {
        clearBySiblingOrSelect(categoryFilter, categoryClearButton);
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

    private void clearBySiblingOrSelect(By selectLocator, By clearButtonLocator) {
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                if (isSiblingClearButtonVisible(clearButtonLocator)) {
                    wait.until(ExpectedConditions.elementToBeClickable(clearButtonLocator)).click();
                    return;
                }

                wait.until(ExpectedConditions.visibilityOfElementLocated(selectLocator));
                Select select = new Select(driver.findElement(selectLocator));
                List<String> optionTexts = readEnabledOptionTexts(select);

                for (String optionTextRaw : optionTexts) {
                    String optionText = optionTextRaw.trim().toLowerCase();
                    boolean isResetOption = optionText.contains("filter")
                            || optionText.contains("category")
                            || optionText.contains("select")
                            || optionText.contains("all")
                            || optionText.isEmpty();

                    if (isResetOption) {
                        select.selectByVisibleText(optionTextRaw);
                        return;
                    }
                }

                if (!optionTexts.isEmpty()) {
                    select.selectByVisibleText(optionTexts.get(0));
                    return;
                }

                throw new IllegalStateException("No enabled option available to reset filter: " + selectLocator);
            } catch (StaleElementReferenceException e) {
                if (attempt == 2) {
                    throw e;
                }
            }
        }

        throw new IllegalStateException("Unable to reset filter after retries: " + selectLocator);
    }

    private void clearBySiblingOrInput(By inputLocator, By clearButtonLocator) {
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                if (isSiblingClearButtonVisible(clearButtonLocator)) {
                    wait.until(ExpectedConditions.elementToBeClickable(clearButtonLocator)).click();
                    return;
                }

                WebElement input = wait.until(ExpectedConditions.elementToBeClickable(inputLocator));
                input.clear();
                return;
            } catch (StaleElementReferenceException e) {
                if (attempt == 2) {
                    throw e;
                }
            }
        }

        throw new IllegalStateException("Unable to clear input after retries: " + inputLocator);
    }

    private List<String> readEnabledOptionTexts(Select select) {
        List<String> optionTexts = new ArrayList<String>();
        for (WebElement option : select.getOptions()) {
            if (option.isEnabled()) {
                optionTexts.add(option.getText());
            }
        }
        return optionTexts;
    }

    private boolean isSiblingClearButtonVisible(By clearButtonLocator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(clearButtonLocator)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }
}
