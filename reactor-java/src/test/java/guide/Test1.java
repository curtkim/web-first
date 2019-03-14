package guide;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import reactor.core.Exceptions;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;
import reactor.util.function.Tuple2;

//https://github.com/reactor/reactor-core/blob/master/reactor-core/src/test/java/reactor/guide/GuideTests.java
public class Test1 {

  @Test
  public void advancedCold() {
    Flux<String> source = Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
        .map(String::toUpperCase);

    source.subscribe(d -> System.out.println("Subscriber 1: "+d));
    source.subscribe(d -> System.out.println("Subscriber 2: "+d));
  }

  @Test
  public void advancedHot() {
    DirectProcessor<String> hotSource = DirectProcessor.create();

    Flux<String> hotFlux = hotSource.map(String::toUpperCase);


    hotFlux.subscribe(d -> System.out.println("Subscriber 1 to Hot Source: "+d));

    hotSource.onNext("blue");
    hotSource.onNext("green");

    hotFlux.subscribe(d -> System.out.println("Subscriber 2 to Hot Source: "+d));

    hotSource.onNext("orange");
    hotSource.onNext("purple");
    hotSource.onComplete();
  }

  @Test
  public void advancedBatchingGrouping() {
    StepVerifier.create(
        Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13)
            .groupBy(i -> i % 2 == 0 ? "even" : "odd")
            .concatMap(g -> g.defaultIfEmpty(-1) //if empty groups, show them
                .map(String::valueOf) //map to string
                .startWith(g.key())) //start with the group's key
    )
        .expectNext("odd")
        .expectNext("1", "3", "5", "11", "13")
        .expectNext("even")
        .expectNext("2", "4", "6", "12")
        .verifyComplete();
  }

  @Test
  public void advancedBatchingWindowing() {
    StepVerifier.create(
        Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13)
            .windowWhile(i -> i % 2 == 0)
            .concatMap(g -> g.defaultIfEmpty(-1))
    )
        .expectNext(-1, -1, -1) //respectively triggered by odd 1 3 5
        .expectNext(2, 4, 6) // triggered by 11
        .expectNext(12) // triggered by 13
        // however, no empty completion window is emitted (would contain extra matching elements)
        .verifyComplete();
  }

  @Test
  public void advancedBatchingBuffering() {
    /*
    StepVerifier.create(
        Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13)
            .bufferWhile(i -> i % 2 == 0)
    )
        .expectNext(Arrays.asList(2, 4, 6)) // triggered by 11
        .expectNext(Collections.singletonList(12)) // triggered by 13
        .verifyComplete();
        */
    Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13)
        .bufferWhile(i -> i % 2 == 0)
        .log()
        .subscribe(System.out::println);

  }

  public String convert(int i) throws IOException {
    if (i > 3) {
      throw new IOException("boom " + i);
    }
    return "OK " + i;
  }

  @Test
  public void errorHandlingPropagateUnwrap() {
    Flux<String> converted = Flux
        .range(1, 10)
        .map(i -> {
          try { return convert(i); }
          catch (IOException e) { throw Exceptions.propagate(e); }
        });

    converted.subscribe(
        v -> System.out.println("RECEIVED: " + v),
        e -> {
          if (Exceptions.unwrap(e) instanceof IOException) {
            System.out.println("Something bad happened with I/O");
          } else {
            System.out.println("Something bad happened");
          }
        }
    );

    StepVerifier.create(converted)
        .expectNext("OK 1")
        .expectNext("OK 2")
        .expectNext("OK 3")
        .verifyErrorMessage("boom 4");
  }

  interface MyEventListener<T> {
    void onDataChunk(List<T> chunk);
    void processComplete();
  }
  interface MyEventProcessor {
    void register(MyEventListener<String> eventListener);
    void dataChunk(String... values);
    void processComplete();
  }

  private MyEventProcessor myEventProcessor = new MyEventProcessor() {

    private MyEventListener<String> eventListener;
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void register(MyEventListener<String> eventListener) {
      this.eventListener = eventListener;
    }

    @Override
    public void dataChunk(String... values) {
      executor.schedule(() -> eventListener.onDataChunk(Arrays.asList(values)),
          500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void processComplete() {
      executor.schedule(() -> eventListener.processComplete(),
          500, TimeUnit.MILLISECONDS);
    }
  };

  @Test
  public void producingCreate() {
    Flux<String> bridge = Flux.create(sink -> {
      myEventProcessor.register( // <4>
          new MyEventListener<String>() { // <1>

            public void onDataChunk(List<String> chunk) {
              for(String s : chunk) {
                sink.next(s); // <2>
              }
            }

            public void processComplete() {
              sink.complete(); // <3>
            }
          });
    });

    StepVerifier.withVirtualTime(() -> bridge)
        .expectSubscription()
        .expectNoEvent(Duration.ofSeconds(10))
        .then(() -> myEventProcessor.dataChunk("foo", "bar", "baz"))
        .expectNext("foo", "bar", "baz")
        .expectNoEvent(Duration.ofSeconds(10))
        .then(() -> myEventProcessor.processComplete())
        .verifyComplete();
  }

  @Test
  public void errorHandlingIntervalMillisRetried() throws InterruptedException {
    //VirtualTimeScheduler virtualTimeScheduler = VirtualTimeScheduler.create();
    //VirtualTimeScheduler.set(virtualTimeScheduler);

    Flux<Tuple2<Long,String>> flux =
        Flux.interval(Duration.ofMillis(250))
            .map(input -> {
              if (input < 3) return "tick " + input;
              throw new RuntimeException("boom");
            })
            .retry(1)
            .elapsed(); // <1>

    //flux.subscribe(System.out::println, System.err::println); // <2>

    //Thread.sleep(10*1000); // <3>

    //virtualTimeScheduler.advanceTimeBy(Duration.ofHours(1));

    StepVerifier.create(flux.log())
        .expectNextMatches(t -> {System.out.println(t); t.getT2().equals("tick 0"); return true;})
        .expectNextMatches(t -> t.getT2().equals("tick 1"))
        .expectNextMatches(t -> t.getT2().equals("tick 2"))
        .expectNextMatches(t -> t.getT2().equals("tick 0"))
        .expectNextMatches(t -> t.getT2().equals("tick 1"))
        .expectNextMatches(t -> t.getT2().equals("tick 2"))
        .verifyErrorMessage("boom");
    /*
    StepVerifier.withVirtualTime(() -> flux, () -> virtualTimeScheduler, Long.MAX_VALUE)
        .thenAwait(Duration.ofSeconds(3))
        .expectNextMatches(t -> t.getT2().equals("tick 0"))
        .expectNextMatches(t -> t.getT2().equals("tick 1"))
        .expectNextMatches(t -> t.getT2().equals("tick 2"))
        .expectNextMatches(t -> t.getT2().equals("tick 0"))
        .expectNextMatches(t -> t.getT2().equals("tick 1"))
        .expectNextMatches(t -> t.getT2().equals("tick 2"))
        .verifyErrorMessage("boom");
        */
  }


}
