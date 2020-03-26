package graphql.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class GraphQlClientTest {

    interface StringApi {
        String greeting();
    }

    @Test void shouldCallStringQuery() {
        StringApi api = buildGraphQlClient(StringApi.class,
            "\"greeting\":\"dummy-greeting\"");

        String greeting = api.greeting();

        then(query()).isEqualTo("greeting");
        then(greeting).isEqualTo("dummy-greeting");
    }


    interface StringListApi {
        List<String> greetings();
    }

    @Test void shouldCallStringListQuery() {
        StringListApi api = buildGraphQlClient(StringListApi.class,
            "\"greetings\":[\"a\",\"b\"]");

        List<String> greetings = api.greetings();

        then(query()).isEqualTo("greetings");
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
        ObjectApi api = buildGraphQlClient(ObjectApi.class,
            "\"greeting\":{\"text\":\"foo\",\"code\":5}");

        Greeting greeting = api.greeting();

        then(query()).isEqualTo("greeting {text code}");
        then(greeting).isEqualTo(new Greeting("foo", 5));
    }


    interface ObjectListApi {
        List<Greeting> greetings();
    }

    @Test void shouldCallObjectListQuery() {
        ObjectListApi api = buildGraphQlClient(ObjectListApi.class,
            "\"greetings\":[{\"text\":\"a\",\"code\":1},{\"text\":\"b\",\"code\":2}]");

        List<Greeting> greeting = api.greetings();

        then(query()).isEqualTo("greetings {text code}");
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
        NestedObjectApi api = buildGraphQlClient(NestedObjectApi.class,
            "\"container\":{\"greeting\":{\"text\":\"a\",\"code\":1},\"count\":3}");

        Container container = api.container();

        then(query()).isEqualTo("container {greeting{text code} count}");
        then(container).isEqualTo(new Container(
            new Greeting("a", 1), 3));
    }


    private <T> T buildGraphQlClient(Class<T> apiClass, String data) {
        return GraphQlClient.newBuilder()
            .endpoint(DUMMY_URI)
            .client(mockClient(data))
            .build(apiClass);
    }

    private Client mockClient(String data) {
        Client mockClient = mock(Client.class);
        WebTarget mockWebTarget = mock(WebTarget.class);

        given(mockClient.target(DUMMY_URI)).willReturn(mockWebTarget);
        given(mockWebTarget.request(APPLICATION_JSON_TYPE)).willReturn(mockInvocationBuilder);
        given(mockInvocationBuilder.post(any()))
            .willReturn(Response.ok("{\"data\":{" + data + "}}").build());

        return mockClient;
    }

    private String query() {
        return stripQueryContainer(captureRequestEntity());
    }

    private String captureRequestEntity() {
        @SuppressWarnings("unchecked") ArgumentCaptor<Entity<String>> captor = ArgumentCaptor.forClass(Entity.class);
        BDDMockito.then(mockInvocationBuilder).should().post(captor.capture());
        return captor.getValue().getEntity();
    }

    private String stripQueryContainer(String response) {
        then(response).startsWith(QUERY_PREFIX).endsWith(QUERY_SUFFIX);
        return response.substring(QUERY_PREFIX.length(), response.length() - QUERY_SUFFIX.length()).trim();
    }


    private final Invocation.Builder mockInvocationBuilder = mock(Invocation.Builder.class);

    private static final URI DUMMY_URI = URI.create("http://dummy-endpoint");
    private static final String QUERY_PREFIX = "{\"query\":\"{";
    private static final String QUERY_SUFFIX = "}\"}";
}
