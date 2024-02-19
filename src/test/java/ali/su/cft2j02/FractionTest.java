package ali.su.cft2j02;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FractionTest {

    Fraction obj;
    FractionableLoggable loggedObj;
    Fractionable cachedObj;
    FractionableLoggable loggedCachedObj;

    @BeforeEach
    void init() {
        obj = new Fraction(1, 3);
        loggedObj = new FractionableLoggerProxy(obj);
        cachedObj = CacheUtils.cache(loggedObj);
        loggedCachedObj = new FractionableLoggerProxy(cachedObj);
    }
    @Test
    @DisplayName("Проверка кэширования последнего вызова")
    void lastCallIsCached() {
        loggedCachedObj.doubleValue();
        Assertions.assertTrue(loggedObj.getCachedInvokesCount() == loggedCachedObj.getCachedInvokesCount(),
                              "Нарушено условие: при первичном вызове происходит вызов оригинального метода для заполнения кэша");

        loggedCachedObj.doubleValue();
        Assertions.assertTrue(loggedObj.getCachedInvokesCount() < loggedCachedObj.getCachedInvokesCount(),
                              "Нарушено условие: при вторичном вызове оригинальный метод не вызывается, значение возвращается из кэша");

    }
    @Test
    @DisplayName("Проверка кэширования предыдущих вызовов")
    void previousCallIsCached() {
        loggedCachedObj.doubleValue();
        Assertions.assertTrue(loggedObj.getCachedInvokesCount() == loggedCachedObj.getCachedInvokesCount(),
                              "Нарушено условие: при первичном вызове происходит вызов оригинального метода для заполнения кэша");

        loggedCachedObj.setNum(2);
        loggedCachedObj.doubleValue();
        Assertions.assertTrue(loggedObj.getCachedInvokesCount() == loggedCachedObj.getCachedInvokesCount(),
                              "Нарушено условие: при первичном вызове с новым состоянием объекта происходит вызов оригинального метода для заполнения кэша");


        loggedCachedObj.setNum(1);
        loggedCachedObj.doubleValue();
        Assertions.assertTrue(loggedObj.getCachedInvokesCount() < loggedCachedObj.getCachedInvokesCount(),
                              "Нарушено условие: при повторном вызове с предыдущим состоянием объекта происходит вызов оригинального метода для заполнения кэша");

    }
    @Test
    @DisplayName("Проверка удаления из кэша невостребованных вызовов")
    void cacheClean() {
        loggedCachedObj.doubleValue();

        loggedCachedObj.setNum(2);
        loggedCachedObj.doubleValue();

        loggedCachedObj.setNum(1);
        loggedCachedObj.doubleValue();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        var prevLoggedObjInvokesCnt = loggedObj.getCachedInvokesCount();
        var prevLoggedCacheInvokesCnt = loggedCachedObj.getCachedInvokesCount();

        loggedCachedObj.setNum(2);
        loggedCachedObj.doubleValue();
        Assertions.assertTrue(
                loggedObj.getCachedInvokesCount() == prevLoggedObjInvokesCnt + 1 && loggedCachedObj.getCachedInvokesCount() == prevLoggedCacheInvokesCnt + 1,
                "Нарушено условие: при истечении времени жизни кешированного вызова, он не был удалён из кэша");

        loggedCachedObj.setNum(1);
        loggedCachedObj.doubleValue();
        Assertions.assertTrue(
                loggedObj.getCachedInvokesCount() == prevLoggedObjInvokesCnt + 2 && loggedCachedObj.getCachedInvokesCount() == prevLoggedCacheInvokesCnt + 2,
                "Нарушено условие: при истечении времени жизни кешированного вызова, он не был удалён из кэша");


    }
    @Test
    @DisplayName("Проверка сохранения в кэше востребованного объекта ")
    void oftenObjectRemainsInCache() {
        var methodInvocationsCount = 10;
        for (int i = 0; i < methodInvocationsCount; i++) {
            try {
                loggedCachedObj.doubleValue();
                Thread.sleep(900);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Assertions.assertTrue(
                loggedObj.getCachedInvokesCount() == 1 && loggedCachedObj.getCachedInvokesCount() == methodInvocationsCount,
                "Нарушено условие: востребованный объект удалён из кэша");

    }
}