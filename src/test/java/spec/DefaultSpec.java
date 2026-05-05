package spec;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static helpers.CustomAllureListener.withCustomTemplates;
import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.STATUS;
import static io.restassured.http.ContentType.JSON;
import static spec.PortfolioSpec.testData;

public class DefaultSpec {

    public static RequestSpecification defLogSpec = with()
            .filter(withCustomTemplates())
            .log().uri()
            .log().method()
            .contentType(JSON);

    /**
     * Reads testData.authorizationToken at REQUEST time (not at spec build time)
     * so tests that rotate the token (e.g. successfulChangePasswordTest) can update
     * testData.authorizationToken and have subsequent requests pick it up.
     */
    private static final Filter dynamicAuthFilter = (req, resp, ctx) -> {
        req.header("Authorization", "Bearer " + testData.authorizationToken);
        return ctx.next(req, resp);
    };

    public static RequestSpecification defLogWithAuthSpec = with()
            .filter(withCustomTemplates())
            .filter(dynamicAuthFilter)
            .log().uri()
            .log().method()
            .contentType(JSON);

    public static ResponseSpecification defRespLogSpec = new ResponseSpecBuilder()
            .log(STATUS)
            .build();
}
