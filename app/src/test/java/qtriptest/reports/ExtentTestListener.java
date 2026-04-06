package qtriptest.reports;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.Status;

import qtriptest.DriverSingleton;

public class ExtentTestListener implements ITestListener {
    @Override
    public void onStart(ITestContext context) {
        ExtentReportManager.getInstance();
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTestManager.createTest(result);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest().log(Status.PASS, "Test passed");
            attachFinalSnapshot(result);
            ExtentTestManager.unload();
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest().log(Status.FAIL, result.getThrowable());
            attachFailureSnapshot(result);
            attachFinalSnapshot(result);
            ExtentTestManager.unload();
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (ExtentTestManager.getTest() != null) {
            if (result.getThrowable() != null) {
                ExtentTestManager.getTest().log(Status.SKIP, result.getThrowable());
            } else {
                ExtentTestManager.getTest().log(Status.SKIP, "Test skipped");
            }
            attachFinalSnapshot(result);
            ExtentTestManager.unload();
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportManager.flushReport();
    }

    private void attachFailureSnapshot(ITestResult result) {
        attachScreenshot(result, "failure", "Failure Snapshot");
    }

    private void attachFinalSnapshot(ITestResult result) {
        attachScreenshot(result, "final", "Final Snapshot");
    }

    private void attachScreenshot(ITestResult result, String suffix, String title) {
        WebDriver driver = DriverSingleton.peekDriver();
        if (driver == null || ExtentTestManager.getTest() == null) {
            return;
        }

        String screenshotPath = ScreenshotUtils.captureScreenshot(driver, buildTestName(result), suffix);
        if (screenshotPath == null) {
            return;
        }

        try {
            ExtentTestManager.getTest().addScreenCaptureFromPath(screenshotPath, title);
        } catch (Exception exception) {
            ExtentTestManager.getTest().log(Status.WARNING,
                    "Unable to attach screenshot: " + exception.getMessage());
        }
    }

    private String buildTestName(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        Object[] parameters = result.getParameters();

        if (parameters == null || parameters.length == 0) {
            return methodName;
        }

        StringBuilder builder = new StringBuilder(methodName);
        for (Object parameter : parameters) {
            builder.append('_').append(String.valueOf(parameter));
        }

        return builder.toString();
    }
}
