package ch.cern.cmms.eamlightejb.cache;

import ch.cern.eam.wshub.core.tools.CacheKey;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.Getter;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
@Getter
public class ExternalCache implements Cacheable {

    private final Map<CacheKey, Cache<String, Object>> cacheMap = buildCacheMap();

    @Override
    public void clearCache() {
        cacheMap.values()
                .forEach(Cache::invalidateAll);
    }

    @Override
    public void setExpiresAfter(long l, TimeUnit timeUnit) {
        cacheMap.values()
                .forEach(cache -> CacheUtils.updateCacheTimeout(cache, l, timeUnit));
    }

    private Map<CacheKey, Cache<String, Object>> buildCacheMap() {
        return Arrays.stream(CacheKey.values())
                .collect(Collectors.toMap(
                        Function.identity(),
                        key -> CacheUtils.buildDefaultCache()
                ));
    }
}
