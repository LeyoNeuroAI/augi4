package tech.intellibio.augi4.chat_session;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import tech.intellibio.augi4.config.BaseIT;


public class ChatSessionResourceTest extends BaseIT {

    @Test
    @Sql({"/data/productData.sql", "/data/chatSessionData.sql"})
    void getAllChatSessions_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/chatSessions")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(2))
                    .body("content.get(0).id", Matchers.equalTo(1100));
    }

    @Test
    @Sql({"/data/productData.sql", "/data/chatSessionData.sql"})
    void getAllChatSessions_filtered() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/chatSessions?filter=1101")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(1))
                    .body("content.get(0).id", Matchers.equalTo(1101));
    }

    @Test
    @Sql({"/data/productData.sql", "/data/chatSessionData.sql"})
    void getChatSession_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/chatSessions/1100")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("sessionId", Matchers.equalTo(17));
    }

    @Test
    void getChatSession_notFound() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/chatSessions/1766")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    @Sql("/data/productData.sql")
    void createChatSession_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/chatSessionDTORequest.json"))
                .when()
                    .post("/api/chatSessions")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(1, chatSessionRepository.count());
    }

    @Test
    void createChatSession_missingField() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/chatSessionDTORequest_missingField.json"))
                .when()
                    .post("/api/chatSessions")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                    .body("fieldErrors.get(0).property", Matchers.equalTo("sessionId"))
                    .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    @Sql({"/data/productData.sql", "/data/chatSessionData.sql"})
    void updateChatSession_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/chatSessionDTORequest.json"))
                .when()
                    .put("/api/chatSessions/1100")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals(32, chatSessionRepository.findById(1100).orElseThrow().getSessionId());
        assertEquals(2, chatSessionRepository.count());
    }

    @Test
    @Sql({"/data/productData.sql", "/data/chatSessionData.sql"})
    void deleteChatSession_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .delete("/api/chatSessions/1100")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, chatSessionRepository.count());
    }

}
