package it;

import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class GraphQlClient {

    public static <T> T graphQlClient(Class<T> apiClass) {
        return apiClass.cast(Proxy.newProxyInstance(apiClass.getClassLoader(), new Class<?>[]{apiClass}, GraphQlClient::invoke));
    }

    public static Object invoke(Object proxy, Method method, Object[] args) {
        GraphQlApi api = RestClientBuilder.newBuilder()
            .baseUri(URI.create("http://localhost:8080/graphql-java-experiment"))
            .build(GraphQlApi.class);

        JsonObjectBuilder request = Json.createObjectBuilder();
        request.add("query", "{ " + query(method, args) + " { " + fields(method.getReturnType()) + " }}");

        String responseString = api.post(request.build().toString());

        JsonObject responseJson = Json.createReader(new StringReader(responseString)).readObject();
        if (responseJson.isNull("data")) {
            throw new RuntimeException("GraphQL error: " + responseJson.getJsonArray("errors"));
        }
        JsonObject data = responseJson.getJsonObject("data");
        return JSONB.fromJson(data.getJsonObject(method.getName()).toString(), method.getReturnType());
    }

    private static String fields(Class<?> type) {
        // TODO this would have to be smarter
        return Stream.of(type.getDeclaredFields()).map(Field::getName).collect(Collectors.joining(" "));
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

    @Path("/graphql")
    public interface GraphQlApi {
        @Consumes(APPLICATION_JSON)
        @Produces(APPLICATION_JSON)
        @POST String post(String request);
    }

    private static final Jsonb JSONB = JsonbBuilder.create();
}
