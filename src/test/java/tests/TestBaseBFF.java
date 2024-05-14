package tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class TestBaseBFF {

    @BeforeAll
    static void beforeAll() {
        RestAssured.baseURI = "https://extra-bff.dropstab.com";
        RestAssured.basePath = "/v1.2/market-data";
    }
}
