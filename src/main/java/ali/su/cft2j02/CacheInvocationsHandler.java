package ali.su.cft2j02;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CacheInvocationsHandler implements InvocationHandler {
    private final Object originalObject;
    private final Map<String, TimestampedObject> cacheWithTtl = new HashMap<>();
    private final BackgroundJob cacheCleaner = new BackgroundJob(this::clearCache);

    public CacheInvocationsHandler(Object originalObject) {
        this.originalObject = originalObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Object retObject;
        Method classMethod = getClassMethod(originalObject, method);

        if (classMethod.isAnnotationPresent(Cache.class)) {
            var ttl = classMethod.getAnnotation(Cache.class).value();
            synchronized (cacheWithTtl) {
                var cacheKey = String.valueOf(method.hashCode()) + String.valueOf(originalObject.hashCode());
                if (cacheWithTtl.containsKey(cacheKey)) {
                    retObject = cacheWithTtl.get(cacheKey).obj;
                    cacheWithTtl.get(cacheKey).refreshTtl(ttl);
                } else {
                    retObject = method.invoke(originalObject, args);
                    cacheWithTtl.put(cacheKey, new TimestampedObject(retObject, ttl));
                }
            }
            return retObject;
        }

//        if (classMethod.isAnnotationPresent(Mutator.class)) {
//            cache.clear();
//        }
        return method.invoke(originalObject, args);
    }

    private Method getClassMethod(Object obj, Method meth) throws NoSuchMethodException {
        return obj.getClass().getMethod(meth.getName(), meth.getParameterTypes());
    }

    public void clearCache() {
        var keysToRemove = new ArrayList<String>();
        cacheWithTtl.forEach((key, val) -> {
            if (val.ttl < System.currentTimeMillis()) keysToRemove.add(key);
        });
        keysToRemove.forEach((key) -> {
            synchronized (cacheWithTtl) {
                cacheWithTtl.remove(key);
            }
        });
    }

    private static class TimestampedObject {
        Object obj;
        long ttl;

        TimestampedObject(Object obj, long milliSeconds) {
            this.obj = obj;
            this.ttl = System.currentTimeMillis() + milliSeconds;
        }

        void refreshTtl(long milliSeconds) {
            this.ttl = System.currentTimeMillis() + milliSeconds;
        }

        @Override
        public String toString() {
            return "CacheEntity{" + "obj=" + obj + ", ttl=" + ttl + '}';
        }
    }
}
