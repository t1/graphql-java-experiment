package graphql.client;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;

import static lombok.AccessLevel.PACKAGE;

@EqualsAndHashCode
@RequiredArgsConstructor(access = PACKAGE)
class FieldInfo {
    private final Field field;

    public TypeInfo getType() {
        return TypeInfo.of(field.getGenericType());
    }

    public String getName() { return field.getName(); }
}
