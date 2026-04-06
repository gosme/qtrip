package qtriptest.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import qtriptest.wrappers.Wrappers;

public class AdventureDetailsPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Wrappers wrappers;

    private final By nameInput = By.name("name");
    private final By dateInput = By.name("date");
    private final By personInput = By.name("person");
    private final By reserveButton = By.xpath("//button[contains(.,'Reserve')]");
    private final By successBanner = By.xpath("//*[contains(text(),'Reservation successful') or contains(text(),'Greetings')]");
    private final By reservationButton = By.xpath("//a[contains(@href,'/pages/reservations') or contains(.,'Reservations')]");

    public AdventureDetailsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.wrappers = new Wrappers(driver);
    }

    public void enterName(String guestName) {
        wrappers.sendKeys(nameInput, guestName, true);
    }

    public void enterDate(String date) {
        selectDateOnCalendar(date);
    }

    public void selectDateOnCalendar(String date) {
        wrappers.sendKeysDate(dateInput, date);
    }

    public void enterPerson(String count) {
        wrappers.sendKeys(personInput, count, true);
    }

    public void clickReserveButton() {
        wrappers.click(reserveButton);
    }

    public boolean verifyReservationSuccess() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(successBanner)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void clickReservationsButton() {
        wrappers.click(reservationButton);
    }
}
