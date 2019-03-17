package async;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.concurrent.ThreadLocalRandom;

import static junit.framework.TestCase.assertTrue;

// publishOn을 flatMap안에 붙이는 게 중요하다.
public class EachLimit {

  @Test
  // 55*100ms 걸린다면 순차로 실행한 것이다.
  // 10*100ms에 조금 넘어서는 수준이어야 한다.
  public void test() {
    int concurrent = 10;
    long startTime = System.currentTimeMillis();
    StepVerifier.create(
            Flux.range(0, 10)
                .flatMapSequential(
                    it ->
                        Mono.fromCallable(
                                () -> {
                                  Thread.sleep((10 - it) * 100);
                                  System.out.println(Thread.currentThread().getName() + " " + it);
                                  return it * 2;
                                })
                            .publishOn(Schedulers.elastic()),
                    concurrent))
        .expectSubscription()
        .expectNext(0, 2, 4, 6, 8, 10, 12, 14, 16, 18)
        .then(() -> assertTrue(System.currentTimeMillis() - startTime < 1500))
        .verifyComplete();
  }
}
