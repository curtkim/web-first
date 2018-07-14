import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Properties;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


// exactlyOnce를 테스트 해보고 싶었으나, TestDriver에서 안되는 건가? comment 처리함
public class BankBalanceExactlyOnceTest {
  private static final String inputTopic = "bank-transactions";
  private static final String outputTopic = "bank-balance-exactly-once";

  private TopologyTestDriver testDriver;
  private StringDeserializer stringDeserializer = new StringDeserializer();

  final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
  final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
  final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

  private ConsumerRecordFactory<String, JsonNode> recordFactory = new ConsumerRecordFactory<>(new StringSerializer(), jsonSerializer);


  @Before
  public void setup(){
    StreamsBuilder builder = new StreamsBuilder();

    KStream<String, JsonNode> bankTransactions = builder.stream(inputTopic, Consumed.with(Serdes.String(), jsonSerde));

    //
    KTable<String, JsonNode> bankBalance = bankTransactions
        .groupByKey()
        .aggregate(
            () -> {
              // create the initial json object for balances
              ObjectNode initialBalance = JsonNodeFactory.instance.objectNode();
              initialBalance.put("count", 0);
              initialBalance.put("balance", 0);
              initialBalance.put("time", Instant.ofEpochMilli(0L).toString());
              return initialBalance;
            },
            (key, transaction, balance) -> newBalance(transaction, balance),
            Materialized.with(Serdes.String(), jsonSerde)
        );

    bankBalance.toStream().to(outputTopic, Produced.with(Serdes.String(), jsonSerde));

    Properties config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "bank-balance-application");
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");
    // Exactly once processing!!
    //config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
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
    testDriver.pipeInput(recordFactory.create(inputTopic, "john", newTransaction("john", 10)));
    testDriver.pipeInput(recordFactory.create(inputTopic, "john", newTransaction("john", 20)));
    testDriver.pipeInput(recordFactory.create(inputTopic, "john", newTransaction("john", 30)));

    testDriver.pipeInput(recordFactory.create(inputTopic, "curt", newTransaction("curt", 40)));

    ProducerRecord<String, JsonNode> record1 = testDriver.readOutput(outputTopic, stringDeserializer, jsonDeserializer);
    assertEquals(10, record1.value().get("balance").asInt());
    assertEquals(1, record1.value().get("count").asInt());
    ProducerRecord<String, JsonNode> record2 = testDriver.readOutput(outputTopic, stringDeserializer, jsonDeserializer);
    assertEquals(30, record2.value().get("balance").asInt());
    assertEquals(2, record2.value().get("count").asInt());
    ProducerRecord<String, JsonNode> record3 = testDriver.readOutput(outputTopic, stringDeserializer, jsonDeserializer);
    assertEquals(60, record3.value().get("balance").asInt());
    assertEquals(3, record3.value().get("count").asInt());

    ProducerRecord<String, JsonNode> record4 = testDriver.readOutput(outputTopic, stringDeserializer, jsonDeserializer);
    assertEquals(40, record4.value().get("balance").asInt());
    assertEquals(1, record4.value().get("count").asInt());

    ProducerRecord<String, JsonNode> record5 = testDriver.readOutput(outputTopic, stringDeserializer, jsonDeserializer);
    assertNull(record5);
  }

  public JsonNode newTransaction(String name, int amount) {
    ObjectNode transaction = JsonNodeFactory.instance.objectNode();
    // Instant.now() is to get the current time using Java 8
    Instant now = Instant.now();

    // we write the data to the json document
    transaction.put("name", name);
    transaction.put("amount", amount);
    transaction.put("time", now.toString());
    return transaction;
  }
}
