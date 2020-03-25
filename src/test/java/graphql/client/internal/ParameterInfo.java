package graphql.client.internal;

import graphql.client.GraphQlClientException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Parameter;

@RequiredArgsConstructor
public class ParameterInfo {
    private final Parameter parameter;
    @Getter private final Object value;

    public String getName() {
        if (!parameter.isNamePresent())
            throw new GraphQlClientException("compile with -parameters to add the parameter names to the class file");
        return parameter.getName();
    }
}
