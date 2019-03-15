package async;

import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.stream.Collectors;

public class CargoTest {

  @Test
  public void test() {
    Flux.interval(Duration.ofMillis(100)).take(Duration.ofSeconds(5))
        .buffer(Duration.ofMillis(300))
        .map(list -> {
          try {
            Thread.sleep(300);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          return String.join(",", list.stream().map(it-> it+"").collect(Collectors.toList()));
        })
        .subscribe(System.out::println);

    try {
      Thread.sleep(7000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
