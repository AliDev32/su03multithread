package ali.su.cft2j02;

public class Ttl implements TimeToLive{
    private long currentTimeMillis;
    private long ttl;
    private boolean isConsiderTtl = true;

    public Ttl(long currentTimeMillis, long ttl) {
        this.currentTimeMillis = currentTimeMillis;
        this.ttl = ttl;
    }

    public Ttl(long currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }

    public Ttl() {
        this(System.currentTimeMillis());
    }

    @Override
    public long getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    @Override
    public long getTtl() {
        return isConsiderTtl ? ttl : 0L;
    }

    @Override
    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    @Override
    public void setIsConsiderTtl(boolean isConsiderTtl) {
        this.isConsiderTtl = isConsiderTtl;
    }
}
