package tech.intellibio.augi4.feedback;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import tech.intellibio.augi4.config.BaseIT;


public class FeedbackResourceTest extends BaseIT {

    @Test
    @Sql("/data/feedbackData.sql")
    void getAllFeedbacks_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/feedbacks")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(2))
                    .body("content.get(0).id", Matchers.equalTo(1400));
    }

    @Test
    @Sql("/data/feedbackData.sql")
    void getAllFeedbacks_filtered() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/feedbacks?filter=1401")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(1))
                    .body("content.get(0).id", Matchers.equalTo(1401));
    }

    @Test
    @Sql("/data/feedbackData.sql")
    void getFeedback_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/feedbacks/1400")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("summary", Matchers.equalTo("Vel eros donec ac odio tempor orci."));
    }

    @Test
    void getFeedback_notFound() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/feedbacks/2066")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    void createFeedback_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/feedbackDTORequest.json"))
                .when()
                    .post("/api/feedbacks")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(1, feedbackRepository.count());
    }

    @Test
    void createFeedback_missingField() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/feedbackDTORequest_missingField.json"))
                .when()
                    .post("/api/feedbacks")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                    .body("fieldErrors.get(0).property", Matchers.equalTo("summary"))
                    .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    @Sql("/data/feedbackData.sql")
    void updateFeedback_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/feedbackDTORequest.json"))
                .when()
                    .put("/api/feedbacks/1400")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals("Erat pellentesque adipiscing commodo elit.", feedbackRepository.findById(1400).orElseThrow().getSummary());
        assertEquals(2, feedbackRepository.count());
    }

    @Test
    @Sql("/data/feedbackData.sql")
    void deleteFeedback_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .delete("/api/feedbacks/1400")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, feedbackRepository.count());
    }

}
