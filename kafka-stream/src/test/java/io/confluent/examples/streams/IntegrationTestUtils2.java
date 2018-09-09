/*
 * Copyright Confluent Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.confluent.examples.streams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.QueryableStoreType;

/** Utility functions to make integration testing more convenient. */
public class IntegrationTestUtils2 {

  private static final int UNLIMITED_MESSAGES = -1;
  public static final long DEFAULT_TIMEOUT = 30 * 1000L;


  /**
   * Returns as many messages as possible from the topic until a (currently hardcoded) timeout is
   * reached.
   *
   * @param topic Kafka topic to read messages from
   * @param consumerConfig Kafka consumer configuration
   * @return The KeyValue elements retrieved via the consumer.
   */
  public static <K, V> List<ConsumerRecord<K, V>> readKeyValues(
      String topic,
      Properties consumerConfig,
      Deserializer<K> keyDeserializer,
      Deserializer<V> valueDeserializer) {
    return readKeyValues(
        topic, consumerConfig, keyDeserializer, valueDeserializer, UNLIMITED_MESSAGES);
  }

  /**
   * Returns up to `maxMessages` by reading via the provided consumer (the topic(s) to read from are
   * already configured in the consumer).
   *
   * @param topic Kafka topic to read messages from
   * @param consumerConfig Kafka consumer configuration
   * @param maxMessages Maximum number of messages to read via the consumer
   * @return The KeyValue elements retrieved via the consumer
   */
  public static <K, V> List<ConsumerRecord<K, V>> readKeyValues(
      String topic,
      Properties consumerConfig,
      Deserializer<K> keyDeserializer,
      Deserializer<V> valueDeserializer,
      int maxMessages) {

    KafkaConsumer<K, V> consumer =
        keyDeserializer != null && valueDeserializer != null
            ? new KafkaConsumer<>(consumerConfig, keyDeserializer, valueDeserializer)
            : new KafkaConsumer<>(consumerConfig);
    consumer.subscribe(Collections.singletonList(topic));
    int pollIntervalMs = 100;
    int maxTotalPollTimeMs = 2000;
    int totalPollTimeMs = 0;
    List<ConsumerRecord<K, V>> consumedValues = new ArrayList<>();
    while (totalPollTimeMs < maxTotalPollTimeMs
        && continueConsuming(consumedValues.size(), maxMessages)) {
      totalPollTimeMs += pollIntervalMs;
      ConsumerRecords<K, V> records = consumer.poll(pollIntervalMs);
      for (ConsumerRecord<K, V> record : records) {
        consumedValues.add(record);
      }
    }
    consumer.close();
    return consumedValues;
  }

  private static boolean continueConsuming(int messagesConsumed, int maxMessages) {
    return maxMessages <= 0 || messagesConsumed < maxMessages;
  }

  /**
   * @param topic Kafka topic to write the data records to
   * @param records Data records to write to Kafka
   * @param producerConfig Kafka producer configuration
   * @param <K> Key type of the data records
   * @param <V> Value type of the data records
   */
  public static <K, V> void produceKeyValuesSynchronously(
      String topic, Collection<KeyValue<K, V>> records, Properties producerConfig)
      throws ExecutionException, InterruptedException {
    Producer<K, V> producer = new KafkaProducer<>(producerConfig);
    for (KeyValue<K, V> record : records) {
      Future<RecordMetadata> f =
          producer.send(new ProducerRecord<>(topic, record.key, record.value));
      f.get();
    }
    producer.flush();
    producer.close();
  }

  public static <K, V> void produceRecordsSynchronously(
      Collection<ProducerRecord<K, V>> records,
      Properties producerConfig,
      Serializer<K> keySerializer,
      Serializer<V> valueSerializer)
      throws ExecutionException, InterruptedException {

    Producer<K, V> producer =
        keySerializer != null && valueSerializer != null
            ? new KafkaProducer<>(producerConfig, keySerializer, valueSerializer)
            : new KafkaProducer<>(producerConfig);

    for (ProducerRecord<K, V> record : records) {
      Future<RecordMetadata> f = producer.send(record);
      f.get();
    }
    producer.flush();
    producer.close();
  }

  public static <V> void produceValuesSynchronously(
      String topic, Collection<V> records, Properties producerConfig)
      throws ExecutionException, InterruptedException {
    Collection<KeyValue<Object, V>> keyedRecords =
        records.stream().map(record -> new KeyValue<>(null, record)).collect(Collectors.toList());
    produceKeyValuesSynchronously(topic, keyedRecords, producerConfig);
  }

  public static <K, V> List<ConsumerRecord<K, V>> waitUntilMinKeyValueRecordsReceived(
      Properties consumerConfig, String topic, int expectedNumRecords) throws InterruptedException {

    return waitUntilMinKeyValueRecordsReceived(
        consumerConfig, topic, expectedNumRecords, null, null, DEFAULT_TIMEOUT);
  }

  /**
   * Wait until enough data (key-value records) has been consumed.
   *
   * @param consumerConfig Kafka Consumer configuration
   * @param topic Topic to consume from
   * @param expectedNumRecords Minimum number of expected records
   * @param waitTime Upper bound in waiting time in milliseconds
   * @return All the records consumed, or null if no records are consumed
   * @throws AssertionError if the given wait time elapses
   */
  public static <K, V> List<ConsumerRecord<K, V>> waitUntilMinKeyValueRecordsReceived(
      Properties consumerConfig,
      String topic,
      int expectedNumRecords,
      Deserializer<K> keyDeserializer,
      Deserializer<V> valueDeserializer,
      long waitTime)
      throws InterruptedException {
    List<ConsumerRecord<K, V>> accumData = new ArrayList<>();
    long startTime = System.currentTimeMillis();
    while (true) {
      List<ConsumerRecord<K, V>> readData =
          readKeyValues(topic, consumerConfig, keyDeserializer, valueDeserializer);
      accumData.addAll(readData);
      if (accumData.size() >= expectedNumRecords) return accumData;
      if (System.currentTimeMillis() > startTime + waitTime)
        throw new AssertionError(
            "Expected "
                + expectedNumRecords
                + " but received only "
                + accumData.size()
                + " records before timeout "
                + waitTime
                + " ms");
      Thread.sleep(Math.min(waitTime, 100L));
    }
  }

  /**
   * Waits until the named store is queryable and, once it is, returns a reference to the store.
   *
   * <p>Caveat: This is a point in time view and it may change due to partition reassignment. That
   * is, the returned store may still not be queryable in case a rebalancing is happening or
   * happened around the same time. This caveat is acceptable for testing purposes when only a
   * single `KafkaStreams` instance of the application is running.
   *
   * @param streams the `KafkaStreams` instance to which the store belongs
   * @param storeName the name of the store
   * @param queryableStoreType the type of the (queryable) store
   * @param <T> the type of the (queryable) store
   * @return the same store, which is now ready for querying (but see caveat above)
   */
  public static <T> T waitUntilStoreIsQueryable(
      final String storeName,
      final QueryableStoreType<T> queryableStoreType,
      final KafkaStreams streams)
      throws InterruptedException {
    while (true) {
      try {
        return streams.store(storeName, queryableStoreType);
      } catch (InvalidStateStoreException ignored) {
        // store not yet ready for querying
        Thread.sleep(50);
      }
    }
  }
}
