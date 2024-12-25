package tech.intellibio.augi4.product;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import tech.intellibio.augi4.config.BaseIT;


public class ProductResourceTest extends BaseIT {

    @Test
    @Sql("/data/productData.sql")
    void getAllProducts_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/products")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(2))
                    .body("content.get(0).id", Matchers.equalTo(1600));
    }

    @Test
    @Sql("/data/productData.sql")
    void getAllProducts_filtered() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/products?filter=1601")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(1))
                    .body("content.get(0).id", Matchers.equalTo(1601));
    }

    @Test
    @Sql("/data/productData.sql")
    void getProduct_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/products/1600")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("description", Matchers.equalTo("Consectetuer adipiscing."));
    }

    @Test
    void getProduct_notFound() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/products/2266")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    void createProduct_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/productDTORequest.json"))
                .when()
                    .post("/api/products")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(1, productRepository.count());
    }

    @Test
    void createProduct_missingField() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/productDTORequest_missingField.json"))
                .when()
                    .post("/api/products")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                    .body("fieldErrors.get(0).property", Matchers.equalTo("name"))
                    .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    @Sql("/data/productData.sql")
    void updateProduct_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/productDTORequest.json"))
                .when()
                    .put("/api/products/1600")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals("Sed diam voluptua.", productRepository.findById(((long)1600)).orElseThrow().getDescription());
        assertEquals(2, productRepository.count());
    }

    @Test
    @Sql("/data/productData.sql")
    void deleteProduct_success() {
        RestAssured
                .given()
                    .sessionId(adminSession())
                    .accept(ContentType.JSON)
                .when()
                    .delete("/api/products/1600")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, productRepository.count());
    }

}
