package old2;

import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

//https://medium.com/@cheron.antoine/reactor-java-4-how-to-take-control-over-the-execution-of-mono-and-flux-ead31dc066
public class TakeControl {

  private <T> T identityWithThreadLogging(T el, String operation) {
    System.out.println(operation + " -- " + el + " -- " + Thread.currentThread().getName());
    return el;
  }

  @Test
  public void flatMapWithoutChangingScheduler() throws InterruptedException {
    Flux.range(1, 3)
        .map(n -> identityWithThreadLogging(n, "map1"))
        .flatMap(n -> Mono.just(n).map(nn -> identityWithThreadLogging(nn, "mono")))
        .subscribeOn(Schedulers.parallel())
        .subscribe(n -> {
          this.identityWithThreadLogging(n, "subscribe");
          System.out.println(n);
        });

    Thread.sleep(1000);
  }

  @Test
  public void flatMapWithChangingScheduler() throws InterruptedException {
    Flux.range(1, 3)
        .map(n -> identityWithThreadLogging(n, "map1"))
        .flatMap(n ->
            Mono.just(n)
                .subscribeOn(Schedulers.single())
                .map(nn -> identityWithThreadLogging(nn, "mono")))
        .subscribeOn(Schedulers.parallel())
        .subscribe(n -> {
          this.identityWithThreadLogging(n, "subscribe");
          System.out.println(n);
        });

    Thread.sleep(1000);
  }

  @Test
  public void complexCase() {
    Flux.range(1, 4).
        subscribeOn(Schedulers.immediate()).
        map(n -> identityWithThreadLogging(n, "map1")).
        flatMap(n -> {
          if (n == 1) return createMonoOnScheduler(n, Schedulers.parallel());
          if (n == 2) return createMonoOnScheduler(n, Schedulers.elastic());
          if (n == 3) return createMonoOnScheduler(n, Schedulers.single());
          return Mono.error(new Exception("error")).subscribeOn(Schedulers.newSingle("error-thread"));
        }).
        map(n -> identityWithThreadLogging(n, "map2")).
        subscribe(
            success -> System.out.println(identityWithThreadLogging(success, "subscribe")),
            error -> System.err.println(identityWithThreadLogging(error, "subscribe, err").getMessage())
        );
  }

  private Mono<Integer> createMonoOnScheduler(Integer n, Scheduler scheduler) {
    return Mono.just(n)
        .subscribeOn(scheduler)
        .map(nn -> identityWithThreadLogging(nn, "mono"));
  }

  @Test
  public void runOnMethodCallOnParallelFluxMatters() {
    Scheduler elastic = Schedulers.elastic();

    // FIRST TEST : .runOn(parallel) after the sleeping map
    ParallelFlux<Integer> fluxAfter = Flux.range(1, 100).parallel()
        .map(Object::toString)
        .map(Integer::valueOf)
        .map(i -> {
          try { Thread.sleep(12); }
          catch(InterruptedException e) { e.printStackTrace(); }
          System.out.println(Thread.currentThread().getName());
          return i;
        })
        .runOn(elastic)
        .map(i -> i % 2 == 0 ? i : i + 10);


    // SECOND TEST : .runOn(parallel) before the sleeping map
    ParallelFlux<Integer> fluxBefore = Flux.range(1, 100).parallel()
        .map(Object::toString)
        .map(Integer::valueOf)
        .runOn(elastic)
        .map(i -> {
          try { Thread.sleep(12); }
          catch(InterruptedException e) { e.printStackTrace(); }
          System.out.println(Thread.currentThread().getName());
          return i;
        })
        .map(i -> i % 2 == 0 ? i : i + 10);


  /*  ---
      Running this method gave me the following results :
      after = 1369ms
      before = 156ms
   */
  }

  @Test
  public void combiningParallelAndSequentialFlux() {
    Flux.range(1, 4)
        .subscribeOn(Schedulers.parallel())
        .map(n -> identityWithThreadLogging(n, "map1"))
        .parallel()
        .runOn(Schedulers.elastic())
        .map(n  -> identityWithThreadLogging(n, "parallelFlux"))
        .sequential()
        .map(n -> identityWithThreadLogging(n, "map2"))
        .subscribe(n -> identityWithThreadLogging(n, "subscribe"));
  }
}
