package graphql.client.internal;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;

import static lombok.AccessLevel.PACKAGE;

@EqualsAndHashCode
@RequiredArgsConstructor(access = PACKAGE)
public class FieldInfo {
    private final Field field;

    public TypeInfo getType() {
        return new TypeInfo(field.getGenericType());
    }

    public String getName() { return field.getName(); }
}
