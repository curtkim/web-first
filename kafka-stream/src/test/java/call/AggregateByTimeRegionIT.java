package call;

import static org.junit.Assert.assertEquals;

import io.confluent.examples.streams.IntegrationTestUtils;
import io.confluent.examples.streams.kafka.EmbeddedSingleNodeKafkaCluster;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import util.JavaDeserializer;
import util.JavaSerializer;

public class AggregateByTimeRegionIT {

  @ClassRule
  public static final EmbeddedSingleNodeKafkaCluster CLUSTER = new EmbeddedSingleNodeKafkaCluster();

  @BeforeClass
  public static void startKafkaCluster() throws Exception {
    CLUSTER.createTopic(inputTopic);
    CLUSTER.createTopic(outputHcodeTopic);
    CLUSTER.createTopic(outputGridTopic);
    CLUSTER.createTopic(viaHcodeTopic);
    CLUSTER.createTopic(viaGridTopic);
  }

  private static final String inputTopic = "call";
  private static final String outputHcodeTopic = "call_hcode";
  private static final String outputGridTopic = "call_grid";
  private static final String viaHcodeTopic = "call_hcode_via";
  private static final String viaGridTopic = "call_grid_via";

  private StringDeserializer stringDeserializer = new StringDeserializer();
  private KafkaStreams kafkaStreams1;
  private KafkaStreams kafkaStreams2;


