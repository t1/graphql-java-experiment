package graphql.client.internal;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.reflect.Modifier.isStatic;

@EqualsAndHashCode
@RequiredArgsConstructor
public class TypeInfo {
    private final Type type;

    public boolean isCollection() {
        if (!(type instanceof ParameterizedType)) {
            return false;
        }
        ParameterizedType parameterizedType = (ParameterizedType) this.type;
        Class<?> rawType = (Class<?>) parameterizedType.getRawType();
        return Collection.class.isAssignableFrom(rawType) && parameterizedType.getActualTypeArguments().length == 1;
    }

    public TypeInfo itemType() {
        assert isCollection();
        return new TypeInfo(((ParameterizedType) type).getActualTypeArguments()[0]);
    }

    public Stream<FieldInfo> fields() {
        return Stream.of(((Class<?>) type).getDeclaredFields())
            .filter(field -> !isStatic(field.getModifiers()))
            .map(FieldInfo::new);
    }

    public boolean isScalar() {
        return SCALAR_TYPES.contains(type);
    }

    private static final List<Type> SCALAR_TYPES = List.of(
        String.class, Integer.class, int.class
        // TODO other scalar types
    );

    public Type getNativeType() { return type; }
}
