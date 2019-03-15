package old2;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class DistinctTest {

  private static List<String> words = Arrays.asList("the",
      "quick",
      "brown",
      "fox",
      "jumped",
      "over",
      "the",
      "lazy",
      "dog");

  @Test
  public void findingMissingLetter() {
    Flux<String> manyLetters = Flux
        .fromIterable(words)
        .flatMap(word -> Flux.fromArray(word.split("")))
        .distinct()
        .sort()
        .zipWith(Flux.range(1, Integer.MAX_VALUE),
            (string, count) -> String.format("%2d. %s", count, string));

    manyLetters.subscribe(System.out::println);
  }

  @Test
  public void distinct(){
    Flux<String> flux = Flux.just("A", "A", "B", "A", "C");

    StepVerifier.create(flux.distinct())
        .expectNext("A")
        .expectNext("B")
        .expectNext("C")
        .expectComplete()
        .verify();
  }

  @Test
  public void distinctUntilChanged(){
    Flux<String> flux = Flux.just("A", "A", "B", "A", "C");

    StepVerifier.create(flux.distinctUntilChanged())
        .expectNext("A")
        .expectNext("B")
        .expectNext("A")
        .expectNext("C")
        .expectComplete()
        .verify();
  }

}
