package spec;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.STATUS;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class AuthUserSpec {

    public static ResponseSpecification loginUserResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .log(STATUS)
            .expectBody(matchesJsonSchemaInClasspath("schemas/auth-schema/login-user-schema.json"))
            .build();

    public static ResponseSpecification negativeLoginUserResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(401)
            .log(STATUS)
            .expectBody(matchesJsonSchemaInClasspath("schemas/auth-schema/negative-login-user-schema.json"))
            .build();

    public static ResponseSpecification currentResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .log(STATUS)
            .expectBody(matchesJsonSchemaInClasspath("schemas/auth-schema/current-schema.json"))
            .build();
}
