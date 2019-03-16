package old2;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class SampleTest {

  static Flux<Tuple2<Long, Long>> interval(long id, int interval, int duration) {
    return Flux.combineLatest((objects)->Tuples.of((Long)objects[0], (Long)objects[1]),
        Mono.just(id),
        Flux.interval(Duration.ofSeconds(interval)).take(Duration.ofSeconds(duration))
    );
  }

  static Flux<Tuple2<Long, Long>> interval(long id) {
    return interval(id, 1, 60);
  }

  @Test
  public void group_by_sample() {
    Flux.merge(interval(1), interval(2))
        .groupBy(it -> it.getT1())
        .flatMap(grouped -> grouped.sample(Duration.ofSeconds(10)))
        .subscribe(System.out::println);

    try {
      Thread.sleep(61*1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void sample_by_another_publisher() {
    Flux<Tuple2<Long, Long>> flux = interval(1);
    flux.sample( flux.filter(it -> it.getT2() % 10 == 0))
        .subscribe(System.out::println);

    try {
      Thread.sleep(61*1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  // do not work
  public void sample_by_another_publisher2() {
    List<Tuple2<Integer, Integer>> list = IntStream.range(0, 61)
        .mapToObj(it -> Tuples.of(1, it))
        .collect(Collectors.toList());

    Flux<Tuple2<Integer, Integer>> flux = Flux.fromIterable(list);

    flux.sample( flux.filter(it -> it.getT2() % 3 == 0))
        .subscribe(System.out::println);

    try {
      Thread.sleep(61*1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
