import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.processor.*;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.test.ConsumerRecordFactory;

import org.apache.kafka.streams.test.OutputVerifier;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Created by curtkim on 2018. 4. 8..
 */
public class TestDriverTest {
  private TopologyTestDriver testDriver;
  private KeyValueStore<String, Long> store;

  private StringDeserializer stringDeserializer = new StringDeserializer();
  private LongDeserializer longDeserializer = new LongDeserializer();
  private ConsumerRecordFactory<String, Long> recordFactory = new ConsumerRecordFactory<>(new StringSerializer(), new LongSerializer());

  @Before
  public void setup() {
    Topology topology = new Topology();
    topology.addSource("sourceProcessor", "input-topic");
    topology.addProcessor("aggregator", ()->new CustomMaxAggregator(), "sourceProcessor");
    topology.addStateStore(
        Stores.keyValueStoreBuilder(
            Stores.inMemoryKeyValueStore("aggStore"),
            Serdes.String(),
            Serdes.Long()).withLoggingDisabled(), // need to disable logging to allow store pre-populating
        "aggregator");
    topology.addSink("sinkProcessor", "result-topic", "aggregator");

    // setup test driver
    Properties config = new Properties();
    config.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "maxAggregation");
    config.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
    config.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    config.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());
    testDriver = new TopologyTestDriver(topology, config);

    // pre-populate store
    store = testDriver.getKeyValueStore("aggStore");
    store.put("a", 21L);
  }

  @After
  public void tearDown() {
    testDriver.close();
  }

  @Test
  public void shouldFlushStoreForFirstInput() {
    testDriver.pipeInput(recordFactory.create("input-topic", "a", 1L, 9999L));
    OutputVerifier.compareKeyValue(testDriver.readOutput("result-topic", stringDeserializer, longDeserializer), "a", 21L);
    Assert.assertNull(testDriver.readOutput("result-topic", stringDeserializer, longDeserializer));
  }

  @Test
  public void shouldNotUpdateStoreForSmallerValue() {
    testDriver.pipeInput(recordFactory.create("input-topic", "a", 1L, 9999L));
    Assert.assertThat(store.get("a"), equalTo(21L));
    OutputVerifier.compareKeyValue(testDriver.readOutput("result-topic", stringDeserializer, longDeserializer), "a", 21L);
    Assert.assertNull(testDriver.readOutput("result-topic", stringDeserializer, longDeserializer));
  }

  @Test
  public void shouldNotUpdateStoreForLargerValue() {
    testDriver.pipeInput(recordFactory.create("input-topic", "a", 42L, 9999L));
    Assert.assertThat(store.get("a"), equalTo(42L));
    OutputVerifier.compareKeyValue(testDriver.readOutput("result-topic", stringDeserializer, longDeserializer), "a", 42L);
    Assert.assertNull(testDriver.readOutput("result-topic", stringDeserializer, longDeserializer));
  }

  @Test
  public void shouldUpdateStoreForNewKey() {
    testDriver.pipeInput(recordFactory.create("input-topic", "b", 21L, 9999L));
    Assert.assertThat(store.get("b"), equalTo(21L));
    OutputVerifier.compareKeyValue(testDriver.readOutput("result-topic", stringDeserializer, longDeserializer), "a", 21L);
    OutputVerifier.compareKeyValue(testDriver.readOutput("result-topic", stringDeserializer, longDeserializer), "b", 21L);
    Assert.assertNull(testDriver.readOutput("result-topic", stringDeserializer, longDeserializer));
  }

  @Test
  public void shouldPunctuateIfEvenTimeAdvances() {
    testDriver.pipeInput(recordFactory.create("input-topic", "a", 1L, 9999L));
    OutputVerifier.compareKeyValue(testDriver.readOutput("result-topic", stringDeserializer, longDeserializer), "a", 21L);

    testDriver.pipeInput(recordFactory.create("input-topic", "a", 1L, 9999L));
    Assert.assertNull(testDriver.readOutput("result-topic", stringDeserializer, longDeserializer));

    testDriver.pipeInput(recordFactory.create("input-topic", "a", 42L, 10000L));
    OutputVerifier.compareKeyValue(testDriver.readOutput("result-topic", stringDeserializer, longDeserializer), "a", 42L);
    Assert.assertNull(testDriver.readOutput("result-topic", stringDeserializer, longDeserializer));
  }

  @Test
  public void shouldPunctuateIfWallClockTimeAdvances() {
    testDriver.advanceWallClockTime(60000);
    OutputVerifier.compareKeyValue(testDriver.readOutput("result-topic", stringDeserializer, longDeserializer), "a", 21L);
    Assert.assertNull(testDriver.readOutput("result-topic", stringDeserializer, longDeserializer));
  }
}


class CustomMaxAggregator implements Processor<String, Long> {
  ProcessorContext context;
  private KeyValueStore<String, Long> store;

  @SuppressWarnings("unchecked")
  @Override
  public void init(ProcessorContext context) {
    this.context = context;
    //context.schedule(60000, PunctuationType.WALL_CLOCK_TIME, (timestamp)->flushStore());
    context.schedule(10000, PunctuationType.STREAM_TIME, (timeStamp)-> flushStore());
    store = (KeyValueStore<String, Long>) context.getStateStore("aggStore");
  }

  @Override
  public void process(String key, Long value) {
    Long oldValue = store.get(key);
    if (oldValue == null || value > oldValue) {
      store.put(key, value);
    }
  }

  private void flushStore() {
    System.out.println("flushStore");
    KeyValueIterator<String, Long> it = store.all();
    while (it.hasNext()) {
      KeyValue<String, Long> next = it.next();
      System.out.println(next.key + " " + next.value);
      context.forward(next.key, next.value);
    }
  }

  @Override
  public void punctuate(long timestamp) {} // deprecated; not used

  @Override
  public void close() {}
}

