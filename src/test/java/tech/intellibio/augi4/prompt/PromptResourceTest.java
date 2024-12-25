package tech.intellibio.augi4.prompt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import tech.intellibio.augi4.config.BaseIT;


public class PromptResourceTest extends BaseIT {

    @Test
    @Sql({"/data/productData.sql", "/data/promptData.sql"})
    void getAllPrompts_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/prompts")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(2))
                    .body("content.get(0).id", Matchers.equalTo(2000));
    }

    @Test
    @Sql({"/data/productData.sql", "/data/promptData.sql"})
    void getAllPrompts_filtered() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/prompts?filter=2001")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(1))
                    .body("content.get(0).id", Matchers.equalTo(2001));
    }

    @Test
    @Sql({"/data/productData.sql", "/data/promptData.sql"})
    void getPrompt_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/prompts/2000")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("chapterNo", Matchers.equalTo(66));
    }

    @Test
    void getPrompt_notFound() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/prompts/2666")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    @Sql("/data/productData.sql")
    void createPrompt_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/promptDTORequest.json"))
                .when()
                    .post("/api/prompts")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(1, promptRepository.count());
    }

    @Test
    void createPrompt_missingField() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/promptDTORequest_missingField.json"))
                .when()
                    .post("/api/prompts")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                    .body("fieldErrors.get(0).property", Matchers.equalTo("chapterNo"))
                    .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    @Sql({"/data/productData.sql", "/data/promptData.sql"})
    void updatePrompt_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/promptDTORequest.json"))
                .when()
                    .put("/api/prompts/2000")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals(81, promptRepository.findById(((long)2000)).orElseThrow().getChapterNo());
        assertEquals(2, promptRepository.count());
    }

    @Test
    @Sql({"/data/productData.sql", "/data/promptData.sql"})
    void deletePrompt_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .delete("/api/prompts/2000")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, promptRepository.count());
    }

}
