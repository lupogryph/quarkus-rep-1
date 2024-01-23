package lupogryph.rep.quarkus;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.responseSpecification;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class GreetingResourceTest {
    @Inject
    GreetingResource resource;

    @Test
    void testHelloEndpoint() {
        given()
                .header("Correlation-Id", "123")
                .when().get("/hello")
                .then()
                .statusCode(200)
                .body(is("123"));
    }

}