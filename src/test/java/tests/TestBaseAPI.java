package tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class TestBaseAPI {

    @BeforeAll
    static void beforeAll() {
        RestAssured.baseURI = "https://api.icodrops.com";
        RestAssured.basePath = "/portfolio";
    }
}