  private void setup2(){
    Properties config2 = new Properties();
    config2.put(StreamsConfig.APPLICATION_ID_CONFIG, "application1");
    config2.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, CLUSTER.bootstrapServers());
    config2.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    config2.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    // The commit interval for flushing records to state stores and downstream must be lower than
    // this integration test's timeout (30 secs) to ensure we observe the expected processing results.
    config2.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100);
    config2.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    // Use a temporary directory for storing state, which will be automatically removed after the test.
    //streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, TestUtils.tempDirectory().getAbsolutePath());

    Serde<CallSummary> callSummarySerge = new Serde<CallSummary>(){
      @Override
      public void configure(Map<String, ?> configs, boolean isKey) {
      }

      @Override
      public void close() {
      }

      @Override
      public Serializer<CallSummary> serializer() {
        return new JavaSerializer();
      }

      @Override
      public Deserializer<CallSummary> deserializer() {
        return new JavaDeserializer();
      }
    };


    StreamsBuilder builder = new StreamsBuilder();
    builder.stream(viaHcodeTopic, Consumed.with(Serdes.String(), callSummarySerge))
        .groupByKey()
        .reduce(CallSummary::reduce, Materialized.with(Serdes.String(), callSummarySerge))
        .toStream()
        .to(outputHcodeTopic, Produced.with(Serdes.String(), callSummarySerge));
    Topology topology2 = builder.build();
    System.out.println(topology2.describe());

    kafkaStreams2 = new KafkaStreams(topology2, config2);
    kafkaStreams2.cleanUp();
    kafkaStreams2.start();
  }

  void setup1(){
    Properties config1 = new Properties();
    config1.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream2");
    config1.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, CLUSTER.bootstrapServers());
    config1.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    config1.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    // The commit interval for flushing records to state stores and downstream must be lower than
    // this integration test's timeout (30 secs) to ensure we observe the expected processing results.
    config1.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100);
    config1.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    // Use a temporary directory for storing state, which will be automatically removed after the test.
    //streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, TestUtils.tempDirectory().getAbsolutePath());

    Topology topology1 = new Topology();
    Function<Call, String> toHcode = (call)-> "11";
    Function<Call, String> toGrid = (call)-> "223/353";
    StreamPartitioner partitioner = (key, value, partition)-> 0;

    topology1.addSource("SOURCE", new LongDeserializer(), new JavaDeserializer(), inputTopic)
        .addProcessor("PROCESS_HCODE", () -> new CallSummaryProcessor(toHcode), "SOURCE")
        //.addProcessor("PROCESS_GRID", () -> new CallSummaryProcessor(toGrid), "SOURCE")
        .addSink("SINK_HCODE", viaHcodeTopic, new StringSerializer(), new JavaSerializer(), partitioner, "PROCESS_HCODE");  // partition을 하나로 모은다
    //.addSink("SINK_GRID", viaGridTopic, new StringSerializer(), new JavaSerializer(), partitioner, "PROCESS_GRID");
    System.out.println(topology1.describe());

    kafkaStreams1 = new KafkaStreams(topology1, config1);
    kafkaStreams1.cleanUp();
    kafkaStreams1.start();
  }

  /*
  void setup1(){
    Function<Call, String> toHcode = (call)-> "11";

    StreamsBuilder builder = new StreamsBuilder();
    KStream<Long, Call> stream = builder.stream(inputTopic);
    TimeWindowedKStream a = stream
            .groupBy((k, v)-> toHcode.apply(v))
            .windowedBy(TimeWindows.of(TimeUnit.MINUTES.toMillis(1)))
  }
  */

  @Before
  public void setup(){
    setup1();
    setup2();
  }

  @After
  public void tearDown() {
    kafkaStreams1.close();
    kafkaStreams2.close();
  }


  @Test
  public void test1() throws ExecutionException, InterruptedException {
    //
    // Step 2: Publish user-region information.
    //
    Properties userRegionsProducerConfig = new Properties();
    userRegionsProducerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, CLUSTER.bootstrapServers());
    userRegionsProducerConfig.put(ProducerConfig.ACKS_CONFIG, "all");
    userRegionsProducerConfig.put(ProducerConfig.RETRIES_CONFIG, 0);
    userRegionsProducerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
    userRegionsProducerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JavaSerializer.class);

    List<ProducerRecord<Long, Call>> producerRecords = new ArrayList<>();
    for(long i = 0;i < 121; i++)
      producerRecords.add( new ProducerRecord<>(inputTopic, 0, i*1000, i, new Call(i, 127, 37, i)));

    IntegrationTestUtils.produceRecordsSynchronously(producerRecords, userRegionsProducerConfig);
    Thread.sleep(10*1000);

    //
    // Step 3: Verify the application's output data.
    //
    Properties consumerConfig = new Properties();
    consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, CLUSTER.bootstrapServers());
    consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer");
    consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JavaDeserializer.class);

    List<KeyValue<String, CallSummary>> results = IntegrationTestUtils.waitUntilMinKeyValueRecordsReceived(consumerConfig, viaHcodeTopic, 3);
    assertEquals( "197001010900:11", results.get(0).key);
    assertEquals( 1l, results.get(0).value.count);
    assertEquals( "197001010901:11", results.get(1).key);
    assertEquals( 59l, results.get(1).value.count);
    assertEquals( "197001010902:11", results.get(2).key);
    assertEquals( 60l, results.get(2).value.count);
  }

  /*
  @Test
  public void test2() {
    ConsumerRecordFactory<String, CallSummary> recordFactory = new ConsumerRecordFactory<>(new StringSerializer(), new JavaSerializer());
    testDriver.pipeInput(recordFactory.create(viaHcodeTopic, "197001010900:11", new CallSummary(2, 200)));
    testDriver.pipeInput(recordFactory.create(viaHcodeTopic, "197001010900:11", new CallSummary(3, 300)));

    // output topic verify
    ProducerRecord rec = testDriver.readOutput(outputHcodeTopic, stringDeserializer, new JavaDeserializer());
    OutputVerifier.compareKeyValue(rec, "197001010900:11", new CallSummary(2, 200));
    rec = testDriver.readOutput(outputHcodeTopic, stringDeserializer, new JavaDeserializer());
    OutputVerifier.compareKeyValue(rec, "197001010900:11", new CallSummary(5, 500));
    rec = testDriver.readOutput(outputHcodeTopic, stringDeserializer, new JavaDeserializer());
    Assert.assertNull(rec);
  }
  */

}


