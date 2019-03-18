package gem;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class Ex04_IgnoreElements {

  @Test
  public void test() {
    Flux<Integer> a = Flux.range(1, 5);
    Flux<String> b = Flux.just("a", "B", "c");

    StepVerifier.create(Flux.concat(a.ignoreElements(), b))
        .expectSubscription()
        .expectNext("a", "B", "c")
        .verifyComplete();

  }
}
