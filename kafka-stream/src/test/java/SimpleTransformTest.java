import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.streams.test.OutputVerifier;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Properties;

public class SimpleTransformTest {

  private static final String inputTopic = "simple-input";
  private static final String outputTopic = "simple-output";

  private TopologyTestDriver testDriver;
  private StringDeserializer stringDeserializer = new StringDeserializer();
  private LongDeserializer longDeserializer = new LongDeserializer();
  private ConsumerRecordFactory<String, String> recordFactory = new ConsumerRecordFactory<>(new StringSerializer(), new StringSerializer());

  @Before
  public void setup(){
    StreamsBuilder builder = new StreamsBuilder();

    KStream<String, String> textLines = builder.stream(inputTopic);
    textLines
        .mapValues((v)-> new JSONObject(v))
        .filter((k,v)-> v.getBoolean("success"))
        .to(outputTopic, Produced.valueSerde(new JSONObjectSerde()));

    Properties config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "simple-java");
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
    testDriver.pipeInput(recordFactory.create(inputTopic, "curt", new JSONObject().put("success", true).put("origin", "부천역").toString() ));
    testDriver.pipeInput(recordFactory.create(inputTopic, "mary", new JSONObject().put("success", false).put("origin", "강남역").toString() ));

    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, stringDeserializer),
        "curt", new JSONObject().put("success", true).put("origin", "부천역").toString());
    Assert.assertNull(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer));
  }
}


class JSONObjectSerde implements Serde<JSONObject>{

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
  }

  @Override
  public void close() {
  }

  @Override
  public Serializer<JSONObject> serializer() {
    return new Serializer<JSONObject>() {
      @Override
      public void configure(Map<String, ?> configs, boolean isKey) {
      }

      @Override
      public byte[] serialize(String topic, JSONObject data) {
        return data.toString().getBytes();
      }

      @Override
      public void close() {

      }
    };
  }

  @Override
  public Deserializer<JSONObject> deserializer() {
    return new Deserializer<JSONObject>() {
      @Override
      public void configure(Map<String, ?> configs, boolean isKey) {

      }

      @Override
      public JSONObject deserialize(String topic, byte[] data) {
        return new JSONObject(new String(data));
      }

      @Override
      public void close() {

      }
    };
  }
}
