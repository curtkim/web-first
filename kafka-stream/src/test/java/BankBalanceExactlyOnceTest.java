import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.streams.test.OutputVerifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Properties;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;



public class BankBalanceExactlyOnceTest {

  private static final String inputTopic = "bank-transactions";
  private static final String outputTopic = "bank-balance-exactly-once";

  private TopologyTestDriver testDriver;
  private StringDeserializer stringDeserializer = new StringDeserializer();
  final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
  final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();

  private ConsumerRecordFactory<String, JsonNode> recordFactory = new ConsumerRecordFactory<>(new StringSerializer(), jsonSerializer);


  @Before
  public void setup(){
    StreamsBuilder builder = new StreamsBuilder();

    // json Serde
    final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

    KStream<String, JsonNode> bankTransactions = builder.stream(inputTopic);

    // create the initial json object for balances
    ObjectNode initialBalance = JsonNodeFactory.instance.objectNode();
    initialBalance.put("count", 0);
    initialBalance.put("balance", 0);
    initialBalance.put("time", Instant.ofEpochMilli(0L).toString());

    KTable<String, JsonNode> bankBalance = bankTransactions
        .groupByKey()
        .aggregate(
            () -> initialBalance,
            (key, transaction, balance) -> newBalance(transaction, balance)
        );

    bankBalance.to(Serdes.String(), jsonSerde, outputTopic);

    Properties config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "bank-balance-application");
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");
    // Exactly once processing!!
    config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
    testDriver = new TopologyTestDriver(builder.build(), config);
  }

  private static JsonNode newBalance(JsonNode transaction, JsonNode balance) {
    // create a new balance json object
    ObjectNode newBalance = JsonNodeFactory.instance.objectNode();
    newBalance.put("count", balance.get("count").asInt() + 1);
    newBalance.put("balance", balance.get("balance").asInt() + transaction.get("amount").asInt());

    Long balanceEpoch = Instant.parse(balance.get("time").asText()).toEpochMilli();
    Long transactionEpoch = Instant.parse(transaction.get("time").asText()).toEpochMilli();
    Instant newBalanceInstant = Instant.ofEpochMilli(Math.max(balanceEpoch, transactionEpoch));
    newBalance.put("time", newBalanceInstant.toString());
    return newBalance;
  }

  @After
  public void tearDown() {
    testDriver.close();
  }

  @Test
  public void test1() {
    testDriver.pipeInput(newTransaction("john", 10));
    testDriver.pipeInput(newTransaction("john", 20));
    testDriver.pipeInput(newTransaction("john", 30));
    testDriver.pipeInput(newTransaction("john", 40));

    OutputVerifier.compareKeyValue(testDriver.readOutput(outputTopic, stringDeserializer, jsonDeserializer), "john", newNode("john", 1, 10));
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

  public ConsumerRecord newTransaction(String name, int amount) {
    ObjectNode transaction = JsonNodeFactory.instance.objectNode();
    // Instant.now() is to get the current time using Java 8
    Instant now = Instant.now();

    // we write the data to the json document
    transaction.put("name", name);
    transaction.put("amount", amount);
    transaction.put("time", now.toString());
    return recordFactory.create(inputTopic, name, transaction);
  }

  public ObjectNode newNode(String name, int count, int amount){
    ObjectNode transaction = JsonNodeFactory.instance.objectNode();
    transaction.put("name", name);
    transaction.put("amount", amount);
    transaction.put("count", count);
    return transaction;
  }
}
