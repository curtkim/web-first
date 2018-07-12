package call;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.streams.test.OutputVerifier;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.JavaDeserializer;
import util.JavaSerializer;

import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

// dsl로 만든 sub-topology가 있으면 동작을 하지 않는다. TestDriver여서 그런가? 아니면 실환경에서도 그럴것인가?
public class AggregateByTimeRegionTest {
  private static final String inputTopic = "call";
  private static final String outputHcodeTopic = "call_hcode";
  private static final String outputGridTopic = "call_grid";
  private static final String viaHcodeTopic = "call_hcode_via";
  private static final String viaGridTopic = "call_grid_via";

  private TopologyTestDriver testDriver;
  private StringDeserializer stringDeserializer = new StringDeserializer();
  private ConsumerRecordFactory<Long, Call> recordFactory = new ConsumerRecordFactory<>(new LongSerializer(), new JavaSerializer());

  @Before
  public void setup(){
    Properties config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "aggregate_region_test");
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");


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


    /*
    StreamsBuilder builder = new StreamsBuilder();
    builder.stream(viaHcodeTopic, Consumed.with(Serdes.String(), callSummarySerge))
        .groupByKey()
        .reduce((a,b)-> a)
        .toStream()
        .to(outputHcodeTopic, Produced.with(Serdes.String(), callSummarySerge));
    builder.stream(viaGridTopic, Consumed.with(Serdes.String(), callSummarySerge))
        .groupByKey()
        .reduce((a,b)-> a)
        .toStream()
        .to(outputGridTopic, Produced.with(Serdes.String(), callSummarySerge));
    Topology topology = builder.build();
    */

    Topology topology = new Topology();

    Function<Call, String> toHcode = (call)-> "11";
    Function<Call, String> toGrid = (call)-> "223/353";
    StreamPartitioner partitioner = (key, value, partition)-> 0;

    topology.addSource("SOURCE", new LongDeserializer(), new JavaDeserializer(), inputTopic)
        .addProcessor("PROCESS_HCODE", () -> new CallSummaryProcessor(toHcode), "SOURCE")
        .addProcessor("PROCESS_GRID", () -> new CallSummaryProcessor(toGrid), "SOURCE")
        .addSink("SINK_HCODE", viaHcodeTopic, new StringSerializer(), new JavaSerializer(), partitioner, "PROCESS_HCODE")  // partition을 하나로 모은다
        .addSink("SINK_GRID", viaGridTopic, new StringSerializer(), new JavaSerializer(), partitioner, "PROCESS_GRID");

    System.out.println(topology.describe());

    testDriver = new TopologyTestDriver(topology, config);
  }

  @After
  public void tearDown() {
    System.out.println("allStateStores : " + testDriver.getAllStateStores());
    testDriver.close();
  }

  @Test
  public void test1() throws InterruptedException {
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
  }

}


