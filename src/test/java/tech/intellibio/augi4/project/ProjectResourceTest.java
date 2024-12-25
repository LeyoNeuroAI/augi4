package tech.intellibio.augi4.project;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import tech.intellibio.augi4.config.BaseIT;


public class ProjectResourceTest extends BaseIT {

    @Test
    @Sql("/data/projectData.sql")
    void getAllProjects_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/projects")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(2))
                    .body("content.get(0).id", Matchers.equalTo(1800));
    }

    @Test
    @Sql("/data/projectData.sql")
    void getAllProjects_filtered() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/projects?filter=1801")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(1))
                    .body("content.get(0).id", Matchers.equalTo(1801));
    }

    @Test
    @Sql("/data/projectData.sql")
    void getProject_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/projects/1800")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("file", Matchers.equalTo("Et ea rebum."));
    }

    @Test
    void getProject_notFound() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/projects/2466")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    void createProject_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/projectDTORequest.json"))
                .when()
                    .post("/api/projects")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(1, projectRepository.count());
    }

    @Test
    void createProject_missingField() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/projectDTORequest_missingField.json"))
                .when()
                    .post("/api/projects")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                    .body("fieldErrors.get(0).property", Matchers.equalTo("goal"))
                    .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    @Sql("/data/projectData.sql")
    void updateProject_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/projectDTORequest.json"))
                .when()
                    .put("/api/projects/1800")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals("Vel illum dolore.", projectRepository.findById(((long)1800)).orElseThrow().getFile());
        assertEquals(2, projectRepository.count());
    }

    @Test
    @Sql("/data/projectData.sql")
    void deleteProject_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .delete("/api/projects/1800")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, projectRepository.count());
    }

}
