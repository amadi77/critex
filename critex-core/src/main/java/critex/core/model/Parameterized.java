package critex.core.model;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Abstract class that provides ParameterizedType support for generic services.
 * This allows automatic extraction of the generic type T at runtime.
 * 
 * @author Ahmad Reza Mokhtari
 */
public abstract class Parameterized<T> {

    private Class<T> clazz;

    @SuppressWarnings("unchecked")
    public Parameterized() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
            if (typeArguments.length > 0 && typeArguments[0] instanceof Class) {
                clazz = (Class<T>) typeArguments[0];
            }
        }
    }

    protected Class<T> getClazz() {
        return clazz;
    }
}