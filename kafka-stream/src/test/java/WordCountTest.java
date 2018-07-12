import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.DriverTestWrapper;

import java.util.Arrays;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

// group count -> KTable with materialized state store
public class WordCountTest {

  private static final String inputTopic = "word-count-input";
  private static final String outputTopic = "word-count-output";

  private DriverTestWrapper wrapper;

  @Before
  public void setup(){
    // 1. <String,String> Serde
    Properties config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-count-java");
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");

    // 2. topology
    StreamsBuilder builder = new StreamsBuilder();
    KStream<String, String> textLines = builder.stream(inputTopic);
    KTable<String, Long> wordCounts = textLines
        .mapValues(textLine -> textLine.toLowerCase())
        .flatMapValues(textLine -> Arrays.asList(textLine.split("\\W+")))
        .selectKey((key, word) -> word)
        .groupByKey()
        .count(Materialized.as("word_count_store")); // KeyValueStore에 저장된다
//        .aggregate(
//            ()-> 0l,
//            (key, newVal, aggregateValue) -> aggregateValue+1l,
//            Materialized.with(Serdes.String(), Serdes.Long())
//        );
    wordCounts.toStream().to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));
    Topology topology = builder.build();

    wrapper = new DriverTestWrapper(inputTopic, outputTopic,
        topology, config,
        new StringSerializer(), new StringSerializer(),
        new StringDeserializer(), new LongDeserializer());
  }

  @After
  public void tearDown() {
    wrapper.tearDown();
  }

  @Test
  public void test1() {
    wrapper.input("", "alice green");
    wrapper.input("", "bob green");
    wrapper.input("", "alice");

    // output topic verify
    wrapper.readOutputAndAssert("alice", 1L);
    wrapper.readOutputAndAssert("green", 1L);
    wrapper.readOutputAndAssert("bob", 1L);
    wrapper.readOutputAndAssert("green", 2L);
    wrapper.readOutputAndAssert("alice", 2L);
    wrapper.readOutputAndAssertNull();

    KeyValueStore<String, Long> store = wrapper.getKeyValueStore("word_count_store");
    assertEquals(2, (long)store.get("alice"));
  }

}
