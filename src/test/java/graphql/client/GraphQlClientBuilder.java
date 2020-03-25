package graphql.client;

import graphql.client.internal.MethodInfo;
import lombok.RequiredArgsConstructor;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.lang.reflect.Proxy;
import java.net.URI;

@RequiredArgsConstructor
public class GraphQlClientBuilder<T> {
    private final Class<T> apiClass;
    private URI endpoint;
    private Client client = DEFAULT_CLIENT;
    private Jsonb jsonb = DEFAULT_JSONB;

    public GraphQlClientBuilder<T> endpoint(String endpoint) {
        return endpoint(URI.create(endpoint));
    }

    public GraphQlClientBuilder<T> endpoint(URI endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public GraphQlClientBuilder<T> client(Client client) {
        this.client = client;
        return this;
    }

    public GraphQlClientBuilder<T> jsonb(Jsonb jsonb) {
        this.jsonb = jsonb;
        return this;
    }

    public T build() {
        // TODO default endpoint from MP Config
        WebTarget webTarget = client.target(endpoint);
        GraphQlClient graphQlClient = new GraphQlClient(webTarget, jsonb);
        return apiClass.cast(Proxy.newProxyInstance(apiClass.getClassLoader(), new Class<?>[]{apiClass},
            (proxy, method, args) -> graphQlClient.invoke(MethodInfo.of(method, args))));
    }

    private static final Client DEFAULT_CLIENT = ClientBuilder.newClient();

    private static final Jsonb DEFAULT_JSONB = JsonbBuilder.create();
}
