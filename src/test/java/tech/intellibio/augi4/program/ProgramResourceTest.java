package tech.intellibio.augi4.program;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import tech.intellibio.augi4.config.BaseIT;


public class ProgramResourceTest extends BaseIT {

    @Test
    @Sql({"/data/productData.sql", "/data/promptData.sql", "/data/countryData.sql", "/data/programData.sql"})
    void getAllPrograms_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/programs")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(2))
                    .body("content.get(0).id", Matchers.equalTo(1700));
    }

    @Test
    @Sql({"/data/productData.sql", "/data/promptData.sql", "/data/countryData.sql", "/data/programData.sql"})
    void getAllPrograms_filtered() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/programs?filter=1701")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(1))
                    .body("content.get(0).id", Matchers.equalTo(1701));
    }

    @Test
    @Sql({"/data/productData.sql", "/data/promptData.sql", "/data/countryData.sql", "/data/programData.sql"})
    void getProgram_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/programs/1700")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("description", Matchers.equalTo("Consectetuer adipiscing."));
    }

    @Test
    void getProgram_notFound() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/programs/2366")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    @Sql({"/data/productData.sql", "/data/promptData.sql", "/data/countryData.sql"})
    void createProgram_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/programDTORequest.json"))
                .when()
                    .post("/api/programs")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(1, programRepository.count());
    }

    @Test
    void createProgram_missingField() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/programDTORequest_missingField.json"))
                .when()
                    .post("/api/programs")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                    .body("fieldErrors.get(0).property", Matchers.equalTo("endDate"))
                    .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    @Sql({"/data/productData.sql", "/data/promptData.sql", "/data/countryData.sql", "/data/programData.sql"})
    void updateProgram_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/programDTORequest.json"))
                .when()
                    .put("/api/programs/1700")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals("Sed diam voluptua.", programRepository.findById(((long)1700)).orElseThrow().getDescription());
        assertEquals(2, programRepository.count());
    }

    @Test
    @Sql({"/data/productData.sql", "/data/promptData.sql", "/data/countryData.sql", "/data/programData.sql"})
    void deleteProgram_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .delete("/api/programs/1700")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, programRepository.count());
    }

}
