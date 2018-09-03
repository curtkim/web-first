package kafka;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Objects;
import java.util.function.Predicate;

public class PartitionWindowKindSummaryTest {

  @Test
  public void test() throws InterruptedException {
    Flux<Record> flux = Flux.create(sink ->{
      new Thread(new RandomGenerator(0, sink)).start();
      new Thread(new RandomGenerator(1, sink)).start();
      //new Thread(new RandomGenerator(2, sink)).start();
    });

    Flux<Summary> flux2 =
        flux.groupBy( r -> r.partition).flatMap(partitionFlux ->
            partitionFlux.windowUntil(new TimePredicate(), true).flatMap(window ->
                window.groupBy(r -> r.kind).flatMap(
                    kindedFlux -> kindedFlux.reduce(
                        new Summary(partitionFlux.key(), kindedFlux.key()),
                        (a,b)-> a.add(b)
                    )
                )
            )
        );

    flux2.subscribe(System.out::println);
    //flux.subscribe(System.out::println);
    Thread.sleep(2300);
  }
}

class Summary{
  int partition;
  long timestamp;
  String kind;
  int sum;

  public Summary(int partition, String kind) {
    this.partition = partition;
    this.kind = kind;
  }

  public Summary add(Record r){
    Summary s1 = new Summary(this.partition, this.kind);
    s1.timestamp = r.timestamp / 1000 * 1000;
    s1.sum = this.sum + r.value;
    return s1;
  }

  @Override
  public String toString() {
    return "Summary{" +
        "partition=" + partition +
        ", timestamp=" + timestamp +
        ", kind='" + kind + '\'' +
        ", sum=" + sum +
        '}';
  }
}

class Record{
  int partition;
  long timestamp;
  String kind;
  int value;

  public Record(int partition, long timestamp, String kind, int value) {
    this.partition = partition;
    this.timestamp = timestamp;
    this.kind = kind;
    this.value = value;
  }

  @Override
  public String toString() {
    return "Record{" +
        "partition=" + partition +
        ", timestamp=" + timestamp +
        ", kind='" + kind + '\'' +
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
        value == record.value &&
        Objects.equals(kind, record.kind);
  }

  @Override
  public int hashCode() {

    return Objects.hash(partition, timestamp, kind, value);
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
      String kind = current % 200 == 0 ? "A" : "B";
      sink.next(new Record(partition, current, kind, 1));
      try {
        Thread.sleep(i);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}


class TimePredicate implements Predicate<Record>{
  long current = 0;

  @Override
  public boolean test(Record rec) {
    long truncated = rec.timestamp/ 1000*1000;
    //System.out.println(rec.timestamp + " " + truncated + " " + rec.partition + " " + rec.value);
    if( truncated > current){
      current = truncated;
      return true;
    }
    return false;
  }
};

