package qa.config;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

// ⬇️ NUEVO: extensión que inicializa/flush el reporte
import qa.report.ExtentReportExtension;
// ⬇️ NUEVO: filtro que envía request/response al reporte
import qa.report.ExtentRestAssuredFilter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThan;

@ExtendWith(ExtentReportExtension.class) // ⬅️ NUEVO
public abstract class BaseTest {

    protected static String BASE_URL;

    @BeforeAll
    static void setupRestAssured() {
        BASE_URL = getEnv("BASE_URL", "https://petstore.swagger.io/v2");
        RestAssured.baseURI = BASE_URL;

        // Pretty print + timeouts
        RestAssured.config = RestAssuredConfig.config()
                .logConfig(LogConfig.logConfig()
                        .enablePrettyPrinting(true)
                        .defaultStream(System.out))
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 10_000)
                        .setParam("http.socket.timeout", 10_000)
                        .setParam("http.connection-manager.timeout", 10_000));

        // Request/Response specs comunes
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .build();

        RestAssured.responseSpecification = new ResponseSpecBuilder()
                .expectResponseTime(lessThan(10_000L))
                .build();

        // ⬇️ SIEMPRE loguear a consola + enviar al reporte Extent
        RestAssured.filters(
                new ExtentRestAssuredFilter(),            // << NUEVO: logs al reporte
                new RequestLoggingFilter(LogDetail.ALL),  // consola
                new ResponseLoggingFilter(LogDetail.ALL)  // consola
        );

        // ⛔️ No usar si quieres log siempre:
        // RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
    }

    protected RequestSpecification request() {
        return given().spec(RestAssured.requestSpecification);
    }

    private static String getEnv(String key, String defaultValue) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) return sys;
        String env = System.getenv(key);
        return (env == null || env.isBlank()) ? defaultValue : env;
    }
}
