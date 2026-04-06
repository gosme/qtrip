package qtriptest.reports;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public final class ExtentReportManager {
    private static final String REPORT_DIRECTORY = "tmp/external_build/reports/extent";
    private static final String REPORT_FILE = "ExtentReport.html";
    private static final String REPORT_TITLE = "QTrip Regression Suite";
    private static final String REPORT_NAME = "Regression test for QTrip";
    private static final String REPORT_CONFIG = "extent-config.xml";
    private static ExtentReports extentReports;

    private ExtentReportManager() {
    }

    public static synchronized ExtentReports getInstance() {
        if (extentReports == null) {
            File reportDirectory = new File(REPORT_DIRECTORY);
            if (!reportDirectory.exists()) {
                reportDirectory.mkdirs();
            }

            File reportFile = new File(reportDirectory, REPORT_FILE);
            resetReportArtifacts(reportDirectory, reportFile);

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFile);
            loadExternalConfig(sparkReporter);
            sparkReporter.config().setDocumentTitle(REPORT_TITLE);
            sparkReporter.config().setReportName(REPORT_NAME);
            sparkReporter.config().setTheme(Theme.STANDARD);

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            extentReports.setSystemInfo("Suite", "QTrip Suite");
            extentReports.setSystemInfo("Module", "QTrip QA V2");
        }

        return extentReports;
    }

    public static synchronized void flushReport() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }

    private static void loadExternalConfig(ExtentSparkReporter sparkReporter) {
        URL configUrl = ExtentReportManager.class.getClassLoader().getResource(REPORT_CONFIG);

        if (configUrl == null) {
            return;
        }

        try {
            sparkReporter.loadXMLConfig(new File(configUrl.toURI()));
        } catch (IOException | URISyntaxException | RuntimeException exception) {
            throw new IllegalStateException("Unable to load Extent configuration from " + REPORT_CONFIG, exception);
        }
    }

    private static void resetReportArtifacts(File reportDirectory, File reportFile) {
        if (reportFile.exists()) {
            reportFile.delete();
        }

        File screenshotDirectory = new File(reportDirectory, "screenshots");
        if (!screenshotDirectory.exists()) {
            return;
        }

        File[] screenshotFiles = screenshotDirectory.listFiles();
        if (screenshotFiles == null) {
            return;
        }

        for (File screenshotFile : screenshotFiles) {
            if (screenshotFile.isFile()) {
                screenshotFile.delete();
            }
        }
    }
}
