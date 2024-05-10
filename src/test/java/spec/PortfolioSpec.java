package spec;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import tests.TestDataAPI;

import static io.restassured.filter.log.LogDetail.STATUS;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class PortfolioSpec {
    static TestDataAPI testData = new TestDataAPI();
    public static ResponseSpecification notesResponseSpec = new ResponseSpecBuilder()
            .log(STATUS)
            .expectBody(matchesJsonSchemaInClasspath("schemas/notes-schema.json"))
            .build();

    public static ResponseSpecification txnResponseSpec = new ResponseSpecBuilder()
            .log(STATUS)
            .expectBody(matchesJsonSchemaInClasspath("schemas/transaction-schema.json"))
            .build();
}
