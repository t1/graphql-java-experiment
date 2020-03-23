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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.reflect.Modifier.isStatic;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Slf4j
public class GraphQlClient {

    public static <T> T graphQlClient(Class<T> apiClass) {
        return apiClass.cast(Proxy.newProxyInstance(apiClass.getClassLoader(), new Class<?>[]{apiClass}, GraphQlClient::invoke));
    }

    public static Object invoke(Object proxy, Method method, Object[] args) {
        String request = request(method, args);

        log.info("request graphql: {}", request);
        String response = API.post(request);
        log.info("response graphql: {}", response);

        return fromJson(method, request, response);
    }

    private static String request(Method method, Object[] args) {
        JsonObjectBuilder request = Json.createObjectBuilder();
        request.add("query", "{ " + query(method, args) + " { " + fields(method.getGenericReturnType()) + " }}");
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

    private static String fields(Type type) {
        if (type instanceof Class) {
            return fields(((Class<?>) type));
        } else if (isCollection(type)) {
            return fields(itemType(type));
        } else {
            throw new RuntimeException("unsupported generic type: " + type);
        }
    }

    private static Type itemType(Type type) {
        return ((ParameterizedType) type).getActualTypeArguments()[0];
    }

    private static boolean isCollection(Type type) {
        return type instanceof ParameterizedType
            && Collection.class.isAssignableFrom((Class<?>) ((ParameterizedType) type).getRawType());
    }

    private static String fields(Class<?> clazz) {
        return Stream.of(clazz.getDeclaredFields())
            .filter(field -> !isStatic(field.getModifiers()))
            .map(GraphQlClient::field)
            .collect(Collectors.joining(" "));
    }

    private static String field(Field field) {
        if (isScalar(field.getType()) || isScalarCollection(field.getGenericType())) {
            return field.getName();
        } else
            return field.getName() + " { " + fields(field.getGenericType()) + " }";
    }

    private static boolean isScalar(Type type) {
        return SCALAR_TYPES.contains(type);
    }

    private static boolean isScalarCollection(Type type) {
        return isCollection(type) && isScalar(itemType(type));
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

    private static final List<Type> SCALAR_TYPES = List.of(
        String.class, Integer.class, int.class
        // TODO other scalar types
    );
    private static final Jsonb JSONB = JsonbBuilder.create();
}
