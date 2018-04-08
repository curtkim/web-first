import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.streams.test.OutputVerifier;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

/**
 * Created by curtkim on 2018. 4. 8..
 */
public class RegionTest {
  private static final String inputTopic = "input-topic";
  private static final String outputTopic = "output-topic";

  private TopologyTestDriver testDriver;

  private StringDeserializer stringDeserializer = new StringDeserializer();
  private LongDeserializer longDeserializer = new LongDeserializer();
  private ConsumerRecordFactory<String, String> recordFactory = new ConsumerRecordFactory<>(new StringSerializer(), new StringSerializer());

  @Before
  public void setup() {
    final Serde<String> stringSerde = Serdes.String();
    final Serde<Long> longSerde = Serdes.Long();

    StreamsBuilder builder = new StreamsBuilder();
    KTable<String, String> userRegionsTable = builder.table(inputTopic);

    KTable<String, Long> usersPerRegionTable = userRegionsTable
        .groupBy((userId, region) -> KeyValue.pair(region, region))
        .count();

    usersPerRegionTable.toStream().to(outputTopic, Produced.with(stringSerde, longSerde));

    // setup test driver
    Properties config = new Properties();
    config.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "maxAggregation");
    config.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
    config.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    config.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    testDriver = new TopologyTestDriver(builder.build(), config);

    // pre-populate store
    //store = testDriver.getKeyValueStore("aggStore");
    //store.put("a", 21L);
  }

  @After
  public void tearDown() {
    testDriver.close();
  }

  @Test
  public void test1() {
    testDriver.pipeInput(recordFactory.create(inputTopic, "alice", "asia"));
    testDriver.pipeInput(recordFactory.create(inputTopic, "bob", "europe"));
    testDriver.pipeInput(recordFactory.create(inputTopic, "alice", "europe"));
    testDriver.pipeInput(recordFactory.create(inputTopic, "bob", "asia"));

    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "asia", 1L);
    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "europe", 1L);

    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "asia", 0L);
    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "europe", 2L);

    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "europe", 1L);
    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "asia", 1L);
    Assert.assertNull(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer));
  }

}
