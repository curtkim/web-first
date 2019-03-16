import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

public class FluxGrid {

  @Test
  public void test() {
    Flux<Integer> flux1 = Flux.range(0, 2);
    Flux<Integer> flux2 = Flux.range(2, 2);
    Flux<Integer> flux3 = Flux.range(4, 2);
    Flux<Integer> flux4 = Flux.range(6, 2);

    Flux<Flux<Integer>> fluxGrid = Flux.just(Flux.concat(flux1, flux2), Flux.concat(flux3, flux4));

    StepVerifier.create(fluxGrid.flatMap(row -> row.publishOn(Schedulers.parallel())))
        .expectSubscription()
        .expectNext(0, 1, 2, 3, 4, 5, 6, 7)
        .verifyComplete();
  }
}
