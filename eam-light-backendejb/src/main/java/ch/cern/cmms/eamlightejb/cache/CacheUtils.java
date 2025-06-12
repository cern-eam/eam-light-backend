package ch.cern.cmms.eamlightejb.cache;

import com.github.benmanes.caffeine.cache.*;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class CacheUtils {

    private static final long DEFAULT_CACHE_EXPIRATION_MINUTES = 10080L; // 7 days

    private CacheUtils() {
        // Utility class, prevent instantiation
    }

    public static <K, V> Cache<K, V> buildDefaultCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(DEFAULT_CACHE_EXPIRATION_MINUTES, TimeUnit.MINUTES)
                .build();
    }

    public static <K, V> LoadingCache<K, V> buildLoadingCache(CacheLoader<? super K, V> loader) {
        return Caffeine.newBuilder()
                .expireAfterWrite(DEFAULT_CACHE_EXPIRATION_MINUTES, TimeUnit.MINUTES)
                .build(loader);
    }

    public static <K, V> void updateCacheTimeout(Cache<K, V> cache, long timeout, TimeUnit unit) {
        Optional<Policy.Expiration<K, V>> policy = cache.policy().expireAfterWrite();
        if (policy.isPresent()) {
            policy.get().setExpiresAfter(timeout, unit);
        } else {
            throw new UnsupportedOperationException("This cache does not support dynamic expiration.");
        }
    }
}
