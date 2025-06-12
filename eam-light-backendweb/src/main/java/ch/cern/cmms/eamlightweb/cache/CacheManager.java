package ch.cern.cmms.eamlightweb.cache;

import ch.cern.cmms.eamlightejb.cache.Cacheable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class CacheManager {

    @Inject
    private Instance<Cacheable> cacheables; // TODO only external service :(

    public void clearAllCaches() {
        cacheables.forEach(Cacheable::clearCache);
    }

    public void setAllExpiresAfter(long l, TimeUnit timeUnit) {
        cacheables.forEach(cacheable -> cacheable.setExpiresAfter(l, timeUnit));
    }

    public List<Cacheable> getAllCacheables() {
        List<Cacheable> list = new ArrayList<>();
        cacheables.forEach(list::add);
        return list;
    }
}