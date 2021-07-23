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

}
