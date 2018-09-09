package kafka;

import domain2.Record;
import domain2.Summary;
import domain2.TimePredicate;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

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


;

