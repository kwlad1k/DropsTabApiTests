package spec;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.STATUS;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class WidgetsSpec {

    public static ResponseSpecification btcDominationResponseSpec = new ResponseSpecBuilder()
            .log(STATUS)
            .expectBody(matchesJsonSchemaInClasspath("schemas/widgets-schema/btc-domination-schema.json"))
            .build();

    public static ResponseSpecification fearGreedIndexResponseSpec = new ResponseSpecBuilder()
            .log(STATUS)
            .expectBody(matchesJsonSchemaInClasspath("schemas/widgets-schema/fear-greed-index-response-schema.json"))
            .build();
}
