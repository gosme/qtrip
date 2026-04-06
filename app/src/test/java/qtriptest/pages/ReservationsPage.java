package qtriptest.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import qtriptest.wrappers.Wrappers;

public class ReservationsPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Wrappers wrappers;

    private final By reservationRows = By.cssSelector("table tbody tr");
    private final By cancelButtons = By.xpath("//button[contains(.,'Cancel')]");

    public ReservationsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.wrappers = new Wrappers(driver);
    }

    public String getTransactionID() {
        List<WebElement> rows = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(reservationRows));
        if (rows.isEmpty()) {
            return "";
        }

        List<WebElement> cells = rows.get(0).findElements(By.tagName("td"));
        return cells.isEmpty() ? "" : cells.get(0).getText().trim();
    }

    public boolean cancelReservation() {
        try {
            WebElement cancelButton = wrappers.findElementWithRetry(cancelButtons);
            if (!wrappers.click(cancelButton)) {
                return false;
            }
            wait.until(ExpectedConditions.stalenessOf(cancelButton));
            return true;
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    public boolean hasReservation(String transactionId) {
        List<WebElement> rows = driver.findElements(reservationRows);
        for (WebElement row : rows) {
            if (row.getText().contains(transactionId)) {
                return true;
            }
        }

        return false;
    }

    public int getReservationCount() {
        return driver.findElements(reservationRows).size();
    }
}
