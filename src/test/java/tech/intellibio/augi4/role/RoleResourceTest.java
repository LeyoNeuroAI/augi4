package tech.intellibio.augi4.role;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import tech.intellibio.augi4.config.BaseIT;


public class RoleResourceTest extends BaseIT {

    @Test
    void getAllRoles_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/roles")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", Matchers.equalTo(2))
                    .body("get(0).id", Matchers.equalTo(2100));
    }

    @Test
    void getRole_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/roles/2100")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("description", Matchers.equalTo("Consectetuer adipiscing."));
    }

    @Test
    void getRole_notFound() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/roles/2766")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    void createRole_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/roleDTORequest.json"))
                .when()
                    .post("/api/roles")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(3, roleRepository.count());
    }

    @Test
    void createRole_missingField() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/roleDTORequest_missingField.json"))
                .when()
                    .post("/api/roles")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                    .body("fieldErrors.get(0).property", Matchers.equalTo("description"))
                    .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    void updateRole_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/roleDTORequest.json"))
                .when()
                    .put("/api/roles/2100")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals("Sed diam voluptua.", roleRepository.findById(((long)2100)).orElseThrow().getDescription());
        assertEquals(2, roleRepository.count());
    }

}
