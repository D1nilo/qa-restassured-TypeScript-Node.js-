package qa.report;

import com.aventstack.extentreports.ExtentTest;

public class ExtentTestManager {
    private static final ThreadLocal<ExtentTest> TEST = new ThreadLocal<>();

    public static ExtentTest startTest(String name, String description) {
        ExtentTest t = ExtentManager.getInstance().createTest(name, description);
        TEST.set(t);
        return t;
    }

    public static ExtentTest getTest() {
        return TEST.get();
    }

    public static void end() {
        TEST.remove();
    }
}
