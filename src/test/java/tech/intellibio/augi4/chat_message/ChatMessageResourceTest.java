package tech.intellibio.augi4.chat_message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import tech.intellibio.augi4.config.BaseIT;


public class ChatMessageResourceTest extends BaseIT {

    @Test
    @Sql({"/data/productData.sql", "/data/chatSessionData.sql", "/data/chatMessageData.sql"})
    void getAllChatMessages_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/chatMessages")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", Matchers.equalTo(2))
                    .body("get(0).id", Matchers.equalTo(1000));
    }

    @Test
    void getAllChatMessages_unauthorized() {
        RestAssured
                .given()
                    .redirects().follow(false)
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/chatMessages")
                .then()
                    .statusCode(HttpStatus.FOUND.value())
                    .header("Location", Matchers.endsWith("/admin/login?loginRequired=true"));
    }

    @Test
    @Sql({"/data/productData.sql", "/data/chatSessionData.sql", "/data/chatMessageData.sql"})
    void getChatMessage_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/chatMessages/1000")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("tokens", Matchers.equalTo(26));
    }

    @Test
    void getChatMessage_notFound() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/chatMessages/1666")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    @Sql({"/data/productData.sql", "/data/chatSessionData.sql"})
    void createChatMessage_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/chatMessageDTORequest.json"))
                .when()
                    .post("/api/chatMessages")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(1, chatMessageRepository.count());
    }

    @Test
    void createChatMessage_missingField() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/chatMessageDTORequest_missingField.json"))
                .when()
                    .post("/api/chatMessages")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                    .body("fieldErrors.get(0).property", Matchers.equalTo("message"))
                    .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    @Sql({"/data/productData.sql", "/data/chatSessionData.sql", "/data/chatMessageData.sql"})
    void updateChatMessage_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/chatMessageDTORequest.json"))
                .when()
                    .put("/api/chatMessages/1000")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals(11, chatMessageRepository.findById(1000).orElseThrow().getTokens());
        assertEquals(2, chatMessageRepository.count());
    }

    @Test
    @Sql({"/data/productData.sql", "/data/chatSessionData.sql", "/data/chatMessageData.sql"})
    void deleteChatMessage_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .delete("/api/chatMessages/1000")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, chatMessageRepository.count());
    }

}
