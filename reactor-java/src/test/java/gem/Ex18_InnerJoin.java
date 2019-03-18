package gem;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class Ex18_InnerJoin {

  @Test
  public void test() {
    Flux<Integer> main = Flux.range(1, 5);
    Flux<String> slave = Flux.just("a", "bb", "ccc");

    StepVerifier.create(
        main.flatMap(len -> slave.filter(s -> s.length() == len))
    )
        .expectSubscription()
        .expectNext("a", "bb", "ccc")
        .verifyComplete();
  }
}
