package old2;

import java.time.Duration;

import org.junit.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class CombiningPublishersIntegrationTest {
  private static Integer min = 1;
  private static Integer max = 5;

  private static Flux<Integer> evenNumbers = Flux.range(min, max).filter(x -> x % 2 == 0);
  private static Flux<Integer> oddNumbers = Flux.range(min, max).filter(x -> x % 2 > 0);

  @Test
  public void givenFluxes_whenMergeDelayErrorIsInvoked_thenMergeDelayError() {
    // time : 0 1 2 3 4
    // even :     2   4
    // odd  :   1 3 5

    int prefetch = 100;
    Flux<Integer> fluxOfIntegers = Flux.mergeDelayError(prefetch,
        evenNumbers.delayElements(Duration.ofMillis(200L)),
        oddNumbers.delayElements(Duration.ofMillis(100L)));

    StepVerifier.create(fluxOfIntegers)
        .expectNext(1)
        .expectNext(2)
        .expectNext(3)
        .expectNext(5)
        .expectNext(4)
        .expectComplete()
        .verify();
  }


    /*@Test
    public void givenFluxes_whenMergeWithDelayedElementsIsInvoked_thenMergeWithDelayedElements() {
        Flux<Integer> fluxOfIntegers = Flux.merge(
                evenNumbers.delayElements(Duration.ofMillis(2000L)),
                oddNumbers.delayElements(Duration.ofMillis(1000L)));

        StepVerifier.create(fluxOfIntegers)
        .expectNext(1)
        .expectNext(2)
        .expectNext(3)
        .expectNext(5)
        .expectNext(4)
        .expectComplete()
        .verify();
    }*/

  @Test
  public void givenFluxes_whenConcatIsInvoked_thenConcat() {
    Flux<Integer> fluxOfIntegers = Flux.concat(
        evenNumbers.delayElements(Duration.ofMillis(200L)),
        oddNumbers.delayElements(Duration.ofMillis(100L)));

    StepVerifier.create(fluxOfIntegers)
        .expectNext(2)
        .expectNext(4)
        .expectNext(1)
        .expectNext(3)
        .expectNext(5)
        .expectComplete()
        .verify();
  }

  @Test
  public void givenFluxes_whenMergeIsInvoked_thenMerge() {
    Flux<Integer> fluxOfIntegers = Flux.merge(
        evenNumbers.delayElements(Duration.ofMillis(100L)),
        oddNumbers.delayElements(Duration.ofMillis(80L)));

    StepVerifier.create(fluxOfIntegers)
        .expectNext(1) // 80ms
        .expectNext(2) // 100ms
        .expectNext(3) // 160ms
        .expectNext(4) // 200ms
        .expectNext(5) // 240ms
        .expectComplete()
        .verify();
  }

  @Test
  public void givenFluxes_whenConcatWithIsInvoked_thenConcatWith() {
    Flux<Integer> fluxOfIntegers = evenNumbers
        .concatWith(oddNumbers);

    StepVerifier.create(fluxOfIntegers)
        .expectNext(2)
        .expectNext(4)
        .expectNext(1)
        .expectNext(3)
        .expectNext(5)
        .expectComplete()
        .verify();
  }

  @Test
  public void givenFluxes_whenCombineLatestIsInvoked_thenCombineLatest() {
    Flux<Integer> fluxOfIntegers = Flux.combineLatest(
        evenNumbers,
        oddNumbers,
        (a, b) -> a + b);

    StepVerifier.create(fluxOfIntegers)
        .expectNext(5) // 4+1
        .expectNext(7) // 4+3
        .expectNext(9) // 4+5
        .expectComplete()
        .verify();

  }

  @Test
  public void givenFluxes_whenCombineLatestIsInvoked_thenCombineLatest1() {
    StepVerifier.create(Flux.combineLatest(obj -> (int) obj[1], evenNumbers, oddNumbers))
        .expectNext(1)
        .expectNext(3)
        .expectNext(5)
        .verifyComplete();
  }

  @Test
  public void givenFluxes_whenMergeSequentialIsInvoked_thenMergeSequential() {
    Flux<Integer> fluxOfIntegers = Flux.mergeSequential(
        evenNumbers,
        oddNumbers);

    StepVerifier.create(fluxOfIntegers)
        .expectNext(2)
        .expectNext(4)
        .expectNext(1)
        .expectNext(3)
        .expectNext(5)
        .expectComplete()
        .verify();
  }


  @Test
  public void givenFluxes_whenMergeWithIsInvoked_thenMergeWith() {
    Flux<Integer> fluxOfIntegers = evenNumbers.mergeWith(oddNumbers);

    StepVerifier.create(fluxOfIntegers)
        .expectNext(2)
        .expectNext(4)
        .expectNext(1)
        .expectNext(3)
        .expectNext(5)
        .expectComplete()
        .verify();
  }

  @Test
  public void givenFluxes_whenZipIsInvoked_thenZip() {
    Flux<Integer> fluxOfIntegers = Flux.zip(
        evenNumbers,
        oddNumbers,
        (a, b) -> a + b);

    StepVerifier.create(fluxOfIntegers)
        .expectNext(3) // 2+1
        .expectNext(7) // 4+3
        .expectComplete()
        .verify();
  }

  @Test
  public void givenFluxes_whenZipWithIsInvoked_thenZipWith() {
    Flux<Integer> fluxOfIntegers = evenNumbers
        .zipWith(oddNumbers,
            (a, b) -> a * b);

    StepVerifier.create(fluxOfIntegers)
        .expectNext(2) // 2*1
        .expectNext(12) // 4*3
        .expectComplete()
        .verify();
  }
}
