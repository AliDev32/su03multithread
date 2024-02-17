package ali.su.cft2j02;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CacheInvocationsHandler implements InvocationHandler {
    private final Object originalObject;
    private final Map<Method, Object> cache = new HashMap<>();

    public CacheInvocationsHandler(Object originalObject) {
        this.originalObject = originalObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Object retObject;
        Method classMethod = getClassMethod(originalObject, method);

        if (classMethod.isAnnotationPresent(Cache.class)) {
            if (cache.containsKey(method)) {
                retObject = cache.get(method);
            } else {
                retObject = method.invoke(originalObject, args);
                cache.put(method, retObject);
            }
            return retObject;
        }

        if (classMethod.isAnnotationPresent(Mutator.class)) {
            cache.clear();
        }
        return method.invoke(originalObject, args);
    }

    private Method getClassMethod(Object obj, Method meth) throws NoSuchMethodException {
        return obj.getClass().getMethod(meth.getName(), meth.getParameterTypes());
    }
}
