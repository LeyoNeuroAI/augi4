package tech.intellibio.augi4.plan;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import tech.intellibio.augi4.config.BaseIT;


public class PlanResourceTest extends BaseIT {

    @Test
    @Sql("/data/planData.sql")
    void getAllPlans_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/plans")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(2))
                    .body("content.get(0).id", Matchers.equalTo(1500));
    }

    @Test
    @Sql("/data/planData.sql")
    void getAllPlans_filtered() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/plans?filter=1501")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(1))
                    .body("content.get(0).id", Matchers.equalTo(1501));
    }

    @Test
    @Sql("/data/planData.sql")
    void getPlan_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/plans/1500")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("document", Matchers.equalTo(59));
    }

    @Test
    void getPlan_notFound() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/plans/2166")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    void createPlan_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/planDTORequest.json"))
                .when()
                    .post("/api/plans")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(1, planRepository.count());
    }

    @Test
    void createPlan_missingField() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/planDTORequest_missingField.json"))
                .when()
                    .post("/api/plans")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                    .body("fieldErrors.get(0).property", Matchers.equalTo("document"))
                    .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    @Sql("/data/planData.sql")
    void updatePlan_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/planDTORequest.json"))
                .when()
                    .put("/api/plans/1500")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals(((long)74), planRepository.findById(((long)1500)).orElseThrow().getDocument());
        assertEquals(2, planRepository.count());
    }

    @Test
    @Sql("/data/planData.sql")
    void deletePlan_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .delete("/api/plans/1500")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, planRepository.count());
    }

}
