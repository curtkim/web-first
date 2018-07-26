package call;

import static org.assertj.core.api.Assertions.assertThat;

import io.confluent.examples.streams.IntegrationTestUtils;
import io.confluent.examples.streams.kafka.EmbeddedSingleNodeKafkaCluster;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
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
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.streams.test.OutputVerifier;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import util.JavaDeserializer;
import util.JavaSerializer;

// dsl로 만든 sub-topology가 있으면 동작을 하지 않는다. TestDriver여서 그런가? 아니면 실환경에서도 그럴것인가?
public class AggregateByTimeRegionIT {

  @ClassRule
  public static final EmbeddedSingleNodeKafkaCluster CLUSTER = new EmbeddedSingleNodeKafkaCluster();

  @BeforeClass
  public static void startKafkaCluster() throws Exception {
    CLUSTER.createTopic(inputTopic, 2, 1);
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
  private KafkaStreams kafkaStreams;

  @Before
  public void setup(){
    Properties config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "user-regions-lambda-integration-test");
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, CLUSTER.bootstrapServers());
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    // The commit interval for flushing records to state stores and downstream must be lower than
    // this integration test's timeout (30 secs) to ensure we observe the expected processing results.
    config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100);
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    // Use a temporary directory for storing state, which will be automatically removed after the test.
    //streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, TestUtils.tempDirectory().getAbsolutePath());


//    Properties config = new Properties();
//    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "aggregate_region_test");
//    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
//    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//    config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");

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
//    builder.stream(viaGridTopic, Consumed.with(Serdes.String(), callSummarySerge))
//        .groupByKey()
//        .reduce((a,b)-> a)
//        .toStream()
//        .to(outputGridTopic, Produced.with(Serdes.String(), callSummarySerge));
    Topology topology = builder.build();


    Function<Call, String> toHcode = (call)-> "11";
    Function<Call, String> toGrid = (call)-> "223/353";
    StreamPartitioner partitioner = (key, value, partition)-> 0;

    topology.addSource("SOURCE", new LongDeserializer(), new JavaDeserializer(), inputTopic)
        .addProcessor("PROCESS_HCODE", () -> new CallSummaryProcessor(toHcode), "SOURCE")
        //.addProcessor("PROCESS_GRID", () -> new CallSummaryProcessor(toGrid), "SOURCE")
        .addSink("SINK_HCODE", viaHcodeTopic, new StringSerializer(), new JavaSerializer(), partitioner, "PROCESS_HCODE");  // partition을 하나로 모은다
        //.addSink("SINK_GRID", viaGridTopic, new StringSerializer(), new JavaSerializer(), partitioner, "PROCESS_GRID");
    System.out.println(topology.describe());

    kafkaStreams = new KafkaStreams(builder.build(), config);
    kafkaStreams.cleanUp();
    kafkaStreams.start();
  }


  @After
  public void tearDown() {
    kafkaStreams.close();
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

    Producer<Long, Call> producer = new KafkaProducer<>(userRegionsProducerConfig);
    Future<RecordMetadata> f = producer.send(new ProducerRecord<>(inputTopic, 0, 0l, 1l, new Call(1l, 127, 37, 10)));
    f.get();
    f = producer.send(new ProducerRecord<>(inputTopic, 0, 60*1000-1l, 2l, new Call(2l, 127, 37, 20)));
    f.get();
    f = producer.send(new ProducerRecord<>(inputTopic, 0, 60*1000+0l, 3l, new Call(3l, 127, 37, 30)));
    f.get();
    f = producer.send(new ProducerRecord<>(inputTopic, 0, 60*1000+1l, 4l, new Call(4l, 127, 37, 40)));
    f.get();
    f = producer.send(new ProducerRecord<>(inputTopic, 0, 60*1000*2+1l, 4l, new Call(5l, 127, 37, 50)));
    f.get();

    producer.flush();
    producer.close();

    Thread.sleep(10*1000);

    //
    // Step 3: Verify the application's output data.
    //
    Properties consumerConfig = new Properties();
    consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, CLUSTER.bootstrapServers());
    consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "user-regions-lambda-integration-test-standard-consumer");
    consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JavaDeserializer.class);

    KafkaConsumer<String, CallSummary> consumer = new KafkaConsumer<>(consumerConfig);
    consumer.subscribe(Collections.singletonList(viaHcodeTopic));
    ConsumerRecords<String, CallSummary> records = consumer.poll(3000);

    System.out.println("---");
    System.out.println(records.count());
    for(ConsumerRecord<String, CallSummary> rec : records)
      System.out.println(rec.key() + " @ " + rec.value());

    records = consumer.poll(3000);

    System.out.println("---");
    System.out.println(records.count());
    for(ConsumerRecord<String, CallSummary> rec : records)
      System.out.println(rec.key() + " @ " + rec.value());
    consumer.close();


    System.out.println("===");
    consumer = new KafkaConsumer<>(consumerConfig);
    consumer.subscribe(Collections.singletonList(outputHcodeTopic));
    records = consumer.poll(3000);

    System.out.println(records.count());
    for(ConsumerRecord<String, CallSummary> rec : records)
      System.out.println(rec.key() + " @ " + rec.value());

    System.out.println("===");
    records = consumer.poll(3000);

    System.out.println(records.count());
    for(ConsumerRecord<String, CallSummary> rec : records)
      System.out.println(rec.key() + " @ " + rec.value());
    consumer.close();

    /*
    List<KeyValue<String, Long>> actualClicksPerRegion = IntegrationTestUtils.waitUntilMinKeyValueRecordsReceived(consumerConfig,
        outputTopic, expectedUsersPerRegion.size());
    streams.close();
    assertThat(actualClicksPerRegion).containsExactlyElementsOf(expectedUsersPerRegion);



    ConsumerRecordFactory<Long, Call> recordFactory = new ConsumerRecordFactory<>(new LongSerializer(), new JavaSerializer());
    testDriver.pipeInput(recordFactory.create(inputTopic, 1l, new Call(1l, 127, 37, 10), 60*1000 - 2));
    testDriver.pipeInput(recordFactory.create(inputTopic, 2l, new Call(2l, 127, 37, 10), 60*1000 - 1));
    testDriver.pipeInput(recordFactory.create(inputTopic, 3l, new Call(3l, 127, 37, 10), 60*1000));

    // output topic verify
    ProducerRecord rec = testDriver.readOutput(viaHcodeTopic, stringDeserializer, new JavaDeserializer());
    OutputVerifier.compareKeyValue(rec, "197001010900:11", new CallSummary(1, 10));
    rec = testDriver.readOutput(viaHcodeTopic, stringDeserializer, new JavaDeserializer());
    OutputVerifier.compareKeyValue(rec, "197001010901:11", new CallSummary(2, 20));
    rec = testDriver.readOutput(viaHcodeTopic, stringDeserializer, new JavaDeserializer());
    Assert.assertNull(rec);
    */
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


