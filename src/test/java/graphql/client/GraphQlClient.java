package graphql.client;

import graphql.client.internal.FieldInfo;
import graphql.client.internal.MethodInfo;
import graphql.client.internal.ParameterInfo;
import graphql.client.internal.TypeInfo;
import lombok.extern.slf4j.Slf4j;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;

@Slf4j
public class GraphQlClient {

    public static <T> T graphQlClient(Class<T> apiClass, URI endpoint) {
        GraphQlClient graphQlClient = new GraphQlClient(endpoint);
        return apiClass.cast(Proxy.newProxyInstance(apiClass.getClassLoader(), new Class<?>[]{apiClass}, graphQlClient::invoke));
    }

    private final WebTarget target;

    private GraphQlClient(URI endpoint) {
        this.target = REST.target(endpoint);
    }

    private Object invoke(Object proxy, Method method, Object[] args) {
        return invoke(MethodInfo.of(method, args));
    }

    private Object invoke(MethodInfo method) {
        String request = request(method);

        log.info("request graphql: {}", request);
        String response = post(request);
        log.info("response graphql: {}", response);

        return fromJson(method, request, response);
    }

    private String request(MethodInfo method) {
        JsonObjectBuilder request = Json.createObjectBuilder();
        request.add("query", "{ " + query(method)
            + " { " + fields(method.getReturnType()) + " }}");
        return request.build().toString();
    }

    private String query(MethodInfo method) {
        StringBuilder query = new StringBuilder(method.getName());
        if (method.getParameterCount() > 0) {
            query.append("(");
            for (ParameterInfo parameter : method.getParameters()) {
                query.append(parameter.getName()).append(": \"").append(parameter.getValue()).append("\"");
            }
            query.append(")");
        }
        return query.toString();
    }

    private String fields(TypeInfo type) {
        if (type.isCollection()) {
            return fields(type.itemType());
        } else {
            return type.fields()
                .map(this::field)
                .collect(Collectors.joining(" "));
        }
    }

    private String field(FieldInfo field) {
        TypeInfo type = field.getType();
        if (type.isScalar() || type.isCollection() && type.itemType().isScalar()) {
            return field.getName();
        } else {
            return field.getName() + " { " + fields(type) + " }";
        }
    }

    private String post(String request) {
        Response response = target.request(APPLICATION_JSON_TYPE).post(Entity.json(request));
        StatusType status = response.getStatusInfo();
        if (status.getFamily() != SUCCESSFUL)
            throw new GraphQlClientException("expected successful status code but got " +
                status.getStatusCode() + " " + status.getReasonPhrase() + ":\n" +
                response.readEntity(String.class));
        return response.readEntity(String.class);
    }

    private Object fromJson(MethodInfo method, String request, String response) {
        JsonObject responseJson = Json.createReader(new StringReader(response)).readObject();
        if (responseJson.isNull("data")) {
            throw new GraphQlClientException("GraphQL error: " + responseJson.getJsonArray("errors") + ":\n  " + request);
        }
        JsonObject data = responseJson.getJsonObject("data");
        JsonValue value = data.get(method.getName());
        return JSONB.fromJson(value.toString(), method.getReturnType().getNativeType());
    }

    private static final Client REST = ClientBuilder.newClient();

    private static final Jsonb JSONB = JsonbBuilder.create();
}
