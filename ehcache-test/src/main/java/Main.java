import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheEventListenerConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.event.*;

import java.io.File;
import java.io.Serializable;
import java.util.EnumSet;

public class Main {
  public static void main(String[] argv) throws InterruptedException {

    CacheEventListenerConfigurationBuilder cacheEventListenerConfiguration = CacheEventListenerConfigurationBuilder
        .newEventListenerConfiguration(new ListenerObject(), EventType.CREATED, EventType.UPDATED)
        .unordered().asynchronous();

    CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
        .with(CacheManagerBuilder.persistence(new File("/tmp", "myData")))
        .build(true);


    Cache<Long, Value> myCache = cacheManager.createCache("myCache",
        CacheConfigurationBuilder.newCacheConfigurationBuilder(
            Long.class, Value.class,
            ResourcePoolsBuilder
              .heap(2)
              //.offheap(1, MemoryUnit.MB)
              .disk(1, MemoryUnit.MB)
        ).build());

    myCache.getRuntimeConfiguration().registerCacheEventListener(new ListenerObject(), EventOrdering.ORDERED,
        EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED));

    myCache.put(1L, new Value("da one!"));

    for(long i = 0; i < 10*1000; i++) {
      myCache.put(i, new Value("da one! "+i));
    }
    for(long i = 0; i < 10*1000; i++) {
      myCache.put(i, new Value("da two! "+i));
    }


    int count  = 0;
    for(long i = 0; i < 10*1000; i++) {
      if( myCache.containsKey(i))
        count++;
    }
    System.out.println(count);

    cacheManager.close();
    Thread.sleep(1000);
  }
}

class Value implements Serializable {
  String str;

  Value(String str){
    this.str = str;
  }

  @Override
  public String toString() {
    return "Value{" +
        "str='" + str + '\'' +
        '}';
  }
}

class ListenerObject implements CacheEventListener {

  @Override
  public void onEvent(CacheEvent event) {
    System.out.println(event);
  }
}