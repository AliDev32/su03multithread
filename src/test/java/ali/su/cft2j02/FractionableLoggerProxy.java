package ali.su.cft2j02;

import java.util.Objects;

public class FractionableLoggerProxy implements FractionableLoggable {

    private final Fractionable originalObject;
    private long cachedInvokesCount;
    private long mutatorInvokesCount;
    public FractionableLoggerProxy(Fractionable obj) {
        originalObject = obj;
    }

    @Override
    @Cache(1000)
    public double doubleValue() {
        cachedInvokesCount++;
        return originalObject.doubleValue();
    }

    @Override
    @Mutator
    public void setNum(int num) {
        mutatorInvokesCount++;
        originalObject.setNum(num);
    }

    @Override
    @Mutator
    public void setDenom(int denom) {
        mutatorInvokesCount++;
        originalObject.setDenom(denom);
    }

    public long getCachedInvokesCount() {
        return cachedInvokesCount;
    }

    public long getMutatorInvokesCount() {
        return mutatorInvokesCount;
    }

    @Override
    public boolean equals(Object o) {
        return originalObject.equals(o);
    }

    @Override
    public int hashCode() {
        return originalObject.hashCode();
    }
}
