package tech.intellibio.augi4.document;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import tech.intellibio.augi4.config.BaseIT;


public class DocumentResourceTest extends BaseIT {

    @Test
    @Sql("/data/documentData.sql")
    void getAllDocuments_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/documents")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(2))
                    .body("content.get(0).id", Matchers.equalTo(1300));
    }

    @Test
    @Sql("/data/documentData.sql")
    void getAllDocuments_filtered() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/documents?filter=1301")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(1))
                    .body("content.get(0).id", Matchers.equalTo(1301));
    }

    @Test
    @Sql("/data/documentData.sql")
    void getDocument_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/documents/1300")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("content", Matchers.equalTo("Sagittis aliquam malesuada bibendum arcu vitae elementum curabitur vitae nunc."));
    }

    @Test
    void getDocument_notFound() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/documents/1966")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    void createDocument_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/documentDTORequest.json"))
                .when()
                    .post("/api/documents")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(1, documentRepository.count());
    }

    @Test
    void createDocument_missingField() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/documentDTORequest_missingField.json"))
                .when()
                    .post("/api/documents")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                    .body("fieldErrors.get(0).property", Matchers.equalTo("content"))
                    .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    @Sql("/data/documentData.sql")
    void updateDocument_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/documentDTORequest.json"))
                .when()
                    .put("/api/documents/1300")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals("Dictum fusce ut placerat orci nulla pellentesque dignissim enim.", documentRepository.findById(1300).orElseThrow().getContent());
        assertEquals(2, documentRepository.count());
    }

    @Test
    @Sql("/data/documentData.sql")
    void deleteDocument_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .delete("/api/documents/1300")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, documentRepository.count());
    }

}
