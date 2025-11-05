package qa.specs;

import qa.config.BaseTest;
import qa.factory.PetFactory;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PetstoreSpec extends BaseTest {
    private static long petId;

    @Test @Order(1)
    void listAvailable() {
        var res = request()
                .queryParam("status", "available")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200);

        int size = res.extract().jsonPath().getList("$").size();
        assertThat(size).isGreaterThan(0);
    }

    @Test @Order(2)
    void createPet() {
        petId = PetFactory.randomId();
        var body = PetFactory.petBody(petId, "Rocky-RA", "available");

        var json = request()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/pet")
                .then()
                .statusCode(200)
                .extract().jsonPath();

        assertThat(json.getLong("id")).isEqualTo(petId);
    }

    @Test @Order(3)
    void updatePet() {
        Assumptions.assumeTrue(petId != 0L);

        var body = PetFactory.petBody(petId, "Rocky-Updated", "sold");

        request()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put("/pet")
                .then()
                .statusCode(200);
    }

    @Test @Order(4)
    void getById() {
        Assumptions.assumeTrue(petId != 0L);

        request()
                .pathParam("id", petId)
                .when()
                .get("/pet/{id}")
                .then()
                .statusCode(anyOf(is(200), is(404)));
    }

    @Test @Order(5)
    void deletePet() throws InterruptedException {
        Assumptions.assumeTrue(petId != 0L);

        // La API de Petstore a veces devuelve 200 (ok) o 404 (si ya no existe).
        request()
                .pathParam("id", petId)
                .when()
                .delete("/pet/{id}")
                .then()
                .statusCode(anyOf(is(200), is(404)));

        // Confirmación robusta: poll hasta 404 (máx ~2s)
        boolean notFound = false;
        for (int i = 0; i < 5; i++) { // 5 intentos con 400ms de espera
            int code = request()
                    .pathParam("id", petId)
                    .when()
                    .get("/pet/{id}")
                    .then()
                    .extract().statusCode();
            if (code == 404) {
                notFound = true;
                break;
            }
            Thread.sleep(400);
        }
        assertThat(notFound)
                .as("El recurso debe quedar no encontrado (404) después de borrar")
                .isTrue();
    }
}
