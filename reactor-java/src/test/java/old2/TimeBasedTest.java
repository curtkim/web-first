package old2;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class TimeBasedTest {

  @Test
  public void test() {
    VirtualTimeScheduler.getOrSet();

    StepVerifier.withVirtualTime(() -> Flux.interval(Duration.ofSeconds(10), Duration.ofSeconds(5)).take(3))
        .expectSubscription()
        .thenAwait(Duration.ofSeconds(10))
        .expectNext(0L)
        .thenAwait(Duration.ofSeconds(5))
        .expectNext(1L)
        .thenAwait(Duration.ofSeconds(5))
        .expectNext(2L)
        .verifyComplete();
  }


}
