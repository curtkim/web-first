package old2;

import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class RxJavaExample {

  @Test
  public void test1() throws InterruptedException {
    long startTime = System.currentTimeMillis();
    Flux.interval(Duration.ofMillis(7))
        .timestamp()
        .sample(Duration.ofSeconds(1))
        .map(tuple -> tuple.getT1() - startTime  + " ms " + tuple.getT2())
        .take(5)
        .subscribe(System.out::println);

    Thread.sleep(1000*10);
  }
}
