package old2;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;

public class CombineLatest {

  @Test
  public void test(){

    Flux<Tuple2> flux = Flux.combineLatest(
        Tuples::fromArray,
        Mono.just("x"),
        Flux.just(1, 2, 3));

    StepVerifier.create(flux)
        .expectNext(Tuples.fromArray(new Object[]{"x", 1}))
        .expectNext(Tuples.fromArray(new Object[]{"x", 2}))
        .expectNext(Tuples.fromArray(new Object[]{"x", 3}))
        .expectComplete()
        .verify();
  }

  @Test
  public void test2() throws InterruptedException {
    Flux<Tuple2> flux = Flux.combineLatest(
        Tuples::fromArray,
        Flux.range(1, 10).delayElements(Duration.ofMillis(100)),
        Flux.just("A","B","C","D","E").delayElements(Duration.ofMillis(200)));

    flux.subscribe(System.out::println);
    Thread.sleep(1000+100);
  }
}
