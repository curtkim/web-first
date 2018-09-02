package kafka;

import domain.Record;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.GroupedFlux;

import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;

public class StreamTimeWindowTest {

  class Summary{
    int partition;
    long timestamp;
    int sum;

    public Summary(int partition) {
      this.partition = partition;
    }

    public Summary add(Record r){
      Summary s1 = new Summary(this.partition);
      s1.timestamp = r.timestamp / 1000 * 1000;
      s1.sum = this.sum + r.value;
      return s1;
    }

    @Override
    public String toString() {
      return "Summary{" +
          "partition=" + partition +
          ", timestamp=" + timestamp +
          ", sum=" + sum +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Summary summary = (Summary) o;
      return partition == summary.partition &&
          timestamp == summary.timestamp &&
          sum == summary.sum;
    }

    @Override
    public int hashCode() {

      return Objects.hash(partition, timestamp, sum);
    }
  }

  class Record{
    int partition;
    long timestamp;
    int value;

    public Record(int partition, long timestamp, int value) {
      this.partition = partition;
      this.timestamp = timestamp;
      this.value = value;
    }

    @Override
    public String toString() {
      return "Record{" +
          "partition=" + partition +
          ", timestamp=" + timestamp +
          ", value=" + value +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Record record = (Record) o;
      return partition == record.partition &&
          timestamp == record.timestamp &&
          value == record.value;
    }

    @Override
    public int hashCode() {

      return Objects.hash(partition, timestamp, value);
    }
  }

  class RandomGenerator implements Runnable{

    FluxSink<Record> sink;
    int partition;
    long current= 0;
    //Random random = new Random();

    RandomGenerator(int partition, FluxSink<Record> sink){
      this.partition = partition;
      this.sink = sink;
    }

    @Override
    public void run() {
      while(true){
        int i = 100;
        current+= i;
        sink.next(new Record(partition, current, i+partition));
        try {
          Thread.sleep(i);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Test
  public void test() throws InterruptedException {
    Flux<Record> flux = Flux.create(sink ->{
      new Thread(new RandomGenerator(0, sink)).start();
      new Thread(new RandomGenerator(1, sink)).start();
    });

    Predicate<Record> trigger = new Predicate<Record>() {
      long current = 0;

      @Override
      public boolean test(Record rec) {
        long truncated = rec.timestamp/ 1000*1000;
        //System.out.println(rec.timestamp + " " + truncated + " " + rec.partition);
        if( truncated > current){
          current = truncated;
          return true;
        }
        return false;
      }
    };

    Flux<Summary> flux2 = flux.groupBy(r -> r.partition)
        .flatMap((GroupedFlux<Integer, Record> partitionFlux) -> {
          int key = partitionFlux.key();
          System.out.println(key);
          return partitionFlux.windowUntil(trigger, true)
              .flatMap(window -> window.reduce(new Summary(key), (a, b) -> a.add(b)));
        });

    flux2.subscribe(System.out::println);
    //flux.subscribe(System.out::println);
    Thread.sleep(3300);
  }
}
