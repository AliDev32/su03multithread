package ali.su.cft2j02;

public interface TimeToLive {
    long getCurrentTimeMillis();
    long getTtl();
    void setTtl(long ttl);
    void setIsConsiderTtl(boolean isConsiderTtl);
}
