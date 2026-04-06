package qtriptest.reports;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public final class ScreenshotUtils {
    private static final String SCREENSHOT_DIRECTORY = "tmp/external_build/reports/extent/screenshots";
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtils() {
    }

    public static String captureScreenshot(WebDriver driver, String testName, String suffix) {
        if (driver == null || !(driver instanceof TakesScreenshot)) {
            return null;
        }

        try {
            File screenshotDirectory = new File(SCREENSHOT_DIRECTORY);
            if (!screenshotDirectory.exists()) {
                screenshotDirectory.mkdirs();
            }

            String safeFileName = buildFileName(testName, suffix);
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path destination = new File(screenshotDirectory, safeFileName).toPath();
            Files.copy(source.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            return destination.toAbsolutePath().toString();
        } catch (IOException | RuntimeException exception) {
            return null;
        }
    }

    private static String buildFileName(String testName, String suffix) {
        String safeName = testName == null ? "unknown_test"
                : testName.replaceAll("[^a-zA-Z0-9._-]", "_");
        String safeSuffix = suffix == null ? "snapshot"
                : suffix.replaceAll("[^a-zA-Z0-9._-]", "_");
        return safeName + "_" + safeSuffix + "_" + TIMESTAMP_FORMAT.format(new Date()) + ".png";
    }
}
