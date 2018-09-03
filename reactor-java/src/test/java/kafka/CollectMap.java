package kafka;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Map;
import java.util.function.Predicate;

public class CollectMap {

  @Test
  public void test() throws InterruptedException {
    Flux<Taxi> flux = Flux.create(sink ->{
      new Thread(new RandomGenerator2(sink)).start();
    });

    flux.windowUntil(new TimePredicate2(), true).flatMap( window ->
        window.collectMap(t -> t.id).flatMapMany(map ->
            Flux.fromIterable(map.values()).groupBy(t -> t.offset).flatMap( grouped ->
                grouped.reduce(
                    new TaxiSummary(grouped.key()),
                    (a,b)-> a.add(b)
                )
            )
        )
    ).subscribe(System.out::println);

    Thread.sleep(2000);
  }

}

class TaxiSummary {

  long timestamp;
  long offset;
  int value;

  TaxiSummary(long offset){
    this.offset = offset;
  }

  TaxiSummary add(Taxi taxi){
    TaxiSummary c = new TaxiSummary(this.offset);
    c.timestamp = taxi.timestamp /1000 *1000;
    c.value = this.value +1;
    return c;
  }

  @Override
  public String toString() {
    return "TaxiSummary{" +
        "timestamp=" + timestamp +
        ", offset=" + offset +
        ", value=" + value +
        '}';
  }
}

class TimePredicate2 implements Predicate<Taxi> {
  long current = 0;

  @Override
  public boolean test(Taxi rec) {
    long truncated = rec.timestamp/ 1000*1000;
    //System.out.println(rec.timestamp + " " + truncated + " " + rec.partition + " " + rec.value);
    if( truncated > current){
      current = truncated;
      return true;
    }
    return false;
  }
};



class Taxi {
  long id;
  long timestamp;
  long offset;

  public Taxi(long id, long timestamp, long offset) {
    this.id = id;
    this.timestamp = timestamp;
    this.offset = offset;
  }

  @Override
  public String toString() {
    return "Taxi{" +
        "id=" + id +
        ", timestamp=" + timestamp +
        ", offset=" + offset +
        '}';
  }
}

class RandomGenerator2 implements Runnable{

  FluxSink<Taxi> sink;
  long current= 0;
  //Random random = new Random();

  RandomGenerator2(FluxSink<Taxi> sink){
    this.sink = sink;
  }

  @Override
  public void run() {
    while(true){
      current += 100;
      for(int i = 0; i < 10; i++)
        sink.next(new Taxi(i, current, current));
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
