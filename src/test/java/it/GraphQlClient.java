package it;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Slf4j
public class GraphQlClient {

    public static <T> T graphQlClient(Class<T> apiClass) {
        return apiClass.cast(Proxy.newProxyInstance(apiClass.getClassLoader(), new Class<?>[]{apiClass}, GraphQlClient::invoke));
    }

    private static Object invoke(Object proxy, Method method, Object[] args) {
        String request = request(method, args);

        log.info("request graphql: {}", request);
        String response = API.post(request);
        log.info("response graphql: {}", response);

        return fromJson(method, request, response);
    }

    private static String request(Method method, Object[] args) {
        JsonObjectBuilder request = Json.createObjectBuilder();
        request.add("query", "{ " + query(method, args)
            + " { " + fields(TypeInfo.of(method.getGenericReturnType())) + " }}");
        return request.build().toString();
    }

    private static String query(Method method, Object[] args) {
        StringBuilder query = new StringBuilder(method.getName());
        if (method.getParameterCount() > 0) {
            query.append("(");
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                if (!parameter.isNamePresent()) {
                    throw new RuntimeException("compile with -parameters to add the parameter names to the class file");
                }
                query.append(parameter.getName()).append(": \"").append(args[i]).append("\"");
            }
            query.append(")");
        }
        return query.toString();
    }

    private static String fields(TypeInfo type) {
        if (type.isCollection()) {
            return fields(type.itemType());
        } else {
            return type.fields()
                .map(GraphQlClient::field)
                .collect(Collectors.joining(" "));
        }
    }

    private static String field(FieldInfo field) {
        TypeInfo type = field.getType();
        if (type.isScalar() || type.isCollection() && type.itemType().isScalar()) {
            return field.getName();
        } else
            return field.getName() + " { " + fields(type) + " }";
    }

    private static Object fromJson(Method method, String request, String response) {
        JsonObject responseJson = Json.createReader(new StringReader(response)).readObject();
        if (responseJson.isNull("data")) {
            throw new RuntimeException("GraphQL error: " + responseJson.getJsonArray("errors") + ":\n  " + request);
        }
        JsonObject data = responseJson.getJsonObject("data");
        JsonValue value = data.get(method.getName());
        return JSONB.fromJson(value.toString(), method.getGenericReturnType());
    }

    @Path("/graphql")
    public interface GraphQlApi {
        @Consumes(APPLICATION_JSON)
        @Produces(APPLICATION_JSON)
        @POST String post(String request);
    }

    private static final GraphQlApi API = RestClientBuilder.newBuilder()
        .baseUri(URI.create("http://localhost:8080/graphql-java-experiment"))
        .build(GraphQlApi.class);

    private static final Jsonb JSONB = JsonbBuilder.create();
}
