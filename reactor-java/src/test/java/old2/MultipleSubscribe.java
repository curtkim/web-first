package old2;

import java.time.Duration;
import reactor.core.publisher.Flux;

public class MultipleSubscribe {
  public static void main(String[] args) {

    /*
    Flux<Integer> flux = Flux.range(1, 10).log();
    flux.map(it -> it*2).subscribe(System.out::println);
    flux.subscribe(System.out::println);
    */

    Flux<Long> flux = Flux.interval(Duration.ofMillis(1000)).share().log();
    flux.map(it -> it*3).subscribe(System.out::println);
    flux.map(it -> it*2).subscribe(System.out::println);
    flux.subscribe(System.out::println);

    try {
      Thread.sleep(1000*5);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
