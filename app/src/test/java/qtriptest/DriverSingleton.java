package qtriptest;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

public final class DriverSingleton {
    private static final boolean USE_REMOTE_DRIVER = false;
    private static final String DEFAULT_REMOTE_URL = "http://localhost:8082/wd/hub";
    private static final String DEFAULT_APPLICATION_URL = "https://qtripdynamic-qa-frontend.vercel.app";
    private static RemoteWebDriver driver;

    private DriverSingleton() {
    }

    public static synchronized WebDriver getDriver() throws MalformedURLException {
        if (driver == null) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-popup-blocking");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--start-maximized");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");

            if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
                options.addArguments("--headless=new");
                options.addArguments("--window-size=1440,900");
            }

            driver = createDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
        }

        return driver;
    }

    public static synchronized WebDriver peekDriver() {
        return driver;
    }

    public static String getBaseUrl() {
        return firstNonBlank(
                System.getProperty("app.url"),
                System.getenv("APP_URL"),
                DEFAULT_APPLICATION_URL);
    }

    public static synchronized void closeDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    private static String resolveRemoteUrl() {
        return firstNonBlank(
                System.getProperty("remote.url"),
                System.getenv("SELENIUM_REMOTE_URL"),
                DEFAULT_REMOTE_URL);
    }

    private static RemoteWebDriver createDriver(ChromeOptions options) throws MalformedURLException {
        if (USE_REMOTE_DRIVER) {
            return new RemoteWebDriver(new URL(resolveRemoteUrl()), options);
        }

        return new ChromeDriver(options);
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }

        return "";
    }
}
