package graphql.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

public class GraphQlClientTest {

    private final GraphQlClientTester tester = new GraphQlClientTester();

    interface StringApi {
        String greeting();
    }

    @Test void shouldCallStringQuery() {
        StringApi api = tester.buildClient(StringApi.class);
        tester.returnsData("\"greeting\":\"dummy-greeting\"");

        String greeting = api.greeting();

        then(tester.query()).isEqualTo("greeting");
        then(greeting).isEqualTo("dummy-greeting");
    }


    interface StringListApi {
        List<String> greetings();
    }

    @Test void shouldCallStringListQuery() {
        StringListApi api = tester.buildClient(StringListApi.class);
        tester.returnsData("\"greetings\":[\"a\",\"b\"]");

        List<String> greetings = api.greetings();

        then(tester.query()).isEqualTo("greetings");
        then(greetings).containsExactly("a", "b");
    }


    interface ObjectApi {
        Greeting greeting();
    }

    @AllArgsConstructor @NoArgsConstructor(force = true)
    @Data public static class Greeting {
        String text;
        int code;
    }

    @Test void shouldCallObjectQuery() {
        ObjectApi api = tester.buildClient(ObjectApi.class);
        tester.returnsData("\"greeting\":{\"text\":\"foo\",\"code\":5}");

        Greeting greeting = api.greeting();

        then(tester.query()).isEqualTo("greeting {text code}");
        then(greeting).isEqualTo(new Greeting("foo", 5));
    }


    interface ObjectListApi {
        List<Greeting> greetings();
    }

    @Test void shouldCallObjectListQuery() {
        ObjectListApi api = tester.buildClient(ObjectListApi.class);
        tester.returnsData("\"greetings\":[{\"text\":\"a\",\"code\":1},{\"text\":\"b\",\"code\":2}]");

        List<Greeting> greeting = api.greetings();

        then(tester.query()).isEqualTo("greetings {text code}");
        then(greeting).containsExactly(
            new Greeting("a", 1),
            new Greeting("b", 2));
    }


    interface NestedObjectApi {
        Container container();
    }

    @AllArgsConstructor @NoArgsConstructor(force = true)
    @Data public static class Container {
        Greeting greeting;
        int count;
    }

    @Test void shouldCallNestedObjectQuery() {
        NestedObjectApi api = tester.buildClient(NestedObjectApi.class);
        tester.returnsData("\"container\":{\"greeting\":{\"text\":\"a\",\"code\":1},\"count\":3}");

        Container container = api.container();

        then(tester.query()).isEqualTo("container {greeting{text code} count}");
        then(container).isEqualTo(new Container(
            new Greeting("a", 1), 3));
    }

    interface ParamApi {
        String greeting(String who);
    }

    @Test void shouldCallParamQuery() {
        ParamApi api = tester.buildClient(ParamApi.class);
        tester.returnsData("\"greeting\":\"hi, foo\"");

        String greeting = api.greeting("foo");

        then(tester.query()).isEqualTo("greeting(who: \\\"foo\\\")");
        then(greeting).isEqualTo("hi, foo");
    }

    interface ParamsApi {
        String greeting(String who, int count);
    }

    @Test void shouldCallTwoParamsQuery() {
        ParamsApi api = tester.buildClient(ParamsApi.class);
        tester.returnsData("\"greeting\":\"hi, foo 3\"");

        String greeting = api.greeting("foo", 3);

        then(tester.query()).isEqualTo("greeting(who: \\\"foo\\\", count: 3)");
        then(greeting).isEqualTo("hi, foo 3");
    }

    @Test void shouldFailStringQueryNotFound() {
        StringApi api = tester.buildClient(StringApi.class);
        tester.returns(Response.serverError().type(TEXT_PLAIN_TYPE).entity("failed").build());

        GraphQlClientException thrown = catchThrowableOfType(api::greeting, GraphQlClientException.class);

        then(tester.query()).isEqualTo("greeting");
        then(thrown).hasMessage("expected successful status code but got 500 Internal Server Error:\n" +
            "failed");
    }

    @Test void shouldFailOnQueryError() {
        StringApi api = tester.buildClient(StringApi.class);
        tester.returns(Response.ok("{\"errors\":[{\"message\":\"failed\"}]}").build());

        GraphQlClientException thrown = catchThrowableOfType(api::greeting, GraphQlClientException.class);

        then(tester.query()).isEqualTo("greeting");
        then(thrown).hasMessage("GraphQL error: [{\"message\":\"failed\"}]:\n" +
            "  {\"query\":\"{ greeting }\"}");
    }
}
