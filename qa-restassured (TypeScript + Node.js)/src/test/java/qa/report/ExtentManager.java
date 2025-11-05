package qa.report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.io.File;

public class ExtentManager {
    private static ExtentReports extent;

    public static synchronized ExtentReports getInstance() {
        if (extent == null) createInstance();
        return extent;
    }

    private static ExtentReports createInstance() {
        String dir = "target/extent";
        new File(dir).mkdirs();
        String reportPath = dir + "/index.html";

        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setDocumentTitle("API Test Report");
        spark.config().setReportName("QA REST Assured");

        extent = new ExtentReports();
        extent.attachReporter(spark);

        extent.setSystemInfo("Project", "qa-restassured");
        extent.setSystemInfo("Base URL", System.getProperty("BASE_URL",
                System.getenv().getOrDefault("BASE_URL", "https://petstore.swagger.io/v2")));
        extent.setSystemInfo("Java", System.getProperty("java.version"));

        return extent;
    }
}
