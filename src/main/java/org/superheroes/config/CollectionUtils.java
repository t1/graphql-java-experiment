package org.superheroes.config;

import lombok.experimental.UtilityClass;

import javax.persistence.NonUniqueResultException;
import javax.ws.rs.NotFoundException;
import java.util.List;

@UtilityClass
public class CollectionUtils {
    public static <T> T single(List<T> list, String hint) {
        switch (list.size()) {
            case 0:
                throw new NotFoundException(hint);
            case 1:
                return list.get(0);
            default:
                throw new NonUniqueResultException(hint);
        }
    }
}
