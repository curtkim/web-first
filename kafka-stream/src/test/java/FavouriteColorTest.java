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
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.streams.test.OutputVerifier;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Properties;

public class FavouriteColorTest {

  private static final String inputTopic = "favourite-colour-input";
  private static final String outputTopic = "favourite-colour-output";
  private static final String viaTopic = "user-keys-and-colours";

  private TopologyTestDriver testDriver;
  private StringDeserializer stringDeserializer = new StringDeserializer();
  private LongDeserializer longDeserializer = new LongDeserializer();
  private ConsumerRecordFactory<String, String> recordFactory = new ConsumerRecordFactory<>(new StringSerializer(), new StringSerializer());

  @Before
  public void setup(){
    StreamsBuilder builder = new StreamsBuilder();

    // Step 1: We create the topic of users keys to colours
    KStream<String, String> textLines = builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String()));

    KStream<String, String> usersAndColours = textLines
        .filter((key, value) -> value.contains(","))
        .selectKey((key, value) -> value.split(",")[0].toLowerCase()) // key선택
        .mapValues(value -> value.split(",")[1].toLowerCase()) // value선택
        .filter((user, colour) -> Arrays.asList("green", "blue", "red").contains(colour));

    usersAndColours.to(viaTopic);

    // step 2 - we read that topic as a KTable so that updates are read correctly
    KTable<String, String> usersAndColoursTable = builder.table(viaTopic, Materialized.as("user_color_map"));

    // step 3 - we count the occurences of colours
    KTable<String, Long> favouriteColours = usersAndColoursTable
        // 5 - we group by colour within the KTable
        .groupBy((user, colour) -> new KeyValue<>(colour, colour))
        .count(Materialized.with(Serdes.String(), Serdes.Long())); // TODO state store 이름을 지정할 수 없다

    // 6 - we output the results to a Kafka Topic - don't forget the serializers
    favouriteColours.toStream().to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));

    Properties config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "favourite-colour-java");
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");

    testDriver = new TopologyTestDriver(builder.build(), config);
  }

  @After
  public void tearDown() {
    System.out.println(testDriver.getAllStateStores());
    testDriver.close();
  }

  @Test
  public void test1() {
    testDriver.pipeInput(recordFactory.create(inputTopic, "", "alice,green"));
    testDriver.pipeInput(recordFactory.create(inputTopic, "", "bob,red"));
    testDriver.pipeInput(recordFactory.create(inputTopic, "", "alice,red"));
    testDriver.pipeInput(recordFactory.create(inputTopic, "", "bob,blue"));

    // via topic verify
    OutputVerifier.compareKeyValue(testDriver.readOutput(viaTopic, stringDeserializer, stringDeserializer), "alice", "green");
    OutputVerifier.compareKeyValue(testDriver.readOutput(viaTopic, stringDeserializer, stringDeserializer), "bob", "red");
    OutputVerifier.compareKeyValue(testDriver.readOutput(viaTopic, stringDeserializer, stringDeserializer), "alice", "red");
    OutputVerifier.compareKeyValue(testDriver.readOutput(viaTopic, stringDeserializer, stringDeserializer), "bob", "blue");
    Assert.assertNull(testDriver.readOutput(viaTopic, stringDeserializer, stringDeserializer));

    // output topic verify
    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "green", 1L);
    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "red", 1L);

    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "green", 0L);
    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "red", 2L);

    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "red", 1L);
    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "blue", 1L);
    Assert.assertNull(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer));
  }

}
