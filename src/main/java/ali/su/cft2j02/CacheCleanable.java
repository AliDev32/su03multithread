package ali.su.cft2j02;

public interface CacheCleanable {
    BackgroundJob getCacheCleaner();
    TimeToLive getTimeToLive();
}
