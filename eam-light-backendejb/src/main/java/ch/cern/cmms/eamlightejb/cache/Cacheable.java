package ch.cern.cmms.eamlightejb.cache;

import java.util.concurrent.TimeUnit;

public interface Cacheable {
    void clearCache();

    void setExpiresAfter(long l, TimeUnit timeUnit);
}
