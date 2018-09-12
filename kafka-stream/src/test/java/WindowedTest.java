import io.confluent.examples.streams.IntegrationTestUtils2;
import io.confluent.examples.streams.kafka.EmbeddedSingleNodeKafkaCluster;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.internals.WindowedDeserializer;
import org.apache.kafka.streams.kstream.internals.WindowedSerializer;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.apache.kafka.test.TestUtils;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class WindowedTest {
  @ClassRule
  public static final EmbeddedSingleNodeKafkaCluster CLUSTER = new EmbeddedSingleNodeKafkaCluster();

  private static final String inputTopic = "input-topic";
  private static final String outputTopic = "output-topic";

  StringSerializer stringSerializer = new StringSerializer();
  StringDeserializer stringDeserializer = new StringDeserializer();
  WindowedSerializer<String> windowedSerializer = new WindowedSerializer<>(stringSerializer);
  WindowedDeserializer<String> windowedDeserializer = new WindowedDeserializer<>(stringDeserializer);
  Serde<Windowed<String>> windowedSerde = Serdes.serdeFrom(windowedSerializer,windowedDeserializer);


  @BeforeClass
  public static void startKafkaCluster() throws Exception {
    CLUSTER.createTopic(inputTopic, 2, 1);
    CLUSTER.createTopic(outputTopic);
  }

  @Test
  public void shouldCountUsersPerRegion() throws Exception {
    // Step 1: Configure and start the processor topology.

    StreamsBuilder builder = new StreamsBuilder();
    StreamPartitioner<Windowed<String>, Integer> partitioner = (Windowed<String> k, Integer v, int numPartitions)-> 0;
    KStream<String, Integer> stream = builder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.Integer()));
    stream.groupByKey()
        .windowedBy(TimeWindows.of(TimeUnit.MINUTES.toMillis(1)))
        .reduce((a,b)-> a+b)
        .toStream()
        .to(outputTopic, Produced.with(windowedSerde, Serdes.Integer(), partitioner));

    KafkaStreams streams = new KafkaStreams(builder.build(), getStreamProperties());
    streams.setUncaughtExceptionHandler((Thread thread, Throwable throwable) -> {
      System.err.println(throwable);
    });
    streams.start();


    // Step 2: Publish
    int MINUTE = 10;
    // 파티션0
    List<ProducerRecord<String, Integer>> records = new ArrayList<>();
    for(long l = 0; l < MINUTE*60; l++)
      records.add(new ProducerRecord<>(inputTopic, 0, l*1000, "A", 1));
    IntegrationTestUtils2.produceRecordsSynchronously(records, getProducerProperties(), new StringSerializer(), new IntegerSerializer());

    // 파티션1
    List<ProducerRecord<String, Integer>> records2 = new ArrayList<>();
    for(long l = 0; l < MINUTE*60; l++)
      records2.add(new ProducerRecord<>(inputTopic, 1, l*1000, "B", 2));
    IntegrationTestUtils2.produceRecordsSynchronously(records2, getProducerProperties(), new StringSerializer(), new IntegerSerializer());

    // 파티션0
    IntegrationTestUtils2.produceRecordsSynchronously(records, getProducerProperties(), new StringSerializer(), new IntegerSerializer());


    // Step 3
    List<ConsumerRecord<Windowed<String>, Integer>> results = IntegrationTestUtils2.waitUntilMinKeyValueRecordsReceived(
        getConsumerProperties(),
        outputTopic,
        10,
        windowedSerde.deserializer(),
        new IntegerDeserializer(),
        10*1000);
    streams.close();

    for(ConsumerRecord<Windowed<String>, Integer> rec : results)
      System.out.println(rec + " " + rec.key().window().start() + "~" + rec.key().window().end());
  }

  private Properties getStreamProperties() {
    Properties streamsConfiguration = new Properties();
    streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
    streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, CLUSTER.bootstrapServers());
    streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName());
    // The commit interval for flushing records to state stores and downstream must be lower than
    // this integration test's timeout (30 secs) to ensure we observe the expected processing results.
    //streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
    //streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 10 * 1024 * 1024L);
    streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10*1000);
    streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 10*1024*1024L);

    streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    // Use a temporary directory for storing state, which will be automatically removed after the test.
    streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, TestUtils.tempDirectory().getAbsolutePath());
    return streamsConfiguration;
  }

  private Properties getConsumerProperties() {
    Properties consumerConfig = new Properties();
    consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, CLUSTER.bootstrapServers());
    consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "user-regions-lambda-integration-test-standard-consumer");
    consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    return consumerConfig;
  }

  private Properties getProducerProperties() {
    Properties userRegionsProducerConfig = new Properties();
    userRegionsProducerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, CLUSTER.bootstrapServers());
    userRegionsProducerConfig.put(ProducerConfig.ACKS_CONFIG, "all");
    userRegionsProducerConfig.put(ProducerConfig.RETRIES_CONFIG, 0);
    return userRegionsProducerConfig;
  }

}

