package graphql.client.internal;

import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.graphql.Query;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public class MethodInfo {
    public static MethodInfo of(Method method, Object... args) { return new MethodInfo(method, args); }

    private final Method method;
    private final Object[] parameterValues;

    public String getName() {
        if (method.isAnnotationPresent(Query.class)) {
            Query query = method.getAnnotation(Query.class);
            if (!query.value().isEmpty()) {
                return query.value();
            }
        }
        return method.getName();
    }

    public int getParameterCount() { return method.getParameterCount(); }

    public TypeInfo getReturnType() { return new TypeInfo(method.getGenericReturnType()); }

    public List<ParameterInfo> getParameters() {
        Parameter[] parameters = method.getParameters();
        assert parameters.length == parameterValues.length;
        List<ParameterInfo> list = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            list.add(new ParameterInfo(parameters[i], parameterValues[i]));
        }
        return list;
    }
}
