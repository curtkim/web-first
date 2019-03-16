import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class FluxGrid {

  public static <T> List<List<T>> partition(List<T> list, int size) {
    final AtomicInteger counter = new AtomicInteger(0);

    return new ArrayList(
        list.stream()
            .collect(Collectors.groupingBy(it -> counter.getAndIncrement() % size))
            .values());
  }

  @Test
  public void test() {
    Flux flux1 = Flux.range(0, 2);
    Flux flux2 = Flux.range(2, 2);
    Flux flux3 = Flux.range(4, 2);
    Flux flux4 = Flux.range(6, 2);

    // Flux<Flux<Flux>>>
    StepVerifier.create(
        Flux.just(flux1, flux2, flux3, flux4)
          .window(2)
          .flatMap(it -> Flux.concat(it))
          .flatMap(it -> it)
    )
        .expectSubscription()
        .expectNext(0, 1, 2, 3, 4, 5, 6, 7)
        .verifyComplete();


    Flux<Flux> fluxGrid = Flux.just(
        Flux.concat(flux1, flux2),
        Flux.concat(flux3, flux4)
    );

    StepVerifier.create(
        fluxGrid.flatMap(row -> row.publishOn(Schedulers.parallel()))
    )
        .expectSubscription()
        .expectNext(0, 1, 2, 3, 4, 5, 6, 7)
        .verifyComplete();
  }
}
