import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyTestDriver;
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

public class WordCountTest {

  private static final String inputTopic = "word-count-input";
  private static final String outputTopic = "word-count-output";

  private TopologyTestDriver testDriver;
  private StringDeserializer stringDeserializer = new StringDeserializer();
  private LongDeserializer longDeserializer = new LongDeserializer();
  private ConsumerRecordFactory<String, String> recordFactory = new ConsumerRecordFactory<>(new StringSerializer(), new StringSerializer());

  @Before
  public void setup(){
    StreamsBuilder builder = new StreamsBuilder();

    // Step 1: We create the topic of users keys to colours
    KStream<String, String> textLines = builder.stream(inputTopic);
    KTable<String, Long> wordCounts = textLines
        .mapValues(textLine -> textLine.toLowerCase())
        .flatMapValues(textLine -> Arrays.asList(textLine.split("\\W+")))
        .selectKey((key, word) -> word)
        .groupByKey()
        //.count("Counts");
        .aggregate(
            ()-> 0l,
            (key, newVal, aggregateValue) -> aggregateValue+1l,
            Materialized.with(Serdes.String(), Serdes.Long())
        );

    wordCounts.toStream().to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));

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
    testDriver.close();
  }

  @Test
  public void test1() {
    testDriver.pipeInput(recordFactory.create(inputTopic, "", "alice green"));
    testDriver.pipeInput(recordFactory.create(inputTopic, "", "bob green"));
    testDriver.pipeInput(recordFactory.create(inputTopic, "", "alice"));

    // output topic verify
    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "alice", 1L);
    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "green", 1L);

    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "bob", 1L);
    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "green", 2L);

    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "alice", 2L);
    Assert.assertNull(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer));
  }

}
