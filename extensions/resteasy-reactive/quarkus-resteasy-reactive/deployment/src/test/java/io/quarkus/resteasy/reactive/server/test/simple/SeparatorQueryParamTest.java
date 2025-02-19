package io.quarkus.resteasy.reactive.server.test.simple;

import static io.restassured.RestAssured.*;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.hamcrest.Matchers;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.Separator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

public class SeparatorQueryParamTest {

    @RegisterExtension
    static QuarkusUnitTest test = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClass(HelloResource.class));

    @Test
    public void noQueryParams() {
        get("/hello")
                .then()
                .statusCode(200)
                .body(Matchers.equalTo("hello world"))
                .header("x-size", "0");
    }

    @Test
    public void singleQueryParam() {
        get("/hello?name=foo")
                .then()
                .statusCode(200)
                .body(Matchers.equalTo("hello foo"))
                .header("x-size", "1");
    }

    @Test
    public void multipleQueryParams() {
        get("/hello?name=foo,bar&name=one,two,three&name=yolo")
                .then()
                .statusCode(200)
                .body(Matchers.equalTo("hello foo bar one two three yolo"))
                .header("x-size", "6");
    }

    @Path("hello")
    public static class HelloResource {

        @GET
        public RestResponse<String> hello(@RestQuery("name") @Separator(",") List<String> names) {
            int size = names.size();
            String body = "";
            if (names.isEmpty()) {
                body = "hello world";
            } else {
                body = "hello " + String.join(" ", names);
            }
            return RestResponse.ResponseBuilder.ok(body).header("x-size", size).build();
        }

    }
}
