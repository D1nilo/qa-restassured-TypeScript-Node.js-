package qa.report;

import com.aventstack.extentreports.Status;
import org.junit.jupiter.api.extension.*;

public class ExtentReportExtension implements BeforeAllCallback, AfterAllCallback,
        BeforeEachCallback, TestWatcher {

    @Override
    public void beforeAll(ExtensionContext context) {
        ExtentManager.getInstance(); // init
    }

    @Override
    public void afterAll(ExtensionContext context) {
        ExtentManager.getInstance().flush(); // write HTML
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        ExtentTestManager.startTest(context.getDisplayName(), context.getUniqueId());
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        ExtentTestManager.getTest().log(Status.PASS, "Test passed");
        ExtentTestManager.end();
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        ExtentTestManager.getTest().log(Status.SKIP, "Test skipped: " + cause);
        ExtentTestManager.end();
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        ExtentTestManager.getTest().fail(cause);
        ExtentTestManager.end();
    }
}
