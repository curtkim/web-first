import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;


public class FluxGrid {

  @Test
  public void test2() {
    List<Integer> jobs = Arrays.asList(0, 1, 2, 3);
    StepVerifier.create(
      Flux.fromIterable(jobs)
          .window(2)
          .concatMap(it -> it.map(i -> Flux.range(i*2, 2)))
          .flatMap(it -> it)
    )
        .expectSubscription()
        .expectNext(0, 1, 2, 3, 4, 5, 6, 7)
        .verifyComplete();
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
