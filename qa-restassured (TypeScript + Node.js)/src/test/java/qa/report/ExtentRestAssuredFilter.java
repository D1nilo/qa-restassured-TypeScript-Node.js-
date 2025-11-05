package qa.report;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class ExtentRestAssuredFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification req,
                           FilterableResponseSpecification res,
                           FilterContext ctx) {

        long start = System.currentTimeMillis();
        Response response = ctx.next(req, res);
        long elapsed = System.currentTimeMillis() - start;

        ExtentTest t = ExtentTestManager.getTest();
        if (t != null) {
            try {
                // ==== REQUEST ====
                t.info("➡️ Request: " + req.getMethod() + " " + req.getURI());

                // Headers
                if (req.getHeaders() != null && !req.getHeaders().asList().isEmpty()) {
                    t.info(MarkupHelper.createCodeBlock(req.getHeaders().toString(), "json"));
                }

                // Body
                Object bodyObj = req.getBody();
                if (bodyObj != null) {
                    String reqBody = bodyObj.toString();
                    if (!reqBody.isBlank()) {
                        t.info(MarkupHelper.createCodeBlock(reqBody, "json"));
                    }
                }

                // ==== RESPONSE ====
                t.info(String.format("⬅️ Response: %s (%d ms)", response.getStatusLine(), elapsed));

                if (response.getHeaders() != null && !response.getHeaders().asList().isEmpty()) {
                    t.info(MarkupHelper.createCodeBlock(response.getHeaders().toString(), "json"));
                }

                String body;
                try {
                    body = response.getBody().prettyPrint();
                } catch (Exception e) {
                    body = response.getBody().asString();
                }

                if (body != null && !body.isBlank()) {
                    t.info(MarkupHelper.createCodeBlock(body, "json"));
                }

            } catch (Exception e) {
                t.warning("⚠️ Error al registrar logs en reporte: " + e.getMessage());
            }
        }

        return response;
    }
}
