package ali.su.cft2j02;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CacheInvocationsHandler implements InvocationHandler, CacheCleanable {
    private final Object originalObject;
    private final Map<String, TimestampedObject> cacheWithTtl = new HashMap<>();
    private final BackgroundJob cacheCleaner = new BackgroundJob(this::clearCache, 500);
    private final TimeToLive timeToLive;

    public CacheInvocationsHandler(Object originalObject, TimeToLive timeToLive) {
        this.originalObject = originalObject;
        this.timeToLive = timeToLive;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Object retObject;
        Method classMethod = getClassMethod(originalObject, method);

        if (classMethod.isAnnotationPresent(Cache.class)) {
            timeToLive.setTtl(classMethod.getAnnotation(Cache.class).value());
            synchronized (cacheWithTtl) {
                var cacheKey = String.valueOf(method.hashCode()) + String.valueOf(originalObject.hashCode());
                if (cacheWithTtl.containsKey(cacheKey)) {
                    retObject = cacheWithTtl.get(cacheKey).obj;
                    cacheWithTtl.get(cacheKey).refreshTtl(timeToLive);
                } else {
                    retObject = method.invoke(originalObject, args);
                    cacheWithTtl.put(cacheKey, new TimestampedObject(retObject, timeToLive));
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

    private void clearCache() {
        var keysToRemove = new ArrayList<String>();
        cacheWithTtl.forEach((key, val) -> {
            if (val.ttl <= timeToLive.getCurrentTimeMillis()) keysToRemove.add(key);
        });
        keysToRemove.forEach((key) -> {
            synchronized (cacheWithTtl) {
                cacheWithTtl.remove(key);
            }
        });
    }

    @Override
    public BackgroundJob getCacheCleaner() {
        return cacheCleaner;
    }

    @Override
    public TimeToLive getTimeToLive() {
        return timeToLive;
    }

    private static class TimestampedObject {
        Object obj;
        long ttl;

        TimestampedObject(Object obj, TimeToLive timeToLive) {
            this.obj = obj;
            this.ttl = timeToLive.getCurrentTimeMillis() + timeToLive.getTtl();
        }

        void refreshTtl( TimeToLive timeToLive) {
            this.ttl = timeToLive.getCurrentTimeMillis() + timeToLive.getTtl();
        }

        @Override
        public String toString() {
            return "CacheEntity{" + "obj=" + obj + ", ttl=" + ttl + '}';
        }
    }
}
