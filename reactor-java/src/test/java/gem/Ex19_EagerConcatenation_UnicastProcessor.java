package gem;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;
import reactor.test.StepVerifier;

public class Ex19_EagerConcatenation_UnicastProcessor {

  @Test
  public void test() {
    StepVerifier.create(
            Flux.range(1, 5)
                .map(
                    v -> {
                      Flux<Integer> o = getSource(v);
                      UnicastProcessor<Integer> up = UnicastProcessor.create();
                      o.subscribe(up);
                      return up; // UnicastProcessor를 반환한다.
                    })
                .concatMap(v -> v))
        .expectSubscription()
        .expectNext(0)
        .expectNext(0, 1)
        .expectNext(0, 1, 2)
        .expectNext(0, 1, 2, 3)
        .expectNext(0, 1, 2, 3, 4)
        .verifyComplete();
  }

  Flux<Integer> getSource(int i) {
    return Flux.range(0, i);
  }
}
