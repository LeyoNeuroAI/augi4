package tech.intellibio.augi4;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import tech.intellibio.augi4.config.BaseIT;


public class Augi4ApplicationTest extends BaseIT {

    @Test
    void contextLoads() {
    }

    @Test
    void springSessionWorks() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/sessionCreate")
                .then()
                    .statusCode(HttpStatus.OK.value());
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/sessionRead")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(Matchers.equalTo("test"));
        ;
    }

}
