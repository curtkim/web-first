import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.streams.test.OutputVerifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;


public class BankBalanceExactlyOnceTest2 {

  private static final String inputTopic = "bank-transactions";
  private static final String outputTopic = "bank-balance-exactly-once";

  private TopologyTestDriver testDriver;
  private StringDeserializer stringDeserializer = new StringDeserializer();
  private LongDeserializer longDeserializer = new LongDeserializer();

  private ConsumerRecordFactory<String, Long> recordFactory = new ConsumerRecordFactory<>(new StringSerializer(), new LongSerializer());


  @Before
  public void setup(){
    StreamsBuilder builder = new StreamsBuilder();

    KStream<String, Long> bankTransactions = builder.stream(inputTopic);
    KTable<String, Long> bankBalance = bankTransactions
        .groupByKey()
        .aggregate(
            () -> 0l,
            (key, transaction, balance) -> transaction + balance
        );

    bankBalance.toStream().to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));

    Properties config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "bank-balance-application");
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");
    // Exactly once processing!!
    config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
    testDriver = new TopologyTestDriver(builder.build(), config);
  }

  @After
  public void tearDown() {
    testDriver.close();
  }

  @Test
  public void test1() {
    testDriver.pipeInput(recordFactory.create(inputTopic, "john", 10l));
    testDriver.pipeInput(recordFactory.create(inputTopic, "john", 20l));
    testDriver.pipeInput(recordFactory.create(inputTopic, "john", 30l));
    testDriver.pipeInput(recordFactory.create(inputTopic, "john", 40l));

    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "john", 10l);
    // output topic verify
    /*
    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "green", 1L);
    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "red", 1L);

    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "green", 0L);
    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "red", 2L);

    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "red", 1L);
    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer), "blue", 1L);
    Assert.assertNull(testDriver.readOutput(outputTopic, stringDeserializer, longDeserializer));
    */
  }

}
