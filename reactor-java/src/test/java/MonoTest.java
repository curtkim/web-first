import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;


public class MonoTest {

  @Test
  public void test_just() {
    StepVerifier.create(Mono.just(1))
        .expectSubscription()
        .expectNext(1)
        .verifyComplete();
  }

  @Test
  public void test_then() {
    // just를 무력화 시키고, Mono<Void>를 만든다.
    StepVerifier.create(Mono.just(1).then())
        .expectSubscription()
        .verifyComplete();
  }

  @Test
  public void test_then_another() {
    StepVerifier.create(Mono.just(1).then(Mono.just(2)))
        .expectSubscription()
        .expectNext(2)
        .verifyComplete();
  }

  @Test
  public void test_then_return() {
    StepVerifier.create(Mono.just(1).thenReturn("Exit"))
        .expectSubscription()
        .expectNext("Exit")
        .verifyComplete();
  }

  @Test
  public void test_then_many() {
    StepVerifier.create(Mono.just(1).thenMany(Flux.just(1,2,3)))
        .expectSubscription()
        .expectNext(1,2,3)
        .verifyComplete();
  }


  @Test
  public void test_empty() {
    StepVerifier.create(Mono.empty())
        .expectSubscription()
        .verifyComplete();
  }

  @Test
  public void test_void() {
    Mono<Void> mono = Mono.empty();

    StepVerifier.create(mono)
        .expectSubscription()
        .verifyComplete();
  }

  @Test
  public void test_when() {

    Flux<Long> a = Flux.interval(Duration.ofMillis(100)).take(5).log();
    Flux<Long> b = Flux.interval(Duration.ofMillis(100)).take(10).log();

    Mono<Void> mono = Mono.when(a, b);

    StepVerifier.create(mono)
        .expectSubscription()
        .verifyComplete();
  }

  @Test
  public void test_when_error() {

    Flux<Long> a = Flux.concat(Flux.interval(Duration.ofMillis(100)).take(5).log(), Flux.error(new RuntimeException("error")));
    Flux<Long> b = Flux.interval(Duration.ofMillis(100)).take(10).log();

    Mono<Void> mono = Mono.when(a, b);

    StepVerifier.create(mono)
        .expectSubscription()
        .expectError(RuntimeException.class);
        //.verifyComplete();
  }
}
