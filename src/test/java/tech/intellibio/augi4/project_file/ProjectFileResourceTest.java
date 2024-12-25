package tech.intellibio.augi4.project_file;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import tech.intellibio.augi4.config.BaseIT;


public class ProjectFileResourceTest extends BaseIT {

    @Test
    @Sql("/data/projectFileData.sql")
    void getAllProjectFiles_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/projectFiles")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(2))
                    .body("content.get(0).id", Matchers.equalTo(1900));
    }

    @Test
    @Sql("/data/projectFileData.sql")
    void getAllProjectFiles_filtered() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/projectFiles?filter=1901")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(1))
                    .body("content.get(0).id", Matchers.equalTo(1901));
    }

    @Test
    @Sql("/data/projectFileData.sql")
    void getProjectFile_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/projectFiles/1900")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("chapterNo", Matchers.equalTo(66));
    }

    @Test
    void getProjectFile_notFound() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/projectFiles/2566")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    void createProjectFile_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/projectFileDTORequest.json"))
                .when()
                    .post("/api/projectFiles")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(1, projectFileRepository.count());
    }

    @Test
    void createProjectFile_missingField() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/projectFileDTORequest_missingField.json"))
                .when()
                    .post("/api/projectFiles")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                    .body("fieldErrors.get(0).property", Matchers.equalTo("chapterNo"))
                    .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    @Sql("/data/projectFileData.sql")
    void updateProjectFile_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/projectFileDTORequest.json"))
                .when()
                    .put("/api/projectFiles/1900")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals(81, projectFileRepository.findById(((long)1900)).orElseThrow().getChapterNo());
        assertEquals(2, projectFileRepository.count());
    }

    @Test
    @Sql("/data/projectFileData.sql")
    void deleteProjectFile_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .delete("/api/projectFiles/1900")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, projectFileRepository.count());
    }

}
