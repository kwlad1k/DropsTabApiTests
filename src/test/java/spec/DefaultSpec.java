package spec;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static helpers.CustomAllureListener.withCustomTemplates;
import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.BODY;
import static io.restassured.filter.log.LogDetail.STATUS;
import static io.restassured.http.ContentType.JSON;
import static spec.PortfolioSpec.testData;

public class DefaultSpec {

    public static RequestSpecification allLoggingSpec = with()
            .filter(withCustomTemplates())
            .log().all()
            .contentType(JSON);

    public static ResponseSpecification allRespLogSpec = new ResponseSpecBuilder()
            .log(STATUS)
            .log(BODY)
            .build();

    public static RequestSpecification defLogSpec = with()
            .filter(withCustomTemplates())
            .log().uri()
            .log().method()
            .contentType(JSON);

    public static RequestSpecification defLogWithAuthSpec = with()
            .filter(withCustomTemplates())
            .header("Authorization", "Bearer " + testData.authorizationToken)
            .log().uri()
            .log().method()
            .contentType(JSON);

    public static ResponseSpecification defRespLogSpec = new ResponseSpecBuilder()
            .log(STATUS)
            .build();
}
