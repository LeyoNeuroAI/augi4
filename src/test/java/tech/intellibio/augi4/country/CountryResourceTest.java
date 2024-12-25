package tech.intellibio.augi4.country;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import tech.intellibio.augi4.config.BaseIT;


public class CountryResourceTest extends BaseIT {

    @Test
    @Sql("/data/countryData.sql")
    void getAllCountries_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/countries")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(2))
                    .body("content.get(0).id", Matchers.equalTo(1200));
    }

    @Test
    @Sql("/data/countryData.sql")
    void getAllCountries_filtered() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/countries?filter=1201")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(1))
                    .body("content.get(0).id", Matchers.equalTo(1201));
    }

    @Test
    @Sql("/data/countryData.sql")
    void getCountry_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/countries/1200")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("code", Matchers.equalTo("Lorem ipsum dolor."));
    }

    @Test
    void getCountry_notFound() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/countries/1866")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    void createCountry_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/countryDTORequest.json"))
                .when()
                    .post("/api/countries")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(1, countryRepository.count());
    }

    @Test
    void createCountry_missingField() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/countryDTORequest_missingField.json"))
                .when()
                    .post("/api/countries")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                    .body("fieldErrors.get(0).property", Matchers.equalTo("code"))
                    .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    @Sql("/data/countryData.sql")
    void updateCountry_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/countryDTORequest.json"))
                .when()
                    .put("/api/countries/1200")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals("Et ea rebum.", countryRepository.findById(((long)1200)).orElseThrow().getCode());
        assertEquals(2, countryRepository.count());
    }

    @Test
    @Sql("/data/countryData.sql")
    void deleteCountry_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .delete("/api/countries/1200")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, countryRepository.count());
    }

}
