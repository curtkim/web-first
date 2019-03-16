package old2;

import domain2.Record;
import domain2.TimePredicate;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class MergeTest {

  @Test
  public void test() {
    Scheduler single = Schedulers.immediate();

    Flux flux1 = SampleTest.interval(1, 1, 10);
    Flux flux2 = SampleTest.interval(2, 2, 20);
    Flux flux3 = SampleTest.interval(3, 3, 20);

    Flux.merge(flux1, flux2, flux3)
        .publishOn(single)
        .doOnNext(it -> {
          System.out.println(Thread.currentThread().getName() + " " + it);
        })
        .doOnComplete(()-> {
          System.out.println(Thread.currentThread().getName() + " " + "done");
          single.dispose();
        })
        .subscribe();

    // TODO sleep을 없애려면 어떻게 해야 하나?
    try {
      Thread.sleep(21000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  // time기준으로 sort되어 처리할 수 있는 방법을 찾자. 현재는 실패
  @Test
  public void mergeSequential(){

    List records = Arrays.asList(
        new Record(0, 200, "A", 1),
        new Record(0, 400, "A", 1),
        new Record(0, 600, "A", 1),
        new Record(0, 800, "A", 1),
        new Record(0, 1000, "A", 1),
        new Record(0, 1200, "A", 1),
        new Record(0, 1400, "A", 1),
        new Record(0, 1600, "A", 1),
        new Record(0, 1800, "A", 1),
        new Record(0, 2000, "A", 1),

        new Record(1, 200, "A", 1),
        new Record(1, 400, "A", 1),
        new Record(1, 600, "A", 1),
        new Record(1, 800, "A", 1),
        new Record(1, 1000, "A", 1),
        new Record(1, 1200, "A", 1),
        new Record(1, 1400, "A", 1),
        new Record(1, 1600, "A", 1),
        new Record(1, 1800, "A", 1),
        new Record(1, 2000, "A", 1)
    );

    Flux<Record> flux = Flux.fromIterable(records);
    Flux.mergeSequential(flux.groupBy(r -> r.partition).flatMap(partitioned ->{
        return partitioned.windowUntil(new TimePredicate(), true);
    })).subscribe(System.out::println);

  }

  @Test
  public void mergeOrdered(){
    StepVerifier.create(Flux.mergeOrdered((a, b)-> a-b, Flux.just(3,4,5), Flux.just(1,2,3)))
      .expectNext(1,2,3,3,4,5)
      .expectComplete()
      .verify();
  }
}
