package graphql.client;

import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URI;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class GraphQlClientTester {
    private final Client mockClient = mock(Client.class);
    private final WebTarget mockWebTarget = mock(WebTarget.class);
    private final Invocation.Builder mockInvocationBuilder = mock(Invocation.Builder.class);
    private Response response;

    public <T> T buildClient(Class<T> apiClass) {
        given(mockClient.target(DUMMY_URI)).willReturn(mockWebTarget);
        given(mockWebTarget.request(APPLICATION_JSON_TYPE)).willReturn(mockInvocationBuilder);
        given(mockInvocationBuilder.post(any())).will(i -> response);

        return GraphQlClient.newBuilder()
            .endpoint(DUMMY_URI)
            .client(mockClient)
            .build(apiClass);
    }

    public void returnsData(String data) {
        this.response = Response.ok("{\"data\":{" + data + "}}").build();
    }

    public void returns(Response response) {
        this.response = response;
    }


    public String query() {
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


    private static final URI DUMMY_URI = URI.create("http://dummy-endpoint");
    private static final String QUERY_PREFIX = "{\"query\":\"{";
    private static final String QUERY_SUFFIX = "}\"}";
}
