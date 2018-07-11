import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.streams.test.OutputVerifier;
import org.junit.Assert;

import java.util.Properties;

public class DriverTestWrapper {

  protected String inputTopic;
  protected Serializer keySerializer;
  protected Serializer valueSerializer;

  protected String outputTopic;
  protected Deserializer keyDeserializer;
  protected Deserializer valueDeserializer;

  protected ConsumerRecordFactory recordFactory;
  protected Topology topology;
  protected TopologyTestDriver testDriver;

  public DriverTestWrapper(String inputTopic, String outputTopic, Topology topology, Properties config, Serializer keySerializer, Serializer valueSerializer, Deserializer keyDeserializer, Deserializer valueDeserializer) {
    this.inputTopic = inputTopic;
    this.keySerializer = keySerializer;
    this.valueSerializer = valueSerializer;
    this.outputTopic = outputTopic;
    this.keyDeserializer = keyDeserializer;
    this.valueDeserializer = valueDeserializer;

    this.topology = topology;
    recordFactory = new ConsumerRecordFactory(keySerializer, valueSerializer);
    testDriver = new TopologyTestDriver(topology, config);
  }

  public void tearDown() {
    testDriver.close();
  }


  void input(Object key, Object value){
    testDriver.pipeInput(recordFactory.create(inputTopic, key, value));
  }
  void readOutputAndAssert(Object key, Object value){
    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, keyDeserializer, valueDeserializer), key, value);
  }
  void readOutputAndAssertNull(){
    Assert.assertNull(testDriver.readOutput(outputTopic, keyDeserializer, valueDeserializer));
  }

  public KeyValueStore<String,Long> getKeyValueStore(String name) {
    return testDriver.getKeyValueStore(name);
  }
}
