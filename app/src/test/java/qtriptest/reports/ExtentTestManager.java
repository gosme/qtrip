package qtriptest.reports;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public final class ExtentTestManager {
    private static final ThreadLocal<ExtentTest> EXTENT_TEST = new ThreadLocal<ExtentTest>();

    private ExtentTestManager() {
    }

    public static synchronized void createTest(ITestResult result) {
        ExtentReports extentReports = ExtentReportManager.getInstance();
        String testName = result.getMethod().getMethodName();
        Object[] parameters = result.getParameters();

        if (parameters != null && parameters.length > 0) {
            String parameterSummary = Arrays.stream(parameters)
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            testName = testName + " [" + parameterSummary + "]";
        }

        ExtentTest extentTest = extentReports.createTest(testName);
        extentTest.assignCategory(result.getMethod().getGroups());
        EXTENT_TEST.set(extentTest);
    }

    public static ExtentTest getTest() {
        return EXTENT_TEST.get();
    }

    public static void unload() {
        EXTENT_TEST.remove();
    }
}
