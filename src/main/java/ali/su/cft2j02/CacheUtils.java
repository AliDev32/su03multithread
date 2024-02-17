package ali.su.cft2j02;

import java.lang.reflect.Proxy;

public class CacheUtils {
    public static <T> T cache(T origObj) {
        if (origObj == null) return null;
        var origObjClassLoader = origObj.getClass().getClassLoader();
        var origObjInterfaces = origObj.getClass().getInterfaces();
        return (T) Proxy.newProxyInstance(origObjClassLoader, origObjInterfaces, new CacheInvocationsHandler(origObj));
    }
}
