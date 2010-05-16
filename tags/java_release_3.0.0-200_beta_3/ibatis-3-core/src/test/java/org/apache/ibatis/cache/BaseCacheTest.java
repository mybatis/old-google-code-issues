package org.apache.ibatis.cache;

import org.apache.ibatis.cache.decorators.*;
import org.apache.ibatis.cache.impl.PerpetualCache;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.*;

public class BaseCacheTest {

  @Test
  public void shouldDemonstrateEqualsAndHashCodeForVariousCacheTypes() {
    PerpetualCache cache = new PerpetualCache("test_cache");
    assertTrue(cache.equals(cache));
    assertTrue(cache.equals(new SynchronizedCache(cache)));
    assertTrue(cache.equals(new SerializedCache(cache)));
    assertTrue(cache.equals(new LoggingCache(cache)));
    assertTrue(cache.equals(new ScheduledCache(cache)));

    assertEquals(cache.hashCode(), new SynchronizedCache(cache).hashCode());
    assertEquals(cache.hashCode(), new SerializedCache(cache).hashCode());
    assertEquals(cache.hashCode(), new LoggingCache(cache).hashCode());
    assertEquals(cache.hashCode(), new ScheduledCache(cache).hashCode());

    Set<Cache> caches = new HashSet<Cache>();
    caches.add(cache);
    caches.add(new SynchronizedCache(cache));
    caches.add(new SerializedCache(cache));
    caches.add(new LoggingCache(cache));
    caches.add(new ScheduledCache(cache));
    assertEquals(1, caches.size());
  }

}
